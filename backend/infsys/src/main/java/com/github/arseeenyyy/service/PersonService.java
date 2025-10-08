package com.github.arseeenyyy.service;

import com.github.arseeenyyy.dto.PersonRequestDto;
import com.github.arseeenyyy.dto.PersonResponseDto;
import com.github.arseeenyyy.mapper.PersonMapper;
import com.github.arseeenyyy.models.Location;
import com.github.arseeenyyy.models.Person;
import com.github.arseeenyyy.repository.LocationRepository;
import com.github.arseeenyyy.repository.PersonRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PersonService {
    
    @Inject
    private PersonRepository personRepository;

    @Inject 
    private LocationRepository locationRepository;
    
    
    @Transactional
    public PersonResponseDto create(PersonRequestDto requestDto) {
        Location location = locationRepository.findById(requestDto.getLocationId());

        Person person = PersonMapper.toEntity(requestDto, location);
        Person savedPerson = personRepository.save(person);
        return PersonMapper.toResponseDto(savedPerson);
    }
    
    public List<PersonResponseDto> getAll() {
        List<Person> persons = personRepository.findAll();
        return persons.stream()
                .map(PersonMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public PersonResponseDto getById(Long id) {
        Person person = personRepository.findById(id); 
        if (person == null) {
            throw new NotFoundException("Person not found with id: " + id);
        }

        return PersonMapper.toResponseDto(person);
    }
    
    @Transactional
    public void delete(Long id) {
        personRepository.delete(id);
    }
    
    @Transactional
    public PersonResponseDto update(Long id, PersonRequestDto requestDto) {
        Person existingPerson = personRepository.findById(id); 
        if (existingPerson == null) {
            throw new NotFoundException("Person not found with id: " + id);
        }
        
        existingPerson.setName(requestDto.getName());
        existingPerson.setEyeColor(requestDto.getEyeColor());
        existingPerson.setHairColor(requestDto.getHairColor());
        existingPerson.setHeight(requestDto.getHeight());
        existingPerson.setNationality(requestDto.getNationality());
        
        Person updatedPerson = personRepository.update(existingPerson);
        return PersonMapper.toResponseDto(updatedPerson);
    }
}