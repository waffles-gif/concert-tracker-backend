package com.concerttracker.backend.repository;

import com.concerttracker.backend.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByUser_IdAndConcert_Id(Long userId, Long concertId);

    List<Attendance> findByUser_Id(Long userId);

    List<Attendance> findByUser_IdAndStatus(Long userId, Attendance.AttendanceStatus status);
}