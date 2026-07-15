package org.fr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @Schema(example = "Estudos")
        @NotBlank
        @Size(max = 50)
        String name,
        @Schema(example = "#FF8800")
        @Size(max = 50)
        String color
) {
}
