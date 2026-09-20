package com.concerttracker.backend.dto.response;

public class ArtistResponse {

    private Long id;
    private String name;
    private String country;
    private String description;
    private String imageUrl;

    public ArtistResponse() {
    }

    public ArtistResponse(Long id, String name, String country, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
