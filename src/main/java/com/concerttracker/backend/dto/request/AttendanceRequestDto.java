package com.concerttracker.backend.dto.request;

import com.concerttracker.backend.entity.Attendance;

public record AttendanceRequestDto(
        Long concertId,
        Attendance.AttendanceStatus status
) {}