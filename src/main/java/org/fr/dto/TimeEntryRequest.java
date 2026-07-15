package org.fr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TimeEntryRequest(
        @Schema(example = "UUID da categoria")
        @NotNull UUID categoryId,
        @Schema(example = "Estudar Spring")
        @NotBlank @Size(max = 160) String title,
        @Schema(example = "2026-07-15T08:00:00-03:00")
        @NotNull OffsetDateTime startDate,
        @Schema(example = "2026-07-15T10:00:00-03:00")
        @NotNull OffsetDateTime endDate,
        @Schema(example = "UUID do nível de produtividade")
        UUID productivityLevelId,
        @Schema(example = "Anotações opcionais")
        @Size(max = 500) String note
) {
}
