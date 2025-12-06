package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.models.Person;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import java.util.List;

@ApplicationScoped
public class PersonRepository extends GenericRepository<Person, Long> {
    
    public List<Person> findByTeamId(Long teamId) {
        var em = getEntityManager();
        try {
            TypedQuery<Person> query = em.createQuery(
                "SELECT p FROM Person p WHERE p.team.id = :teamId", Person.class);
            query.setParameter("teamId", teamId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Person> findByUserId(Long userId) {
        var em = getEntityManager();
        try {
            TypedQuery<Person> query = em.createQuery(
                "SELECT p FROM Person p WHERE p.user.id = :userId", Person.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}