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

    public CalendarResponse get(String username, LocalDate startDate, LocalDate endDate) {
        log.info("Iniciando get - {} - {} - {}", username, startDate, endDate);
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Datas do calendário são obrigatórias");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Data inicial deve ser menor ou igual à data final");
        }
        LocalDate lastDayOfMonth = startDate.withDayOfMonth(startDate.lengthOfMonth());
        if (!startDate.withDayOfMonth(1).equals(endDate.withDayOfMonth(1)) || endDate.isAfter(lastDayOfMonth)) {
            throw new IllegalArgumentException("O intervalo do calendário deve ficar dentro do mesmo mês");
        }
        return new CalendarResponse(
                startDate,
                startDate,
                endDate,
                timeEntryService.listRange(username, startDate, endDate),
                categoryService.list(username),
                productivityLevelService.list(username)
        );
    }
}
