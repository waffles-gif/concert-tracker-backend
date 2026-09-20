package com.concerttracker.backend.controller;

import com.concerttracker.backend.dto.request.VenueCreateRequest;
import com.concerttracker.backend.dto.request.VenueUpdateRequest;
import com.concerttracker.backend.dto.response.ConcertResponse;
import com.concerttracker.backend.dto.response.VenueResponse;
import com.concerttracker.backend.service.ConcertService;
import com.concerttracker.backend.service.VenueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/venues")
public class VenueController {

    private final VenueService venueService;
    private final ConcertService concertService;

    public VenueController(VenueService venueService, ConcertService concertService) {
        this.venueService = venueService;
        this.concertService = concertService;
    }

    @GetMapping
    public ResponseEntity<List<VenueResponse>> findAll() {
        return ResponseEntity.ok(venueService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VenueResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(venueService.findById(id));
    }

    @GetMapping("/{id}/concerts")
    public ResponseEntity<List<ConcertResponse>> findConcerts(@PathVariable Long id) {
        return ResponseEntity.ok(concertService.findByVenue(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VenueResponse> create(@Valid @RequestBody VenueCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(venueService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VenueResponse> update(
            @PathVariable Long id, @Valid @RequestBody VenueUpdateRequest request) {
        return ResponseEntity.ok(venueService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        venueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
