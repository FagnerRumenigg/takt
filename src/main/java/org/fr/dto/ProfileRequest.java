package org.fr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileRequest(
        @NotBlank @Size(max = 120) String areaOfActuation,
        @NotBlank @Size(max = 120) String role,
        @NotBlank @Size(max = 120) String jobLevel
) {
}
