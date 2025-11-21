package com.github.arseeenyyy.mapper;

import com.github.arseeenyyy.dto.location.LocationRequestDto;
import com.github.arseeenyyy.dto.location.LocationResponseDto;
import com.github.arseeenyyy.models.Location;
import com.github.arseeenyyy.models.User;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LocationMapper {
    
    public static Location toEntity(LocationRequestDto requestDto, User user) {
        Location location = new Location();
        location.setX(requestDto.getX());
        location.setY(requestDto.getY());
        location.setZ(requestDto.getZ());
        location.setName(requestDto.getName());
        location.setUser(user);
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