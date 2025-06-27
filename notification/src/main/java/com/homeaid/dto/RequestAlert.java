package com.homeaid.dto;

import com.homeaid.domain.Notification;
import com.homeaid.domain.enumerate.NotificationEventType;
import com.homeaid.domain.enumerate.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RequestAlert {

    private NotificationEventType notificationEventType;

    // 수신자 정보
    private Long targetId;

    private UserRole targetRole;

    private String content;

    private Long relatedEntityId;  // 예약 ID, 매칭 ID 등

    public static Notification toNotification(RequestAlert notification) {
        return Notification.builder()
                .eventType(notification.getNotificationEventType())
                .targetId(notification.getTargetId())
                .targetRole(notification.getTargetRole())
                .relatedEntityId(notification.getRelatedEntityId())
                .build();
    }

}
