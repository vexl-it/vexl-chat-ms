package com.cleevio.vexl.module.challenge.service;

import com.cleevio.vexl.common.IntegrationTest;
import com.cleevio.vexl.common.cryptolib.CLibrary;
import com.cleevio.vexl.module.challenge.exception.ChallengeMissingException;
import com.cleevio.vexl.utils.CryptographyTestKeysUtil;
import com.cleevio.vexl.utils.RequestCreatorTestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@IntegrationTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChallengeServiceIT {

    @Autowired
    private ChallengeService challengeService;

    private static final String PUBLIC_KEY_USER_A = CryptographyTestKeysUtil.PUBLIC_KEY_USER_A;
    private static final String PRIVATE_KEY_USER_A = CryptographyTestKeysUtil.PRIVATE_KEY_USER_A;
    private static final String PRIVATE_KEY_USER_B = CryptographyTestKeysUtil.PUBLIC_KEY_USER_B;

    @Test
    void testSigningChallenge_shouldSignAndBeSuccessfullyVerified() {
        String challenge = this.challengeService.createChallenge(RequestCreatorTestUtil.createChallengeRequest(PUBLIC_KEY_USER_A));
        String signature = CLibrary.CRYPTO_LIB.ecdsa_sign(PUBLIC_KEY_USER_A, PRIVATE_KEY_USER_A, challenge, challenge.length());
        boolean signedChallengeValid = this.challengeService.isSignedChallengeValid(PUBLIC_KEY_USER_A, signature);

        assertThat(signedChallengeValid).isTrue();
    }

    @Test
    void testSigningOldChallenge_shouldNotBeValid() {
        String oldChallenge = this.challengeService.createChallenge(RequestCreatorTestUtil.createChallengeRequest(PUBLIC_KEY_USER_A));
        String newChallenge = this.challengeService.createChallenge(RequestCreatorTestUtil.createChallengeRequest(PUBLIC_KEY_USER_A));
        String signature = CLibrary.CRYPTO_LIB.ecdsa_sign(PUBLIC_KEY_USER_A, PRIVATE_KEY_USER_A, oldChallenge, oldChallenge.length());
        boolean signedChallengeValid = this.challengeService.isSignedChallengeValid(PUBLIC_KEY_USER_A, signature);

        assertThat(signedChallengeValid).isFalse();
    }

    @Test
    void testSigningNewChallenge_shouldBeValid() {
        String oldChallenge = this.challengeService.createChallenge(RequestCreatorTestUtil.createChallengeRequest(PUBLIC_KEY_USER_A));
        String newChallenge1 = this.challengeService.createChallenge(RequestCreatorTestUtil.createChallengeRequest(PUBLIC_KEY_USER_A));
        String newChallenge2 = this.challengeService.createChallenge(RequestCreatorTestUtil.createChallengeRequest(PUBLIC_KEY_USER_A));
        String signature = CLibrary.CRYPTO_LIB.ecdsa_sign(PUBLIC_KEY_USER_A, PRIVATE_KEY_USER_A, newChallenge2, newChallenge2.length());
        boolean signedChallengeValid = this.challengeService.isSignedChallengeValid(PUBLIC_KEY_USER_A, signature);

        assertThat(signedChallengeValid).isTrue();
    }

    @Test
    void testSigningChallengeForDifferentPublicKey_shouldReturnException() {
        String challenge = this.challengeService.createChallenge(RequestCreatorTestUtil.createChallengeRequest(PRIVATE_KEY_USER_B));
        String signature = CLibrary.CRYPTO_LIB.ecdsa_sign(PUBLIC_KEY_USER_A, PRIVATE_KEY_USER_A, challenge, challenge.length());

        assertThrows(
                ChallengeMissingException.class,
                () -> this.challengeService.isSignedChallengeValid(PUBLIC_KEY_USER_A, signature)
        );
    }
}
