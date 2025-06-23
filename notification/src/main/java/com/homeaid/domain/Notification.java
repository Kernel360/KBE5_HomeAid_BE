package com.homeaid.domain;

import com.homeaid.domain.enumerate.*;
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
    @Column(nullable = false)
    private NotificationEventType eventType;

    @Column(nullable = true)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = true)
    private Long targetId;      //관리자일 경우 null

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private UserRole targetRole;

    private Long senderId;

    @Enumerated(EnumType.STRING)
    private UserRole senderType;

    // 관련 엔티티 정보
    private Long relatedEntityId;

    @Enumerated(EnumType.STRING)
    private RelatedEntity relatedEntityType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status = NotificationStatus.UNREAD;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    private LocalDateTime lastSentAt;

    @Builder
    public Notification(NotificationEventType eventType,
                        Long targetId, UserRole targetRole,
                        Long senderId, UserRole senderType, Long relatedEntityId,
                        RelatedEntity relatedEntityType) {
        this.eventType = eventType;
        this.targetId = targetId;
        this.targetRole = targetRole;
        this.senderId = senderId;
        this.senderType = senderType;
        this.relatedEntityId = relatedEntityId;
        this.relatedEntityType = relatedEntityType;
    }

    public void markAsRead() {
        this.status = NotificationStatus.READ;
        this.readAt = LocalDateTime.now();
    }

    public void markAsSent() {
        this.lastSentAt = LocalDateTime.now();
    }
}