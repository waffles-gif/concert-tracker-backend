package com.concerttracker.backend.event;

import com.concerttracker.backend.entity.Follow;
import com.concerttracker.backend.entity.User;
import com.concerttracker.backend.repository.FollowRepository;
import com.concerttracker.backend.repository.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmailNotificationListener {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public EmailNotificationListener(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

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

    /**
     * Al crearse un concierto, avisa a los usuarios que siguen al artista y viven en el mismo
     * país y ciudad del venue. No bloquea la creación del concierto porque corre en otro hilo (@Async).
     */
    @Async
    @EventListener
    public void handleConcertCreatedEvent(ConcertCreatedEvent event) {
        List<Follow> followers = followRepository.findByArtistId(event.artistId());
        for (Follow follow : followers) {
            userRepository.findById(follow.getUserId()).ifPresent(user -> {
                if (matchesLocation(user, event)) {
                    System.out.println("[ASYNC EVENT] Notificando a " + user.getEmail()
                            + " sobre el nuevo concierto " + event.concertId()
                            + " del artista " + event.artistId()
                            + " en " + event.venueCity() + ", " + event.venueCountry());
                }
            });
        }
    }

    private boolean matchesLocation(User user, ConcertCreatedEvent event) {
        return event.venueCountry() != null && event.venueCountry().equalsIgnoreCase(user.getCountry())
                && event.venueCity() != null && event.venueCity().equalsIgnoreCase(user.getCity());
    }
}
