package com.github.arseeenyyy.service;

import java.util.List;
import java.util.stream.Collectors;

import com.github.arseeenyyy.dto.dragonCave.DragonCaveRequestDto;
import com.github.arseeenyyy.dto.dragonCave.DragonCaveResponseDto;
import com.github.arseeenyyy.mapper.DragonCaveMapper;
import com.github.arseeenyyy.models.DragonCave;
import com.github.arseeenyyy.models.User;
import com.github.arseeenyyy.repository.DragonCaveRepository;
import com.github.arseeenyyy.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class DragonCaveService {
    
    @Inject
    private DragonCaveRepository repository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private JwtService jwtService;
    
    @Transactional
    public DragonCaveResponseDto create(DragonCaveRequestDto requestDto, String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        DragonCave cave = DragonCaveMapper.toEntity(requestDto, user);
        DragonCave savedCave = repository.save(cave);
        return DragonCaveMapper.toResponseDto(savedCave);
    }
    
    public List<DragonCaveResponseDto> getAll(String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        
        List<DragonCave> caves;
        if (isAdmin(jwtToken)) {
            caves = repository.findAll();
        } else {
            caves = repository.findByUserId(userId);
        }
        
        return caves.stream()
                .map(DragonCaveMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public DragonCaveResponseDto getById(Long id, String jwtToken) {
        DragonCave cave = repository.findById(id); 
        if (cave == null) {
            throw new NotFoundException("DragonCave not found with id: " + id);
        }

        checkUserAccess(cave, jwtToken);
        
        return DragonCaveMapper.toResponseDto(cave);
    }
    
    @Transactional
    public void delete(Long id, String jwtToken) {
        DragonCave cave = repository.findById(id);
        if (cave == null) {
            throw new NotFoundException("DragonCave not found with id: " + id);
        }

        checkUserAccess(cave, jwtToken);
        
        repository.delete(id);
    }
    
    @Transactional
    public DragonCaveResponseDto update(Long id, DragonCaveRequestDto requestDto, String jwtToken) {
        DragonCave existingCave = repository.findById(id);
        if (existingCave == null) {
            throw new NotFoundException("DragonCave not found with id: " + id);
        }

        checkUserAccess(existingCave, jwtToken);
        
        existingCave.setNumberOfTreasures(requestDto.getNumberOfTreasures());
        
        DragonCave updatedCave = repository.update(existingCave);
        return DragonCaveMapper.toResponseDto(updatedCave);
    }

    private void checkUserAccess(DragonCave cave, String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        
        if (isAdmin(jwtToken)) {
            return;
        }

        if (!cave.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: You don't have permission to access this cave");
        }
    }

    private boolean isAdmin(String jwtToken) {
        String role = jwtService.getRoleFromToken(jwtToken);
        return "ADMIN".equals(role);
    }
}