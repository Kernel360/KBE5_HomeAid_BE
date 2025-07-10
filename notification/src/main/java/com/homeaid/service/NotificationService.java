package com.homeaid.service;

import com.homeaid.domain.Notification;
import com.homeaid.domain.enumerate.NotificationStatus;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.NotificationErrorCode;
import com.homeaid.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Transactional
    public void createNotification(Notification notification) {
        try {
            notificationRepository.save(notification);
        } catch (Exception e) {
            log.error("알림 생성 실패", e);
        }
    }

    //연결시 사용자의 읽지 않은 알림들
    @Transactional(readOnly = true)
    public List<Notification> getUnReadAlerts(Long userId, UserRole userRole) {
        List<Notification> notifications = null;
        if (UserRole.ADMIN.equals(userRole)) {
            notifications = notificationRepository.findByTargetRoleAndStatusOrderByCreatedAtDesc(userRole, NotificationStatus.UNREAD);
        }
        notifications = notificationRepository.findByTargetIdAndStatusOrderByCreatedAtDesc(userId, NotificationStatus.UNREAD);

        return Optional.ofNullable(notifications).orElse(Collections.emptyList());
    }

    @Transactional
    public void updateMarkSentAt(List<Notification> notifications) {
        notifications.forEach(Notification::markAsSent);
    }

    @Transactional
    public void updateMarkRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() ->
                new CustomException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));

        notification.markAsRead();
    }
}