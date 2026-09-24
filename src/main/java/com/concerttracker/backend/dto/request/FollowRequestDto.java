package com.concerttracker.backend.dto.request;

import jakarta.validation.constraints.NotNull;

public record FollowRequestDto(
        @NotNull(message = "El artistId es obligatorio")
        Long artistId
) {}
