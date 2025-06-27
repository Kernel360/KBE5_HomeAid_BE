package com.homeaid.service;

import com.homeaid.domain.Notification;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.dto.RequestAlert;
import com.homeaid.dto.ResponseAlert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Component
@RequiredArgsConstructor
@Slf4j
public class SseNotificationService {

    // 사용자별 SSE 연결 관리
    private final Map<Long, SseEmitter> connections = new ConcurrentHashMap<>();
    private final Set<Long> adminIds = ConcurrentHashMap.newKeySet();
    private final NotificationService notificationService;

    public SseEmitter createConnection(Long userId, UserRole userRole) {
        log.info("connection before {}", connections.size());

        SseEmitter existingEmitter = connections.get(userId);
        if (existingEmitter != null) {
            try {
                existingEmitter.complete();
            } catch (Exception e) {
                log.info("이미 존재하는 Emitter 연결 해제 실패");
                log.error(e.getMessage());

            }
        }

        SseEmitter emitter = new SseEmitter(120_000L); // 2분 타임아웃

        if (userRole == UserRole.ADMIN) {
            adminIds.add(userId);
        }
        // 연결 정리 이벤트 처리
        emitter.onCompletion(() -> {
            connections.remove(userId);
            log.info("Connection onCompletion closed id: {}", userId);
        });
        emitter.onTimeout(() -> {
            connections.remove(userId);
            log.info("Connection timed out id: {}", userId);
            emitter.complete();
        });
        emitter.onError(e -> {
            connections.remove(userId);
            log.info("Connection error closed id: {}", userId);
        });
        connections.put(userId, emitter);
        log.info("connection count: {}", connections.size());

        return emitter;
    }

    // 특정 사용자에게 실시간 알림 전송
    public void sendAlertToUser(Long targetId, Notification notification) {
        SseEmitter emitter = connections.get(targetId);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("new-notification")
                        .data(ResponseAlert.toDto(notification)));
            } catch (IOException e) {
                log.info("SseEmitter sending error");
                connections.remove(targetId);
                emitter.complete();
            }
        }
        notification.markAsSent();
    }

    @Async
    public void createAlertByRequestAlert(RequestAlert requestAlert) {
        Notification notification = RequestAlert.toNotification(requestAlert);
        notificationService.createNotification(notification);

        if (!connections.containsKey(notification.getTargetId())) {
            return;
        }

        sendAlertToUser(requestAlert.getTargetId(), notification);
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
            connections.remove(userId);
        }
        return emitter;
    }

    @Scheduled(fixedDelay = 30000) //60초
    public void unSentAllNotifications() {
        Set<Long> connectionIds = connections.keySet();

        // 최근 10분 내 생성 + 5분 이상 전송 안한 알림
        LocalDateTime recentCutoff = LocalDateTime.now().minusMinutes(10);
        LocalDateTime sendCutoff = LocalDateTime.now().minusMinutes(5);

        if (connectionIds.isEmpty()) {
            log.info("스케쥴러 연결된 아이디 없음");
            return;
        }

        List<Notification> onlineUserAlerts = notificationService.getUnReadAlerts(connectionIds, recentCutoff, sendCutoff);
        List<Notification> onlineAdminAlerts = notificationService.getUnreadAdminAlerts(recentCutoff, sendCutoff);

        for (Notification alert : onlineUserAlerts) {
            sendAlertToUser(alert.getTargetId(), alert);
        }
        broadcastAdminAlert(onlineAdminAlerts);
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
                    } catch (IOException e) {
                        connections.remove(adminId);
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

                connections.remove(userId);
                log.info("emitter disconnected");
                log.info("emitter 확인: {}", emitter);

            } catch (Exception e) {
                log.warn("SSE 정상 종료 실패 - userId: {}", userId, e);
            }
        }
    }
}
