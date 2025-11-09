package com.github.arseeenyyy.repository;

import java.util.List;
import java.util.stream.Collectors;

import com.github.arseeenyyy.dto.person.PersonResponseDto;
import com.github.arseeenyyy.dto.team.TeamDto;
import com.github.arseeenyyy.mapper.PersonMapper;
import com.github.arseeenyyy.models.Person;
import com.github.arseeenyyy.models.Team;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;

@ApplicationScoped
public class TeamRepository {
    
    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private UserTransaction userTransaction;

    public Team save(Team team) {
        try {
            userTransaction.begin();
            entityManager.persist(team);
            userTransaction.commit();
            return team;
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception ex) {
                throw new RuntimeException("Rollback failed", ex);
            }
            throw new RuntimeException("Save failed", e);
        }
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
        try {
            userTransaction.begin();
            Team team = entityManager.find(Team.class, id);
            if (team != null) {
                entityManager.remove(team);
            }
            userTransaction.commit();
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception ex) {
                throw new RuntimeException("Rollback failed", ex);
            }
            throw new RuntimeException("Delete failed", e);
        }
    }

    public Team update(Team team) {
        try {
            userTransaction.begin();
            Team merged = entityManager.merge(team);
            userTransaction.commit();
            return merged;
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception ex) {
                throw new RuntimeException("Rollback failed", ex);
            }
            throw new RuntimeException("Update failed", e);
        }
    }
    
    public boolean existsByName(String name) {
        Long count = entityManager.createQuery(
            "SELECT COUNT(t) FROM Team t WHERE t.name = :name", Long.class)
            .setParameter("name", name)
            .getSingleResult();
        return count > 0;
    }
}