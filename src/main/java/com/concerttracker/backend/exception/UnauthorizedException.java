package com.concerttracker.backend.exception;

/**
 * Se lanza en login con credenciales inválidas, o token inválido/expirado.
 */

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}