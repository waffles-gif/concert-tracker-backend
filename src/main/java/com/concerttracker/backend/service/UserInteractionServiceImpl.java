package com.concerttracker.backend.service;

import com.concerttracker.backend.dto.request.AttendanceRequestDto;
import com.concerttracker.backend.dto.request.FollowRequestDto;
import com.concerttracker.backend.dto.request.ReviewRequestDto;
import com.concerttracker.backend.dto.request.AttendanceResponseDto;
import com.concerttracker.backend.dto.request.FollowResponseDto;
import com.concerttracker.backend.dto.request.ReviewResponseDto;
import com.concerttracker.backend.entity.Attendance;
import com.concerttracker.backend.entity.Follow;
import com.concerttracker.backend.entity.Review;
import com.concerttracker.backend.event.AttendanceCreatedEvent;
import com.concerttracker.backend.event.ReviewCreatedEvent;
import com.concerttracker.backend.repository.AttendanceRepository;
import com.concerttracker.backend.repository.FollowRepository;
import com.concerttracker.backend.repository.ReviewRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserInteractionServiceImpl implements UserInteractionService {

    private final AttendanceRepository attendanceRepository;
    private final ReviewRepository reviewRepository;
    private final FollowRepository followRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UserInteractionServiceImpl(AttendanceRepository attendanceRepository,
                                      ReviewRepository reviewRepository,
                                      FollowRepository followRepository,
                                      ApplicationEventPublisher eventPublisher) {
        this.attendanceRepository = attendanceRepository;
        this.reviewRepository = reviewRepository;
        this.followRepository = followRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public AttendanceResponseDto setOrUpdateAttendance(Long userId, AttendanceRequestDto dto) {
        Attendance attendance = attendanceRepository.findByUserIdAndConcertId(userId, dto.concertId())
                .orElse(Attendance.builder()
                        .userId(userId)
                        .concertId(dto.concertId())
                        .build());

        attendance.setStatus(dto.status());
        Attendance saved = attendanceRepository.save(attendance);

        eventPublisher.publishEvent(new AttendanceCreatedEvent(userId, dto.concertId(), dto.status().name()));

        return new AttendanceResponseDto(
                saved.getId(),
                saved.getUserId(),
                saved.getConcertId(),
                saved.getStatus(),
                saved.getCreatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> getUserHistory(Long userId, Attendance.AttendanceStatus statusFilter) {
        List<Attendance> list = (statusFilter != null)
                ? attendanceRepository.findByUserIdAndStatus(userId, statusFilter)
                : attendanceRepository.findByUserId(userId);

        return list.stream()
                .map(a -> new AttendanceResponseDto(
                        a.getId(),
                        a.getUserId(),
                        a.getConcertId(),
                        a.getStatus(),
                        a.getCreatedAt()))
                .toList();
    }

    @Override
    @Transactional
    public ReviewResponseDto createReview(Long userId, ReviewRequestDto dto) {
        Attendance attendance = attendanceRepository.findByUserIdAndConcertId(userId, dto.concertId())
                .orElseThrow(() -> new IllegalStateException("No puedes dejar una reseña sin haber registrado asistencia previa."));

        if (attendance.getStatus() != Attendance.AttendanceStatus.YA_FUI) {
            throw new IllegalStateException("Solo puedes dejar una reseña si el estado de tu asistencia es 'YA_FUI'.");
        }

        Review review = Review.builder()
                .userId(userId)
                .concertId(dto.concertId())
                .rating(dto.rating())
                .comment(dto.comment())
                .build();

        Review saved = reviewRepository.save(review);

        eventPublisher.publishEvent(new ReviewCreatedEvent(userId, dto.concertId(), dto.rating()));

        return new ReviewResponseDto(
                saved.getId(),
                saved.getUserId(),
                saved.getConcertId(),
                saved.getRating(),
                saved.getComment(),
                saved.getCreatedAt()
        );
    }

    @Override
    @Transactional
    public FollowResponseDto followArtist(Long userId, FollowRequestDto dto) {
        Follow follow = Follow.builder()
                .userId(userId)
                .artistId(dto.artistId())
                .build();

        Follow saved = followRepository.save(follow);

        return new FollowResponseDto(
                saved.getId(),
                saved.getUserId(),
                saved.getArtistId(),
                saved.getCreatedAt()
        );
    }
}