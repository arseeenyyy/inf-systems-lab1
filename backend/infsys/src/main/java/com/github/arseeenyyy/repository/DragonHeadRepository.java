package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.models.DragonHead;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;
import java.util.List;

@ApplicationScoped 
public class DragonHeadRepository {
    
    @PersistenceContext 
    private EntityManager entityManager;

    @Inject
    private UserTransaction userTransaction;

    public DragonHead save(DragonHead head) {
        try {
            userTransaction.begin();
            entityManager.persist(head);
            userTransaction.commit();
            return head;
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception rollbackEx) {
                throw new RuntimeException("Failed to rollback transaction", rollbackEx);
            }
            throw new RuntimeException("Failed to save DragonHead", e);
        }
    }

    public List<DragonHead> findAll() {
        return entityManager.createQuery("SELECT h FROM DragonHead h", DragonHead.class)
            .getResultList();
    }

    public DragonHead findById(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.find(DragonHead.class, id);
    }

    public void delete(Long id) {
        try {
            userTransaction.begin();
            DragonHead head = entityManager.find(DragonHead.class, id);
            if (head != null) {
                entityManager.remove(head);
            }
            userTransaction.commit();
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception rollbackEx) {
                throw new RuntimeException("Failed to rollback transaction", rollbackEx);
            }
            throw new RuntimeException("Failed to delete DragonHead", e);
        }
    }

    public DragonHead update(DragonHead head) {
        try {
            userTransaction.begin();
            DragonHead updated = entityManager.merge(head);
            userTransaction.commit();
            return updated;
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception rollbackEx) {
                throw new RuntimeException("Failed to rollback transaction", rollbackEx);
            }
            throw new RuntimeException("Failed to update DragonHead", e);
        }
    }

    public List<DragonHead> findByUserId(Long userId) {
        return entityManager.createQuery(
            "SELECT h FROM DragonHead h WHERE h.user.id = :userId", DragonHead.class)
            .setParameter("userId", userId)
            .getResultList();
    }
}