package org.fr.exception;

public class DomainException extends RuntimeException {
    public DomainException(String defaultMessage) {
        super(defaultMessage);
    }

    public DomainException(String defaultMessage, String message) {
        super(message == null || message.isBlank() ? defaultMessage : message);
    }
}
