package com.cleevio.vexl.module.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class MessageResponse {

    @Schema(description = "Message id")
    private final String id;

    @Schema(description = "Message encrypted text")
    private final String text;

    @Schema(description = "When message was created and sent")
    private final Date createdAt;

    @Schema(description = "Sender public key")
    private final String senderPublicKey;
}
