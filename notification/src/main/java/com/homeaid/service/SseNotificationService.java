package com.homeaid.service;

import com.homeaid.domain.Notification;
import com.homeaid.domain.enumerate.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        SseEmitter emitter = new SseEmitter(300_000L); // 5분 타임아웃

        if (userRole == UserRole.ADMIN) {
            adminIds.add(userId);
        }
        // 연결 정리 이벤트 처리
        emitter.onCompletion(() -> {
            connections.remove(userId);
            log.info("Connection onCompletion closed");
        });
        emitter.onTimeout(() -> {
            emitter.complete();
            connections.remove(userId);
            log.info("Connection timed out");
        });
        emitter.onError(e -> {
            connections.remove(userId);
            log.info("Connection error closed");
        });
        connections.put(userId, emitter);
        log.info("connection count: {}", connections.size());

        return emitter;
    }

    // 특정 사용자에게 실시간 알림 전송
    public void sendAlertToUser(Long userId, Notification notification) {
        SseEmitter emitter = connections.get(userId);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("new-notification")
                        .data(notification));
            } catch (IOException e) {
                log.info("SseEmitter sending error");
                connections.remove(userId);
                emitter.complete();
            }
        }
    }

    @Transactional
    public void sendUnreadNotifications(Long userId, UserRole userType, SseEmitter emitter) {
        List<Notification> unreadNotifications =
                notificationService.getUnreadNotifications(userId, userType);
        try {
            emitter.send(SseEmitter.event()
                    .name("unread-notification")
                    .data(unreadNotifications));
        } catch (IOException e) {
            connections.remove(userId);
        }
    }

    //특정 사용자에게 알람 전송
    public SseEmitter sendNotificationByConnection(List<Notification> notifications, SseEmitter emitter, Long userId) {
        try {
            emitter.send(SseEmitter.event()
                    .name("unread-notification")
                    .data(notifications));
        } catch (IOException e) {
            connections.remove(userId);
        }
        return emitter;
    }

    //현재 관리자는 타겟아이디가 관리자 id인 사람만 알림을 받을수 있다
    //관리자가 받아야하는 알림을 모든 관리자가 일괄되게 받을려면 변경해야함
    @Scheduled(fixedDelay = 30000) //60초
    public void unSentAllNotifications() {
        log.info("schedule schedule schedule schedule notifications");

        Set<Long> connectionIds = connections.keySet();

        if (connectionIds.isEmpty()) {
            log.info("스케쥴러 , 연결된 아이디 없음");
            return;
        }

        List<Notification> onlineUserAlerts = notificationService.findByTargetIdAndUnsent(connectionIds);
        List<Notification> onlineAdminAlerts = notificationService.getUnreadNotificationAdmin();

        for (Notification alert : onlineUserAlerts) {
            sendAlertToUser(alert.getTargetId(), alert);
        }
        broadcastAdminNotification(onlineAdminAlerts);
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
                // 🎯 30초 안에 끊어진 연결 발견!
                // TCP 타임아웃(2시간) 기다리지 않음
                zombieConnections.add(userId);
                log.info("좀비 연결 발견: {}", userId);
            }
        });

        zombieConnections.forEach(connections::remove);
    }

    public void broadcastAdminNotification(List<Notification> notifications) {
        connections.keySet().stream()
                .filter(adminIds::contains)
                .forEach(adminId -> {
                    SseEmitter emitter = connections.get(adminId);
                    try {
                        emitter.send(SseEmitter.event()
                                .name("unread-notification")
                                .data(notifications));
                    } catch (IOException e) {
                        connections.remove(adminId);
                    }
                });
    }
}
