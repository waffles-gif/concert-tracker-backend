package com.concerttracker.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ConcertDetailResponse {

    private Long id;
    private String title;
    private LocalDateTime date;
    private BigDecimal ticketPrice;
    private String imageUrl;
    private ArtistResponse artist;
    private VenueResponse venue;

    public ConcertDetailResponse() {
    }

    public ConcertDetailResponse(Long id, String title, LocalDateTime date, BigDecimal ticketPrice,
                                 String imageUrl, ArtistResponse artist, VenueResponse venue) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.ticketPrice = ticketPrice;
        this.imageUrl = imageUrl;
        this.artist = artist;
        this.venue = venue;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ArtistResponse getArtist() {
        return artist;
    }

    public VenueResponse getVenue() {
        return venue;
    }
}
