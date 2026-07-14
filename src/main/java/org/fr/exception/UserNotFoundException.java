package org.fr.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserNotFoundException extends UsernameNotFoundException {
    public static final String DEFAULT_MESSAGE = "Usuário não encontrado";

    public UserNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public UserNotFoundException(String message) {
        super(message == null || message.isBlank() ? DEFAULT_MESSAGE : message);
    }
}
