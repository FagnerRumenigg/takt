package org.fr.exception;

public class ForbiddenCategoryOperationException extends DomainException {
    public static final String DEFAULT_MESSAGE = "Não é permitido alterar categoria global";

    public ForbiddenCategoryOperationException() {
        super(DEFAULT_MESSAGE);
    }

    public ForbiddenCategoryOperationException(String message) {
        super(DEFAULT_MESSAGE, message);
    }
}
