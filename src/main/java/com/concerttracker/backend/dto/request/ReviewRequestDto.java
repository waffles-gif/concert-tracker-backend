package com.concerttracker.backend.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewRequestDto(
        @NotNull(message = "El concertId es obligatorio")
        Long concertId,

        @NotNull(message = "El rating es obligatorio")
        @Min(value = 1, message = "El rating mínimo es 1")
        @Max(value = 5, message = "El rating máximo es 5")
        Integer rating,

        @Size(max = 1000, message = "El comentario no puede superar los 1000 caracteres")
        String comment
) {}
