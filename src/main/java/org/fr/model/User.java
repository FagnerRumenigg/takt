package org.fr.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 80)
    private String username;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(name = "full_name", length = 160)
    private String fullName;

    @Column(name = "birth_date")
    private java.time.LocalDate birthDate;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() {
        var now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public static User of(User source) {
        return of(source, source == null ? null : source.getPassword());
    }

    public static User of(User source, String password) {
        if (source == null) {
            return User.builder().build();
        }
        return User.builder()
                .id(source.getId())
                .username(source.getUsername())
                .email(source.getEmail())
                .fullName(source.getFullName())
                .birthDate(source.getBirthDate())
                .password(password != null ? password : source.getPassword())
                .emailVerified(source.isEmailVerified())
                .profile(source.getProfile())
                .createdAt(source.getCreatedAt())
                .updatedAt(source.getUpdatedAt())
                .build();
    }
}
