package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.dto.PersonResponseDto;
import com.github.arseeenyyy.dto.TeamDto;
import com.github.arseeenyyy.mapper.PersonMapper;
import com.github.arseeenyyy.models.Person;
import com.github.arseeenyyy.models.Team;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TeamRepository {
    
    @PersistenceContext
    private EntityManager entityManager;

    public Team save(Team team) {
        entityManager.persist(team);
        return team;
    }

    public List<Team> findAll() {
        return entityManager.createQuery("SELECT t FROM Team t", Team.class)
                .getResultList();
    }
    public List<TeamDto> findAllWithMembers() {
        List<Team> teams = findAll();
        return teams.stream().map(team -> {
            List<Person> members = entityManager.createQuery(
                "SELECT p FROM Person p WHERE p.team.id = :teamId", Person.class)
                .setParameter("teamId", team.getId())
                .getResultList();
            
            List<PersonResponseDto> membersDto = members.stream()
                .map(PersonMapper::toResponseDto)
                .collect(Collectors.toList());
                
            return new TeamDto(team.getId(), team.getName(), membersDto.size(), membersDto);
        }).toList();
    }

    public Team findById(Long id) {
        return entityManager.find(Team.class, id);
    }

    public void delete(Long id) {
        Team team = entityManager.find(Team.class, id);
        if (team != null) {
            entityManager.remove(team);
        }
    }

    public Team update(Team team) {
        return entityManager.merge(team);
    }
    
    public boolean existsByName(String name) {
        Long count = entityManager.createQuery(
            "SELECT COUNT(t) FROM Team t WHERE t.name = :name", Long.class)
            .setParameter("name", name)
            .getSingleResult();
        return count > 0;
    }
}