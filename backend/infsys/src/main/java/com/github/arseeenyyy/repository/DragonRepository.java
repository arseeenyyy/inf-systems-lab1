package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.models.Color;
import com.github.arseeenyyy.models.Dragon;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import java.util.List;

@ApplicationScoped
public class DragonRepository extends GenericRepository<Dragon, Long> {
    
    public List<Dragon> findByColor(String color) {
        var em = getEntityManager();
        try {
            Color colorEnum = Color.valueOf(color.toUpperCase());
            TypedQuery<Dragon> query = em.createQuery(
                "SELECT d FROM Dragon d WHERE d.color = :color", Dragon.class);
            query.setParameter("color", colorEnum);
            return query.getResultList();
        } catch (IllegalArgumentException e) {
            return List.of();
        } finally {
            em.close();
        }
    }
    
    public List<Dragon> findByNameStartingWith(String substring) {
        var em = getEntityManager();
        try {
            TypedQuery<Dragon> query = em.createQuery(
                "SELECT d FROM Dragon d WHERE LOWER(d.name) LIKE LOWER(:substring)", Dragon.class);
            query.setParameter("substring", substring + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Dragon> findByCaveId(Long caveId) {
        var em = getEntityManager();
        try {
            TypedQuery<Dragon> query = em.createQuery(
                "SELECT d FROM Dragon d WHERE d.cave.id = :caveId", Dragon.class);
            query.setParameter("caveId", caveId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Dragon> findByKillerId(Long killerId) {
        var em = getEntityManager();
        try {
            TypedQuery<Dragon> query = em.createQuery(
                "SELECT d FROM Dragon d WHERE d.killer.id = :killerId", Dragon.class);
            query.setParameter("killerId", killerId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Dragon> findByHeadId(Long headId) {
        var em = getEntityManager();
        try {
            TypedQuery<Dragon> query = em.createQuery(
                "SELECT d FROM Dragon d WHERE d.head.id = :headId", Dragon.class);
            query.setParameter("headId", headId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Dragon> findByUserId(Long userId) {
        var em = getEntityManager();
        try {
            TypedQuery<Dragon> query = em.createQuery(
                "SELECT d FROM Dragon d WHERE d.user.id = :userId", Dragon.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}