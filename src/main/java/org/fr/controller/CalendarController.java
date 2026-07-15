package org.fr.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.fr.dto.CalendarResponse;
import org.fr.service.CalendarService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/takt/calendar")
@RequiredArgsConstructor
@Tag(name = "Calendar", description = "Dados para o dashboard diário")
@SecurityRequirement(name = "bearerAuth")
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping
    @Operation(
            summary = "Retorna o dashboard diário",
            parameters = {
                    @Parameter(name = "startDate", description = "Início do intervalo no formato yyyy-MM-dd", example = "2026-07-01"),
                    @Parameter(name = "endDate", description = "Fim do intervalo no formato yyyy-MM-dd", example = "2026-07-31")
            },
            responses = @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Dashboard diário",
                    content = @Content(
                            schema = @Schema(implementation = CalendarResponse.class),
                            examples = @ExampleObject(name = "Calendar", value = """
                                    {
                                      "date": "2026-07-01",
                                      "startDate": "2026-07-01",
                                      "endDate": "2026-07-31",
                                      "timeEntries": [],
                                      "categories": [],
                                      "productivityLevels": []
                                    }
                                    """)
                    )
            )
    )
    public ResponseEntity<CalendarResponse> get(
            Authentication authentication,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        return ResponseEntity.ok(calendarService.get(authentication.getName(), startDate, endDate));
    }
}
