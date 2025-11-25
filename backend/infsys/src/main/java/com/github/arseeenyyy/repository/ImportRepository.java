package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.models.ImportOperation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class ImportRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public ImportOperation save(ImportOperation op) {
        em.persist(op);
        return op;
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