package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.models.Dragon;
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
            } catch (Exception rollbackEx) {
                throw new RuntimeException("Failed to rollback transaction", rollbackEx);
            }
            throw new RuntimeException("Failed to save DragonCave", e);
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
                List<Dragon> dragonsWithThisCave = entityManager.createQuery(
                    "SELECT d FROM Dragon d WHERE d.cave.id = :caveId", Dragon.class)
                    .setParameter("caveId", id)
                    .getResultList();
                
                for (Dragon dragon : dragonsWithThisCave) {
                    dragon.setCave(null);
                    entityManager.merge(dragon);
                }
                entityManager.remove(cave);
            }
            userTransaction.commit();
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception rollbackEx) {
                throw new RuntimeException("Failed to rollback transaction", rollbackEx);
            }
            throw new RuntimeException("Failed to delete DragonCave", e);
        }
    }

    public DragonCave update(DragonCave cave) {
        try {
            userTransaction.begin();
            DragonCave updated = entityManager.merge(cave);
            userTransaction.commit();
            return updated;
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception rollbackEx) {
                throw new RuntimeException("Failed to rollback transaction", rollbackEx);
            }
            throw new RuntimeException("Failed to update DragonCave", e);
        }
    }

    public List<DragonCave> findByUserId(Long userId) {
        return entityManager.createQuery(
            "SELECT c FROM DragonCave c WHERE c.user.id = :userId", DragonCave.class)
            .setParameter("userId", userId)
            .getResultList();
    }
}