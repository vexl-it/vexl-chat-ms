package com.cleevio.vexl.module.push.service;


public interface NotificationService {

    void sendPushNotification(String pushToken, String title, String text);
}
