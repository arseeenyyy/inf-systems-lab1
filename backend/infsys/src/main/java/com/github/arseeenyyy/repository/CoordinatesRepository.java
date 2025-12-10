package com.github.arseeenyyy.repository;

import java.util.List;

import com.github.arseeenyyy.models.Coordinates;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class CoordinatesRepository extends GenericRepository<Coordinates, Long> {
    
    public List<Coordinates> findByUserId(Long userId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Coordinates> query = em.createQuery(
                "SELECT c FROM Coordinates c WHERE c.user.id = :userId", Coordinates.class);
            query.setParameter("userId", userId);
            query.setHint("org.hibernate.cacheable", true); 
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Coordinates> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Coordinates c", Coordinates.class)
                    .setHint("org.hibernate.cacheable", true) 
                    .getResultList();
        } finally {
            em.close();
        }
    }
}