package org.fr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fr.dto.CalendarResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CalendarService {

    private final TimeEntryService timeEntryService;
    private final CategoryService categoryService;
    private final ProductivityLevelService productivityLevelService;

    public CalendarResponse get(String username, LocalDate date) {
        log.info("Iniciando get - {} - {}", username, date);
        return new CalendarResponse(
                timeEntryService.listDay(username, date),
                categoryService.list(username),
                productivityLevelService.list(username)
        );
    }
}
