package com.concerttracker.backend.exception;

/**
 * Se lanza cuando el estado de asistencia que se intenta registrar no es válido
 * para el momento del concierto (por ejemplo, marcar "YA_FUI" antes de que ocurra).
 */
public class AttendanceNotAllowedException extends RuntimeException {

    public AttendanceNotAllowedException(String message) {
        super(message);
    }
}
