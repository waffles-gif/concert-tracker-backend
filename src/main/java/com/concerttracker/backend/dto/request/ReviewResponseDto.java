package com.concerttracker.backend.dto.request;

import java.time.LocalDateTime;

public record ReviewResponseDto(
        Long id,
        Long userId,
        Long concertId,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {}