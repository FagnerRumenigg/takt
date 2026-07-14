package org.fr.exception;

public class EmailAlreadyUsedException extends DomainException {
    public static final String DEFAULT_MESSAGE = "Email já existe";

    public EmailAlreadyUsedException() {
        super(DEFAULT_MESSAGE);
    }

    public EmailAlreadyUsedException(String message) {
        super(DEFAULT_MESSAGE, message);
    }
}
