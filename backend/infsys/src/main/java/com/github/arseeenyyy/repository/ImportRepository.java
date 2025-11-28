package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.models.ImportOperation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;
import java.util.List;

@ApplicationScoped
public class ImportRepository {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction userTransaction;

    public ImportOperation save(ImportOperation op) {
        try {
            userTransaction.begin();
            em.persist(op);
            userTransaction.commit();
            return op;
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception rollbackEx) {
                throw new RuntimeException("Failed to rollback transaction", rollbackEx);
            }
            throw new RuntimeException("Failed to save ImportOperation", e);
        }
    }

    public List<ImportOperation> findByUserId(Long userId) {
        return em.createQuery(
                "SELECT i FROM ImportOperation i WHERE i.userId = :userId ORDER BY i.id DESC",
                ImportOperation.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<ImportOperation> findAll() {
        return em.createQuery(
                "SELECT i FROM ImportOperation i ORDER BY i.id DESC",
                ImportOperation.class)
                .getResultList();
    }
}