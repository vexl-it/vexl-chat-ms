package com.cleevio.vexl.module.inbox.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record BlockInboxRequest(

        @NotBlank
        @Schema(required = true, description = "Public key of inbox which is blocking other inbox.")
        String publicKey,

        @NotBlank
        @Schema(required = true, description = "Public key which will be blocked/unblocked.")
        String publicKeyToBlock,

        @NotBlank
        @Schema(required = true, description = "Verification that you are the actual owner of the public key under which you block/unblock public key.")
        String signature,

        @NotNull
        @Schema(required = true, description = "Whether you block (true) or unblock (false) the public key.")
        Boolean block

) {
}
