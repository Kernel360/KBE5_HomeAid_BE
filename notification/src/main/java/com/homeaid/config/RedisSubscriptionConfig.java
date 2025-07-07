package com.homeaid.config;

import com.homeaid.service.NotificationSubscriber;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisSubscriptionConfig {
    private final RedisMessageListenerContainer container;
    private final NotificationSubscriber notificationSubscriber;

    @PostConstruct
    public void subscribeToNotifications() {
        container.addMessageListener(
                notificationSubscriber,
                new PatternTopic("notification-channel")
        );

        container.addMessageListener(
                notificationSubscriber,
                new PatternTopic("notification-admin-channel")
        );
    }
}
