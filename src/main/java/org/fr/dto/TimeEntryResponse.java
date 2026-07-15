package org.fr.dto;

import lombok.Builder;
import org.fr.model.TimeEntry;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record TimeEntryResponse(
        UUID id,
        UUID categoryId,
        String title,
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        UUID productivityLevelId,
        String note,
        OffsetDateTime createdAt
) {
    public static TimeEntryResponse from(TimeEntry timeEntry) {
        return new TimeEntryResponse(
                timeEntry.getId(),
                timeEntry.getCategory().getId(),
                timeEntry.getTitle(),
                timeEntry.getStartDate(),
                timeEntry.getEndDate(),
                timeEntry.getProductivityLevel() == null ? null : timeEntry.getProductivityLevel().getId(),
                timeEntry.getNote(),
                timeEntry.getCreatedAt()
        );
    }
}
