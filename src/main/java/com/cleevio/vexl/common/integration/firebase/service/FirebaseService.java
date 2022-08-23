package com.cleevio.vexl.common.integration.firebase.service;

import com.cleevio.vexl.module.push.dto.PushMessageDto;
import com.cleevio.vexl.module.push.service.NotificationService;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
            var messageBuilder = Message.builder();

            messageBuilder.setNotification(Notification.builder().setTitle(dto.title()).setBody(dto.text()).build());
            messageBuilder.setToken(dto.token());
            messageBuilder.putData(TITLE, dto.title());
            messageBuilder.putData(BODY, dto.text());
            messageBuilder.putData(TYPE, dto.messageType().name());
            messageBuilder.putData(INBOX, dto.receiverPublicKey());
            messageBuilder.putData(SENDER, dto.senderPublicKey());
            messageBuilder.setAndroidConfig(AndroidConfig.builder().build());

            final String response = FirebaseMessaging.getInstance().sendAsync(messageBuilder.build()).get();
            log.info("Sent message: " + response);
        } catch (Exception e) {
            log.error("Error sending notification", e);
        }

    }
}
