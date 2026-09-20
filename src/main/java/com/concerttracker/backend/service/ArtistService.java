package com.concerttracker.backend.service;

import com.concerttracker.backend.dto.request.ArtistCreateRequest;
import com.concerttracker.backend.dto.request.ArtistUpdateRequest;
import com.concerttracker.backend.dto.response.ArtistResponse;

import java.util.List;

public interface ArtistService {

    ArtistResponse create(ArtistCreateRequest request);

    List<ArtistResponse> findAll();

    ArtistResponse findById(Long id);

    ArtistResponse update(Long id, ArtistUpdateRequest request);

    void delete(Long id);
}
