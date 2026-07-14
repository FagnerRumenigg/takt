package org.fr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Schema(example = "fagner")
        @NotBlank @Size(min = 3, max = 80) String username,
        @Schema(example = "fagner@gmail.com")
        @NotBlank @Size(min = 5, max = 160) String email,
        @Schema(example = "Senha@123")
        @NotBlank
        @Size(min = 6, max = 100)
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{6,}$",
                message = "Senha deve ter no mínimo 6 caracteres, 1 maiúscula, 1 número e 1 caractere especial"
        )
        String password
) {
    public static RegisterRequest of(String username, String email, String password) {
        return new RegisterRequest(username, email, password);
    }
}
