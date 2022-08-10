package com.cleevio.vexl.module.inbox.event;

import com.cleevio.vexl.module.inbox.enums.MessageType;

import javax.validation.constraints.NotBlank;

public record PushNotificationEvent(

        @NotBlank
        String token,

        @NotBlank
        MessageType messageType,

        @NotBlank
        String receiverPublicKey,

        @NotBlank
        String senderPublicKey

) {
}
