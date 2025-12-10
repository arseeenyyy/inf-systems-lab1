package com.github.arseeenyyy.service;

import com.github.arseeenyyy.cache.CacheStatisticsLogging;
import com.github.arseeenyyy.dto.coordinates.CoordinatesRequestDto;
import com.github.arseeenyyy.dto.coordinates.CoordinatesResponseDto;
import com.github.arseeenyyy.mapper.CoordinatesMapper;
import com.github.arseeenyyy.models.Coordinates;
import com.github.arseeenyyy.models.User;
import com.github.arseeenyyy.repository.CoordinatesRepository;
import com.github.arseeenyyy.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@CacheStatisticsLogging
public class CoordinatesService {
    
    @Inject
    private CoordinatesRepository repository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private JwtService jwtService;
    
    public CoordinatesResponseDto create(CoordinatesRequestDto requestDto, String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Coordinates coordinates = CoordinatesMapper.toEntity(requestDto, user);
        Coordinates savedCoordinates = repository.save(coordinates);
        return CoordinatesMapper.toResponseDto(savedCoordinates);
    }

    public List<CoordinatesResponseDto> getAll(String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        
        List<Coordinates> coordinates;
        if (isAdmin(jwtToken)) {
            coordinates = repository.findAll();
        } else {
            coordinates = repository.findByUserId(userId);
        }
        
        return coordinates.stream()
                .map(CoordinatesMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public CoordinatesResponseDto getById(Long id, String jwtToken) {
        Coordinates coordinates = repository.findById(id); 
        if (coordinates == null) {
            throw new NotFoundException("Coordinates not found with id: " + id);
        }

        checkUserAccess(coordinates, jwtToken);
        
        return CoordinatesMapper.toResponseDto(coordinates);
    }

    public void delete(Long id, String jwtToken) {
        Coordinates coordinates = repository.findById(id);
        if (coordinates == null) {
            throw new NotFoundException("Coordinates not found with id: " + id);
        }

        checkUserAccess(coordinates, jwtToken);
        
        repository.delete(id);
    }

    public CoordinatesResponseDto update(Long id, CoordinatesRequestDto requestDto, String jwtToken) {
        Coordinates existingCoordinates = repository.findById(id);
        if (existingCoordinates == null) {
            throw new NotFoundException("Coordinates not found with id: " + id);
        }

        checkUserAccess(existingCoordinates, jwtToken);
        
        existingCoordinates.setX(requestDto.getX());
        existingCoordinates.setY(requestDto.getY());
        
        Coordinates updatedCoordinates = repository.update(existingCoordinates);
        return CoordinatesMapper.toResponseDto(updatedCoordinates);
    }
    
    public long count(String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        
        if (isAdmin(jwtToken)) {
            return repository.findAll().size();
        } else {
            return repository.findByUserId(userId).size();
        }
    }

    private void checkUserAccess(Coordinates coordinates, String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        
        if (isAdmin(jwtToken)) {
            return;
        }

        if (!coordinates.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: You don't have permission to access these coordinates");
        }
    }

    private boolean isAdmin(String jwtToken) {
        String role = jwtService.getRoleFromToken(jwtToken);
        return "ADMIN".equals(role);
    }
}