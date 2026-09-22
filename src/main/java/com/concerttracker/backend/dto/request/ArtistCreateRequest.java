package com.concerttracker.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class ArtistCreateRequest {

    @NotBlank(message = "El nombre del artista es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String name;

    @Size(max = 100, message = "El país no puede superar los 100 caracteres")
    private String country;

    @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
    private String description;

    @Size(max = 500, message = "La URL de la imagen no puede superar los 500 caracteres")
    private String imageUrl;

    private Set<Long> genreIds;

    public ArtistCreateRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Set<Long> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(Set<Long> genreIds) {
        this.genreIds = genreIds;
    }
}
