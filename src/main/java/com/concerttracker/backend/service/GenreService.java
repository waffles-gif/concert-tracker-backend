package com.concerttracker.backend.service;

import com.concerttracker.backend.dto.request.GenreCreateRequest;
import com.concerttracker.backend.dto.request.GenreUpdateRequest;
import com.concerttracker.backend.dto.response.GenreResponse;

import java.util.List;

public interface GenreService {

    GenreResponse create(GenreCreateRequest request);

    List<GenreResponse> findAll();

    GenreResponse findById(Long id);

    GenreResponse update(Long id, GenreUpdateRequest request);

    void delete(Long id);
}