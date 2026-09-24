package com.concerttracker.backend.service;

import com.concerttracker.backend.dto.request.VenueCreateRequest;
import com.concerttracker.backend.dto.request.VenueUpdateRequest;
import com.concerttracker.backend.dto.response.VenueResponse;
import com.concerttracker.backend.entity.Venue;
import com.concerttracker.backend.exception.ResourceInUseException;
import com.concerttracker.backend.exception.ResourceNotFoundException;
import com.concerttracker.backend.repository.ConcertRepository;
import com.concerttracker.backend.repository.VenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;
    private final ConcertRepository concertRepository;

    public VenueServiceImpl(VenueRepository venueRepository, ConcertRepository concertRepository) {
        this.venueRepository = venueRepository;
        this.concertRepository = concertRepository;
    }

    @Override
    @Transactional
    public VenueResponse create(VenueCreateRequest request) {
        Venue venue = new Venue(request.getName(), request.getCity(), request.getCountry(),
                request.getAddress(), request.getCapacity());
        return toResponse(venueRepository.save(venue));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VenueResponse> findAll() {
        return venueRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VenueResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Override
    @Transactional
    public VenueResponse update(Long id, VenueUpdateRequest request) {
        Venue venue = findEntityById(id);
        venue.setName(request.getName());
        venue.setCity(request.getCity());
        venue.setCountry(request.getCountry());
        venue.setAddress(request.getAddress());
        venue.setCapacity(request.getCapacity());
        return toResponse(venueRepository.save(venue));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Venue venue = findEntityById(id);
        if (concertRepository.existsByVenueId(id)) {
            throw new ResourceInUseException(
                    "No se puede eliminar el venue con id " + id + " porque tiene conciertos asociados");
        }
        venueRepository.delete(venue);
    }

    private Venue findEntityById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue no encontrado con id: " + id));
    }

    private VenueResponse toResponse(Venue venue) {
        return new VenueResponse(venue.getId(), venue.getName(), venue.getCity(), venue.getCountry(),
                venue.getAddress(), venue.getCapacity());
    }
}
