package com.github.arseeenyyy.service;

import com.github.arseeenyyy.dto.DragonCaveRequestDto;
import com.github.arseeenyyy.dto.DragonCaveResponseDto;
import com.github.arseeenyyy.mapper.DragonCaveMapper;
import com.github.arseeenyyy.models.DragonCave;
import com.github.arseeenyyy.repository.DragonCaveRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class DragonCaveService {
    
    @Inject
    private DragonCaveRepository repository;
    
    @Transactional
    public DragonCaveResponseDto create(DragonCaveRequestDto requestDto) {
        DragonCave cave = DragonCaveMapper.toEntity(requestDto);
        DragonCave savedCave = repository.save(cave);
        return DragonCaveMapper.toResponseDto(savedCave);
    }
    
    public List<DragonCaveResponseDto> getAll() {
        List<DragonCave> caves = repository.findAll();
        return caves.stream()
                .map(DragonCaveMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public DragonCaveResponseDto getById(Long id) {
        DragonCave cave = repository.findById(id); 
        if (cave == null) {
            throw new NotFoundException("DragonCave not found with id: " + id);
        }
        return DragonCaveMapper.toResponseDto(cave);
    }
    
    @Transactional
    public void delete(Long id) {
        repository.delete(id);
    }
    
    @Transactional
    public DragonCaveResponseDto update(Long id, DragonCaveRequestDto requestDto) {
        DragonCave existingCave = repository.findById(id);
        if (existingCave == null) {
            throw new NotFoundException("DragonCave not found with id: " + id);
        }        
        existingCave.setNumberOfTreasures(requestDto.getNumberOfTreasures());
        
        DragonCave updatedCave = repository.update(existingCave);
        return DragonCaveMapper.toResponseDto(updatedCave);
    }
}