// package com.github.arseeenyyy.service;

// import com.github.arseeenyyy.dto.auth.LoginRequestDto;
// import com.github.arseeenyyy.dto.auth.RegisterRequestDto;
// import com.github.arseeenyyy.dto.auth.UserResponseDto;
// import com.github.arseeenyyy.models.Role;
// import com.github.arseeenyyy.models.User;
// import com.github.arseeenyyy.repository.UserRepository;

// import jakarta.enterprise.context.ApplicationScoped;
// import jakarta.inject.Inject;
// import jakarta.ws.rs.BadRequestException;
// import jakarta.ws.rs.NotAuthorizedException;

// @ApplicationScoped
// public class AuthService {
    
//     @Inject
//     private UserRepository userRepository;
    
//     @Inject
//     private PasswordService passwordService;
    
//     public UserResponseDto register(RegisterRequestDto requestDto) {
//         if (userRepository.findByUsername(requestDto.getUsername()) != null) {
//             throw new BadRequestException("Username already exists");
//         }
        
//         String salt = passwordService.generateSalt();
//         String passwordHash = passwordService.hashPassword(requestDto.getPassword(), salt);
        
//         User user = new User();
//         user.setUsername(requestDto.getUsername());
//         user.setPasswordHash(passwordHash);
//         user.setPasswordSalt(salt);
//         user.setRole(Role.USER);
        
//         User savedUser = userRepository.save(user);
//         return toResponseDto(savedUser);
//     }
    
//     public UserResponseDto login(LoginRequestDto requestDto) {
//         User user = userRepository.findByUsername(requestDto.getUsername());
//         if (user == null) {
//             throw new NotAuthorizedException("Invalid credentials");
//         }
        
//         boolean passwordValid = passwordService.checkPassword(
//             requestDto.getPassword(), 
//             user.getPasswordSalt(), 
//             user.getPasswordHash()
//         );
        
//         if (!passwordValid) {
//             throw new NotAuthorizedException("Invalid credentials");
//         }
        
//         return toResponseDto(user);
//     }
    

    
//     private UserResponseDto toResponseDto(User user) {
//         return new UserResponseDto(user.getId(), user.getUsername(), user.getRole());
//     }
// }