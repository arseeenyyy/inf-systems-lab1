package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.models.Coordinates;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped 
public class CoordinatesRepository {

    @PersistenceContext 
    private EntityManager entityManager;

    @Transactional
    public Coordinates save(Coordinates coordinates) {
        entityManager.persist(coordinates);
        return coordinates;
    }

    public List<Coordinates> findAll() {
        return entityManager.createQuery("SELECT c FROM Coordinates c", Coordinates.class)
            .getResultList();
    }

    public Coordinates findById(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.find(Coordinates.class, id);
    }

    @Transactional
    public void delete(Long id) {
        Coordinates coordinates = entityManager.find(Coordinates.class, id);
        if (coordinates != null) {
            entityManager.remove(coordinates);
        }
    }

    @Transactional
    public Coordinates update(Coordinates coordinates) {
        return entityManager.merge(coordinates);
    }

    public List<Coordinates> findByUserId(Long userId) {
        return entityManager.createQuery(
            "SELECT c FROM Coordinates c WHERE c.user.id = :userId", Coordinates.class)
            .setParameter("userId", userId)
            .getResultList();
    }
}