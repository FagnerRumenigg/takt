package org.fr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fr.dto.TimeEntryRequest;
import org.fr.dto.TimeEntryResponse;
import org.fr.exception.CategoryNotFoundException;
import org.fr.exception.ForbiddenCategoryOperationException;
import org.fr.exception.TimeEntryNotFoundException;
import org.fr.exception.TimeEntryValidationException;
import org.fr.model.Category;
import org.fr.model.ProductivityLevel;
import org.fr.model.TimeEntry;
import org.fr.model.User;
import org.fr.repository.CategoryRepository;
import org.fr.repository.ProductivityLevelRepository;
import org.fr.repository.TimeEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TimeEntryService {

    private static final ZoneId DEFAULT_ZONE = ZoneId.of("America/Sao_Paulo");

    private final TimeEntryRepository timeEntryRepository;
    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final ProductivityLevelRepository productivityLevelRepository;

    public List<TimeEntryResponse> list(String username) {
        log.info("Iniciando list - {}", username);
        return timeEntryRepository.findByUser_UsernameOrderByStartDateAsc(username).stream().map(TimeEntryResponse::from).toList();
    }

    public List<TimeEntryResponse> listDay(String username, LocalDate day) {
        log.info("Iniciando listDay - {} - {}", username, day);
        return listRange(username, day, day);
    }

    public List<TimeEntryResponse> listRange(String username, LocalDate startDate, LocalDate endDate) {
        log.info("Iniciando listRange - {} - {} - {}", username, startDate, endDate);
        OffsetDateTime start = startDate.atStartOfDay(DEFAULT_ZONE).toOffsetDateTime();
        OffsetDateTime end = endDate.plusDays(1).atStartOfDay(DEFAULT_ZONE).toOffsetDateTime();
        return timeEntryRepository.findByUser_UsernameAndStartDateGreaterThanEqualAndStartDateLessThanOrderByStartDateAsc(username, start, end)
                .stream()
                .map(TimeEntryResponse::from)
                .toList();
    }

    public TimeEntryResponse create(String username, TimeEntryRequest request) {
        log.info("Iniciando create - {}", username);
        validateRequest(request, null, username);
        User user = userService.loadDomainUserByUsername(username);
        Category category = categoryRepository.findById(request.categoryId()).orElseThrow(CategoryNotFoundException::new);
        validateCategoryOwner(username, category);
        ProductivityLevel level = resolveProductivityLevel(username, request.productivityLevelId());
        TimeEntry timeEntry = timeEntryRepository.save(TimeEntry.builder()
                .user(user)
                .category(category)
                .title(request.title())
                .startDate(request.startDate().atZone(DEFAULT_ZONE).toOffsetDateTime())
                .endDate(request.endDate().atZone(DEFAULT_ZONE).toOffsetDateTime())
                .productivityLevel(level)
                .note(request.note())
                .build());
        return TimeEntryResponse.from(timeEntry);
    }

    public TimeEntryResponse update(String username, UUID id, TimeEntryRequest request) {
        log.info("Iniciando update - {} - {}", username, id);
        TimeEntry timeEntry = timeEntryRepository.findById(id).orElseThrow(TimeEntryNotFoundException::new);
        validateOwner(username, timeEntry);
        validateRequest(request, id, username);
        Category category = categoryRepository.findById(request.categoryId()).orElseThrow(CategoryNotFoundException::new);
        validateCategoryOwner(username, category);
        ProductivityLevel level = resolveProductivityLevel(username, request.productivityLevelId());
        timeEntry.setCategory(category);
        timeEntry.setTitle(request.title());
        timeEntry.setStartDate(request.startDate().atZone(DEFAULT_ZONE).toOffsetDateTime());
        timeEntry.setEndDate(request.endDate().atZone(DEFAULT_ZONE).toOffsetDateTime());
        timeEntry.setProductivityLevel(level);
        timeEntry.setNote(request.note());
        return TimeEntryResponse.from(timeEntryRepository.save(timeEntry));
    }

    public void delete(String username, UUID id) {
        log.info("Iniciando delete - {} - {}", username, id);
        TimeEntry timeEntry = timeEntryRepository.findById(id).orElseThrow(TimeEntryNotFoundException::new);
        validateOwner(username, timeEntry);
        timeEntryRepository.delete(timeEntry);
    }

    private void validateRequest(TimeEntryRequest request, UUID currentId, String username) {
        if (request.startDate() == null || request.endDate() == null || !request.startDate().isBefore(request.endDate())) {
            throw new TimeEntryValidationException("A data de início deve ser menor que a data de fim");
        }
        if (request.note() != null && request.note().length() > 500) {
            throw new TimeEntryValidationException("Nota deve ter no máximo 500 caracteres");
        }
        OffsetDateTime start = request.startDate().atZone(DEFAULT_ZONE).toOffsetDateTime();
        OffsetDateTime end = request.endDate().atZone(DEFAULT_ZONE).toOffsetDateTime();
        boolean conflict = timeEntryRepository.existsByUser_UsernameAndStartDateLessThanAndEndDateGreaterThan(username, end, start);
        if (conflict && currentId != null) {
            List<TimeEntry> entries = timeEntryRepository.findByUser_UsernameOrderByStartDateAsc(username);
            conflict = entries.stream().anyMatch(entry ->
                    !entry.getId().equals(currentId) &&
                    entry.getStartDate().isBefore(end) &&
                    entry.getEndDate().isAfter(start));
        }
        if (conflict && currentId == null) {
            throw new TimeEntryValidationException("Já existe um bloco de tempo nesse intervalo");
        }
    }

    private void validateOwner(String username, TimeEntry timeEntry) {
        if (!timeEntry.getUser().getUsername().equals(username)) {
            throw new ForbiddenCategoryOperationException("Bloco de tempo pertence a outro usuário");
        }
    }

    private void validateCategoryOwner(String username, Category category) {
        if (category.getUser() != null && !category.getUser().getUsername().equals(username)) {
            throw new ForbiddenCategoryOperationException("Categoria pertence a outro usuário");
        }
    }

    private ProductivityLevel resolveProductivityLevel(String username, UUID productivityLevelId) {
        if (productivityLevelId == null) {
            return null;
        }
        ProductivityLevel level = productivityLevelRepository.findById(productivityLevelId)
                .orElseThrow(() -> new TimeEntryValidationException("Nível de produtividade não encontrado"));
        if (!level.getUser().getUsername().equals(username)) {
            throw new ForbiddenCategoryOperationException("Nível de produtividade pertence a outro usuário");
        }
        return level;
    }
}
