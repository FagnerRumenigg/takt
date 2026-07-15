package org.fr.service;

import org.fr.model.RefreshToken;
import org.fr.model.User;
import org.fr.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks private RefreshTokenService refreshTokenService;

    @Test
    void storeShouldPersistToken() {
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken token = refreshTokenService.store(User.builder().username("fagner").build(), "token", OffsetDateTime.now().plusHours(1));

        assertThat(token.isRevoked()).isFalse();
    }

    @Test
    void verifyShouldThrowWhenMissingToken() {
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.verify("token"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void revokeShouldSaveRevokedToken() {
        RefreshToken token = RefreshToken.builder().tokenHash("hash").build();
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(token));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        refreshTokenService.revoke("token");

        verify(refreshTokenRepository).save(argThat(RefreshToken::isRevoked));
    }
}
