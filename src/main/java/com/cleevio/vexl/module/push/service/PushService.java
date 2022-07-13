package com.cleevio.vexl.module.push.service;

import com.cleevio.vexl.module.inbox.event.PushNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PushService {

    private final NotificationService notificationService;

    public void sendPushNotification(PushNotificationEvent event) {
        switch (event.messageType()) {
            case MESSAGE ->
                    notificationService.sendPushNotification(event, "New message", "You have received a new message");
            case APPROVE_MESSAGING ->
                    notificationService.sendPushNotification(event, "Approval", "You have been approved.");
            case REQUEST_MESSAGING ->
                    notificationService.sendPushNotification(event, "New request", "You have received a new request");
            case DISAPPROVE_MESSAGING ->
                    notificationService.sendPushNotification(event, "Approval rejected", "You have been rejected");
            case DELETE_CHAT ->
                    notificationService.sendPushNotification(event, "Chat deletion", "One of your chat has been deleted.");
            case REQUEST_REVEAL ->
                    notificationService.sendPushNotification(event, "Request reveal", "You have been requested for reveal");
            case APPROVE_REVEAL ->
                    notificationService.sendPushNotification(event, "Approve reveal", "Reveal was approved.");
            case DISAPPROVE_REVEAL ->
                    notificationService.sendPushNotification(event, "Disapproval reveal", "Request for reveal was disapprove.");
        }
    }
}
