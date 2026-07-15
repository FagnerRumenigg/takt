package org.fr.service;

import org.fr.dto.CategoryResponse;
import org.fr.dto.ProductivityLevelResponse;
import org.fr.dto.TimeEntryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

    @Mock private TimeEntryService timeEntryService;
    @Mock private CategoryService categoryService;
    @Mock private ProductivityLevelService productivityLevelService;

    @InjectMocks private CalendarService calendarService;

    @Test
    void getShouldAggregateData() {
        when(timeEntryService.listRange("fagner", LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31))).thenReturn(List.of());
        when(categoryService.list("fagner")).thenReturn(List.of());
        when(productivityLevelService.list("fagner")).thenReturn(List.of());

        var response = calendarService.get("fagner", LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31));

        assertThat(response.timeEntries()).isEmpty();
        assertThat(response.date()).isEqualTo(LocalDate.of(2026, 7, 1));
        assertThat(response.startDate()).isEqualTo(LocalDate.of(2026, 7, 1));
        assertThat(response.endDate()).isEqualTo(LocalDate.of(2026, 7, 31));
    }
}
