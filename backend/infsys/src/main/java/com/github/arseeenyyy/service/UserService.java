package com.github.arseeenyyy.service;

import java.util.List;
import java.util.stream.Collectors;

import com.github.arseeenyyy.dto.user.UserRequestDto;
import com.github.arseeenyyy.dto.user.UserResponseDto;
import com.github.arseeenyyy.mapper.UserMapper;
import com.github.arseeenyyy.models.User;
import com.github.arseeenyyy.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class UserService {
    
    @Inject
    private UserRepository repository;
        
    public UserResponseDto create(UserRequestDto requestDto) {
        User user = UserMapper.toEntity(requestDto);
        User savedUser = repository.save(user);
        return UserMapper.toResponseDto(savedUser);
    }

    public List<UserResponseDto> getAll() {
        List<User> users = repository.findAll();
        return users.stream()
                .map(UserMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto getById(Long id) {
        User user = repository.findById(id); 
        if (user == null) {
            throw new NotFoundException("User not found with id: " + id);
        }
        return UserMapper.toResponseDto(user);
    }

    public void delete(Long id) {
        repository.delete(id);
    }

    @Transactional
    public UserResponseDto update(Long id, UserRequestDto requestDto) {
        User existingUser = repository.findById(id);
        if (existingUser == null) {
            throw new NotFoundException("User not found with id: " + id);
        }
        existingUser.setUsername(requestDto.getUsername());
        existingUser.setPassword(requestDto.getPassword());
        existingUser.setRole(requestDto.getRole());
        
        User updatedUser = repository.update(existingUser);
        return UserMapper.toResponseDto(updatedUser);
    }
}