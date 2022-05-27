package com.cleevio.vexl.module.challenge.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "challenge")
public record ChallengeConfig(

        int expiration //minutes

) {
}
