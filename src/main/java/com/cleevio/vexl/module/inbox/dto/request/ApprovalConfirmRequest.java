package com.cleevio.vexl.module.inbox.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record ApprovalConfirmRequest(

        @NotBlank
        @Schema(required = true, description = "Public key of inbox which confirms the sender.")
        String publicKey,

        @NotBlank
        @Schema(required = true, description = "Public key of inbox you want to confirm.")
        String publicKeyToConfirm,

        @NotBlank
        @Schema(required = true, description = "Verification that you are the actual owner of the public key under which you confirm receipt of messages.")
        String signature,

        @NotBlank
        @Schema(required = true, description = "Confirmation message.")
        String message,

        @NotNull
        @Schema(required = true, description = "If you want to approve user, send 'true'. If you want to disapprove user, send 'false'")
        boolean approve

) {
}
