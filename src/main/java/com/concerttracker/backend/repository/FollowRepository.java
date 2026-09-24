package com.concerttracker.backend.repository;

import com.concerttracker.backend.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByUserIdAndArtistId(Long userId, Long artistId);

    List<Follow> findByUserId(Long userId);

    List<Follow> findByArtistId(Long artistId);

    boolean existsByUserIdAndArtistId(Long userId, Long artistId);
}