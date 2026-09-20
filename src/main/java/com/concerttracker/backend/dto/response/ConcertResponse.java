package com.concerttracker.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ConcertResponse {

    private Long id;
    private String title;
    private LocalDateTime date;
    private BigDecimal ticketPrice;
    private String imageUrl;
    private Long artistId;
    private String artistName;
    private Long venueId;
    private String venueName;
    private String city;
    private String country;

    public ConcertResponse() {
    }

    public ConcertResponse(Long id, String title, LocalDateTime date, BigDecimal ticketPrice, String imageUrl,
                           Long artistId, String artistName, Long venueId, String venueName,
                           String city, String country) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.ticketPrice = ticketPrice;
        this.imageUrl = imageUrl;
        this.artistId = artistId;
        this.artistName = artistName;
        this.venueId = venueId;
        this.venueName = venueName;
        this.city = city;
        this.country = country;
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

    public Long getArtistId() {
        return artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public Long getVenueId() {
        return venueId;
    }

    public String getVenueName() {
        return venueName;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }
}
