package com.concerttracker.backend.service;

import com.concerttracker.backend.dto.request.ConcertCreateRequest;
import com.concerttracker.backend.dto.request.ConcertUpdateRequest;
import com.concerttracker.backend.dto.response.ConcertDetailResponse;
import com.concerttracker.backend.dto.response.ConcertResponse;

import java.time.LocalDate;
import java.util.List;

public interface ConcertService {

    ConcertDetailResponse create(ConcertCreateRequest request);

    List<ConcertResponse> findAll();

    ConcertDetailResponse findById(Long id);

    ConcertDetailResponse update(Long id, ConcertUpdateRequest request);

    void delete(Long id);

    List<ConcertResponse> search(String artist, String country, String city, LocalDate date, Boolean upcoming);

    List<ConcertResponse> findByVenue(Long venueId);

    List<ConcertResponse> findByArtist(Long artistId);
}
