package com.cleevio.vexl.module.inbox.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

public record UpdateInboxRequest(

        @NotBlank
        @Schema(required = true, description = "Public key of the inbox you want to update. The public key itself cannot be updated.")
        String publicKey,

        @NotBlank
        @Schema(required = true, description = "New value of Firebase token.")
        String token

) {
}
