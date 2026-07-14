package org.fr.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UserUpdateRequest(
    @Schema(example = "Fagner Ramos")
    @Size(max = 160) String fullName,
        @Schema(example = "10/08/1995")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate birthDate
) {
}
