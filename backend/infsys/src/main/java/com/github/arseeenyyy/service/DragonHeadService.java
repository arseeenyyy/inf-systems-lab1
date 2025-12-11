package com.github.arseeenyyy.service;

import java.util.List;
import java.util.stream.Collectors;

import com.github.arseeenyyy.cache.CacheStatisticsLogging;
import com.github.arseeenyyy.dto.dragonHead.DragonHeadRequestDto;
import com.github.arseeenyyy.dto.dragonHead.DragonHeadResponseDto;
import com.github.arseeenyyy.mapper.DragonHeadMapper;
import com.github.arseeenyyy.models.DragonHead;
import com.github.arseeenyyy.models.User;
import com.github.arseeenyyy.repository.DragonHeadRepository;
import com.github.arseeenyyy.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class DragonHeadService {
    
    @Inject
    private DragonHeadRepository repository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private JwtService jwtService;
    
    public DragonHeadResponseDto create(DragonHeadRequestDto requestDto, String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        DragonHead head = DragonHeadMapper.toEntity(requestDto, user);
        DragonHead savedHead = repository.save(head);
        return DragonHeadMapper.toResponseDto(savedHead);
    }

    @CacheStatisticsLogging
    public List<DragonHeadResponseDto> getAll(String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        
        List<DragonHead> heads;
        if (isAdmin(jwtToken)) {
            heads = repository.findAll();
        } else {
            heads = repository.findByUserId(userId);
        }
        
        return heads.stream()
                .map(DragonHeadMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @CacheStatisticsLogging
    public DragonHeadResponseDto getById(Long id, String jwtToken) {
        DragonHead head = repository.findById(id);
        if (head == null) {
            throw new NotFoundException("DragonHead not found with id: " + id);
        }

        checkUserAccess(head, jwtToken);
        
        return DragonHeadMapper.toResponseDto(head);
    }
    
    public void delete(Long id, String jwtToken) {
        DragonHead head = repository.findById(id);
        if (head == null) {
            throw new NotFoundException("DragonHead not found with id: " + id);
        }

        checkUserAccess(head, jwtToken);
        
        repository.delete(id);
    }
    
    public DragonHeadResponseDto update(Long id, DragonHeadRequestDto requestDto, String jwtToken) {
        DragonHead existingHead = repository.findById(id);
        if (existingHead == null) {
            throw new NotFoundException("DragonHead not found with id: " + id);
        }

        checkUserAccess(existingHead, jwtToken);
        
        existingHead.setSize(requestDto.getSize());
        existingHead.setEyesCount(requestDto.getEyesCount());
        
        DragonHead updatedHead = repository.update(existingHead);
        return DragonHeadMapper.toResponseDto(updatedHead);
    }

    private void checkUserAccess(DragonHead head, String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        
        if (isAdmin(jwtToken)) {
            return;
        }

        if (!head.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: You don't have permission to access this head");
        }
    }

    private boolean isAdmin(String jwtToken) {
        String role = jwtService.getRoleFromToken(jwtToken);
        return "ADMIN".equals(role);
    }
}