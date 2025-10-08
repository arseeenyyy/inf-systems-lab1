package com.github.arseeenyyy.mapper;

import com.github.arseeenyyy.dto.LocationRequestDto;
import com.github.arseeenyyy.dto.LocationResponseDto;
import com.github.arseeenyyy.models.Location;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LocationMapper {
    
    public static Location toEntity(LocationRequestDto requestDto) {
        Location location = new Location();
        location.setX(requestDto.getX());
        location.setY(requestDto.getY());
        location.setZ(requestDto.getZ());
        location.setName(requestDto.getName());
        return location;
    }
    
    public static LocationResponseDto toResponseDto(Location location) {
        return new LocationResponseDto(
            location.getId(),
            location.getX(),
            location.getY(),
            location.getZ(),
            location.getName()
        );
    }
}