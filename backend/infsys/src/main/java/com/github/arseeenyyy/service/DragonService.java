package com.github.arseeenyyy.service;

import com.github.arseeenyyy.dto.DragonRequestDto;
import com.github.arseeenyyy.dto.DragonResponseDto;
import com.github.arseeenyyy.mapper.DragonMapper;
import com.github.arseeenyyy.models.Coordinates;
import com.github.arseeenyyy.models.Dragon;
import com.github.arseeenyyy.models.DragonCave;
import com.github.arseeenyyy.models.DragonHead;
import com.github.arseeenyyy.models.Person;
import com.github.arseeenyyy.repository.CoordinatesRepository;
import com.github.arseeenyyy.repository.DragonCaveRepository;
import com.github.arseeenyyy.repository.DragonHeadRepository;
import com.github.arseeenyyy.repository.DragonRepository;
import com.github.arseeenyyy.repository.PersonRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class DragonService {
    
    @Inject
    private DragonRepository dragonRepository;

    @Inject 
    private CoordinatesRepository coordinatesRepository;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private DragonCaveRepository dragonCaveRepository;

    @Inject  
    private DragonHeadRepository dragonHeadRepository;
    

    @Transactional
    public DragonResponseDto create(DragonRequestDto requestDto) {
        Coordinates coordinates = coordinatesRepository.findById(requestDto.getCoordinatesId());
        Person killer = personRepository.findById(requestDto.getKillerId());
        DragonCave cave = dragonCaveRepository.findById(requestDto.getCaveId());
        DragonHead head = dragonHeadRepository.findById(requestDto.getHeadId());

        if (coordinates == null) {
            throw new NotFoundException("incorrect id's of related objects");
        }
        
        Dragon dragon = DragonMapper.toEntity(requestDto, coordinates, cave, killer, head);
        Dragon savedDragon = dragonRepository.save(dragon);
        return DragonMapper.toResponseDto(savedDragon);
    }
    
    @Transactional
    public List<DragonResponseDto> getAll() {
        List<Dragon> dragons = dragonRepository.findAll();
        return dragons.stream()
                .map(DragonMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public DragonResponseDto getById(Long id) {
        Dragon dragon = dragonRepository.findById(id);
        if (dragon == null) {
            throw new NotFoundException("Dragon not found with id: " + id); 
        }
        return DragonMapper.toResponseDto(dragon);
    }
    
    // @Transactional
    // public void delete(Long id) {
    //     dragonRepository.delete(id);
    // }

    @Transactional
    public void delete(Long id) {
        Dragon dragon = dragonRepository.findById(id);
        if (dragon == null) {
            throw new NotFoundException("Dragon not found with id: " + id);
        }
        
        DragonCave cave = dragon.getCave();
        Person killer = dragon.getKiller();
        DragonHead head = dragon.getHead();
        
        if (cave != null) {
            clearCaveReferences(cave.getId());
        }
        if (head != null) {
            clearHeadReferences(head.getId());
        }
        if (killer != null) {
            clearKillerReferences(killer.getId());
        }
        
        dragonRepository.delete(id);
    }

    private void clearCaveReferences(Long caveId) {
        List<Dragon> dragonsWithCave = dragonRepository.findByCaveId(caveId);
        for (Dragon d : dragonsWithCave) {
            d.setCave(null);
            dragonRepository.update(d);
        }
    }

    private void clearHeadReferences(Long headId) {
        List<Dragon> dragonsWithHead = dragonRepository.findByHeadId(headId);
        for (Dragon d : dragonsWithHead) {
            d.setHead(null);
            dragonRepository.update(d);
        }
    }

    private void clearKillerReferences(Long killerId) {
        List<Dragon> dragonsWithKiller = dragonRepository.findByKillerId(killerId);
        for (Dragon d : dragonsWithKiller) {
            d.setKiller(null);
            dragonRepository.update(d);
        }
    }
    
    @Transactional
    public DragonResponseDto update(Long id, DragonRequestDto requestDto) {
        Dragon existingDragon = dragonRepository.findById(id);
        if (existingDragon == null) {
            throw new NotFoundException("Dragon not found with id: " + id);
        }        
        existingDragon.setName(requestDto.getName());
        existingDragon.setAge(requestDto.getAge());
        existingDragon.setWeight(requestDto.getWeight());
        existingDragon.setColor(requestDto.getColor());
        existingDragon.setCharacter(requestDto.getCharacter());
        existingDragon.setCave(dragonCaveRepository.findById(requestDto.getCaveId()));
        existingDragon.setHead(dragonHeadRepository.findById(requestDto.getHeadId()));
        existingDragon.setKiller(personRepository.findById(requestDto.getKillerId()));
        
        Dragon updatedDragon = dragonRepository.update(existingDragon);
        return DragonMapper.toResponseDto(updatedDragon);
    }
    
    public List<DragonResponseDto> findByColor(String color) {
        List<Dragon> dragons = dragonRepository.findByColor(color);
        return dragons.stream()
                .map(DragonMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public void deleteAllByColor(String color) {
        List<Dragon> dragons = dragonRepository.findByColor(color);
        for (Dragon dragon : dragons) {
            dragonRepository.delete(dragon.getId());
        }
    }
    
    @Transactional
    public void deleteOneByColor(String color) {
        List<Dragon> dragons = dragonRepository.findByColor(color);
        if (!dragons.isEmpty()) {
            dragonRepository.delete(dragons.get(0).getId());
        }
    }
    
    @Transactional
    public List<DragonResponseDto> findByNameStartingWith(String substring) {
        List<Dragon> dragons = dragonRepository.findByNameStartingWith(substring);
        return dragons.stream()
                .map(DragonMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}