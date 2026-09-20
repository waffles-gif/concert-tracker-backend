package com.concerttracker.backend.event;

/**
 * Se publica al crear un concierto. Persona 3 lo escucha (@EventListener + @Async) para avisar
 * a los usuarios que siguen al artista y viven en el mismo país y ciudad del venue.
 */
public record ConcertCreatedEvent(Long concertId, Long artistId, String venueCountry, String venueCity) {
}
