package com.concerttracker.backend.exception;

/**
 * Se lanza cuando un usuario intenta dejar una reseña sin cumplir la regla de negocio:
 * su asistencia a ese concierto debe estar marcada como "YA_FUI".
 */
public class ReviewNotAllowedException extends RuntimeException {

    public ReviewNotAllowedException(String message) {
        super(message);
    }
}
