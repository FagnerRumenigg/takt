package org.fr.repository;

import org.fr.model.TimeEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, UUID> {
    List<TimeEntry> findByUser_UsernameOrderByStartDateAsc(String username);
    List<TimeEntry> findByUser_UsernameAndStartDateBetweenOrderByStartDateAsc(String username, OffsetDateTime start, OffsetDateTime end);
    List<TimeEntry> findByUser_UsernameAndStartDateGreaterThanEqualAndStartDateLessThanOrderByStartDateAsc(String username, OffsetDateTime start, OffsetDateTime end);
    boolean existsByUser_UsernameAndStartDateLessThanAndEndDateGreaterThan(String username, OffsetDateTime end, OffsetDateTime start);
}
