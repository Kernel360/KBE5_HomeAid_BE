package com.homeaid.dto;

import com.homeaid.domain.Notification;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class ResponseAlert {
    private Long alertId;
    private String message;
    private LocalDateTime createdAt;

    private Long relatedEntityId;
    private String relatedEntityType;

    public static ResponseAlert toDto(Notification notification) {
        return ResponseAlert.builder()
                .alertId(notification.getId())
                .message(notification.getEventType().getDefaultMessage())
                .createdAt(notification.getCreatedAt())
                .relatedEntityId(notification.getRelatedEntityId())
                .relatedEntityType(notification.getRelatedEntityType().name())
                .build();
    }
}
