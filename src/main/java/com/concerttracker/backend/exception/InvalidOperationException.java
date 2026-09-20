package com.concerttracker.backend.exception;

/**
 * Se lanza cuando la operación no es válida por el estado actual de los datos
 * (por ejemplo, borrar un venue que todavía tiene conciertos).
 */
public class InvalidOperationException extends RuntimeException {

    public InvalidOperationException(String message) {
        super(message);
    }
}
