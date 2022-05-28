package com.cleevio.vexl.module.challenge.service;

import com.cleevio.vexl.module.challenge.config.ChallengeConfig;
import com.cleevio.vexl.module.challenge.dto.request.CreateChallengeRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ChallengeServiceTest {

    private static final CreateChallengeRequest CREATE_CHALLENGE_REQUEST;
    private static final String PUBLIC_KEY;

    private final ChallengeRepository challengeRepository = mock(ChallengeRepository.class);
    private final ChallengeConfig config = new ChallengeConfig(30);

    private final ChallengeService challengeService = new ChallengeService(
            challengeRepository,
            config
    );

    static {
        PUBLIC_KEY = "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUU0d0VBWUhLb1pJemowQ0FRWUZLNEVFQUNFRE9nQUVqT2xDSnhwVHFFZ1k2T0FER2lTdXdUbjBJZWFIZHZEawo0NkZYeDM5Yk5memY0Ry9zcFZXb1NibTIvODVhbmNodDE1c2hzSmdONnVBPQotLS0tLUVORCBQVUJMSUMgS0VZLS0tLS0K";
        CREATE_CHALLENGE_REQUEST = new CreateChallengeRequest(PUBLIC_KEY);
    }

    @Test
    void createChallenge_shouldBeCreated() {
        String challenge = challengeService.createChallenge(CREATE_CHALLENGE_REQUEST);
        assertThat(challenge).isNotBlank();
    }
}
