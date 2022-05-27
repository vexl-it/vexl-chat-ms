package com.cleevio.vexl.module.inbox.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

public record CreateInboxRequest(

        @NotBlank
        @Schema(required = true, description = "Identifier of the inbox. Must be unique.")
        String publicKey,

        @NotBlank
        @Schema(required = true, description = "Firebase token for notification about new messages.")
        String token

) {
}
