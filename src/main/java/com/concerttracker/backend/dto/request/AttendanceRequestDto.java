package com.concerttracker.backend.dto.request;

import com.concerttracker.backend.entity.Attendance;
import jakarta.validation.constraints.NotNull;

public record AttendanceRequestDto(
        @NotNull(message = "El concertId es obligatorio")
        Long concertId,

        @NotNull(message = "El status es obligatorio (VOY_A_IR o YA_FUI)")
        Attendance.AttendanceStatus status
) {}
