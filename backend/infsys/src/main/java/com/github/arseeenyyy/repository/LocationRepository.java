package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.models.Location;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import java.util.List;

@ApplicationScoped
public class LocationRepository extends GenericRepository<Location, Long> {
    
    public List<Location> findByUserId(Long userId) {
        var em = getEntityManager();
        try {
            TypedQuery<Location> query = em.createQuery(
                "SELECT l FROM Location l WHERE l.user.id = :userId", Location.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}