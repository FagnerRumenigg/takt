package org.fr.repository;

import org.fr.model.EmailConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface EmailConfirmationTokenRepository extends JpaRepository<EmailConfirmationToken, UUID> {
    Optional<EmailConfirmationToken> findByTokenHash(String tokenHash);
    void deleteByExpiresAtBefore(OffsetDateTime cutoff);
    void deleteByUser_Email(String email);
}
