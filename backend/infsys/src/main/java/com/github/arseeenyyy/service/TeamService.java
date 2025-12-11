package com.github.arseeenyyy.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.arseeenyyy.cache.CacheStatisticsLogging;
import com.github.arseeenyyy.dto.person.PersonResponseDto;
import com.github.arseeenyyy.dto.team.TeamCreateRequestDto;
import com.github.arseeenyyy.dto.team.TeamCreateResponseDto;
import com.github.arseeenyyy.dto.team.TeamDto;
import com.github.arseeenyyy.dto.team.TeamToCaveRequestDto;
import com.github.arseeenyyy.dto.team.TeamToCaveResponseDto;
import com.github.arseeenyyy.mapper.PersonMapper;
import com.github.arseeenyyy.models.Dragon;
import com.github.arseeenyyy.models.DragonCave;
import com.github.arseeenyyy.models.Person;
import com.github.arseeenyyy.models.Team;
import com.github.arseeenyyy.models.User;
import com.github.arseeenyyy.repository.DragonCaveRepository;
import com.github.arseeenyyy.repository.DragonRepository;
import com.github.arseeenyyy.repository.PersonRepository;
import com.github.arseeenyyy.repository.TeamRepository;
import com.github.arseeenyyy.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class TeamService {
    
    @Inject
    private TeamRepository teamRepository;
    
    @Inject
    private PersonRepository personRepository;
    
    @Inject
    private DragonCaveRepository dragonCaveRepository;
    
    @Inject
    private DragonRepository dragonRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private JwtService jwtService;

    public TeamCreateResponseDto createTeam(TeamCreateRequestDto requestDto, String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (teamRepository.existsByNameAndUser(requestDto.getName(), user)) {
            throw new IllegalArgumentException("Team with name '" + requestDto.getName() + "' already exists");
        }
        
        Team team = new Team();
        team.setName(requestDto.getName());
        team.setUser(user);
        Team savedTeam = teamRepository.save(team);
        
        int addedMembers = 0;
        List<Person> members = new ArrayList<>();
        for (Long personId : requestDto.getPersonsIds()) {
            Person person = personRepository.findById(personId);
            if (person != null && checkUserAccess(person, jwtToken)) {
                person.setTeam(savedTeam);
                personRepository.update(person);
                members.add(person);
                addedMembers++;
            }
        } 
        
        List<PersonResponseDto> membersDto = members.stream()
            .map(PersonMapper::toResponseDto) 
            .collect(Collectors.toList());
            
        return new TeamCreateResponseDto(savedTeam.getId(), addedMembers, membersDto);
    }

    @CacheStatisticsLogging
    public List<TeamDto> getAllTeams(String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        User user = userRepository.findById(userId);
        
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if ("ADMIN".equals(user.getRole().name())) {
            return teamRepository.findAllWithMembers();
        } else {
            return teamRepository.findByUserIdWithMembers(userId);
        }
    }
    
    @CacheStatisticsLogging
    public Team getTeamById(Long id, String jwtToken) {
        Team team = teamRepository.findById(id);
        if (team == null) {
            throw new NotFoundException("Team not found with id: " + id);
        }

        checkUserAccess(team, jwtToken);
        return team;
    }
    
    public void deleteTeam(Long id, String jwtToken) {
        Team team = teamRepository.findById(id);
        if (team == null) {
            throw new NotFoundException("Team not found with id: " + id);
        }

        checkUserAccess(team, jwtToken);
        
        List<Person> teamMembers = personRepository.findByTeamId(id);
        
        for (Person person : teamMembers) {
            List<Dragon> killedDragons = dragonRepository.findByKillerId(person.getId());
            for (Dragon dragon : killedDragons) {
                dragon.setKiller(null);
                dragonRepository.update(dragon);
            }
        }
        for (Person person : teamMembers) {
            personRepository.delete(person.getId());
        }
        teamRepository.delete(id);
    }

    public TeamToCaveResponseDto sendTeamToCave(TeamToCaveRequestDto requestDto, String jwtToken) {
        Team team = teamRepository.findById(requestDto.getTeamId());
        if (team == null) {
            throw new NotFoundException("Team not found with id: " + requestDto.getTeamId());
        }

        checkUserAccess(team, jwtToken);
        
        DragonCave cave = dragonCaveRepository.findById(requestDto.getCaveId());
        if (cave == null) {
            throw new NotFoundException("Cave not found with id: " + requestDto.getCaveId());
        }
        
        List<Dragon> dragonsInCave = dragonRepository.findByCaveId(requestDto.getCaveId());
        List<Person> teamMembers = personRepository.findByTeamId(requestDto.getTeamId());
        
        Long treasures = cave.getNumberOfTreasures();
        int dragonsKilled = 0;
        
        if (!dragonsInCave.isEmpty() && !teamMembers.isEmpty()) {
            Person randomKiller = teamMembers.get(0); 
            
            for (Dragon dragon : dragonsInCave) {
                if (dragon.getKiller() == null) { 
                    dragon.setKiller(randomKiller);
                    dragon.setCave(null); 
                    dragonRepository.update(dragon);
                    dragonsKilled++;
                }
            }
            
            List<Dragon> remainingDragons = dragonRepository.findByCaveId(requestDto.getCaveId());
            for (Dragon dragon : remainingDragons) {
                dragon.setCave(null);
                dragonRepository.update(dragon);
            }
            
            dragonCaveRepository.delete(requestDto.getCaveId());
        }
        
        return new TeamToCaveResponseDto(treasures, dragonsKilled);
    }

    public TeamCreateResponseDto addMembersToTeam(Long teamId, List<Long> personIds, String jwtToken) {
        Team team = teamRepository.findById(teamId);
        if (team == null) {
            throw new NotFoundException("Team not found with id: " + teamId);
        }

        checkUserAccess(team, jwtToken);
        
        int addedMembers = 0;
        List<Person> members = new ArrayList<>();
        for (Long personId : personIds) {
            Person person = personRepository.findById(personId);
            if (person != null && person.getTeam() == null && checkUserAccess(person, jwtToken)) {
                person.setTeam(team);
                personRepository.update(person);
                members.add(person);
                addedMembers++;
            }
        }
        List<PersonResponseDto> membersDto = members.stream()
            .map(PersonMapper::toResponseDto)
            .collect(Collectors.toList());
            
        return new TeamCreateResponseDto(teamId, addedMembers, membersDto);
    }
    
    public TeamCreateResponseDto removeMembersFromTeam(Long teamId, List<Long> personIds, String jwtToken) {
        Team team = teamRepository.findById(teamId);
        if (team == null) {
            throw new NotFoundException("Team not found with id: " + teamId);
        }

        checkUserAccess(team, jwtToken);

        int removedMembers = 0;
        for (Long personId : personIds) {
            Person person = personRepository.findById(personId);
            if (person != null && team.equals(person.getTeam()) && checkUserAccess(person, jwtToken)) {
                person.setTeam(null);
                personRepository.update(person);
                removedMembers++;
            }
        }
        
        return new TeamCreateResponseDto(teamId, removedMembers, new ArrayList<>());
    }

    private void checkUserAccess(Team team, String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        User user = userRepository.findById(userId);
        
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if ("ADMIN".equals(user.getRole().name())) {
            return;
        }

        if (!team.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: You don't have permission to access this team");
        }
    }

    private boolean checkUserAccess(Person person, String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        User user = userRepository.findById(userId);
        
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if ("ADMIN".equals(user.getRole().name())) {
            return true;
        }

        return person.getUser().getId().equals(userId);
    }
}