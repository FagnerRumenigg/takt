package org.fr.exception;

public class TimeEntryValidationException extends DomainException {
    public TimeEntryValidationException(String message) {
        super("Bloco de tempo inválido", message);
    }
}
