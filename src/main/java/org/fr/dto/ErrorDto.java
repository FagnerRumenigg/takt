package org.fr.dto;

import java.time.Instant;

public record ErrorDto(String message, String details, int status, Instant timestamp, String path) {
    public ErrorDto(String message, String details, int status, String path) {
        this(message, details, status, Instant.now(), path);
    }
}
