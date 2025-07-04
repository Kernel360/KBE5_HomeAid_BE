package com.homeaid.repository;

import com.homeaid.domain.Notification;
import com.homeaid.domain.enumerate.NotificationStatus;
import com.homeaid.domain.enumerate.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByTargetIdAndStatusOrderByCreatedAtDesc(Long userId, NotificationStatus notificationStatus);

    List<Notification> findByTargetRoleAndStatusOrderByCreatedAtDesc(UserRole userType, NotificationStatus notificationStatus);

    @Query("""
        SELECT
            n
        FROM
            Notification n
        WHERE
            n.targetId IN :connectionIds
            AND n.status = 'UNREAD'
            AND n.createdAt >= :recentCutoff
            AND n.lastSentAt <= :sentCutoff
        ORDER BY
            n.createdAt DESC
    """)
    List<Notification> findUnSentAlerts(
            Set<Long> connectionIds,
            LocalDateTime recentCutoff,
            LocalDateTime sentCutoff
    );

    @Query("""
        SELECT
            n
        FROM
            Notification n
        WHERE
            n.targetRole = 'ADMIN'
            AND n.status = 'UNREAD'
            AND n.createdAt >= :recentCutoff
            AND n.lastSentAt <= :sentCutoff
        ORDER BY
            n.createdAt DESC
    """)
    List<Notification> findUnsetAdminAlerts(
            LocalDateTime recentCutoff,
            LocalDateTime sentCutoff
    );
}
