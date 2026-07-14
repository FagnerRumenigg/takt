package org.fr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fr.model.RefreshToken;
import org.fr.model.User;
import org.fr.repository.RefreshTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.HexFormat;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken store(User user, String refreshToken, OffsetDateTime expiresAt) {
        log.info("Iniciando store");
        return refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .tokenHash(hash(refreshToken))
                .expiresAt(expiresAt)
                .revoked(false)
                .build());
    }

    public RefreshToken verify(String refreshToken) {
        log.info("Iniciando verify");
        RefreshToken token = refreshTokenRepository.findByTokenHash(hash(refreshToken))
                .orElseThrow(() -> new IllegalArgumentException("Refresh token inválido"));
        if (token.isRevoked() || token.getExpiresAt().isBefore(OffsetDateTime.now())) {
            log.info("Refresh token inválido ou expirado");
            throw new IllegalArgumentException("Refresh token inválido");
        }
        log.info("Finalizando verify");
        return token;
    }

    public void revoke(String refreshToken) {
        log.info("Iniciando revoke");
        refreshTokenRepository.findByTokenHash(hash(refreshToken)).ifPresent(token -> {
            RefreshToken updatedToken = RefreshToken.of(token);
            updatedToken.setRevoked(true);
            refreshTokenRepository.save(updatedToken);
        });
        log.info("Finalizando revoke");
    }

    @Scheduled(cron = "0 0 * * * *")
    public void cleanExpiredTokens() {
        log.info("Iniciando cleanExpiredTokens");
        refreshTokenRepository.deleteByExpiresAtBefore(OffsetDateTime.now());
        log.info("Finalizando cleanExpiredTokens");
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (Exception ex) {
            throw new IllegalStateException("Erro ao processar refresh token", ex);
        }
    }
}
