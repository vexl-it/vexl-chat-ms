package com.cleevio.vexl.common.integration.firebase.service;

import com.cleevio.vexl.module.push.dto.PushMessageDto;
import com.cleevio.vexl.module.push.service.NotificationService;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FirebaseService implements NotificationService {

    private static final String TITLE = "title";
    private static final String BODY = "body";
    private static final String TYPE = "type";
    private static final String INBOX = "inbox";
    private static final String SENDER = "sender";

    public void sendPushNotification(final PushMessageDto dto) {
        try {
            Map<String, Object> dataIos = new HashMap<>();
            dataIos.put(TITLE, dto.title());
            dataIos.put(BODY, dto.text());
            ApnsConfig apnsConfig = ApnsConfig.builder()
                    .setAps(Aps.builder()
                            .putAllCustomData(dataIos)
                            .build())
                    .build();

            var messageBuilder = Message.builder();

            messageBuilder.setToken(dto.token());
            messageBuilder.putData(TITLE, dto.title());
            messageBuilder.putData(BODY, dto.text());
            messageBuilder.putData(TYPE, dto.messageType().name());
            messageBuilder.putData(INBOX, dto.receiverPublicKey());
            messageBuilder.putData(SENDER, dto.senderPublicKey());
            messageBuilder.setApnsConfig(apnsConfig);
            messageBuilder.setAndroidConfig(AndroidConfig.builder().build());

            final String response = FirebaseMessaging.getInstance().sendAsync(messageBuilder.build()).get();
            log.info("Sent message: " + response);
        } catch (Exception e) {
            log.error("Error sending notification", e);
        }

    }
}
