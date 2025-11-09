package com.github.arseeenyyy.mapper;

import com.github.arseeenyyy.dto.user.UserResponseDto;
import com.github.arseeenyyy.dto.user.UserRequestDto;
import com.github.arseeenyyy.models.User;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserMapper {
    
    public static User toEntity(UserRequestDto requestDto) {
        User user = new User();
        user.setUsername(requestDto.getUsername()); 
        user.setRole(requestDto.getRole());
        return user;
    }

    public static UserResponseDto toResponseDto(User user) {
        return new UserResponseDto(
            user.getId(),
            user.getUsername(), 
            user.getRole()
        );
    }
}