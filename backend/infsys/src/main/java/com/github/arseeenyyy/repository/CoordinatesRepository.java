package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.models.Coordinates;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;
import java.util.List;

@ApplicationScoped 
public class CoordinatesRepository {

    @PersistenceContext 
    private EntityManager entityManager;

    @Inject
    private UserTransaction userTransaction;

    public Coordinates save(Coordinates coordinates) {
        try {
            userTransaction.begin();
            entityManager.persist(coordinates);
            userTransaction.commit();
            return coordinates;
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception rollbackEx) {
                throw new RuntimeException("Failed to rollback transaction", rollbackEx);
            }
            throw new RuntimeException("Failed to save coordinates", e);
        }
    }

    public List<Coordinates> findAll() {
        return entityManager.createQuery("SELECT c FROM Coordinates c", Coordinates.class)
            .getResultList();
    }

    public Coordinates findById(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.find(Coordinates.class, id);
    }

    public void delete(Long id) {
        try {
            userTransaction.begin();
            Coordinates coordinates = entityManager.find(Coordinates.class, id);
            if (coordinates != null) {
                entityManager.remove(coordinates);
            }
            userTransaction.commit();
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception rollbackEx) {
                throw new RuntimeException("Failed to rollback transaction", rollbackEx);
            }
            throw new RuntimeException("Failed to delete coordinates", e);
        }
    }

    public Coordinates update(Coordinates coordinates) {
        try {
            userTransaction.begin();
            Coordinates updated = entityManager.merge(coordinates);
            userTransaction.commit();
            return updated;
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception rollbackEx) {
                throw new RuntimeException("Failed to rollback transaction", rollbackEx);
            }
            throw new RuntimeException("Failed to update coordinates", e);
        }
    }

    public List<Coordinates> findByUserId(Long userId) {
        return entityManager.createQuery(
            "SELECT c FROM Coordinates c WHERE c.user.id = :userId", Coordinates.class)
            .setParameter("userId", userId)
            .getResultList();
    }
}