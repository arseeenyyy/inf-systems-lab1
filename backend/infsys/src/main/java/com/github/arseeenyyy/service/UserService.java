package com.github.arseeenyyy.service;

import java.util.List;
import java.util.stream.Collectors;

import com.github.arseeenyyy.dto.user.AuthResponseDto;
import com.github.arseeenyyy.dto.user.LoginRequestDto;
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

    @Inject
    private PasswordService passwordService;

    @Inject
    private JwtService jwtService;
        
    @Transactional
    public UserResponseDto create(UserRequestDto requestDto) {
        User existingUser = repository.findByUsername(requestDto.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("User with username '" + requestDto.getUsername() + "' already exists");
        }

        User user = UserMapper.toEntity(requestDto);
        String hashedPassword = passwordService.hashPassword(requestDto.getPassword());
        user.setPassword(hashedPassword);
        
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

    @Transactional
    public void delete(Long id) {
        repository.delete(id);
    }

    @Transactional
    public UserResponseDto update(Long id, UserRequestDto requestDto) {
        User existingUser = repository.findById(id);
        if (existingUser == null) {
            throw new NotFoundException("User not found with id: " + id);
        }        
        
        if (!existingUser.getUsername().equals(requestDto.getUsername())) {
            User userWithSameUsername = repository.findByUsername(requestDto.getUsername());
            if (userWithSameUsername != null && !userWithSameUsername.getId().equals(id)) {
                throw new RuntimeException("User with username '" + requestDto.getUsername() + "' already exists");
            }
            existingUser.setUsername(requestDto.getUsername());
        }
        existingUser.setRole(requestDto.getRole());        
        if (requestDto.getPassword() != null && !requestDto.getPassword().trim().isEmpty()) {
            String hashedPassword = passwordService.hashPassword(requestDto.getPassword());
            existingUser.setPassword(hashedPassword);
        }
        User updatedUser = repository.update(existingUser);
        return UserMapper.toResponseDto(updatedUser);
    }

    public AuthResponseDto authenticate(LoginRequestDto loginRequest) {
        User user = repository.findByUsername(loginRequest.getUsername());
        if (user == null) {
            return new AuthResponseDto(null, null, "Invalid username or password");
        }

        if (!passwordService.verifyPassword(loginRequest.getPassword(), user.getPassword())) {
            return new AuthResponseDto(null, null, "Invalid username or password");
        }

        String jwt = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        return new AuthResponseDto(user.getId(), jwt, "Authentication successful");
    }

    public boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }

    public User getUserFromToken(String token) {
        if (!validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }
        Long userId = jwtService.getUserIdFromToken(token);
        return repository.findById(userId);
    }
}