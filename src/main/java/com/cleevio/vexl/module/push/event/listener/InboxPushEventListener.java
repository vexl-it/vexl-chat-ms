package com.cleevio.vexl.module.push.event.listener;

import com.cleevio.vexl.module.inbox.event.PushNotificationEvent;
import com.cleevio.vexl.module.push.service.PushService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class InboxPushEventListener {

    private final PushService pushService;

    @EventListener
    public void onPushNotificationEvent(final PushNotificationEvent event) {
        this.pushService.sendPushNotification(event);
    }
}
