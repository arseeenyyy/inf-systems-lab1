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
            } catch (Exception ex) {
                throw new RuntimeException("Rollback failed", ex);
            }
            throw new RuntimeException("Save failed", e);
        }
    }

    public List<Coordinates> findAll() {
        return entityManager.createQuery("SELECT C FROM Coordinates C", Coordinates.class)
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
            } catch (Exception ex) {
                throw new RuntimeException("Rollback failed", ex);
            }
            throw new RuntimeException("Delete failed", e);
        }
    }

    public Coordinates update(Coordinates coordinates) {
        try {
            userTransaction.begin();
            Coordinates merged = entityManager.merge(coordinates);
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
}