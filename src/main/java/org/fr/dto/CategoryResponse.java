package org.fr.dto;

import lombok.Builder;
import org.fr.model.Category;

import java.util.UUID;

@Builder
public record CategoryResponse(UUID id, String name, String color, UUID userId, java.time.OffsetDateTime createdAt) {
    public static CategoryResponse from(Category category) {
        return of(
                category.getId(),
                category.getName(),
                category.getColor(),
                category.getUser() == null ? null : category.getUser().getId(),
                category.getCreatedAt()
        );
    }

    public static CategoryResponse of(UUID id, String name, String color, UUID userId, java.time.OffsetDateTime createdAt) {
        return new CategoryResponse(id, name, color, userId, createdAt);
    }
}
