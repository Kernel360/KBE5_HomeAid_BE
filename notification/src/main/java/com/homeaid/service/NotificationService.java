package com.homeaid.service;

import com.homeaid.domain.Notification;
import com.homeaid.domain.enumerate.NotificationStatus;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.dto.RequestNotification;
import com.homeaid.repository.NotificationRepository;
import jakarta.transaction.Transactional;
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
            notificationRepository.save(RequestNotification.toNotification(requestNotification));
        } catch (Exception e) {
            log.error("알림 생성 실패", e);
        }
    }

    // 최초 연결시 사용자의 읽지 않은 알림들
    public List<Notification> getUnreadNotifications(Long userId, UserRole userType) {
        if (userType == UserRole.ADMIN) {
            //관리자는 여려명에게 다보낸다하면 그중 한명이 알람을 읽고 해당 이벤트를 처리를 안한다면?..
        }
        return notificationRepository.findByTargetIdAndStatus(userId, NotificationStatus.UNREAD);
    }

    public List<Notification> getUnreadNotificationsForAdmin(UserRole userType) {
        return notificationRepository.findByTargetRoleAndStatus(userType, NotificationStatus.UNREAD);
    }


    public List<Notification> findByTargetIdAndUnsent(Set<Long> connectionIds) {
        log.info("스케줄러 데이터 확인할 아이디들 {}", connectionIds.toString());
        return notificationRepository.findByTargetIdAndUnsent(connectionIds);
    }

    @Transactional
    public void updateNotificationMarkSent(List<Notification> notifications) {
        notifications.forEach(Notification::markAsSent);
        log.info("✅ {} 개의 알림이 isSent=true로 업데이트됨", notifications.size());
    }

    public List<Notification> getUnreadNotificationAdmin() {
        log.info("관리자 안읽은 알림");
        return notificationRepository.findByTargetRoleAndStatus(UserRole.ADMIN, NotificationStatus.UNREAD);
    }
}