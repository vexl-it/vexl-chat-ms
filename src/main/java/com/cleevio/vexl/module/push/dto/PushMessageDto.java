package com.cleevio.vexl.module.push.dto;

import com.cleevio.vexl.module.inbox.constant.MessageType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record PushMessageDto (

        @NotBlank
        String title,

        @NotBlank
        String text,

        @NotBlank
        String token,

        @NotNull
		MessageType messageType,

        @NotBlank
        String receiverPublicKey,

        @NotBlank
        String senderPublicKey

) {}
