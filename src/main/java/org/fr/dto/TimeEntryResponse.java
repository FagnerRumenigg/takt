package org.fr.dto;

import lombok.Builder;
import org.fr.model.TimeEntry;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record TimeEntryResponse(
        UUID id,
        UUID categoryId,
        String title,
        LocalDateTime startDate,
        LocalDateTime endDate,
        UUID productivityLevelId,
        String note,
        LocalDateTime createdAt
) {
    public static TimeEntryResponse from(TimeEntry timeEntry) {
        return new TimeEntryResponse(
                timeEntry.getId(),
                timeEntry.getCategory().getId(),
                timeEntry.getTitle(),
                timeEntry.getStartDate().toLocalDateTime(),
                timeEntry.getEndDate().toLocalDateTime(),
                timeEntry.getProductivityLevel() == null ? null : timeEntry.getProductivityLevel().getId(),
                timeEntry.getNote(),
                timeEntry.getCreatedAt() == null ? null : timeEntry.getCreatedAt().toLocalDateTime()
        );
    }
}
