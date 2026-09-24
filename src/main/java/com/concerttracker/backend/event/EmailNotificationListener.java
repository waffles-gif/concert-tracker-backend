package com.concerttracker.backend.event;

import com.concerttracker.backend.entity.Follow;
import com.concerttracker.backend.entity.User;
import com.concerttracker.backend.repository.FollowRepository;
import com.concerttracker.backend.repository.UserRepository;
import com.concerttracker.backend.service.EmailService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import java.util.List;

@Component
public class EmailNotificationListener {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public EmailNotificationListener(FollowRepository followRepository, UserRepository userRepository,
                                     EmailService emailService) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Async
    @EventListener
    public void handleAttendanceEvent(AttendanceCreatedEvent event) {
        userRepository.findById(event.userId()).ifPresent(user -> {
            Context context = new Context();
            context.setVariable("status", event.status());
            context.setVariable("concertId", event.concertId());
            emailService.sendHtmlEmail(user.getEmail(), "Confirmación de asistencia",
                    "attendance-confirmation", context);
        });
    }

    @Async
    @EventListener
    public void handleReviewEvent(ReviewCreatedEvent event) {
        userRepository.findById(event.userId()).ifPresent(user -> {
            Context context = new Context();
            context.setVariable("concertId", event.concertId());
            emailService.sendHtmlEmail(user.getEmail(), "Reseña registrada", "review-confirmation", context);
        });
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
                    Context context = new Context();
                    context.setVariable("concertId", event.concertId());
                    context.setVariable("city", event.venueCity());
                    context.setVariable("country", event.venueCountry());
                    emailService.sendHtmlEmail(user.getEmail(), "Nuevo concierto de un artista que sigues",
                            "concert-notification", context);
                }
            });
        }
    }

    private boolean matchesLocation(User user, ConcertCreatedEvent event) {
        return event.venueCountry() != null && event.venueCountry().equalsIgnoreCase(user.getCountry())
                && event.venueCity() != null && event.venueCity().equalsIgnoreCase(user.getCity());
    }
}
