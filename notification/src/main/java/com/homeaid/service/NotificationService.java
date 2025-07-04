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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public void createNotification(Notification notification) {
        try {
            notificationRepository.save(notification);
        } catch (Exception e) {
            log.error("알림 생성 실패", e);
        }
    }

    //연결시 사용자의 읽지 않은 알림들
    @Transactional(readOnly = true)
    public List<Notification> getUnReadAlerts(Long userId) {
        return notificationRepository.findByTargetIdAndStatusOrderByCreatedAtDesc(userId, NotificationStatus.UNREAD);
    }

    //연결시 관리자의 읽지 않은 알림들
    @Transactional(readOnly = true)
    public List<Notification> getUnReadAdminAlerts(UserRole userType) {
        return notificationRepository.findByTargetRoleAndStatusOrderByCreatedAtDesc(userType, NotificationStatus.UNREAD);
    }

    @Transactional(readOnly = true)
    public List<Notification> getUnReadAlerts(Set<Long> connectionIds, LocalDateTime recentCutoff, LocalDateTime sendCutoff) {
        return notificationRepository.findUnSentAlerts(connectionIds, recentCutoff, sendCutoff);
    }

    @Transactional
    public void updateMarkSentAt(List<Notification> notifications) {
        notifications.forEach(Notification::markAsSent);
    }

    @Transactional(readOnly = true)
    public List<Notification> getUnreadAdminAlerts(LocalDateTime recentCutoff, LocalDateTime sendCutoff) {
        return notificationRepository.findUnsetAdminAlerts(recentCutoff, sendCutoff);
    }

    @Transactional
    public void updateMarkRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() ->
                new CustomException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));

        notification.markAsRead();
    }
}