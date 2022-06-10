package com.cleevio.vexl.module.inbox.dto.response;

import com.cleevio.vexl.module.inbox.enums.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record MessagesResponse(

        List<MessageResponse> messages

) {

    public record MessageResponse(

            @Schema(description = "Encrypted message.")
            String message,

            @Schema(description = "Public key of sender. Reply to this public key.")
            String senderPublicKey,

            @Schema(description = "Type of message.")
            MessageType messageType

    ) {
    }
}
