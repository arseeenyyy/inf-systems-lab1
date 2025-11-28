package com.github.arseeenyyy.service;

import java.util.List;
import java.util.stream.Collectors;

import com.github.arseeenyyy.dto.location.LocationRequestDto;
import com.github.arseeenyyy.dto.location.LocationResponseDto;
import com.github.arseeenyyy.mapper.LocationMapper;
import com.github.arseeenyyy.models.Location;
import com.github.arseeenyyy.models.User;
import com.github.arseeenyyy.repository.LocationRepository;
import com.github.arseeenyyy.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class LocationService {
    
    @Inject
    private LocationRepository locationRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private JwtService jwtService;
    
    public LocationResponseDto createLocation(LocationRequestDto requestDto, String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Location location = LocationMapper.toEntity(requestDto, user);
        Location savedLocation = locationRepository.save(location);
        return LocationMapper.toResponseDto(savedLocation);
    }
    
    public List<LocationResponseDto> getAllLocations(String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        
        List<Location> locations;
        if (isAdmin(jwtToken)) {
            locations = locationRepository.findAll();
        } else {
            locations = locationRepository.findByUserId(userId);
        }
        
        return locations.stream()
                .map(LocationMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public LocationResponseDto getLocationById(Long id, String jwtToken) {
        Location location = locationRepository.findById(id);
        if (location == null) {
            throw new NotFoundException("Location not found with id: " + id);
        }

        checkUserAccess(location, jwtToken);
        
        return LocationMapper.toResponseDto(location);
    }
    
    public void deleteLocation(Long id, String jwtToken) {
        Location location = locationRepository.findById(id);
        if (location == null) {
            throw new NotFoundException("Location not found with id: " + id);
        }

        checkUserAccess(location, jwtToken);
        
        locationRepository.delete(id);
    }
    
    public LocationResponseDto updateLocation(Long id, LocationRequestDto requestDto, String jwtToken) {
        Location existingLocation = locationRepository.findById(id);
        if (existingLocation == null) {
            throw new NotFoundException("Location not found with id: " + id);
        }

        checkUserAccess(existingLocation, jwtToken);
        
        existingLocation.setX(requestDto.getX());
        existingLocation.setY(requestDto.getY());
        existingLocation.setZ(requestDto.getZ());
        existingLocation.setName(requestDto.getName());
        
        Location updatedLocation = locationRepository.update(existingLocation);
        return LocationMapper.toResponseDto(updatedLocation);
    }

    private void checkUserAccess(Location location, String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        
        if (isAdmin(jwtToken)) {
            return;
        }

        if (!location.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: You don't have permission to access this location");
        }
    }

    private boolean isAdmin(String jwtToken) {
        String role = jwtService.getRoleFromToken(jwtToken);
        return "ADMIN".equals(role);
    }
}