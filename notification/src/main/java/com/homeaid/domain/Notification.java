package com.homeaid.domain;

import com.homeaid.domain.enumerate.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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

    // 기본 정보
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private NotificationEventType eventType;

    @Column(nullable = true)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    // 수신자 정보
    @Column(nullable = true)
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private UserRole targetRole;

    // 발신자 정보 (선택적)
    private Long senderId;

    @Enumerated(EnumType.STRING)
    private UserRole senderType;

    // 관련 엔티티 정보
    private Long relatedEntityId;  // 예약 ID, 매칭 ID 등

    @Enumerated(EnumType.STRING)
    private RelatedEntity relatedEntityType;  // "RESERVATION", "MATCHING" 등

    // 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status = NotificationStatus.UNREAD;

    // 메타데이터 (JSON 형태로 추가 정보 저장)
    // {"customerName": "홍길동", "serviceType": "청소" 등}
//    @Column(columnDefinition = "TEXT")
//    private String metadata;

    // 스케줄 관련
    // 예약 발송 시간
//    private LocalDateTime scheduledAt;
//    private boolean isScheduled = false;
    private boolean isSent = false;

    // 감사 정보
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

//    @LastModifiedDate
//    private LocalDateTime updatedAt;

    private LocalDateTime readAt;

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

    // 비즈니스 메서드
    public void markAsRead() {
        this.status = NotificationStatus.READ;
        this.readAt = LocalDateTime.now();
    }

    public void markAsSent() {
        this.isSent = true;
    }

//    public boolean isReadyToSend() {
//        return isScheduled && !isSent &&
//                (scheduledAt == null || LocalDateTime.now().isAfter(scheduledAt));
//    }
}