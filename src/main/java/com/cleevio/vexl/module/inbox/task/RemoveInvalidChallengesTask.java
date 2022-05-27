package com.cleevio.vexl.module.inbox.task;

import com.cleevio.vexl.module.challenge.service.ChallengeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@AllArgsConstructor
public class RemoveInvalidChallengesTask {

    private final ChallengeService challengeService;

    @Scheduled(fixedDelay = 300_000)
    @Transactional(rollbackFor = Throwable.class)
    public void removeInvalidChallenges() {
        log.info("Removing invalid or expired challenges");
        this.challengeService.removeInvalidAndExpiredChallenges();
    }
}
