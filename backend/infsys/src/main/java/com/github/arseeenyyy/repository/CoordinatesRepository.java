package com.github.arseeenyyy.repository;


import java.util.List;

import com.github.arseeenyyy.models.Coordinates;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped 
public class CoordinatesRepository {

    @PersistenceContext 
    private EntityManager entityManager;

    public Coordinates save(Coordinates coordinates) {
        entityManager.persist(coordinates); 
        return coordinates;
    }

    public List<Coordinates> findAll() {
        return entityManager.createQuery("SELECT C FROM Coordinates C", Coordinates.class)
            .getResultList();
    }

    public Coordinates findById(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.find(Coordinates.class, id);
    }

    public void delete(Long id) {
        Coordinates coordinates = entityManager.find(Coordinates.class, id);
        if (coordinates != null) {
            entityManager.remove(coordinates);
        }
    }

    public Coordinates update(Coordinates coordinates) {
        return entityManager.merge(coordinates);
    }

}