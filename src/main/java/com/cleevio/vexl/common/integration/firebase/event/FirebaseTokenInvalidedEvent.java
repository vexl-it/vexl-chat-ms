package com.cleevio.vexl.common.integration.firebase.event;

import javax.validation.constraints.NotBlank;

public record FirebaseTokenInvalidedEvent(

        @NotBlank
        String inboxPublicKey,

        @NotBlank
        String firebaseToken

) {
}
