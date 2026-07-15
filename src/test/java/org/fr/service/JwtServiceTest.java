package org.fr.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService(
            "dev-secret-change-me-dev-secret-change-me",
            60000L,
            120000L
    );

    @Test
    void shouldGenerateAndParseAccessToken() {
        String token = jwtService.generateAccessToken("fagner");

        assertThat(jwtService.extractSubject(token)).isEqualTo("fagner");
        assertThat(jwtService.isValidAccessToken(token, "fagner")).isTrue();
    }

    @Test
    void shouldExposeAccessTokenExpiration() {
        assertThat(jwtService.getAccessTokenExpirationMs()).isEqualTo(60000L);
    }
}
