package com.homeaid.controller;

import com.homeaid.security.user.CustomUserDetails;
import com.homeaid.service.SseNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final SseNotificationService sseNotificationService;

    @GetMapping(value = "/connection", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal CustomUserDetails user) {
        log.info("🔥 SSE 연결 요청 도착! User: {}", user != null ? user.getUsername() : "null");

        if (user == null) {
            log.error("❌ User가 null입니다!");
            throw new RuntimeException("인증되지 않은 사용자");
        }
        log.info("subscribe subscribe subscribe {}", user.getUsername());

        return sseNotificationService.createConnection(user.getUserId(), user.getUserRole());
    }
}
