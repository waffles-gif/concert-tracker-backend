package com.concerttracker.backend.event;

// ReviewCreatedEvent.java
public record ReviewCreatedEvent(Long userId, Long concertId, Integer rating) {}
