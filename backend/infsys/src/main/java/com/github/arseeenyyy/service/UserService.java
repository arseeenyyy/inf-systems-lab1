package com.github.arseeenyyy.service;

import com.github.arseeenyyy.dto.UserRequestDto;
import com.github.arseeenyyy.dto.UserResponseDto;
import com.github.arseeenyyy.models.User;
import com.github.arseeenyyy.models.Role;
import com.github.arseeenyyy.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserService {
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private PasswordService passwordService;

    public UserResponseDto create(UserRequestDto requestDto) {
        if (userRepository.findByUsername(requestDto.getUsername()) != null) {
            throw new BadRequestException("Username already exists");
        }
        
        String salt = passwordService.generateSalt();
        String passwordHash = passwordService.hashPassword(requestDto.getPassword(), salt);
        
        User user = new User();
        user.setUsername(requestDto.getUsername());
        user.setPasswordHash(passwordHash);
        user.setPasswordSalt(salt);
        user.setRole(Role.USER); 
        
        User savedUser = userRepository.save(user);
        return toResponseDto(savedUser);
    }

    public List<UserResponseDto> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto getById(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new NotFoundException("User not found with id: " + id);
        }
        return toResponseDto(user);
    }

    public UserResponseDto getByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found with username: " + username);
        }
        return toResponseDto(user);
    }

    public void delete(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new NotFoundException("User not found with id: " + id);
        }
        userRepository.delete(id);
    }

    // public UserResponseDto updateRole(Long id, Role newRole) {
    //     User user = userRepository.findById(id);
    //     if (user == null) {
    //         throw new NotFoundException("User not found with id: " + id);
    //     }
        
    //     user.setRole(newRole);
    //     User updatedUser = userRepository.update(user);
    //     return toResponseDto(updatedUser);
    // }

    // public UserResponseDto updatePassword(Long id, String newPassword) {
    //     User user = userRepository.findById(id);
    //     if (user == null) {
    //         throw new NotFoundException("User not found with id: " + id);
    //     }
        
    //     String newSalt = passwordService.generateSalt();
    //     String newPasswordHash = passwordService.hashPassword(newPassword, newSalt);
        
    //     user.setPasswordHash(newPasswordHash);
    //     user.setPasswordSalt(newSalt);
        
    //     User updatedUser = userRepository.update(user);
    //     return toResponseDto(updatedUser);
    // }

    // public void createAdminIfNotExists() {
    //     if (userRepository.findByUsername("admin") == null) {
    //         String salt = passwordService.generateSalt();
    //         String passwordHash = passwordService.hashPassword("admin123", salt);
            
    //         User admin = new User();
    //         admin.setUsername("admin");
    //         admin.setPasswordHash(passwordHash);
    //         admin.setPasswordSalt(salt);
    //         admin.setRole(Role.ADMIN);
            
    //         userRepository.save(admin);
    //         System.out.println("Admin user created: admin / admin123");
    //     }
    // }

    private UserResponseDto toResponseDto(User user) {
        return new UserResponseDto(
            user.getId(),
            user.getUsername(),
            user.getRole()
        );
    }
}