package com.concerttracker.backend.dto.request;

import java.time.LocalDateTime;

public record FollowResponseDto(
        Long id,
        Long userId,
        Long artistId,
        LocalDateTime createdAt
) {}
