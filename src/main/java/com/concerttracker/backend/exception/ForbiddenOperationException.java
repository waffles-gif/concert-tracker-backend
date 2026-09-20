package com.concerttracker.backend.exception;

/**
 * Se lanza cuando el usuario está autenticado pero la regla de negocio no le permite la operación
 * (por ejemplo, dejar una reseña sin haber marcado "ya fui" en ese concierto).
 */
public class ForbiddenOperationException extends RuntimeException {

    public ForbiddenOperationException(String message) {
        super(message);
    }
}
