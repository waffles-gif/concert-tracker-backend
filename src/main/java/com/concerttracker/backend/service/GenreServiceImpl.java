package com.concerttracker.backend.service;

import com.concerttracker.backend.dto.request.GenreCreateRequest;
import com.concerttracker.backend.dto.request.GenreUpdateRequest;
import com.concerttracker.backend.dto.response.GenreResponse;
import com.concerttracker.backend.entity.Genre;
import com.concerttracker.backend.exception.DuplicateResourceException;
import com.concerttracker.backend.exception.ResourceNotFoundException;
import com.concerttracker.backend.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    public GenreServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public GenreResponse create(GenreCreateRequest request) {
        if (genreRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException(
                    "Ya existe un género con el nombre: " + request.getName());
        }
        Genre saved = genreRepository.save(new Genre(request.getName()));
        return toResponse(saved);
    }

    @Override
    public List<GenreResponse> findAll() {
        return genreRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public GenreResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Override
    public GenreResponse update(Long id, GenreUpdateRequest request) {
        Genre genre = findEntityById(id);
        genre.setName(request.getName());
        return toResponse(genreRepository.save(genre));
    }

    @Override
    public void delete(Long id) {
        if (!genreRepository.existsById(id)) {
            throw new ResourceNotFoundException("Género no encontrado con id: " + id);
        }
        genreRepository.deleteById(id);
    }

    private Genre findEntityById(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Género no encontrado con id: " + id));
    }

    private GenreResponse toResponse(Genre genre) {
        return new GenreResponse(genre.getId(), genre.getName());
    }
}