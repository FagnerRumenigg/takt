package org.fr.exception;

public class TimeEntryNotFoundException extends DomainException {
    public static final String DEFAULT_MESSAGE = "Bloco de tempo não encontrado";

    public TimeEntryNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public TimeEntryNotFoundException(String message) {
        super(DEFAULT_MESSAGE, message);
    }
}
