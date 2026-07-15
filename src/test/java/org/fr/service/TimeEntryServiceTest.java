package org.fr.service;

import org.fr.dto.TimeEntryRequest;
import org.fr.model.Category;
import org.fr.model.ProductivityLevel;
import org.fr.model.TimeEntry;
import org.fr.model.User;
import org.fr.repository.CategoryRepository;
import org.fr.repository.ProductivityLevelRepository;
import org.fr.repository.TimeEntryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeEntryServiceTest {

    @Mock private TimeEntryRepository timeEntryRepository;
    @Mock private UserService userService;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ProductivityLevelRepository productivityLevelRepository;

    @InjectMocks private TimeEntryService timeEntryService;

    @Test
    void createShouldPersistEntry() {
        User user = User.builder().username("fagner").build();
        Category category = Category.builder().id(UUID.randomUUID()).user(user).build();
        when(userService.loadDomainUserByUsername("fagner")).thenReturn(user);
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(timeEntryRepository.existsByUser_UsernameAndStartDateLessThanAndEndDateGreaterThan(any(), any(), any())).thenReturn(false);
        when(timeEntryRepository.save(any(TimeEntry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = timeEntryService.create("fagner", new TimeEntryRequest(category.getId(), "Título", OffsetDateTime.now(), OffsetDateTime.now().plusHours(1), null, "Nota"));

        assertThat(response.title()).isEqualTo("Título");
    }

    @Test
    void listDayShouldReturnEntries() {
        when(timeEntryRepository.findByUser_UsernameAndStartDateBetweenOrderByStartDateAsc(any(), any(), any()))
                .thenReturn(List.of());

        var result = timeEntryService.listDay("fagner", LocalDate.now());

        assertThat(result).isEmpty();
    }

    @Test
    void createShouldFailWhenDatesAreInvalid() {
        assertThatThrownBy(() -> timeEntryService.create("fagner",
                new TimeEntryRequest(UUID.randomUUID(), "Título", OffsetDateTime.now().plusHours(1), OffsetDateTime.now(), null, null)))
                .isInstanceOf(org.fr.exception.TimeEntryValidationException.class);
    }

    @Test
    void createShouldFailWhenNoteIsTooLong() {
        String note = "x".repeat(501);
        assertThatThrownBy(() -> timeEntryService.create("fagner",
                new TimeEntryRequest(UUID.randomUUID(), "Título", OffsetDateTime.now(), OffsetDateTime.now().plusHours(1), null, note)))
                .isInstanceOf(org.fr.exception.TimeEntryValidationException.class);
    }

    @Test
    void updateShouldPersistChanges() {
        User user = User.builder().username("fagner").build();
        Category category = Category.builder().id(UUID.randomUUID()).user(user).build();
        TimeEntry timeEntry = TimeEntry.builder().id(UUID.randomUUID()).user(user).category(category).startDate(OffsetDateTime.now()).endDate(OffsetDateTime.now().plusHours(1)).build();
        when(timeEntryRepository.findById(timeEntry.getId())).thenReturn(Optional.of(timeEntry));
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(timeEntryRepository.existsByUser_UsernameAndStartDateLessThanAndEndDateGreaterThan(any(), any(), any())).thenReturn(false);
        when(timeEntryRepository.save(any(TimeEntry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = timeEntryService.update("fagner", timeEntry.getId(),
                new TimeEntryRequest(category.getId(), "Novo", OffsetDateTime.now(), OffsetDateTime.now().plusHours(1), null, "Nota"));

        assertThat(response.title()).isEqualTo("Novo");
    }

    @Test
    void deleteShouldRemoveEntry() {
        User user = User.builder().username("fagner").build();
        TimeEntry timeEntry = TimeEntry.builder().id(UUID.randomUUID()).user(user).category(Category.builder().id(UUID.randomUUID()).user(user).build()).startDate(OffsetDateTime.now()).endDate(OffsetDateTime.now().plusHours(1)).build();
        when(timeEntryRepository.findById(timeEntry.getId())).thenReturn(Optional.of(timeEntry));

        timeEntryService.delete("fagner", timeEntry.getId());

        verify(timeEntryRepository).delete(timeEntry);
    }
}
