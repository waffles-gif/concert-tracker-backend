package com.concerttracker.backend.exception;

/**
 * Se lanza al intentar eliminar un recurso que todavía tiene otros recursos
 * asociados (por ejemplo, un venue o un artista con conciertos vigentes).
 */
public class ResourceInUseException extends RuntimeException {

    public ResourceInUseException(String message) {
        super(message);
    }
}
