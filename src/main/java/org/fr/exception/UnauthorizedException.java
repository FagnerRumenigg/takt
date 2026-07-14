package org.fr.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("Token ausente ou inválido");
    }
}
