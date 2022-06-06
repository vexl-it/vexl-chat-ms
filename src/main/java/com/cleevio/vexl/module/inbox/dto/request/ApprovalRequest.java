package com.cleevio.vexl.module.inbox.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

public record ApprovalRequest(

        @NotBlank
        @Schema(required = true, description = "Public key of inbox you want to permission from.")
        String publicKey,

        @NotBlank
        @Schema(required = true, description = "BE can't tell what message is what. Please encrypt the message " +
                "type here so that when you decrypt it, you can tell that it is a permission request.")
        String message

) {
}
