package org.fr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(example = "fagner")
        @NotBlank String username,
        @Schema(example = "Senha@123")
        @NotBlank String password
) {
}
