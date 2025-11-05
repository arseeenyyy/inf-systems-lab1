package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.models.Location;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;
import java.util.List;

@ApplicationScoped
public class LocationRepository {
    
    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private UserTransaction userTransaction;
    
    public Location save(Location location) {
        try {
            userTransaction.begin();
            entityManager.persist(location);
            userTransaction.commit();
            return location;
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception ex) {
                throw new RuntimeException("Rollback failed", ex);
            }
            throw new RuntimeException("Save failed", e);
        }
    }
    
    public List<Location> findAll() {
        return entityManager.createQuery("SELECT l FROM Location l", Location.class)
                .getResultList();
    }
    

    public Location findById(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.find(Location.class, id);
    }
    

    public void delete(Long id) {
        try {
            userTransaction.begin();
            Location location = entityManager.find(Location.class, id);
            if (location != null) {
                entityManager.remove(location);
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
    
    public Location update(Location location) {
        try {
            userTransaction.begin();
            Location merged = entityManager.merge(location);
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