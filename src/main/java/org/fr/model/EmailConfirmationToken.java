package org.fr.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "email_confirmation_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", nullable = false, length = 128, unique = true)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Column(nullable = false)
    private boolean used;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void prePersist() {
        createdAt = OffsetDateTime.now();
    }

    public static EmailConfirmationToken of(EmailConfirmationToken source) {
        if (source == null) {
            return EmailConfirmationToken.builder().build();
        }
        return EmailConfirmationToken.builder()
                .id(source.getId())
                .user(source.getUser())
                .tokenHash(source.getTokenHash())
                .expiresAt(source.getExpiresAt())
                .used(source.isUsed())
                .createdAt(source.getCreatedAt())
                .build();
    }
}
