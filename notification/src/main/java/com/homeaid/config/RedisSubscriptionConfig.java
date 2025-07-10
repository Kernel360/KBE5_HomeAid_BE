package com.homeaid.config;

import com.homeaid.service.NotificationSubscriber;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import static com.homeaid.service.NotificationPublisher.NOTIFICATION_ADMIN_CHANNEL;
import static com.homeaid.service.NotificationPublisher.NOTIFICATION_CHANNEL;

@Component
@RequiredArgsConstructor
public class RedisSubscriptionConfig {
    private final RedisMessageListenerContainer container;
    private final NotificationSubscriber notificationSubscriber;

    @PostConstruct
    public void subscribeToNotifications() {
        container.addMessageListener(
                notificationSubscriber,
                new PatternTopic(NOTIFICATION_CHANNEL)
        );

        container.addMessageListener(
                notificationSubscriber,
                new PatternTopic(NOTIFICATION_ADMIN_CHANNEL)
        );
        container.start();
    }
}
