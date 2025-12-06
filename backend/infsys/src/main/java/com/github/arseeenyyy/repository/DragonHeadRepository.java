package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.models.DragonHead;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import java.util.List;

@ApplicationScoped
public class DragonHeadRepository extends GenericRepository<DragonHead, Long> {
    
    public List<DragonHead> findByUserId(Long userId) {
        var em = getEntityManager();
        try {
            TypedQuery<DragonHead> query = em.createQuery(
                "SELECT h FROM DragonHead h WHERE h.user.id = :userId", DragonHead.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}