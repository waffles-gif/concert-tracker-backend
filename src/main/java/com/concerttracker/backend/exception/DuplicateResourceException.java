package com.concerttracker.backend.exception;

/**
 * Se lanza al intentar crear un recurso que ya existe (género duplicado,
 * email ya registrado, etc etc).
 */

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}