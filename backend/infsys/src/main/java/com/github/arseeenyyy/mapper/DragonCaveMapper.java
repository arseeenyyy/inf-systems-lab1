package com.github.arseeenyyy.mapper;

import com.github.arseeenyyy.dto.dragonCave.DragonCaveRequestDto;
import com.github.arseeenyyy.dto.dragonCave.DragonCaveResponseDto;
import com.github.arseeenyyy.models.DragonCave;
import com.github.arseeenyyy.models.User;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped 
public class DragonCaveMapper {
    public static DragonCave toEntity(DragonCaveRequestDto requestDto, User user) {
        DragonCave cave = new DragonCave();
        cave.setNumberOfTreasures(requestDto.getNumberOfTreasures());
        cave.setUser(user);
        return cave;
    } 

    public static DragonCaveResponseDto toResponseDto(DragonCave cave) {
        return new DragonCaveResponseDto(
            cave.getId(),
            cave.getNumberOfTreasures()
        );
    }
}