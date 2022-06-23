package com.cleevio.vexl.module.inbox.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

public record DeletionRequest (

    @NotBlank
    @Schema(required = true, description = "Public key of an Inbox.")
    String publicKey

) {
}
