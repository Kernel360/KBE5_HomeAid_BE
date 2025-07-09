package com.homeaid.domain;


import com.homeaid.domain.enumerate.NotificationEventType;
import com.homeaid.domain.enumerate.NotificationStatus;
import com.homeaid.domain.enumerate.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", columnDefinition = "VARCHAR(50)")
    private AlertType eventType;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Long targetId;      //관리자일 경우 null

    @Enumerated(EnumType.STRING)
    private UserRole targetRole;

    // 관련 엔티티 정보
    private Long relatedEntityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status = NotificationStatus.UNREAD;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    private LocalDateTime lastSentAt;

    @Builder
    public Notification(AlertType eventType,
                        Long targetId,
                        UserRole targetRole,
                        Long relatedEntityId,
                        String content) {
        this.eventType = eventType;
        this.targetId = targetId;
        this.targetRole = targetRole;
        this.relatedEntityId = relatedEntityId;
        this.lastSentAt = LocalDateTime.now();
        this.content = content;
    }

    public void markAsRead() {
        this.status = NotificationStatus.READ;
        this.readAt = LocalDateTime.now();
    }

    public void markAsSent() {
        this.lastSentAt = LocalDateTime.now();
    }
}