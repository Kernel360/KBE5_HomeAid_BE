package com.homeaid.repository;

import com.homeaid.domain.Notification;
import com.homeaid.domain.enumerate.NotificationStatus;
import com.homeaid.domain.enumerate.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByTargetIdAndStatus(Long userId, NotificationStatus notificationStatus);

    List<Notification> findByTargetRoleAndStatus(UserRole userType, NotificationStatus notificationStatus);


    //온라인사용자의
    @Query("SELECT a FROM Notification a WHERE " +
            "a.targetId IN :onlineUsers AND " +
            "a.isSent = false " +
            "ORDER BY a.createdAt DESC")
    List<Notification> findByTargetIdAndUnsent(Set<Long> onlineUsers);
}
