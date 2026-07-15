package org.fr.dto;

import java.time.LocalDate;
import java.util.List;

public record CalendarResponse(
        LocalDate date,
        LocalDate startDate,
        LocalDate endDate,
        List<TimeEntryResponse> timeEntries,
        List<CategoryResponse> categories,
        List<ProductivityLevelResponse> productivityLevels
) {
}
