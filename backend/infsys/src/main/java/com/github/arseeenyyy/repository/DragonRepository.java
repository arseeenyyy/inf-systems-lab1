package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.models.Color;
import com.github.arseeenyyy.models.Dragon;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped 
public class DragonRepository {
    
    @PersistenceContext 
    private EntityManager entityManager;

    @Transactional
    public Dragon save(Dragon dragon) {
        entityManager.persist(dragon);
        return dragon;
    } 

    public List<Dragon> findAll() {
        return entityManager.createQuery("SELECT d FROM Dragon d", Dragon.class)
            .getResultList();
    }

    public Dragon findById(Long id) {
        return entityManager.find(Dragon.class, id);
    }

    @Transactional
    public void delete(Long id) {
        Dragon dragon = entityManager.find(Dragon.class, id);
        if (dragon != null) {
            entityManager.remove(dragon);
        }
    }

    @Transactional
    public Dragon update(Dragon dragon) {
        return entityManager.merge(dragon);
    }

    public List<Dragon> findByColor(String color) {
        try {
            Color colorEnum = Color.valueOf(color.toUpperCase());
            return entityManager.createQuery(
                "SELECT d FROM Dragon d WHERE d.color = :color", Dragon.class)
                .setParameter("color", colorEnum)
                .getResultList();
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    public List<Dragon> findByNameStartingWith(String substring) {
        return entityManager.createQuery(
            "SELECT d FROM Dragon d WHERE LOWER(d.name) LIKE LOWER(:substring)", Dragon.class)
            .setParameter("substring", substring + "%")
            .getResultList();
    }

    public List<Dragon> findByCaveId(Long caveId) {
        return entityManager.createQuery(
            "SELECT d FROM Dragon d WHERE d.cave.id = :caveId", Dragon.class)
            .setParameter("caveId", caveId)
            .getResultList();
    }
    
    public List<Dragon> findByKillerId(Long killerId) {
        return entityManager.createQuery(
            "SELECT d FROM Dragon d WHERE d.killer.id = :killerId", Dragon.class)
            .setParameter("killerId", killerId)
            .getResultList();
    }
    
    public List<Dragon> findByHeadId(Long headId) {
        return entityManager.createQuery(
            "SELECT d FROM Dragon d WHERE d.head.id = :headId", Dragon.class)
            .setParameter("headId", headId)
            .getResultList();
    }

    public List<Dragon> findByUserId(Long userId) {
        return entityManager.createQuery(
            "SELECT d FROM Dragon d WHERE d.user.id = :userId", Dragon.class)
            .setParameter("userId", userId)
            .getResultList();
    }
}