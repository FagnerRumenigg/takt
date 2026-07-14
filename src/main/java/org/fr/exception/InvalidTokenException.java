package org.fr.exception;

public class InvalidTokenException extends DomainException {
    public InvalidTokenException(String defaultMessage) {
        super(defaultMessage);
    }

    public InvalidTokenException(String defaultMessage, String message) {
        super(defaultMessage, message);
    }
}
