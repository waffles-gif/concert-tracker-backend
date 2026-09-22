package com.concerttracker.backend.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationListener {

    @Async
    @EventListener
    public void handleAttendanceEvent(AttendanceCreatedEvent event) {
        // Lógica de envío de correo
        System.out.println("[ASYNC EVENT] Enviando email de confirmación de asistencia al usuario "
                + event.userId() + " para el concierto " + event.concertId());
    }

    @Async
    @EventListener
    public void handleReviewEvent(ReviewCreatedEvent event) {
        // Lógica de notificación/email cuando se crea una reseña
        System.out.println("[ASYNC EVENT] Enviando email de confirmación de reseña registrada para el concierto "
                + event.concertId());
    }
}