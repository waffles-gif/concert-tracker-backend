package com.concerttracker.backend.event;

// AttendanceCreatedEvent.java
public record AttendanceCreatedEvent(Long userId, Long concertId, String status) {}

