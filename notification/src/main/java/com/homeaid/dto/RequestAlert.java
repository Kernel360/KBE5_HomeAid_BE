package com.homeaid.dto;

import com.homeaid.domain.Notification;
import com.homeaid.domain.enumerate.AlertType;
import com.homeaid.domain.enumerate.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestAlert {

    private AlertType alertType;

    // 수신자 정보
    private Long targetId;

    private UserRole targetRole;

    private Long relatedEntityId;  // 예약 ID, 매칭 ID 등

    private String content;

    @Builder
    private RequestAlert(AlertType alertType, Long targetId, UserRole targetRole, Long relatedEntityId, String content) {
        this.alertType = alertType;
        this.targetId = targetId;
        this.targetRole = targetRole;
        this.relatedEntityId = relatedEntityId;
        this.content = content;
    }

    public static RequestAlert createAlert(AlertType alertType, Long targetId, UserRole targetRole, Long relatedEntityId, String content) {
        return RequestAlert.builder()
                .alertType(alertType)
                .targetId(targetId)
                .targetRole(targetRole)
                .relatedEntityId(relatedEntityId)
                .content(content)
                .build();
    }

    public static Notification toNotification(RequestAlert notification) {
        return Notification.builder()
                .eventType(notification.getAlertType())
                .targetId(notification.getTargetId())
                .targetRole(notification.getTargetRole())
                .relatedEntityId(notification.getRelatedEntityId())
                .content(notification.getContent())
                .build();
    }

}
