package org.fr.dto;

import lombok.Builder;
import org.fr.model.Profile;

import java.util.UUID;

@Builder
public record ProfileResponse(UUID id, String areaOfActuation, String role, String jobLevel) {
    public static ProfileResponse from(Profile profile) {
        return of(profile.getId(), profile.getAreaOfActuation(), profile.getRole(), profile.getJobLevel());
    }

    public static ProfileResponse of(UUID id, String areaOfActuation, String role, String jobLevel) {
        return new ProfileResponse(id, areaOfActuation, role, jobLevel);
    }
}
