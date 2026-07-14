package org.fr.exception;

public class UsernameAlreadyUsedException extends DomainException {
    public static final String DEFAULT_MESSAGE = "Username já existe";

    public UsernameAlreadyUsedException() {
        super(DEFAULT_MESSAGE);
    }

    public UsernameAlreadyUsedException(String message) {
        super(DEFAULT_MESSAGE, message);
    }
}
