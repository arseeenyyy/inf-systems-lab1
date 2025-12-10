package com.github.arseeenyyy.service;

import java.util.List;
import java.util.stream.Collectors;

import com.github.arseeenyyy.cache.CacheStatisticsLogging;
import com.github.arseeenyyy.dto.dragon.DragonRequestDto;
import com.github.arseeenyyy.dto.dragon.DragonResponseDto;
import com.github.arseeenyyy.mapper.DragonMapper;
import com.github.arseeenyyy.models.Coordinates;
import com.github.arseeenyyy.models.Dragon;
import com.github.arseeenyyy.models.DragonCave;
import com.github.arseeenyyy.models.DragonHead;
import com.github.arseeenyyy.models.Person;
import com.github.arseeenyyy.models.User;
import com.github.arseeenyyy.repository.CoordinatesRepository;
import com.github.arseeenyyy.repository.DragonCaveRepository;
import com.github.arseeenyyy.repository.DragonHeadRepository;
import com.github.arseeenyyy.repository.DragonRepository;
import com.github.arseeenyyy.repository.PersonRepository;
import com.github.arseeenyyy.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ForbiddenException;

@ApplicationScoped
@CacheStatisticsLogging
public class DragonService {
    
    @Inject
    private DragonRepository dragonRepository;

    @Inject 
    private CoordinatesRepository coordinatesRepository;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private DragonCaveRepository dragonCaveRepository;

    @Inject  
    private DragonHeadRepository dragonHeadRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private JwtService jwtService;

    public DragonResponseDto create(DragonRequestDto requestDto, String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new ForbiddenException("User not found");
        }

        Coordinates coordinates = coordinatesRepository.findById(requestDto.getCoordinatesId());
        Person killer = requestDto.getKillerId() != null ? personRepository.findById(requestDto.getKillerId()) : null;
        DragonCave cave = requestDto.getCaveId() != null ? dragonCaveRepository.findById(requestDto.getCaveId()) : null;
        DragonHead head = requestDto.getHeadId() != null ? dragonHeadRepository.findById(requestDto.getHeadId()) : null;

        if (coordinates == null) {
            throw new NotFoundException("incorrect id's of related objects");
        }
        
