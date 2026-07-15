package org.fr.exception;

public class CategoryNotFoundException extends DomainException {
    public static final String DEFAULT_MESSAGE = "Categoria não encontrada";

    public CategoryNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public CategoryNotFoundException(String message) {
        super(DEFAULT_MESSAGE, message);
    }
}
