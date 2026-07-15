package org.fr.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fr.dto.TimeEntryRequest;
import org.fr.dto.TimeEntryResponse;
import org.fr.service.TimeEntryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/takt/time-entries")
@RequiredArgsConstructor
@Tag(name = "Time Entries", description = "Blocos de tempo")
@SecurityRequirement(name = "bearerAuth")
public class TimeEntryController {

    private final TimeEntryService timeEntryService;

    @GetMapping
    @Operation(summary = "Listar todos os blocos de tempo")
    public ResponseEntity<List<TimeEntryResponse>> list(Authentication authentication) {
        return ResponseEntity.ok(timeEntryService.list(authentication.getName()));
    }

    @GetMapping("/day")
    @Operation(summary = "Listar blocos de um dia")
    public ResponseEntity<List<TimeEntryResponse>> listDay(Authentication authentication, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day) {
        return ResponseEntity.ok(timeEntryService.listDay(authentication.getName(), day));
    }

    @PostMapping
    @Operation(
            summary = "Criar bloco de tempo",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = TimeEntryRequest.class),
                            examples = @ExampleObject(name = "TimeEntryCreate", value = """
                                    {
                                      "categoryId": "uuid-da-categoria",
                                      "title": "Estudar Spring",
                                      "startDate": "2026-07-15T08:00:00-03:00",
                                      "endDate": "2026-07-15T10:00:00-03:00",
                                      "productivityLevelId": "uuid-do-nivel",
                                      "note": "Revisar controllers"
                                    }
                                    """)
                    )
            )
    )
    public ResponseEntity<TimeEntryResponse> create(Authentication authentication, @Valid @RequestBody TimeEntryRequest request) {
        return ResponseEntity.ok(timeEntryService.create(authentication.getName(), request));
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Atualizar bloco de tempo",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = TimeEntryRequest.class),
                            examples = @ExampleObject(name = "TimeEntryUpdate", value = """
                                    {
                                      "categoryId": "uuid-da-categoria",
                                      "title": "Estudar Spring Boot",
                                      "startDate": "2026-07-15T09:00:00-03:00",
                                      "endDate": "2026-07-15T11:00:00-03:00",
                                      "productivityLevelId": "uuid-do-nivel",
                                      "note": "Atualizado"
                                    }
                                    """)
                    )
            )
    )
    public ResponseEntity<TimeEntryResponse> update(Authentication authentication, @PathVariable UUID id, @Valid @RequestBody TimeEntryRequest request) {
        return ResponseEntity.ok(timeEntryService.update(authentication.getName(), id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir bloco de tempo")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable UUID id) {
        timeEntryService.delete(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
