package com.concerttracker.backend.repository;

import com.concerttracker.backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByUser_IdAndConcert_Id(Long userId, Long concertId);

    List<Review> findByConcert_Id(Long concertId);

    List<Review> findByUser_Id(Long userId);
}