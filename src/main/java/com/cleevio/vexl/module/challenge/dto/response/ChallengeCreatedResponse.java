package com.cleevio.vexl.module.challenge.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChallengeCreatedResponse(

        @Schema(description = "Challenge what client has to sign with a private key.")
        String challenge

) {
}
