package com.cleevio.vexl.integration.firebase.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FirebaseService implements NotificationService {

    public void sendPushNotification(String pushToken, String title, String text) {
        try {
            Message message = Message.builder()
                    .setToken(pushToken)
                    .setNotification(Notification.builder().setTitle(title).setBody(text).build())
                    .build();

            String response = FirebaseMessaging.getInstance().sendAsync(message).get();
            log.info("Sent message: " + response);
        } catch (Exception e) {
            log.error("Error sending notification", e);
        }

    }
}
