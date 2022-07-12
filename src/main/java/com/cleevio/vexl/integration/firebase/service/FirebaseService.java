package com.cleevio.vexl.integration.firebase.service;

import com.cleevio.vexl.module.inbox.enums.PlatformType;
import com.cleevio.vexl.module.push.model.PushMessage;
import com.cleevio.vexl.module.push.service.NotificationService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class FirebaseService implements NotificationService {

    public void sendPushNotification(String pushToken, String title, String text) {
        try {
            var messageBuilder = configureMessage(PlatformType.ANDROID);

            messageBuilder.setNotification(Notification.builder().setTitle(title).setBody(text).build());
            messageBuilder.setToken(pushToken);

            String response = FirebaseMessaging.getInstance().sendAsync(messageBuilder.build()).get();
            log.info("Sent message: " + response);
        } catch (Exception e) {
            log.error("Error sending notification", e);
        }

    }

    private Message.Builder configureMessage(PlatformType platform) {

        try {
            var messageBuilder = Message.builder();
            PushMessage pushMessage = new PushMessage();

            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(pushMessage);

            Map<String, Object> dataIos = mapper.readValue(json, Map.class);

            dataIos.put("type", pushMessage.getTitle());

            switch (platform) {
                case IOS -> {
                    ApnsConfig apnsConfig = ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .putAllCustomData(dataIos)
                                    .build())
                            .build();
                    messageBuilder.setApnsConfig(apnsConfig);
                }
                case ANDROID -> {
                    AndroidConfig androidConfig = AndroidConfig.builder()
                            .build();
                    messageBuilder
                            .putData("json", json)
                            .putData("type", pushMessage.getTitle())
                            .setAndroidConfig(androidConfig);
                }
            }

            return messageBuilder;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
