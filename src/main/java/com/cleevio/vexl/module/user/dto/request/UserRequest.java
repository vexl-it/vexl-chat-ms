package com.cleevio.vexl.module.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;

@Data
public class UserRequest {

    @NonNull
    @NotBlank
    @Schema(required = true, description = "User public key in Base64 format")
    private final String userPublicKey;

    @NonNull
    @NotBlank
    @Schema(required = true)
    private final String username;
}
