package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.models.Coordinates;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import java.util.List;

@ApplicationScoped
public class CoordinatesRepository extends GenericRepository<Coordinates, Long> {
    
    public List<Coordinates> findByUserId(Long userId) {
        var em = getEntityManager();
        try {
            TypedQuery<Coordinates> query = em.createQuery(
                "SELECT c FROM Coordinates c WHERE c.user.id = :userId", Coordinates.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Coordinates> findByXGreaterThan(double x) {
        var em = getEntityManager();
        try {
            TypedQuery<Coordinates> query = em.createQuery(
                "SELECT c FROM Coordinates c WHERE c.x > :x", Coordinates.class);
            query.setParameter("x", x);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Coordinates> findByYLessThan(double y) {
        var em = getEntityManager();
        try {
            TypedQuery<Coordinates> query = em.createQuery(
                "SELECT c FROM Coordinates c WHERE c.y < :y", Coordinates.class);
            query.setParameter("y", y);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}