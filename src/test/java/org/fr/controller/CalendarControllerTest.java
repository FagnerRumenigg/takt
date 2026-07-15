package org.fr.controller;

import org.fr.dto.CalendarResponse;
import org.fr.service.CalendarService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CalendarControllerTest {

    @Test
    void getShouldDelegateToService() {
        CalendarService calendarService = mock(CalendarService.class);
        CalendarController controller = new CalendarController(calendarService);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("fagner");
        when(calendarService.get("fagner", LocalDate.of(2026, 7, 8))).thenReturn(new CalendarResponse(java.util.List.of(), java.util.List.of(), java.util.List.of()));

        ResponseEntity<CalendarResponse> response = controller.get(authentication, LocalDate.of(2026, 7, 8));

        assertThat(response.getBody().categories()).isEmpty();
    }
}
