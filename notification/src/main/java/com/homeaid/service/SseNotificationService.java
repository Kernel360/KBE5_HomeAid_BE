package com.homeaid.service;

import com.homeaid.domain.Notification;
import com.homeaid.domain.enumerate.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
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
        SseEmitter emitter = new SseEmitter(60_000L); // 30초 타임아웃

        // 연결 정리 이벤트 처리
        emitter.onCompletion(() -> connections.remove(userId));
        emitter.onTimeout(() -> connections.remove(userId));
        emitter.onError(e -> connections.remove(userId));

        //연결 끊어졌을 때 최초에 읽지 않은 알림 보낸거 isSent 다시 false 처리해야 다음 접속시 안읽은 알림 발송 가능함

        connections.put(userId, emitter);

        log.info("connection count: {}", connections.size());

        // 🎯 연결 즉시 읽지 않은 알림 전송
        sendUnreadNotifications(userId, userRole, emitter);

        return emitter;
    }

    // 특정 사용자에게 실시간 알림 전송
    public void sendAlertToUser(Long userId, Notification notification) {
        //String connectionKey = userId + "_" + userType.name();
        SseEmitter emitter = connections.get(userId);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("new-notification")
                        .data(notification));
            } catch (IOException e) {
                connections.remove(userId);
            }
        }
    }

    private void sendUnreadNotifications(Long userId, UserRole userType, SseEmitter emitter) {
        List<Notification> unreadNotifications =
                notificationService.getUnreadNotifications(userId, userType);

        //관리자면 관리자 리스트로 알림 아니면 단일 알림

        try {
            emitter.send(SseEmitter.event()
                    .name("unread-notification")
                    .data(unreadNotifications));
        } catch (IOException e) {
            connections.remove(userId);
        }
        // 보낸 알림 isSent true 처리
        //그래야 스케줄에서 읽지 않는 알람 중복 발송 안함
    }

    //현재 관리자는 타겟아이디가 관리자 id인 사람만 알림을 받을수 있다
    //관리자가 받아야하는 알림을 모든 관리자가 일괄되게 받을려면 변경해야함
    @Scheduled(fixedDelay = 60000) //60초
    public void unSentAllNotifications() {
        log.info("schedule schedule schedule schedule notifications");

        Set<Long> connectionIds = connections.keySet();

        if (connectionIds.isEmpty()) {
            log.info("스케쥴러 , 연결된 아이디 없음");
            return;
        }

        List<Notification> onlineUserAlerts = notificationService.findByTargetIdAndUnsent(connectionIds);

        for (Notification alert : onlineUserAlerts) {
            sendAlertToUser(alert.getTargetId(), alert);
        }
    }

}
