package org.fr.repository;

import org.fr.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
    void deleteByExpiresAtBefore(OffsetDateTime cutoff);
    void deleteByUser_Email(String email);
}
