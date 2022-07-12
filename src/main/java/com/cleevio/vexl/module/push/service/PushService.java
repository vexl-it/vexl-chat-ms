package com.cleevio.vexl.module.push.service;

import com.cleevio.vexl.module.inbox.enums.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PushService {

    private final NotificationService notificationService;

    public void sendPushNotification(String token, MessageType messageType) {
        switch (messageType) {
            case MESSAGE -> notificationService.sendPushNotification(token, "New message", "You have received a new message");
            case APPROVE_MESSAGING -> notificationService.sendPushNotification(token, "Approval", "You have been approved.");
            case REQUEST_MESSAGING -> notificationService.sendPushNotification(token, "New request", "You have received a new request");
            case DISAPPROVE_MESSAGING -> notificationService.sendPushNotification(token, "Approval rejected", "You have been rejected");
            case DELETE_CHAT -> notificationService.sendPushNotification(token, "Chat deletion", "One of your chat has been deleted.");
        }
    }
}
