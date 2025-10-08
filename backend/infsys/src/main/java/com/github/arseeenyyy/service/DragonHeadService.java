package com.github.arseeenyyy.service;

import com.github.arseeenyyy.dto.DragonHeadRequestDto;
import com.github.arseeenyyy.dto.DragonHeadResponseDto;
import com.github.arseeenyyy.mapper.DragonHeadMapper;
import com.github.arseeenyyy.models.DragonHead;
import com.github.arseeenyyy.repository.DragonHeadRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class DragonHeadService {
    
    @Inject
    private DragonHeadRepository repository;
    
    @Transactional
    public DragonHeadResponseDto create(DragonHeadRequestDto requestDto) {
        DragonHead head = DragonHeadMapper.toEntity(requestDto);
        DragonHead savedHead = repository.save(head);
        return DragonHeadMapper.toResponseDto(savedHead);
    }
    
    public List<DragonHeadResponseDto> getAll() {
        List<DragonHead> heads = repository.findAll();
        return heads.stream()
                .map(DragonHeadMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public DragonHeadResponseDto getById(Long id) {
        DragonHead head = repository.findById(id);
        if (head == null) {
            throw new NotFoundException("DragonHead not found with id: " + id);
        }
        return DragonHeadMapper.toResponseDto(head);
    }
    
    @Transactional
    public void delete(Long id) {
        repository.delete(id);
    }
    
    @Transactional
    public DragonHeadResponseDto update(Long id, DragonHeadRequestDto requestDto) {
        DragonHead existingHead = repository.findById(id);
        if (existingHead == null) {
            throw new NotFoundException("DragonHead not found with id: " + id);
        }        
        existingHead.setSize(requestDto.getSize());
        existingHead.setEyesCount(requestDto.getEyesCount());
        
        DragonHead updatedHead = repository.update(existingHead);
        return DragonHeadMapper.toResponseDto(updatedHead);
    }
}