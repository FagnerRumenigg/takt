package org.fr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductivityLevelRequest(
        @Schema(example = "1")
        @NotNull Integer displayOrder,
        @Schema(example = "Alta")
        @NotBlank @Size(max = 80) String name
) {
}
