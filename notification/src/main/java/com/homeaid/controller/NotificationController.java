package com.homeaid.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.domain.Notification;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.dto.ResponseAlert;
import com.homeaid.security.jwt.JwtTokenProvider;
import com.homeaid.security.user.CustomUserDetails;
import com.homeaid.service.NotificationService;
import com.homeaid.service.SseNotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.List;
@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final SseNotificationService sseNotificationService;
    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping(value = "/connection", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(HttpServletRequest request) {

        String token = jwtTokenProvider.resolveToken(request);
        jwtTokenProvider.validateToken(token);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        UserRole userRole = UserRole.valueOf(
                jwtTokenProvider.getRoleFromToken(token).replace("ROLE_", ""));

        return sseNotificationService.createConnection(userId, userRole);
    }

    @PatchMapping("/{alertId}")
    public ResponseEntity<CommonApiResponse<Void>> modifyAlertToRead(@PathVariable (name = "alertId") Long alertId) {
        log.info("modifyAlertToRead alertId: {}", alertId);
        notificationService.updateMarkRead(alertId);
        return new ResponseEntity<>(CommonApiResponse.success(), HttpStatus.OK);
    }

    @PostMapping("/disconnect")
    public ResponseEntity<CommonApiResponse<Void>> disconnect(@AuthenticationPrincipal CustomUserDetails user) {
        sseNotificationService.gracefulDisconnect(user.getUserId());
        return ResponseEntity.ok(CommonApiResponse.success());
    }

    @GetMapping
    public ResponseEntity<CommonApiResponse<List<ResponseAlert>>> getAllAlerts(
            @AuthenticationPrincipal CustomUserDetails user) {
        List<Notification> notifications = null;

        if (UserRole.ADMIN.equals(user.getUserRole())) {
            notifications = notificationService.getUnReadAdminAlerts(UserRole.ADMIN); // 고정값 전달
        } else {
            notifications = notificationService.getUnReadAlerts(user.getUserId());
        }

        List<ResponseAlert> responseAlerts = null;
        if (notifications != null) {
            responseAlerts = notifications.stream().map(ResponseAlert::toDto).toList();
        }

        return ResponseEntity.ok(CommonApiResponse.success(responseAlerts));
    }
}
