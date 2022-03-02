package com.cleevio.vexl.module.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MessageRequest {

    @NotBlank
    @NotNull
    @Schema(required = true, description = "User public key - for identification in GetStream as a sender.")
    private final String userPublicKey;

    @NotBlank
    @NotNull
    @Schema(required = true, description = "Receiver public key - to whom we send the message.")
    private final String receiverPublicKey;

    @NotBlank
    @NotNull
    @Schema(required = true, description = "Encrypted message with receiver's public key with ECIES algorithm.")
    private final String encryptedMessage;
}
