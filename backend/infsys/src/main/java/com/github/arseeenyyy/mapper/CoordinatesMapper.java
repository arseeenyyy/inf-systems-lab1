package com.github.arseeenyyy.mapper;

import com.github.arseeenyyy.dto.coordinates.CoordinatesRequestDto;
import com.github.arseeenyyy.dto.coordinates.CoordinatesResponseDto;
import com.github.arseeenyyy.models.Coordinates;
import com.github.arseeenyyy.models.User;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CoordinatesMapper {
    public static Coordinates toEntity(CoordinatesRequestDto requestDto, User user) {
        Coordinates coordinates = new Coordinates();
        coordinates.setX(requestDto.getX());
        coordinates.setY(requestDto.getY());
        coordinates.setUser(user);
        return coordinates;
    }

    public static CoordinatesResponseDto toResponseDto(Coordinates coordinates) {
        return new CoordinatesResponseDto(
            coordinates.getId(), 
            coordinates.getX(), 
            coordinates.getY()
        );
    }
}
