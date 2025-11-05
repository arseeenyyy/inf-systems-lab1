package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.models.DragonCave;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;
import java.util.List;

@ApplicationScoped 
public class DragonCaveRepository {
    
    @PersistenceContext 
    private EntityManager entityManager;

    @Inject
    private UserTransaction userTransaction;

    public DragonCave save(DragonCave cave) {
        try {
            userTransaction.begin();
            entityManager.persist(cave);
            userTransaction.commit();
            return cave;
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception ex) {
                throw new RuntimeException("Rollback failed", ex);
            }
            throw new RuntimeException("Save failed", e);
        }
    } 

    public List<DragonCave> findAll() {
        return entityManager.createQuery("SELECT c FROM DragonCave c", DragonCave.class)
            .getResultList();
    }

    public DragonCave findById(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.find(DragonCave.class, id);
    }

    public void delete(Long id) {
        try {
            userTransaction.begin();
            DragonCave cave = entityManager.find(DragonCave.class, id);
            if (cave != null) {
                entityManager.remove(cave);
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

    public DragonCave update(DragonCave cave) {
        try {
            userTransaction.begin();
            DragonCave merged = entityManager.merge(cave);
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