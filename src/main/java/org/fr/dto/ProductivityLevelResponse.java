package org.fr.dto;

import lombok.Builder;
import org.fr.model.ProductivityLevel;

import java.util.UUID;

@Builder
public record ProductivityLevelResponse(UUID id, Integer displayOrder, String name) {
    public static ProductivityLevelResponse from(ProductivityLevel productivityLevel) {
        return of(productivityLevel.getId(), productivityLevel.getDisplayOrder(), productivityLevel.getName());
    }

    public static ProductivityLevelResponse of(UUID id, Integer displayOrder, String name) {
        return new ProductivityLevelResponse(id, displayOrder, name);
    }
}
