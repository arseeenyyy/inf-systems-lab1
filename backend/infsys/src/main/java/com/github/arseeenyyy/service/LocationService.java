package com.github.arseeenyyy.service;

import java.util.List;
import java.util.stream.Collectors;

import com.github.arseeenyyy.dto.location.LocationRequestDto;
import com.github.arseeenyyy.dto.location.LocationResponseDto;
import com.github.arseeenyyy.mapper.LocationMapper;
import com.github.arseeenyyy.models.Location;
import com.github.arseeenyyy.repository.LocationRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class LocationService {
    
    @Inject
    private LocationRepository locationRepository;
    
    public LocationResponseDto createLocation(LocationRequestDto requestDto) {
        Location location = LocationMapper.toEntity(requestDto);
        Location savedLocation = locationRepository.save(location);
        return LocationMapper.toResponseDto(savedLocation);
    }
    
    public List<LocationResponseDto> getAllLocations() {
        List<Location> locations = locationRepository.findAll();
        return locations.stream()
                .map(LocationMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public LocationResponseDto getLocationById(Long id) {
        Location location = locationRepository.findById(id);
        if (location == null) {
            throw new NotFoundException("Location not found with id: " + id);
        }
        return LocationMapper.toResponseDto(location);
    }
    
    public void deleteLocation(Long id) {
        locationRepository.delete(id);
    }
    
    @Transactional
    public LocationResponseDto updateLocation(Long id, LocationRequestDto requestDto) {
        Location existingLocation = locationRepository.findById(id);
        if (existingLocation == null) {
            throw new NotFoundException("Location not found with id: " + id);
        }
        
        existingLocation.setX(requestDto.getX());
        existingLocation.setY(requestDto.getY());
        existingLocation.setZ(requestDto.getZ());
        existingLocation.setName(requestDto.getName());
        
        Location updatedLocation = locationRepository.update(existingLocation);
        return LocationMapper.toResponseDto(updatedLocation);
    }
}