package com.cleevio.vexl.module.inbox.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record AllowanceRequest(

        @Schema(required = true, description = "Public key of inbox you want to allowance from.")
        String publicKey,

        @Schema(required = true, description = "BE can't tell what message is what. Please encrypt the message " +
                "type here so that when you decrypt it, you can tell that it is an allowance request.")
        String message

) {
}
