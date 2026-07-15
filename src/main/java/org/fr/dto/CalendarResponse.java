package org.fr.dto;

import java.util.List;

public record CalendarResponse(
        List<TimeEntryResponse> timeEntries,
        List<CategoryResponse> categories,
        List<ProductivityLevelResponse> productivityLevels
) {
}
