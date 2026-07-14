package org.fr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @Schema(example = "token-longo-gerado-no-link")
        @NotBlank String token,
        @Schema(example = "SenhaNova@123")
        @NotBlank
        @Size(min = 6, max = 100)
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{6,}$",
                message = "Senha deve ter no mínimo 6 caracteres, 1 maiúscula, 1 número e 1 caractere especial"
        )
        String newPassword
) {
}
