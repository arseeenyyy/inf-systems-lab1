package com.github.arseeenyyy.service;

import com.github.arseeenyyy.dto.*;
import com.github.arseeenyyy.mapper.PersonMapper;
import com.github.arseeenyyy.models.Dragon;
import com.github.arseeenyyy.models.DragonCave;
import com.github.arseeenyyy.models.Person;
import com.github.arseeenyyy.models.Team;
import com.github.arseeenyyy.repository.DragonCaveRepository;
import com.github.arseeenyyy.repository.DragonRepository;
import com.github.arseeenyyy.repository.PersonRepository;
import com.github.arseeenyyy.repository.TeamRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Transactional
    public TeamCreateResponseDto createTeam(TeamCreateRequestDto requestDto) {
        if (teamRepository.existsByName(requestDto.getName())) {
            throw new IllegalArgumentException("Team with name '" + requestDto.getName() + "' already exists");
        }
        
        Team team = new Team();
        team.setName(requestDto.getName());
        Team savedTeam = teamRepository.save(team);
        
        int addedMembers = 0;
        List<Person> members = new ArrayList<>();
        for (Long personId : requestDto.getPersonsIds()) {
            Person person = personRepository.findById(personId);
            if (person != null) {
                person.setTeam(savedTeam);
                personRepository.update(person);
                members.add(person);
                addedMembers++;
            }
        } 
        
        List<PersonResponseDto> membersDto = members.stream()
            .map(p -> PersonMapper.toResponseDto(p)) 
            .collect(Collectors.toList());
            
        return new TeamCreateResponseDto(savedTeam.getId(), addedMembers, membersDto);
    }
    public List<TeamDto> getAllTeams() {
        return teamRepository.findAllWithMembers();
    }
    
    public Team getTeamById(Long id) {
        Team team = teamRepository.findById(id);
        if (team == null) {
            throw new NotFoundException("Team not found with id: " + id);
        }
        return team;
    }
    
    @Transactional
    public void deleteTeam(Long id) {
        Team team = teamRepository.findById(id);
        if (team == null) {
            throw new NotFoundException("Team not found with id: " + id);
        }
        
        List<Person> teamMembers = personRepository.findByTeamId(id);
        for (Person person : teamMembers) {
            person.setTeam(null);
            personRepository.update(person);
        }
        
        teamRepository.delete(id);
    }
    
    // @Transactional
    // public TeamToCaveResponseDto sendTeamToCave(TeamToCaveRequestDto requestDto) {
    //     Team team = teamRepository.findById(requestDto.getTeamId());
    //     if (team == null) {
    //         throw new NotFoundException("Team not found with id: " + requestDto.getTeamId());
    //     }
        
    //     DragonCave cave = dragonCaveRepository.findById(requestDto.getCaveId());
    //     if (cave == null) {
    //         throw new NotFoundException("Cave not found with id: " + requestDto.getCaveId());
    //     }
        
    //     List<Dragon> dragonsInCave = dragonRepository.findByCaveId(requestDto.getCaveId());
    //     List<Person> teamMembers = personRepository.findByTeamId(requestDto.getTeamId());
        
    //     Long treasures = cave.getNumberOfTreasures();
    //     int dragonsKilled = 0;
        
    //     if (!dragonsInCave.isEmpty() && !teamMembers.isEmpty()) {
    //         Person randomKiller = teamMembers.get(0); 
            
    //         for (Dragon dragon : dragonsInCave) {
    //             if (dragon.getKiller() == null) { 
    //                 dragon.setKiller(randomKiller);
    //                 dragonRepository.update(dragon);
    //                 dragonsKilled++;
    //             }
    //         }
    //         cave.setNumberOfTreasures(1L);
    //         dragonCaveRepository.update(cave);            
    //     }
        
    //     return new TeamToCaveResponseDto(treasures, dragonsKilled);
    // }
    @Transactional
    public TeamToCaveResponseDto sendTeamToCave(TeamToCaveRequestDto requestDto) {
        Team team = teamRepository.findById(requestDto.getTeamId());
        if (team == null) {
            throw new NotFoundException("Team not found with id: " + requestDto.getTeamId());
        }
        
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
            
            dragonCaveRepository.delete(requestDto.getCaveId());
        }
        
        return new TeamToCaveResponseDto(treasures, dragonsKilled);
    }
    @Transactional
    public TeamCreateResponseDto addMembersToTeam(Long teamId, List<Long> personIds) {
        Team team = teamRepository.findById(teamId);
        if (team == null) {
            throw new NotFoundException("Team not found with id: " + teamId);
        }
        
        int addedMembers = 0;
        List<Person> members = new ArrayList<>();
        for (Long personId : personIds) {
            Person person = personRepository.findById(personId);
            if (person != null && person.getTeam() == null) {
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
    
    @Transactional
    public TeamCreateResponseDto removeMembersFromTeam(Long teamId, List<Long> personIds) {
        Team team = teamRepository.findById(teamId);
        if (team == null) {
            throw new NotFoundException("Team not found with id: " + teamId);
        }
        int removedMembers = 0;
        for (Long personId : personIds) {
            Person person = personRepository.findById(personId);
            if (person != null && team.equals(person.getTeam())) {
                person.setTeam(null);
                personRepository.update(person);
                removedMembers++;
            }
        }
        
        return new TeamCreateResponseDto(teamId, removedMembers, new ArrayList<>());
    }
}