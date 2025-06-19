package com.homeaid.dto;

import com.homeaid.domain.Notification;
import com.homeaid.domain.enumerate.NotificationEventType;
import com.homeaid.domain.enumerate.RelatedEntity;
import com.homeaid.domain.enumerate.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RequestNotification {

    private NotificationEventType notificationEventType;

    // 수신자 정보
    private Long targetId;

    private UserRole targetRole;

    // 발신자 정보 (선택적)
    private Long senderId;

    private UserRole senderType;

    // 관련 엔티티 정보
    private Long relatedEntityId;  // 예약 ID, 매칭 ID 등
    private RelatedEntity relatedEntityType;

    public static Notification toNotification(RequestNotification notification) {
        return Notification.builder()
                .eventType(notification.getNotificationEventType())
                .targetId(notification.getTargetId())
                .targetRole(notification.getTargetRole())
                .senderId(notification.getSenderId())
                .senderType(notification.getSenderType())
                .relatedEntityId(notification.getRelatedEntityId())
                .relatedEntityType(notification.getRelatedEntityType())
                .build();
    }

}
