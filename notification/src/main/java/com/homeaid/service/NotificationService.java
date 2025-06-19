package com.homeaid.service;

import com.homeaid.domain.Notification;
import com.homeaid.domain.enumerate.NotificationStatus;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.dto.RequestNotification;
import com.homeaid.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    //다른 비즈니스로직중 알림이벤트 추가할때
    @Async
    public void createNotification(RequestNotification requestNotification) {
        try {
            // 1. DB에 알림 저장
            notificationRepository.save(RequestNotification.toNotification(requestNotification));

            // 2. 실시간 전송 (해당 사용자가 온라인이면)
//            sseService.sendNotificationToUser(
//                    notification.getRecipientId(),
//                    notification.getRecipientType(),
//                    notification
//            );

        } catch (Exception e) {
            log.error("알림 생성 실패", e);
        }
    }

    // 최초 연결시 사용자의 읽지 않은 알림들
    public List<Notification> getUnreadNotifications(Long userId, UserRole userType) {
        return notificationRepository.findByTargetIdAndStatus(userId, NotificationStatus.UNREAD);
    }

    public List<Notification> getUnreadNotificationsForAdmin(UserRole userType) {
        return notificationRepository.findByTargetRoleAndStatus(userType, NotificationStatus.UNREAD);
    }


    public List<Notification> findByTargetIdAndUnsent(Set<Long> connectionIds) {
        return notificationRepository.findByTargetIdAndUnsent(connectionIds);
    }
}