package org.fr.dto;

import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.fr.model.User;

import java.util.UUID;

@Builder
public record UserResponse(
        UUID id,
        String username,
        String email,
        String fullName,
        @JsonFormat(pattern = "dd/MM/yyyy") java.time.LocalDate birthDate,
        ProfileResponse profile
) {
    public static UserResponse from(User user) {
        return of(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getBirthDate(),
                user.getProfile() == null ? null : ProfileResponse.from(user.getProfile())
        );
    }

    public static UserResponse of(UUID id, String username, String email, String fullName, java.time.LocalDate birthDate, ProfileResponse profile) {
        return new UserResponse(id, username, email, fullName, birthDate, profile);
    }
}
