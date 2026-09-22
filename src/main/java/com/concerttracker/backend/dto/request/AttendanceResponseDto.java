package com.concerttracker.backend.dto.request;

import com.concerttracker.backend.entity.Attendance;
import java.time.LocalDateTime;

public record AttendanceResponseDto(
        Long id,
        Long userId,
        Long concertId,
        Attendance.AttendanceStatus status,
        LocalDateTime createdAt
) {}
