package com.concerttracker.backend.repository;

import com.concerttracker.backend.entity.Concert;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class ConcertSpecifications {

    private ConcertSpecifications() {
    }

    public static Specification<Concert> artistNameContains(String artist) {
        return (root, query, cb) -> cb.like(
                cb.lower(root.get("artist").get("name")),
                "%" + artist.toLowerCase() + "%");
    }

    public static Specification<Concert> countryEquals(String country) {
        return (root, query, cb) -> cb.equal(
                cb.lower(root.get("venue").get("country")),
                country.toLowerCase());
    }

    public static Specification<Concert> cityEquals(String city) {
        return (root, query, cb) -> cb.equal(
                cb.lower(root.get("venue").get("city")),
                city.toLowerCase());
    }

    public static Specification<Concert> onDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return (root, query, cb) -> cb.and(
                cb.greaterThanOrEqualTo(root.get("date"), start),
                cb.lessThan(root.get("date"), end));
    }

    public static Specification<Concert> upcoming(boolean upcoming) {
        LocalDateTime now = LocalDateTime.now();
        return (root, query, cb) -> upcoming
                ? cb.greaterThanOrEqualTo(root.get("date"), now)
                : cb.lessThan(root.get("date"), now);
    }
}
