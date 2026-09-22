package com.concerttracker.backend.controller;

import com.concerttracker.backend.dto.request.AttendanceRequestDto;
import com.concerttracker.backend.dto.request.FollowRequestDto;
import com.concerttracker.backend.dto.request.ReviewRequestDto;
import com.concerttracker.backend.dto.request.AttendanceResponseDto;
import com.concerttracker.backend.dto.request.FollowResponseDto;
import com.concerttracker.backend.dto.request.ReviewResponseDto;
import com.concerttracker.backend.entity.Attendance;
import com.concerttracker.backend.service.UserInteractionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-interactions")
public class UserInteractionController {

    private final UserInteractionService userInteractionService;

    public UserInteractionController(UserInteractionService userInteractionService) {
        this.userInteractionService = userInteractionService;
    }

    // Endpoint 1: Registrar o cambiar estado de asistencia (VOY_A_IR / YA_FUI)
    @PostMapping("/attendance")
    public ResponseEntity<AttendanceResponseDto> setAttendance(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody AttendanceRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userInteractionService.setOrUpdateAttendance(userId, dto));
    }

    // Endpoint 2: Historial personal (filtrar por estado VOY_A_IR / YA_FUI opcional)
    @GetMapping("/attendance/history")
    public ResponseEntity<List<AttendanceResponseDto>> getUserHistory(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Attendance.AttendanceStatus status) {
        return ResponseEntity.ok(userInteractionService.getUserHistory(userId, status));
    }

    // Endpoint 3: Crear reseña (la validación YA_FUI se ejecuta en el Service)
    @PostMapping("/reviews")
    public ResponseEntity<ReviewResponseDto> createReview(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody ReviewRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userInteractionService.createReview(userId, dto));
    }

    // Endpoint 4: Seguir a un artista
    @PostMapping("/follow")
    public ResponseEntity<FollowResponseDto> followArtist(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody FollowRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userInteractionService.followArtist(userId, dto));
    }
}
