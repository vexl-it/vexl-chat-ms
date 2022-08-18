package com.cleevio.vexl.module.inbox.event;

import com.cleevio.vexl.module.inbox.constant.MessageType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record NewMessageReceivedEvent(

        @NotBlank
        String token,

        @NotNull
        MessageType messageType,

        @NotBlank
        String receiverPublicKey,

        @NotBlank
        String senderPublicKey

) {
}
