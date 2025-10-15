package com.github.arseeenyyy.service;

import com.github.arseeenyyy.dto.TeamCreateRequestDto;
import com.github.arseeenyyy.dto.TeamCreateResponseDto;
import com.github.arseeenyyy.dto.TeamToCaveRequestDto;
import com.github.arseeenyyy.dto.TeamToCaveResponseDto;
import com.github.arseeenyyy.models.DragonCave;
import com.github.arseeenyyy.models.Person;
import com.github.arseeenyyy.models.Team;
import com.github.arseeenyyy.repository.DragonCaveRepository;
import com.github.arseeenyyy.repository.PersonRepository;
import com.github.arseeenyyy.repository.TeamRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import java.util.List;

@ApplicationScoped
public class TeamService {
    
    @Inject
    private TeamRepository teamRepository;
    
    @Inject
    private PersonRepository personRepository;
    
    @Inject
    private DragonCaveRepository dragonCaveRepository;

    @Transactional
    public TeamCreateResponseDto createTeam(TeamCreateRequestDto requestDto) {
        if (teamRepository.existsByName(requestDto.getName())) {
            throw new IllegalArgumentException("Team with name '" + requestDto.getName() + "' already exists");
        }
        
        Team team = new Team();
        team.setName(requestDto.getName());
        Team savedTeam = teamRepository.save(team);
        
        int addedMembers = 0;
        for (Long personId : requestDto.getPersonsIds()) {
            Person person = personRepository.findById(personId);
            if (person != null) {
                person.setTeam(savedTeam);
                personRepository.update(person);
                addedMembers++;
            }
        }
        
        return new TeamCreateResponseDto(savedTeam.getId(), addedMembers);
    }
    
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }
    
    public Team getTeamById(Long id) {
        Team team = teamRepository.findById(id);
        if (team == null) {
            throw new NotFoundException("team not found with id: " + id);
        }
        return team;
    }
    
    @Transactional
    public void deleteTeam(Long id) {
        List<Person> teamMembers = personRepository.findByTeamId(id);
        for (Person person : teamMembers) {
            person.setTeam(null);
            personRepository.update(person);
        }
        
        teamRepository.delete(id);
    }
    
    @Transactional
    public TeamToCaveResponseDto sendTeamToCave(TeamToCaveRequestDto requestDto) {
        Team team = teamRepository.findById(requestDto.getTeamId());
        if (team == null) {
            throw new NotFoundException("team not found with id: " + requestDto.getTeamId());
        }
        
        DragonCave cave = dragonCaveRepository.findById(requestDto.getCaveId());
        if (cave == null) {
            throw new NotFoundException("cave not found with id: " + requestDto.getCaveId());
        }
                // .orElseThrow(() -> new NotFoundException("Cave not found with id: " + requestDto.getCaveId()));
        
        Long treasures = cave.getNumberOfTreasures();
        
        return new TeamToCaveResponseDto(treasures);
    }
    
    @Transactional
    public TeamCreateResponseDto addMembersToTeam(Long teamId, List<Long> personIds) {
        Team team = teamRepository.findById(teamId);
        if (team == null) {
            throw new NotFoundException("team not found with id: " + teamId);
        }        
        int addedMembers = 0;
        for (Long personId : personIds) {
            Person person = personRepository.findById(personId);
            if (person != null && person.getTeam() == null) {
                person.setTeam(team);
                personRepository.update(person);
                addedMembers++;
            }
        }
        
        return new TeamCreateResponseDto(teamId, addedMembers);
    }
    
    @Transactional
    public TeamCreateResponseDto removeMembersFromTeam(Long teamId, List<Long> personIds) {
        Team team = teamRepository.findById(teamId);
        if (team == null) {
            throw new NotFoundException("team not found with id: " + teamId);
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
        
        return new TeamCreateResponseDto(teamId, removedMembers);
    }
}