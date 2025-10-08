package com.github.arseeenyyy.mapper;

import com.github.arseeenyyy.dto.PersonRequestDto;
import com.github.arseeenyyy.dto.PersonResponseDto;
import com.github.arseeenyyy.models.Location;
import com.github.arseeenyyy.models.Person;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PersonMapper {

    public static Person toEntity(PersonRequestDto requestDto, Location location) {
        Person person = new Person();
        person.setName(requestDto.getName()); 
        person.setEyeColor(requestDto.getEyeColor());
        person.setHairColor(requestDto.getHairColor()); 
        person.setLocation(location); 
        person.setHeight(requestDto.getHeight());
        person.setNationality(requestDto.getNationality());
        return person;
    }

//    public static PersonResponseDto toResponseDto(Person person) {
//         PersonResponseDto dto = new PersonResponseDto();
//         dto.setId(person.getId());
//         dto.setName(person.getName());
//         dto.setEyeColor(person.getEyeColor());
//         dto.setHairColor(person.getHairColor());
//         dto.setHeight(person.getHeight());
//         dto.setNationality(person.getNationality());
        
//         if (person.getLocation() != null) {
//             dto.setLocation(LocationMapper.toResponseDto(person.getLocation()));
//         } else {
//             dto.setLocation(null);
//         }
        
//         if (person.getTeam() != null) {
//             dto.setTeamName(person.getTeam().getTeamName());
//         } else {
//             dto.setTeamName(null);
//         }
        
//         return dto;
//     }
    public static PersonResponseDto toResponseDto(Person person) {
        return new PersonResponseDto(
            person.getId(), 
            person.getName(), 
            person.getEyeColor(), 
            person.getHairColor(), 
            person.getLocation() != null ? LocationMapper.toResponseDto(person.getLocation()) : null,
            person.getHeight(), 
            person.getNationality()
        );
    }
}