package org.fr.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Operation(
            summary = "Listar todos os blocos de tempo",
            responses = @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lista de blocos",
                    content = @Content(
                            schema = @Schema(implementation = TimeEntryResponse.class),
                            examples = @ExampleObject(name = "TimeEntryList", value = """
                                    [
                                      {
                                        "id": "55555555-5555-5555-5555-555555555555",
                                        "categoryId": "22222222-2222-2222-2222-222222222222",
                                        "title": "Estudar Spring",
                                        "startDate": "2026-07-15T08:00:00",
                                        "endDate": "2026-07-15T10:00:00",
                                        "productivityLevelId": "33333333-3333-3333-3333-333333333333",
                                        "note": "Revisar controllers"
                                      }
                                    ]
                                    """)
                    )
            )
    )
    public ResponseEntity<List<TimeEntryResponse>> list(Authentication authentication) {
        List<TimeEntryResponse> timeEntries = timeEntryService.list(authentication.getName());
        if (timeEntries.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(timeEntries);
    }

    @GetMapping("/day")
    @Operation(
            summary = "Listar blocos de um dia",
            parameters = @io.swagger.v3.oas.annotations.Parameter(name = "day", description = "Data no formato yyyy-MM-dd", example = "2026-07-15")
    )
    public ResponseEntity<List<TimeEntryResponse>> listDay(
            Authentication authentication,
            @RequestParam("day")
            @io.swagger.v3.oas.annotations.Parameter(name = "day", example = "2026-07-15")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate day
    ) {
        List<TimeEntryResponse> timeEntries = timeEntryService.listDay(authentication.getName(), day);
        if (timeEntries.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(timeEntries);
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
                                      "startDate": "2026-07-15T08:00:00",
                                      "endDate": "2026-07-15T10:00:00",
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
                                      "startDate": "2026-07-15T09:00:00",
                                      "endDate": "2026-07-15T11:00:00",
                                      "productivityLevelId": "uuid-do-nivel",
                                      "note": "Atualizado"
                                    }
                                    """)
                    )
            )
    )
    public ResponseEntity<TimeEntryResponse> update(Authentication authentication, @PathVariable("id") UUID id, @Valid @RequestBody TimeEntryRequest request) {
        return ResponseEntity.ok(timeEntryService.update(authentication.getName(), id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir bloco de tempo")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable("id") UUID id) {
        timeEntryService.delete(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
