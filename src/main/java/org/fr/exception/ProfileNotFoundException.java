package org.fr.exception;

public class ProfileNotFoundException extends DomainException {
    public static final String DEFAULT_MESSAGE = "Profile não encontrado";

    public ProfileNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public ProfileNotFoundException(String message) {
        super(DEFAULT_MESSAGE, message);
    }
}
