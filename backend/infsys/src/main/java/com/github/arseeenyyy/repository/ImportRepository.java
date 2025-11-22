package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.models.ImportOperation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class ImportRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(ImportOperation op) {
        em.persist(op);
    }

    public List<ImportOperation> findAllPaged(int page, int size) {
        return em.createQuery(
                        "SELECT i FROM ImportOperation i ORDER BY i.timestamp DESC",
                        ImportOperation.class
                )
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<ImportOperation> findByUserIdPaged(Long userId, int page, int size) {
        return em.createQuery(
                        "SELECT i FROM ImportOperation i WHERE i.user.id = :userId ORDER BY i.timestamp DESC",
                        ImportOperation.class
                )
                .setParameter("userId", userId)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long countAll() {
        return em.createQuery("SELECT COUNT(i) FROM ImportOperation i", Long.class)
                .getSingleResult();
    }

    public long countByUserId(Long userId) {
        return em.createQuery("SELECT COUNT(i) FROM ImportOperation i WHERE i.user.id = :userId", Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }
}