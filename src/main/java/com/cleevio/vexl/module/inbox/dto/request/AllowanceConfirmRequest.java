package com.cleevio.vexl.module.inbox.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record AllowanceConfirmRequest(

        @Schema(required = true, description = "Public key of inbox which confirms the sender.")
        String publicKey,

        @Schema(required = true, description = "Public key of inbox you want to confirm.")
        String publicKeyToConfirm,

        @Schema(required = true, description = "Verification that you are the actual owner of the public key under which you confirm receipt of messages.")
        String signature

) {
}
