package com.homeaid.controller;

import com.homeaid.domain.Notification;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.ErrorCode;
import com.homeaid.security.user.CustomUserDetails;
import com.homeaid.service.NotificationService;
import com.homeaid.service.SseNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.List;
@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final SseNotificationService sseNotificationService;
    private final NotificationService notificationService;

    @GetMapping(value = "/connection", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal CustomUserDetails user) {

        SseEmitter sseEmitterInstance = sseNotificationService.createConnection(user.getUserId(), user.getUserRole());

        List<Notification> notifications = null;
        if (user.getUserRole().equals(UserRole.ADMIN)) {
            notifications = notificationService.getUnReadAdminAlerts(user.getUserRole());
        } else {
            notifications = notificationService.getUnReadAlerts(user.getUserId());
        }

        SseEmitter sseEmitter = sseNotificationService.sendAlertByConnection(notifications, sseEmitterInstance, user.getUserId());
        notificationService.updateMarkSentAt(notifications);
        return sseEmitter;
    }
}
