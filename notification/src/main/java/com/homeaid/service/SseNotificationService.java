package com.homeaid.service;

import com.homeaid.domain.Notification;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.dto.RequestAlert;
import com.homeaid.dto.ResponseAlert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SseNotificationService {

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
                log.error(e.getMessage());
            }
        }

        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        connections.put(userId, emitter);

        if (userRole == UserRole.ADMIN) {
            adminIds.add(userId);
        }
        // 연결 정리 이벤트 처리
        emitter.onCompletion(() -> {
            removeConnection(userId);
        });
        emitter.onTimeout(() -> {
            removeConnection(userId);
        });
        emitter.onError(e -> {
            removeConnection(userId);
        });

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("Connected successfully"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

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
            } catch (IOException e) {
                return false;
            }
        }
        return true;
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
        updateSentAlerts(Collections.singletonList(notification));
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
                        removeConnection(adminId);
                    }
                });
    }

    public void gracefulDisconnect(Long userId) {
        SseEmitter emitter = connections.get(userId);
        if (emitter != null) {
            try {
                emitter.complete();
            } finally {
                removeConnection(userId);
            }
        }
    }

    private void removeConnection(Long userId) {
        connections.remove(userId);
        adminIds.remove(userId);
    }

    public void updateSentAlerts(List<Notification> notifications) {
        try {
            notificationService.updateMarkSentAt(notifications);
        } catch (Exception e) {
            log.error("알림 전송 상태 업데이트 실패", e);
            throw e;
        }
    }

    @Scheduled(fixedRate = 30000) //30초마다 핑
    public void sendHeartbeat() {
        connections.forEach((userId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("ping")
                        .data(System.currentTimeMillis()));
            } catch (IOException ignored) {
                emitter.complete();
            }
        });
    }
}
