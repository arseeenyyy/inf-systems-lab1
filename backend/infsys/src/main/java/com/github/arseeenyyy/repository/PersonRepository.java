package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.models.Person;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;
import java.util.List;

@ApplicationScoped 
public class PersonRepository {
    
    @PersistenceContext 
    private EntityManager entityManager;

    @Inject
    private UserTransaction userTransaction;

    public Person save(Person person) {
        try {
            userTransaction.begin();
            entityManager.persist(person);
            userTransaction.commit();
            return person;
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception rollbackEx) {
                throw new RuntimeException("Failed to rollback transaction", rollbackEx);
            }
            throw new RuntimeException("Failed to save Person", e);
        }
    }

    public List<Person> findAll() {
        return entityManager.createQuery("SELECT p FROM Person p", Person.class)
            .getResultList();
    }

    public Person findById(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.find(Person.class, id);
    }

    public void delete(Long id) {
        try {
            userTransaction.begin();
            Person person = entityManager.find(Person.class, id);
            if (person != null) {
                entityManager.remove(person);
            }
            userTransaction.commit();
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception rollbackEx) {
                throw new RuntimeException("Failed to rollback transaction", rollbackEx);
            }
            throw new RuntimeException("Failed to delete Person", e);
        }
    }

    public Person update(Person person) {
        try {
            userTransaction.begin();
            Person updated = entityManager.merge(person);
            userTransaction.commit();
            return updated;
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception rollbackEx) {
                throw new RuntimeException("Failed to rollback transaction", rollbackEx);
            }
            throw new RuntimeException("Failed to update Person", e);
        }
    }

    public List<Person> findByTeamId(Long teamId) {
        return entityManager.createQuery(
            "SELECT p FROM Person p WHERE p.team.id = :teamId", Person.class)
            .setParameter("teamId", teamId)
            .getResultList();
    }

    public List<Person> findByUserId(Long userId) {
        return entityManager.createQuery(
            "SELECT p FROM Person p WHERE p.user.id = :userId", Person.class)
            .setParameter("userId", userId)
            .getResultList();
    }
}