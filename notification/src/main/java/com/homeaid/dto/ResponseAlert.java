package com.homeaid.dto;

import com.homeaid.domain.Notification;
import com.homeaid.util.DateTimeUtil;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseAlert {
    private Long alertId;
    private String message;
    private String createdAt;
    private Long relatedEntityId;
    private String eventType;
    private String content;

    public static ResponseAlert toDto(Notification notification) {
        return ResponseAlert.builder()
                .alertId(notification.getId())
                .message(notification.getEventType().getDefaultMessage())
                .createdAt(DateTimeUtil.formatToSimpleDateTime(notification.getCreatedAt()))
                .relatedEntityId(notification.getRelatedEntityId())
                .eventType(notification.getEventType().name())
                .content(notification.getContent())
                .build();
    }
}
