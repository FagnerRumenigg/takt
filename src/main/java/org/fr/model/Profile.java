package org.fr.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "area_of_actuation", nullable = false, length = 120)
    private String areaOfActuation;

    @Column(nullable = false, length = 120)
    private String role;

    @Column(name = "job_level", nullable = false, length = 120)
    private String jobLevel;

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

    public static Profile of(Profile source) {
        if (source == null) {
            return Profile.builder().build();
        }
        return Profile.builder()
                .id(source.getId())
                .areaOfActuation(source.getAreaOfActuation())
                .role(source.getRole())
                .jobLevel(source.getJobLevel())
                .build();
    }
}
