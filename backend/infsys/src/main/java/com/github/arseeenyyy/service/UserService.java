package com.github.arseeenyyy.service;

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
    private UserRepository userRepository;

    @Inject
    private PasswordService passwordService;

    @Inject
    private JwtService jwtService;

    @Transactional
    public UserResponseDto register(UserRequestDto requestDto) {
        User existingUser = userRepository.findByUsername(requestDto.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("Username already exists");
        }

        User user = UserMapper.toEntity(requestDto);
        String hashedPassword = passwordService.hashPassword(requestDto.getPassword());
        user.setPassword(hashedPassword);

        User savedUser = userRepository.save(user);

        String jwt = jwtService.generateToken(savedUser.getId(), savedUser.getUsername(), savedUser.getRole().name());

        return new UserResponseDto(savedUser.getId(), jwt);
    }

    public UserResponseDto login(LoginRequestDto loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername());
        if (user == null) {
            throw new RuntimeException("Invalid username or password");
        }

        if (!passwordService.verifyPassword(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String jwt = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole().name());

        return new UserResponseDto(user.getId(), jwt);
    }

    public User getById(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new NotFoundException("User not found with id: " + id);
        }
        return user;
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException("User not found with id: " + userId);
        }

        deleteUserData(userId);

        userRepository.delete(user);
    }

    private void deleteUserData(Long userId) {
        //TODO do smth with deleting data
        System.out.println("stupid shit");
    }

    public boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }

    public User getUserFromToken(String token) {
        if (!validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }
        Long userId = jwtService.getUserIdFromToken(token);
        return userRepository.findById(userId);
    }
}