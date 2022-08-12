package com.cleevio.vexl.module.push.dto;

import com.cleevio.vexl.module.inbox.constant.MessageType;

public record PushMessageDto (

        String title,
        String text,
        String token,
		MessageType messageType,
        String receiverPublicKey,
        String senderPublicKey

) {}
