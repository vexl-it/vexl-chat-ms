package com.cleevio.vexl.utils;

import com.cleevio.vexl.module.challenge.dto.request.CreateChallengeRequest;
import com.cleevio.vexl.module.inbox.dto.SignedChallenge;
import com.cleevio.vexl.module.inbox.dto.request.CreateInboxRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestCreatorTestUtil {

    public static CreateInboxRequest createInboxRequest(String publicKey) {
        return new CreateInboxRequest(
                publicKey,
                null,
                new SignedChallenge("dummy_challenge", "dummy_signature")
        );
    }

    public static CreateChallengeRequest createChallengeRequest(String publicKey) {
        return new CreateChallengeRequest(publicKey);
    }

}
