package com.homeaid.service;

import com.homeaid.domain.Notification;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.dto.RequestAlert;
import com.homeaid.dto.ResponseAlert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SseNotificationService {

    // 사용자별 SSE 연결 관리
    private final Map<Long, SseEmitter> connections = new ConcurrentHashMap<>();
    private final Set<Long> adminIds = ConcurrentHashMap.newKeySet();
    private final NotificationService notificationService;
    private final Long SSE_TIMEOUT;

    public SseNotificationService(NotificationService notificationService,
                                  @Value("${sse.timeout}") Long sseTimeout) {
        this.notificationService = notificationService;
        this.SSE_TIMEOUT = sseTimeout;
    }

    public SseEmitter createConnection(Long userId, UserRole userRole) {

        SseEmitter existingEmitter = connections.get(userId);
        if (existingEmitter != null) {
            try {
                existingEmitter.complete();
            } catch (Exception e) {
                log.info("이미 존재하는 Emitter 연결 해제 실패");
            }
        }

        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT); // 2분 타임아웃

        if (userRole == UserRole.ADMIN) {
            adminIds.add(userId);
        }
        // 연결 정리 이벤트 처리
        emitter.onCompletion(() -> {
            removeConnection(userId);
            log.info("Connection onCompletion closed id: {}", userId);
        });
        emitter.onTimeout(() -> {
            removeConnection(userId);
            log.info("Connection timed out id: {}", userId);
            emitter.complete();
        });
        emitter.onError(e -> {
            removeConnection(userId);
            log.info("Connection error closed id: {}", userId);
        });
        connections.put(userId, emitter);
        log.info("connection 추가 총 count: {}", connections.size());

        return emitter;
    }

    // 특정 사용자에게 실시간 알림 전송
    public boolean sendAlertToUser(Long targetId, ResponseAlert responseAlert) {
        SseEmitter emitter = connections.get(targetId);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("new-notification")
                        .data(responseAlert));
                log.info("emitter sent new notification {}", responseAlert);
            } catch (IOException e) {
                log.info("SseEmitter sending error");
                removeConnection(targetId);
                emitter.complete();
                return false;
            }
        }
        return true;
//        notification.markAsSent();
    }

    @Async
    public void createAlertByRequestAlert(RequestAlert requestAlert) {
        Notification notification = RequestAlert.toNotification(requestAlert);
        notificationService.createNotification(notification);

        if (!connections.containsKey(notification.getTargetId())) {
            return;
        }

        boolean sendResult = sendAlertToUser(requestAlert.getTargetId(), ResponseAlert.toDto(notification));
        if (sendResult) {
            notification.markAsSent();
        }
    }

    @Async
    public void createAdminAlertByRequestAlert(RequestAlert requestAlert) {
        Notification notification = RequestAlert.toNotification(requestAlert);
        notificationService.createNotification(notification);

        if (adminIds.isEmpty()) {
            return;
        }

        broadcastAdminAlert(Collections.singletonList(notification));
    }

    //sse 연결시 알람 전송
    public SseEmitter sendAlertByConnection(List<Notification> notifications, SseEmitter emitter, Long userId) {
        try {
            emitter.send(SseEmitter.event()
                    .name("unread-notification")
                    .data(notifications.stream().map(ResponseAlert::toDto)));
        } catch (IOException e) {
            removeConnection(userId);
        }
        return emitter;
    }

    @Scheduled(fixedDelay = 30000) //60초
    @Transactional(readOnly = true) // 읽기 전용으로 명시
    public void unSentAllNotifications() {
        Set<Long> connectionIds = connections.keySet();

        // 최근 10분 내 생성 + 5분 이상 전송 안한 알림
        LocalDateTime recentCutoff = LocalDateTime.now().minusMinutes(10);
        LocalDateTime sendCutoff = LocalDateTime.now().minusMinutes(5);

        if (connectionIds.isEmpty()) {
            log.info("스케쥴러 연결된 아이디 없음");
            return;
        }
            log.info("스케줄러 시작");
        try {
        List<Notification> onlineUserAlerts = notificationService.getUnReadAlerts(connectionIds, recentCutoff, sendCutoff);
        List<Notification> onlineAdminAlerts = notificationService.getUnreadAdminAlerts(recentCutoff, sendCutoff);

            // SSE 전송은 트랜잭션 외부에서 수행
            processUserAlerts(onlineUserAlerts);
            processAdminAlerts(onlineAdminAlerts);
        } catch (Exception e) {
            log.error("스케줄러 실행 중 오류 발생", e);
        }

    }

    @Scheduled(fixedRate = 30000) // 30초마다
    public void sendHeartbeat() {
        List<Long> zombieConnections = new ArrayList<>();

        connections.forEach((userId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("ping")
                        .data(System.currentTimeMillis()));
            } catch (IOException e) {
                zombieConnections.add(userId);
                log.info("좀비 연결 발견: {}", userId);
            }
        });

        zombieConnections.forEach(connections::remove);
    }

    public void broadcastAdminAlert(List<Notification> notifications) {
        connections.keySet().stream()
                .filter(adminIds::contains)
                .forEach(adminId -> {
                    SseEmitter emitter = connections.get(adminId);
                    try {
                        emitter.send(SseEmitter.event()
                                .name("new-notification")
                                .data(notifications.stream().map(ResponseAlert::toDto)));
                        log.info("emitter admin sent new notification");

                    } catch (IOException e) {
                        removeConnection(adminId);
                    }
                });
        notifications.forEach(Notification::markAsSent);
    }

    public void gracefulDisconnect(Long userId) {
        SseEmitter emitter = connections.get(userId);
        if (emitter != null) {
            try {

                // 클라이언트에 종료 신호 전송
                emitter.send(SseEmitter.event()
                    .name("disconnect")
                    .data("Server initiated disconnect"));
            } catch (IOException | IllegalStateException e) {
                log.warn("SSE 전송 중단됨 - 연결이 이미 끊겼거나 전송 실패. userId: {}", userId, e);
            } finally {
                emitter.complete();
                log.info("SSE 연결 제거 완료 - userId: {}", userId);
            }
        }
    }

    private void removeConnection(Long userId) {
        connections.remove(userId);
    }

    // 3. SSE 전송과 DB 업데이트 분리
    private void processUserAlerts(List<Notification> alerts) {
        List<Notification> successfullySent = new ArrayList<>();

        for (Notification alert : alerts) {
            try {
                boolean sent = sendAlertToUser(alert.getTargetId(), ResponseAlert.toDto(alert));
                if (sent) {
                    successfullySent.add(alert);
                }
            } catch (Exception e) {
                log.error("알림 전송 실패: alertId={}", alert.getId(), e);
            }
        }

        // 성공한 알림만 DB 업데이트
        if (!successfullySent.isEmpty()) {
            updateSentAlerts(successfullySent);
        }
    }

    private void processAdminAlerts(List<Notification> alerts) {
        if (!alerts.isEmpty()) {
            try {
                broadcastAdminAlert(alerts);
                updateSentAlerts(alerts);
            } catch (Exception e) {
                log.error("관리자 알림 전송 실패", e);
            }
        }
    }

    // 4. DB 업데이트를 별도 트랜잭션으로 분리
    public void updateSentAlerts(List<Notification> notifications) {
        try {
            notificationService.updateMarkSentAt(notifications);
        } catch (Exception e) {
            log.error("알림 전송 상태 업데이트 실패", e);
            throw e; // 트랜잭션 롤백 보장
        }
    }
}