        Dragon dragon = DragonMapper.toEntity(requestDto, coordinates, cave, killer, head, user);
        Dragon savedDragon = dragonRepository.save(dragon);
        return DragonMapper.toResponseDto(savedDragon);
    }
    
    public List<DragonResponseDto> getAll(String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        User user = userRepository.findById(userId);
        
        if (user == null) {
            throw new ForbiddenException("User not found");
        }

        List<Dragon> dragons;
        if ("ADMIN".equals(user.getRole().name())) {
            dragons = dragonRepository.findAll();
        } else {
            dragons = dragonRepository.findByUserId(userId);
        }
        
        return dragons.stream()
                .map(DragonMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public DragonResponseDto getById(Long id, String jwtToken) {
        Dragon dragon = dragonRepository.findById(id);
        if (dragon == null) {
            throw new NotFoundException("Dragon not found with id: " + id); 
        }

        checkUserAccess(dragon, jwtToken);
        
        return DragonMapper.toResponseDto(dragon);
    }

    public void delete(Long id, String jwtToken) {
        Dragon dragon = dragonRepository.findById(id);
        if (dragon == null) {
            throw new NotFoundException("Dragon not found with id: " + id);
        }

        checkUserAccess(dragon, jwtToken);
        
        DragonCave cave = dragon.getCave();
        Person killer = dragon.getKiller();
        DragonHead head = dragon.getHead();
        
        if (cave != null) {
            clearCaveReferences(cave.getId());
        }
        if (head != null) {
            clearHeadReferences(head.getId());
        }
        if (killer != null) {
            clearKillerReferences(killer.getId());
        }
        
        dragonRepository.delete(id);
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
    
    public DragonResponseDto update(Long id, DragonRequestDto requestDto, String jwtToken) {
        Dragon existingDragon = dragonRepository.findById(id);
        if (existingDragon == null) {
            throw new NotFoundException("Dragon not found with id: " + id);
        }

        checkUserAccess(existingDragon, jwtToken);
        
        existingDragon.setName(requestDto.getName());
        existingDragon.setAge(requestDto.getAge());
        existingDragon.setWeight(requestDto.getWeight());
        existingDragon.setColor(requestDto.getColor());
        existingDragon.setCharacter(requestDto.getCharacter());
        existingDragon.setCave(requestDto.getCaveId() != null ? dragonCaveRepository.findById(requestDto.getCaveId()) : null);
        existingDragon.setHead(requestDto.getHeadId() != null ? dragonHeadRepository.findById(requestDto.getHeadId()) : null);
        existingDragon.setKiller(requestDto.getKillerId() != null ? personRepository.findById(requestDto.getKillerId()) : null);
        
        Dragon updatedDragon = dragonRepository.update(existingDragon);
        return DragonMapper.toResponseDto(updatedDragon);
    }
    
    public List<DragonResponseDto> findByColor(String color, String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        User user = userRepository.findById(userId);
        
        List<Dragon> dragons;
        if ("ADMIN".equals(user.getRole().name())) {
            dragons = dragonRepository.findByColor(color);
        } else {
            dragons = dragonRepository.findByColor(color).stream()
                    .filter(d -> d.getUser().getId().equals(userId))
                    .collect(Collectors.toList());
        }
        
        return dragons.stream()
                .map(DragonMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
   public void deleteAllByColor(String color, String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        User user = userRepository.findById(userId);
        
        if (user == null) {
            throw new ForbiddenException("User not found");
        }

        List<Dragon> dragonsToDelete;
        if ("ADMIN".equals(user.getRole().name())) {
            dragonsToDelete = dragonRepository.findByColor(color);
        } else {
            dragonsToDelete = dragonRepository.findByColor(color).stream()
                    .filter(d -> d.getUser().getId().equals(userId))
                    .collect(Collectors.toList());
        }

        for (Dragon dragon : dragonsToDelete) {
            deleteDragonWithCleanup(dragon.getId());
        }
    }

    public void deleteOneByColor(String color, String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        User user = userRepository.findById(userId);
        
        if (user == null) {
            throw new ForbiddenException("User not found");
        }

        List<Dragon> dragons;
        if ("ADMIN".equals(user.getRole().name())) {
            dragons = dragonRepository.findByColor(color);
        } else {
            dragons = dragonRepository.findByColor(color).stream()
                    .filter(d -> d.getUser().getId().equals(userId))
                    .collect(Collectors.toList());
        }

        if (!dragons.isEmpty()) {
            Dragon dragonToDelete = dragons.get(0); 
            deleteDragonWithCleanup(dragonToDelete.getId());
        }
    }
    
    private void deleteDragonWithCleanup(Long dragonId) {
        Dragon dragon = dragonRepository.findById(dragonId);
        if (dragon != null) {
            DragonCave cave = dragon.getCave();
            Person killer = dragon.getKiller();
            DragonHead head = dragon.getHead();
            
            if (cave != null) {
                clearCaveReferences(cave.getId());
            }
            if (head != null) {
                clearHeadReferences(head.getId());
            }
            if (killer != null) {
                clearKillerReferences(killer.getId());
            }
            
            dragonRepository.delete(dragonId);
        }
    }
    
    public List<DragonResponseDto> findByNameStartingWith(String substring, String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        User user = userRepository.findById(userId);
        
        if (user == null) {
            throw new ForbiddenException("User not found");
        }

        List<Dragon> dragons;
        if ("ADMIN".equals(user.getRole().name())) {
            dragons = dragonRepository.findByNameStartingWith(substring);
        } else {
            dragons = dragonRepository.findByNameStartingWith(substring).stream()
                    .filter(d -> d.getUser().getId().equals(userId))
                    .collect(Collectors.toList());
        }
        
        return dragons.stream()
                .map(DragonMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    private void checkUserAccess(Dragon dragon, String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        User user = userRepository.findById(userId);
        
        if (user == null) {
            throw new ForbiddenException("User not found");
        }

        if ("ADMIN".equals(user.getRole().name())) {
            return;
        }

        if (!dragon.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Access denied: You don't have permission to access this dragon");
        }
    }
}