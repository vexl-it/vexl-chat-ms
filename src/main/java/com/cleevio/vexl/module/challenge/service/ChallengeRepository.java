package com.cleevio.vexl.module.challenge.service;

import com.cleevio.vexl.module.challenge.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    Optional<Challenge> findByPublicKey(String publicKey);
}