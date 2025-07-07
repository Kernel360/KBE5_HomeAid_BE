package com.homeaid.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeaid.dto.RequestAlert;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.NotificationErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import static com.homeaid.service.NotificationPublisher.NOTIFICATION_ADMIN_CHANNEL;
import static com.homeaid.service.NotificationPublisher.NOTIFICATION_CHANNEL;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSubscriber implements MessageListener {
    private final SseNotificationService sseNotificationService;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = new String(pattern);
            String messageBody = new String(message.getBody());
            RequestAlert requestAlert = objectMapper.readValue(messageBody, RequestAlert.class);

            if (NOTIFICATION_CHANNEL.equals(channel)) {
                sseNotificationService.createAlertByRequestAlert(requestAlert);
            } else if (NOTIFICATION_ADMIN_CHANNEL.equals(channel)) {
                sseNotificationService.createAdminAlertByRequestAlert(requestAlert);
            }
        } catch (JsonProcessingException e) {
            log.error("알림 처리 실패", e);
            throw new CustomException(NotificationErrorCode.MESSAGE_CONVERT_JSON_FAIL);
        }

    }
}
