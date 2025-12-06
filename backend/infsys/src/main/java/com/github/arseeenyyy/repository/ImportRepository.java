package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.models.ImportOperation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import java.util.List;

@ApplicationScoped
public class ImportRepository extends GenericRepository<ImportOperation, Long> {
    
    public List<ImportOperation> findByUserId(Long userId) {
        var em = getEntityManager();
        try {
            TypedQuery<ImportOperation> query = em.createQuery(
                "SELECT i FROM ImportOperation i WHERE i.userId = :userId ORDER BY i.id DESC", 
                ImportOperation.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<ImportOperation> findAll() {
        var em = getEntityManager();
        try {
            TypedQuery<ImportOperation> query = em.createQuery(
                "SELECT i FROM ImportOperation i ORDER BY i.id DESC", 
                ImportOperation.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}