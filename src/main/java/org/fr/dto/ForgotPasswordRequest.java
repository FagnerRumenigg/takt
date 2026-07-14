package org.fr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(@Schema(example = "fagner@gmail.com") @NotBlank String email) {
}
