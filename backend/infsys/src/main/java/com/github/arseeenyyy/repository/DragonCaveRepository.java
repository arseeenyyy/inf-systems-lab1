package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.models.DragonCave;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped 
public class DragonCaveRepository {
    
    @PersistenceContext 
    private EntityManager entityManager;

    @Transactional 
    public DragonCave save(DragonCave cave) {
        entityManager.persist(cave); 
        return cave;
    } 

    public List<DragonCave> findAll() {
        return entityManager.createQuery("SELECT c FROM DragonCave c", DragonCave.class)
            .getResultList();
    }

    public DragonCave findById(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.find(DragonCave.class, id);
    }
    @Transactional 
    public void delete(Long id) {
        DragonCave cave = entityManager.find(DragonCave.class, id);
        if (cave != null) {
            entityManager.remove(cave);
        }
    }

    @Transactional
    public DragonCave update(DragonCave cave) {
        return entityManager.merge(cave);
    }
}