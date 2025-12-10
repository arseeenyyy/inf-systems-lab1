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
            query.setHint("org.hibernate.cacheable", true);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}