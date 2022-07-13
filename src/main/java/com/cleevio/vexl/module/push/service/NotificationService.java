package com.cleevio.vexl.module.push.service;

import com.cleevio.vexl.module.inbox.event.PushNotificationEvent;

public interface NotificationService {

    void sendPushNotification(PushNotificationEvent event, String title, String text);
}
