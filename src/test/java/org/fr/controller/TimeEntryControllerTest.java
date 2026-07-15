package org.fr.controller;

import org.fr.dto.TimeEntryRequest;
import org.fr.dto.TimeEntryResponse;
import org.fr.service.TimeEntryService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TimeEntryControllerTest {

    @Test
    void listShouldDelegateToService() {
        TimeEntryService service = mock(TimeEntryService.class);
        TimeEntryController controller = new TimeEntryController(service);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("fagner");
        when(service.list("fagner")).thenReturn(List.of());

        ResponseEntity<List<TimeEntryResponse>> response = controller.list(authentication);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }

    @Test
    void createShouldDelegateToService() {
        TimeEntryService service = mock(TimeEntryService.class);
        TimeEntryController controller = new TimeEntryController(service);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("fagner");
        when(service.create(eq("fagner"), any())).thenReturn(TimeEntryResponse.builder().id(UUID.randomUUID()).title("Título").build());

        ResponseEntity<TimeEntryResponse> response = controller.create(authentication, new TimeEntryRequest(UUID.randomUUID(), "Título", LocalDateTime.now(), LocalDateTime.now().plusHours(1), null, "Nota"));

        assertThat(response.getBody().title()).isEqualTo("Título");
    }
}
