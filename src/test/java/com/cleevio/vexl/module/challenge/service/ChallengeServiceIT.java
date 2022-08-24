package com.cleevio.vexl.module.challenge.service;

import com.cleevio.vexl.common.IntegrationTest;
import com.cleevio.vexl.common.cryptolib.CLibrary;
import com.cleevio.vexl.module.challenge.exception.ChallengeExpiredException;
import com.cleevio.vexl.module.inbox.dto.SignedChallenge;
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

    private final ChallengeService challengeService;

    @Autowired
    public ChallengeServiceIT(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    private static final String PUBLIC_KEY_USER_A = CryptographyTestKeysUtil.PUBLIC_KEY_USER_A;
    private static final String PRIVATE_KEY_USER_A = CryptographyTestKeysUtil.PRIVATE_KEY_USER_A;
    private static final String PRIVATE_KEY_USER_B = CryptographyTestKeysUtil.PUBLIC_KEY_USER_B;

    @Test
    void testSigningChallenge_shouldSignAndBeSuccessfullyVerified() {
        final String challenge = this.challengeService.createChallenge(RequestCreatorTestUtil.createChallengeRequest(PUBLIC_KEY_USER_A));
        final String signature = CLibrary.CRYPTO_LIB.ecdsa_sign(PUBLIC_KEY_USER_A, PRIVATE_KEY_USER_A, challenge, challenge.length());
        final boolean signedChallengeValid = this.challengeService.isSignedChallengeValid(PUBLIC_KEY_USER_A, new SignedChallenge(challenge, signature));

        assertThat(signedChallengeValid).isTrue();
    }

    @Test
    void testSigningOldChallenge_shouldNotBeValid() {
        final String oldChallenge = this.challengeService.createChallenge(RequestCreatorTestUtil.createChallengeRequest(PUBLIC_KEY_USER_A));
        final String newChallenge = this.challengeService.createChallenge(RequestCreatorTestUtil.createChallengeRequest(PUBLIC_KEY_USER_A));
        final String signature = CLibrary.CRYPTO_LIB.ecdsa_sign(PUBLIC_KEY_USER_A, PRIVATE_KEY_USER_A, oldChallenge, oldChallenge.length());
        final boolean signedChallengeValid = this.challengeService.isSignedChallengeValid(PUBLIC_KEY_USER_A, new SignedChallenge(newChallenge, signature));

        assertThat(signedChallengeValid).isFalse();
    }

    @Test
    void testVerifyAlreadyUsedChallenge_shouldReturnsChallengeExpiredException() {
        final String oldChallenge = this.challengeService.createChallenge(RequestCreatorTestUtil.createChallengeRequest(PUBLIC_KEY_USER_A));
        this.challengeService.createChallenge(RequestCreatorTestUtil.createChallengeRequest(PUBLIC_KEY_USER_A));
        final String signature = CLibrary.CRYPTO_LIB.ecdsa_sign(PUBLIC_KEY_USER_A, PRIVATE_KEY_USER_A, oldChallenge, oldChallenge.length());

        //First use
        this.challengeService.isSignedChallengeValid(PUBLIC_KEY_USER_A, new SignedChallenge(oldChallenge, signature));

        //Second use
        assertThrows(
                ChallengeExpiredException.class,
                () -> this.challengeService.isSignedChallengeValid(PUBLIC_KEY_USER_A, new SignedChallenge(oldChallenge, signature))
        );
    }

    @Test
    void testSigningNewChallenge_shouldBeValid() {
        final String oldChallenge = this.challengeService.createChallenge(RequestCreatorTestUtil.createChallengeRequest(PUBLIC_KEY_USER_A));
        final String newChallenge1 = this.challengeService.createChallenge(RequestCreatorTestUtil.createChallengeRequest(PUBLIC_KEY_USER_A));
        final String newChallenge2 = this.challengeService.createChallenge(RequestCreatorTestUtil.createChallengeRequest(PUBLIC_KEY_USER_A));
        final String signature = CLibrary.CRYPTO_LIB.ecdsa_sign(PUBLIC_KEY_USER_A, PRIVATE_KEY_USER_A, newChallenge2, newChallenge2.length());
        final boolean signedChallengeValid = this.challengeService.isSignedChallengeValid(PUBLIC_KEY_USER_A, new SignedChallenge(newChallenge2, signature));

        assertThat(signedChallengeValid).isTrue();
    }

    @Test
    void testSigningChallengeForDifferentPublicKey_shouldReturnException() {
        final String challenge = this.challengeService.createChallenge(RequestCreatorTestUtil.createChallengeRequest(PRIVATE_KEY_USER_B));
        final String signature = CLibrary.CRYPTO_LIB.ecdsa_sign(PUBLIC_KEY_USER_A, PRIVATE_KEY_USER_A, challenge, challenge.length());

        assertThrows(
                ChallengeExpiredException.class,
                () -> this.challengeService.isSignedChallengeValid(PUBLIC_KEY_USER_A, new SignedChallenge(challenge, signature))
        );
    }
}
