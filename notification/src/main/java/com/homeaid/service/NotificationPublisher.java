package com.homeaid.service;

import com.homeaid.dto.RequestAlert;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    public static final String NOTIFICATION_CHANNEL = "notification_channel";
    public static final String NOTIFICATION_ADMIN_CHANNEL = "notification_admin_channel";

    public void publishNotification(RequestAlert alert) {
        redisTemplate.convertAndSend(NOTIFICATION_CHANNEL, alert);
    }

    public void publishAdminNotification(RequestAlert alert) {
        redisTemplate.convertAndSend(NOTIFICATION_ADMIN_CHANNEL, alert);
    }
}
