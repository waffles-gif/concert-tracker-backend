package com.concerttracker.backend.service;

import com.concerttracker.backend.dto.request.VenueCreateRequest;
import com.concerttracker.backend.dto.request.VenueUpdateRequest;
import com.concerttracker.backend.dto.response.VenueResponse;

import java.util.List;

public interface VenueService {

    VenueResponse create(VenueCreateRequest request);

    List<VenueResponse> findAll();

    VenueResponse findById(Long id);

    VenueResponse update(Long id, VenueUpdateRequest request);

    void delete(Long id);
}
