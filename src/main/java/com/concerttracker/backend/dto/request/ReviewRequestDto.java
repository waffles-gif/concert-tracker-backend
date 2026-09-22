package com.concerttracker.backend.dto.request;

public record ReviewRequestDto(
        Long concertId,
        Integer rating,
        String comment
) {}