package com.github.arseeenyyy.service;

import com.github.arseeenyyy.dto.CoordinatesRequestDto;
import com.github.arseeenyyy.dto.CoordinatesResponseDto;
import com.github.arseeenyyy.mapper.CoordinatesMapper;
import com.github.arseeenyyy.models.Coordinates;
import com.github.arseeenyyy.repository.CoordinatesRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;


@ApplicationScoped
public class CoordinatesService {
    
    @Inject
    private CoordinatesRepository repository;
        
    @Transactional
    public CoordinatesResponseDto create(CoordinatesRequestDto requestDto) {
        Coordinates coordinates = CoordinatesMapper.toEntity(requestDto);
        Coordinates savedCoordinates = repository.save(coordinates);
        return CoordinatesMapper.toResponseDto(savedCoordinates);
    }
    // public boolean existsById(Long id) {
    //     return repository.findById(id).isPresent();
    // }
}