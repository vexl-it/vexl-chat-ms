package com.cleevio.vexl.module.inbox.dto.request;

import com.cleevio.vexl.common.annotation.NullOrNotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;

public record CreateInboxRequest(

        @NotBlank
        @Schema(required = true, description = "Identifier of the inbox. Must be unique.")
        String publicKey,

        @Nullable
        @NullOrNotBlank
        @Schema(description = "Firebase token for notification about new messages.")
        String token

) {
}
