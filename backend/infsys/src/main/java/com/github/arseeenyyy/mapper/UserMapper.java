package com.github.arseeenyyy.mapper;

import com.github.arseeenyyy.dto.user.UserReponseDto;
import com.github.arseeenyyy.dto.user.UserRequestDto;
import com.github.arseeenyyy.models.User;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserMapper {
    
    public static User toEntity(UserRequestDto requestDto) {
        User user = new User();
        user.setUsername(requestDto.getUsername()); 
        user.setPassword(requestDto.getPassword()); 
        user.setRole(requestDto.getRole());
        return user;
    }

    public static UserReponseDto toResponseDto(User user) {
        return new UserReponseDto(
            user.getId(),
            user.getUsername(), 
            user.getPassword(),
            user.getRole()
        );
    }
}
