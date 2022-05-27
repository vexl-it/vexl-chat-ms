package com.cleevio.vexl.module.inbox.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record SendMessageRequest(

        @Schema(required = true, description = "Public key of an user or an offer to whom the message is to be sent")
        String receiverPublicKey,

        @Schema(required = true, description = "Message to be sent")
        String message
) {
}
