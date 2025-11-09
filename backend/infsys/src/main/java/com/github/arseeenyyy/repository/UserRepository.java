package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.models.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;
import java.util.List;

@ApplicationScoped 
public class UserRepository {

    @PersistenceContext 
    private EntityManager entityManager;

    @Inject
    private UserTransaction userTransaction;

    public User save(User user) {
        try {
            userTransaction.begin();
            entityManager.persist(user);
            userTransaction.commit();
            return user;
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception ex) {
                throw new RuntimeException("Rollback failed", ex);
            }
            throw new RuntimeException("Save failed", e);
        }
    }

    public List<User> findAll() {
        return entityManager.createQuery("SELECT u FROM User u", User.class)
            .getResultList();
    }

    public User findById(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.find(User.class, id);
    }

    public void delete(Long id) {
        try {
            userTransaction.begin();
            User user = entityManager.find(User.class, id);
            if (user != null) {
                entityManager.remove(user);
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

    public User update(User user) {
        try {
            userTransaction.begin();
            User merged = entityManager.merge(user);
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

    public User findByUsername(String username) {
        return entityManager.createQuery(
            "SELECT u FROM User u WHERE u.username = :username", User.class)
            .setParameter("username", username)
            .getResultStream()
            .findFirst()
            .orElse(null);
    }
}