package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.dto.person.PersonResponseDto;
import com.github.arseeenyyy.dto.team.TeamDto;
import com.github.arseeenyyy.mapper.PersonMapper;
import com.github.arseeenyyy.models.Person;
import com.github.arseeenyyy.models.Team;
import com.github.arseeenyyy.models.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TeamRepository extends GenericRepository<Team, Long> {
    
    public List<Team> findByUserId(Long userId) {
        var em = getEntityManager();
        try {
            TypedQuery<Team> query = em.createQuery(
                "SELECT t FROM Team t WHERE t.user.id = :userId", Team.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<TeamDto> findAllWithMembers() {
        List<Team> teams = findAll();
        return teams.stream().map(this::toTeamDto).collect(Collectors.toList());
    }
    
    public List<TeamDto> findByUserIdWithMembers(Long userId) {
        List<Team> teams = findByUserId(userId);
        return teams.stream().map(this::toTeamDto).collect(Collectors.toList());
    }
    
    private TeamDto toTeamDto(Team team) {
        var em = getEntityManager();
        try {
            TypedQuery<Person> query = em.createQuery(
                "SELECT p FROM Person p WHERE p.team.id = :teamId", Person.class);
            query.setParameter("teamId", team.getId());
            List<Person> members = query.getResultList();
            
            List<PersonResponseDto> membersDto = members.stream()
                .map(PersonMapper::toResponseDto)
                .collect(Collectors.toList());
                
            return new TeamDto(team.getId(), team.getName(), membersDto.size(), membersDto);
        } finally {
            em.close();
        }
    }
    
    public boolean existsByName(String name) {
        var em = getEntityManager();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(t) FROM Team t WHERE t.name = :name", Long.class)
                .setParameter("name", name)
                .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
    
    public boolean existsByNameAndUser(String name, User user) {
        var em = getEntityManager();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(t) FROM Team t WHERE t.name = :name AND t.user = :user", Long.class)
                .setParameter("name", name)
                .setParameter("user", user)
                .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}