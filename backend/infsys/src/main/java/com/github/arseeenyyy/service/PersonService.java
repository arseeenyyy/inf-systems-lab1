package com.github.arseeenyyy.service;

import java.util.List;
import java.util.stream.Collectors;

import com.github.arseeenyyy.dto.person.PersonRequestDto;
import com.github.arseeenyyy.dto.person.PersonResponseDto;
import com.github.arseeenyyy.mapper.PersonMapper;
import com.github.arseeenyyy.models.Location;
import com.github.arseeenyyy.models.Person;
import com.github.arseeenyyy.models.User;
import com.github.arseeenyyy.repository.LocationRepository;
import com.github.arseeenyyy.repository.PersonRepository;
import com.github.arseeenyyy.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class PersonService {
    
    @Inject
    private PersonRepository personRepository;

    @Inject 
    private LocationRepository locationRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private JwtService jwtService;
    
    @Transactional
    public PersonResponseDto create(PersonRequestDto requestDto, String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Location location = requestDto.getLocationId() != null ? 
            locationRepository.findById(requestDto.getLocationId()) : null;

        Person person = PersonMapper.toEntity(requestDto, location, user);
        Person savedPerson = personRepository.save(person);
        return PersonMapper.toResponseDto(savedPerson);
    }
    
    public List<PersonResponseDto> getAll(String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        
        List<Person> persons;
        if (isAdmin(jwtToken)) {
            persons = personRepository.findAll();
        } else {
            persons = personRepository.findByUserId(userId);
        }
        
        return persons.stream()
                .map(PersonMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public PersonResponseDto getById(Long id, String jwtToken) {
        Person person = personRepository.findById(id); 
        if (person == null) {
            throw new NotFoundException("Person not found with id: " + id);
        }

        checkUserAccess(person, jwtToken);

        return PersonMapper.toResponseDto(person);
    }
    
    @Transactional
    public void delete(Long id, String jwtToken) {
        Person person = personRepository.findById(id);
        if (person == null) {
            throw new NotFoundException("Person not found with id: " + id);
        }

        checkUserAccess(person, jwtToken);
        
        personRepository.delete(id);
    }
    
    @Transactional
    public PersonResponseDto update(Long id, PersonRequestDto requestDto, String jwtToken) {
        Person existingPerson = personRepository.findById(id); 
        if (existingPerson == null) {
            throw new NotFoundException("Person not found with id: " + id);
        }

        checkUserAccess(existingPerson, jwtToken);
        
        existingPerson.setName(requestDto.getName());
        existingPerson.setEyeColor(requestDto.getEyeColor());
        existingPerson.setHairColor(requestDto.getHairColor());
        existingPerson.setHeight(requestDto.getHeight());
        existingPerson.setNationality(requestDto.getNationality());
        
        Person updatedPerson = personRepository.update(existingPerson);
        return PersonMapper.toResponseDto(updatedPerson);
    }

    private void checkUserAccess(Person person, String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        
        if (isAdmin(jwtToken)) {
            return;
        }

        if (!person.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: You don't have permission to access this person");
        }
    }

    private boolean isAdmin(String jwtToken) {
        String role = jwtService.getRoleFromToken(jwtToken);
        return "ADMIN".equals(role);
    }
}