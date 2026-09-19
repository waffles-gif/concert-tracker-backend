package com.concerttracker.backend.dto.response;

public class GenreResponse {

    private Long id;
    private String name;

    public GenreResponse() {
    }

    public GenreResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}