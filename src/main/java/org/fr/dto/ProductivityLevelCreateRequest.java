package org.fr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductivityLevelCreateRequest(
        @Schema(example = "Foco profundo")
        @NotBlank @Size(max = 80) String name,
        @Schema(example = "5", nullable = true)
        Integer displayOrder
) {
}
