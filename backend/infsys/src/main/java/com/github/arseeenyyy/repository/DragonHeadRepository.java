package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.models.DragonHead;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped 
public class DragonHeadRepository {
    
    @PersistenceContext 
    private EntityManager entityManager;

    @Transactional 
    public DragonHead save(DragonHead head) {
        entityManager.persist(head); 
        return head;
    } 

    public List<DragonHead> findAll() {
        return entityManager.createQuery("SELECT h FROM DragonHead h", DragonHead.class)
            .getResultList();
    }

    public DragonHead findById(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.find(DragonHead.class, id);
    }


    @Transactional 
    public void delete(Long id) {
        DragonHead head = entityManager.find(DragonHead.class, id);
        if (head != null) {
            entityManager.remove(head);
        }
    }

    @Transactional
    public DragonHead update(DragonHead head) {
        return entityManager.merge(head);
    }
}