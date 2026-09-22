package com.concerttracker.backend.dto.response;

import java.util.List;

public class ArtistResponse {

    private Long id;
    private String name;
    private String country;
    private String description;
    private String imageUrl;
    private List<GenreResponse> genres;

    public ArtistResponse() {
    }

    public ArtistResponse(Long id, String name, String country, String description,
                           String imageUrl, List<GenreResponse> genres) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.description = description;
        this.imageUrl = imageUrl;
        this.genres = genres;
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

    public List<GenreResponse> getGenres() {
        return genres;
    }
}
