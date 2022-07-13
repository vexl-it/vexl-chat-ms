package com.cleevio.vexl.integration.firebase.service;

import com.cleevio.vexl.module.inbox.event.PushNotificationEvent;
import com.cleevio.vexl.module.push.service.NotificationService;
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

    public void sendPushNotification(final PushNotificationEvent event, final String title, final String text) {
        try {
            var messageBuilder = Message.builder();

            messageBuilder.setNotification(Notification.builder().setTitle(title).setBody(text).build());
            messageBuilder.setToken(event.token());
            messageBuilder.putData(TITLE, title);
            messageBuilder.putData(BODY, text);
            messageBuilder.putData(TYPE, event.messageType().name());
            messageBuilder.putData(INBOX, event.publicKey());

            final String response = FirebaseMessaging.getInstance().sendAsync(messageBuilder.build()).get();
            log.info("Sent message: " + response);
        } catch (Exception e) {
            log.error("Error sending notification", e);
        }

    }
}
