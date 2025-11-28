package com.github.arseeenyyy.mapper;

import com.github.arseeenyyy.dto.dragon.DragonRequestDto;
import com.github.arseeenyyy.dto.dragon.DragonResponseDto;
import com.github.arseeenyyy.models.Coordinates;
import com.github.arseeenyyy.models.Dragon;
import com.github.arseeenyyy.models.DragonCave;
import com.github.arseeenyyy.models.DragonHead;
import com.github.arseeenyyy.models.Person;
import com.github.arseeenyyy.models.User;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped 
public class DragonMapper {
    
    public static Dragon toEntity(DragonRequestDto requestDto, Coordinates coordinates, 
                                DragonCave cave, Person killer, DragonHead head, User user) {
        Dragon dragon = new Dragon();
        dragon.setName(requestDto.getName());
        dragon.setCoordinates(coordinates);
        dragon.setCave(cave);
        dragon.setKiller(killer);
        dragon.setAge(requestDto.getAge());
        dragon.setWeight(requestDto.getWeight());
        dragon.setColor(requestDto.getColor());
        dragon.setCharacter(requestDto.getCharacter());
        dragon.setHead(head);
        dragon.setUser(user); 
        return dragon;
    }

    public static DragonResponseDto toResponseDto(Dragon dragon) {
        return new DragonResponseDto(
            dragon.getId(), 
            dragon.getName(), 
            CoordinatesMapper.toResponseDto(dragon.getCoordinates()), 
            dragon.getCreationDate(), 
            dragon.getCave() != null ? DragonCaveMapper.toResponseDto(dragon.getCave()) : null,
            dragon.getKiller() != null ? PersonMapper.toResponseDto(dragon.getKiller()) : null,
            dragon.getAge(), 
            dragon.getWeight(), 
            dragon.getColor(), 
            dragon.getCharacter(), 
            dragon.getHead() != null ? DragonHeadMapper.toResponseDto(dragon.getHead()) : null,
            dragon.getUser().getId()
        );
    }
}