package com.concerttracker.backend.service;

import com.concerttracker.backend.dto.request.ArtistCreateRequest;
import com.concerttracker.backend.dto.request.ArtistUpdateRequest;
import com.concerttracker.backend.dto.response.ArtistResponse;
import com.concerttracker.backend.entity.Artist;
import com.concerttracker.backend.exception.DuplicateResourceException;
import com.concerttracker.backend.exception.InvalidOperationException;
import com.concerttracker.backend.exception.ResourceNotFoundException;
import com.concerttracker.backend.repository.ArtistRepository;
import com.concerttracker.backend.repository.ConcertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;
    private final ConcertRepository concertRepository;

    public ArtistServiceImpl(ArtistRepository artistRepository, ConcertRepository concertRepository) {
        this.artistRepository = artistRepository;
        this.concertRepository = concertRepository;
    }

    @Override
    @Transactional
    public ArtistResponse create(ArtistCreateRequest request) {
        if (artistRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Ya existe un artista con el nombre: " + request.getName());
        }
        Artist artist = new Artist(request.getName(), request.getCountry(),
                request.getDescription(), request.getImageUrl());
        return toResponse(artistRepository.save(artist));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArtistResponse> findAll() {
        return artistRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ArtistResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Override
    @Transactional
    public ArtistResponse update(Long id, ArtistUpdateRequest request) {
        Artist artist = findEntityById(id);
        if (artistRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            throw new DuplicateResourceException("Ya existe un artista con el nombre: " + request.getName());
        }
        artist.setName(request.getName());
        artist.setCountry(request.getCountry());
        artist.setDescription(request.getDescription());
        artist.setImageUrl(request.getImageUrl());
        return toResponse(artistRepository.save(artist));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Artist artist = findEntityById(id);
        if (concertRepository.existsByArtistId(id)) {
            throw new InvalidOperationException(
                    "No se puede eliminar el artista con id " + id + " porque tiene conciertos asociados");
        }
        artistRepository.delete(artist);
    }

    private Artist findEntityById(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artista no encontrado con id: " + id));
    }

    private ArtistResponse toResponse(Artist artist) {
        return new ArtistResponse(artist.getId(), artist.getName(), artist.getCountry(),
                artist.getDescription(), artist.getImageUrl());
    }
}
