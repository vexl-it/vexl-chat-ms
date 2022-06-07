package com.cleevio.vexl.integration.firebase.service;

public interface NotificationService {

    void sendPushNotification(String pushToken, String title, String text);
}
