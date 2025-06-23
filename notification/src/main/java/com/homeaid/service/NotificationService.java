package com.homeaid.service;

import com.homeaid.domain.Notification;
import com.homeaid.domain.enumerate.NotificationStatus;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.dto.RequestNotification;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.NotificationErrorCode;
import com.homeaid.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Async
    public void createNotification(RequestNotification requestNotification) {
        try {
            notificationRepository.save(RequestNotification.toNotification(requestNotification));
        } catch (Exception e) {
            log.error("알림 생성 실패", e);
        }
    }

    //연결시 사용자의 읽지 않은 알림들
    public List<Notification> getUnReadAlerts(Long userId) {
        return notificationRepository.findByTargetIdAndStatus(userId, NotificationStatus.UNREAD);
    }

    //연결시 관리자의 읽지 않은 알림들
    public List<Notification> getUnReadAdminAlerts(UserRole userType) {
        return notificationRepository.findByTargetRoleAndStatus(userType, NotificationStatus.UNREAD);
    }

    public List<Notification> getUnReadAlerts(Set<Long> connectionIds, LocalDateTime recentCutoff, LocalDateTime sendCutoff) {
        return notificationRepository.findUnSentAlerts(connectionIds, recentCutoff, sendCutoff);
    }

    @Transactional
    public void updateMarkSentAt(List<Notification> notifications) {
        notifications.forEach(Notification::markAsSent);
        log.info("✅ {} 개의 알림이 lastSentAt 업데이트됨", notifications.size());
    }

    public List<Notification> getUnreadAdminAlerts(LocalDateTime recentCutoff, LocalDateTime sendCutoff) {
        log.info("관리자 안읽은 알림");
        return notificationRepository.findUnsetAdminAlerts(recentCutoff, sendCutoff);
    }

    @Transactional
    public void updateMarkRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() ->
                new CustomException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));

        notification.markAsRead();
    }
}