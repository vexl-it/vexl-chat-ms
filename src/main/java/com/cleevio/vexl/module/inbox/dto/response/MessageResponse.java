package com.cleevio.vexl.module.inbox.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MessageResponse (

    @Schema(description = "Encrypted message.")
    String message,

    @Schema(description = "Public key of sender. Reply to this public key.")
    String senderPublicKey

) {
}
