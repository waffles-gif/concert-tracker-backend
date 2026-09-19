package com.concerttracker.backend.exception;

/**
 * Se lanza cuando se busca un recurso por id y no existe.
 */

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}