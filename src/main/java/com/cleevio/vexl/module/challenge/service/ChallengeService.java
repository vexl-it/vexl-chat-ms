package com.cleevio.vexl.module.challenge.service;

import com.cleevio.vexl.common.constant.ModuleLockNamespace;
import com.cleevio.vexl.common.cryptolib.CLibrary;
import com.cleevio.vexl.common.service.AdvisoryLockService;
import com.cleevio.vexl.module.challenge.config.ChallengeConfig;
import com.cleevio.vexl.module.challenge.constant.ChallengeAdvisoryLock;
import com.cleevio.vexl.module.challenge.dto.request.CreateChallengeRequest;
import com.cleevio.vexl.module.challenge.entity.Challenge;
import com.cleevio.vexl.module.challenge.exception.ChallengeCreateException;
import com.cleevio.vexl.module.challenge.exception.ChallengeMissingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.Base64;

@Service
@Slf4j
@Validated
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final AdvisoryLockService advisoryLockService;
    private final ChallengeConfig config;
    private static final String SHA256 = "SHA-256";

    @Transactional
    public String createChallenge(@Valid CreateChallengeRequest request) {
        advisoryLockService.lock(
                ModuleLockNamespace.CHALLENGE,
                ChallengeAdvisoryLock.CREATE_CHALLENGE.name(),
                request.publicKey()
        );

        log.info("Creating new challenge for public key [{}]", request.publicKey());
        try {
            final String challenge = generateRandomChallenge();
            invalidatedOldChallengeAndCreateNew(challenge, request.publicKey());
            return challenge;
        } catch (NoSuchAlgorithmException e) {
            log.error("Error while generating the challenge.", e);
            throw new ChallengeCreateException();
        } catch (Exception e) {
            log.error("Error while creating the challenge", e);
            throw new ChallengeCreateException();
        }
    }

    @Transactional
    public boolean isSignedChallengeValid(@NotBlank String publicKey, @NotBlank String signature) {
        Challenge challenge = this.challengeRepository.findByPublicKey(publicKey)
                .orElseThrow(ChallengeMissingException::new);

        if (ZonedDateTime.now().isAfter(challenge.getCreatedAt().plusMinutes(config.expiration()))) {
            log.info("Challenge [{}] is expired. Setting to invalid and returning exception.", challenge);
            invalidateChallenge(challenge);
            throw new ChallengeMissingException();
        }

        return CLibrary.CRYPTO_LIB.ecdsa_verify(
                challenge.getPublicKey(),
                challenge.getChallenge(),
                challenge.getChallenge().length(),
                signature
        );
    }

    private void invalidateChallenge(Challenge challenge) {
        challenge.setValid(false);
        this.challengeRepository.save(challenge);
    }

    private void invalidatedOldChallengeAndCreateNew(String challenge, String publicKey) {
        Challenge challengeEntity = this.challengeRepository.findByPublicKey(publicKey)
                .orElse(null);

        if (challengeEntity != null) {
            log.info("Previous challenge [{}] will be invalidated.", challengeEntity);
            challengeEntity.setValid(false);
            this.challengeRepository.save(challengeEntity);
        }

        Challenge newChallenge = createActiveChallengeEntity(challenge, publicKey);
        Challenge savedChallenge = this.challengeRepository.save(newChallenge);
        log.info("New challenge [{}] has been saved", savedChallenge);
    }

    private Challenge createActiveChallengeEntity(String challenge, String publicKey) {
        return Challenge.builder()
                .challenge(challenge)
                .publicKey(publicKey)
                .valid(true)
                .build();
    }

    private String generateRandomChallenge() throws NoSuchAlgorithmException {
        byte[] bytes = generateCodeVerifier().getBytes(StandardCharsets.US_ASCII);
        MessageDigest messageDigest = MessageDigest.getInstance(SHA256);
        messageDigest.update(bytes, 0, bytes.length);
        byte[] digest = messageDigest.digest();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }

    private String generateCodeVerifier() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] codeVerifier = new byte[32];
        secureRandom.nextBytes(codeVerifier);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier);
    }

    @Transactional
    public void removeInvalidAndExpiredChallenges() {
        advisoryLockService.lock(
                ModuleLockNamespace.CHALLENGE,
                ChallengeAdvisoryLock.REMOVE_CHALLENGE_TASK.name()
        );

        this.challengeRepository.removeInvalidAndExpiredChallenges(ZonedDateTime.now().minusMinutes(config.expiration()));
    }
}
