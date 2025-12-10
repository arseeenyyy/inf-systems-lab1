package com.github.arseeenyyy.service;

import com.github.arseeenyyy.cache.CacheStatisticsLogging;
import com.github.arseeenyyy.dto.user.LoginRequestDto;
import com.github.arseeenyyy.dto.user.UserRequestDto;
import com.github.arseeenyyy.dto.user.UserResponseDto;
import com.github.arseeenyyy.mapper.UserMapper;
import com.github.arseeenyyy.models.Dragon;
import com.github.arseeenyyy.models.User;
import com.github.arseeenyyy.repository.DragonRepository;
import com.github.arseeenyyy.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.util.List;

@ApplicationScoped
@CacheStatisticsLogging
public class UserService {

    @Inject
    private UserRepository userRepository;

    @Inject
    private DragonRepository dragonRepository;

    @Inject
    private PasswordService passwordService;

    @Inject
    private JwtService jwtService;

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

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException("User not found with id: " + userId);
        }

        deleteUserData(userId);

        userRepository.delete(userId);
    }

    private void deleteUserData(Long userId) {
        List<Dragon> userDragons = dragonRepository.findByUserId(userId);
        
        for (Dragon dragon : userDragons) {
            deleteDragonAndClearReferences(dragon);
        }
    }

    private void deleteDragonAndClearReferences(Dragon dragon) {
        if (dragon.getCave() != null) {
            clearCaveReferences(dragon.getCave().getId());
        }
        if (dragon.getHead() != null) {
            clearHeadReferences(dragon.getHead().getId());
        }
        if (dragon.getKiller() != null) {
            clearKillerReferences(dragon.getKiller().getId());
        }
        
        dragonRepository.delete(dragon.getId());
    }

    private void clearCaveReferences(Long caveId) {
        List<Dragon> dragonsWithCave = dragonRepository.findByCaveId(caveId);
        for (Dragon d : dragonsWithCave) {
            d.setCave(null);
            dragonRepository.update(d);
        }
    }

    private void clearHeadReferences(Long headId) {
        List<Dragon> dragonsWithHead = dragonRepository.findByHeadId(headId);
        for (Dragon d : dragonsWithHead) {
            d.setHead(null);
            dragonRepository.update(d);
        }
    }

    private void clearKillerReferences(Long killerId) {
        List<Dragon> dragonsWithKiller = dragonRepository.findByKillerId(killerId);
        for (Dragon d : dragonsWithKiller) {
            d.setKiller(null);
            dragonRepository.update(d);
        }
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