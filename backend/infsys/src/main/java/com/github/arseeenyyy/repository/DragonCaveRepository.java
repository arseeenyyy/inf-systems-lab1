package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.models.Dragon;
import com.github.arseeenyyy.models.DragonCave;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import java.util.List;

@ApplicationScoped
public class DragonCaveRepository extends GenericRepository<DragonCave, Long> {
        
    @Override
    public void delete(Long id) {
        var em = getEntityManager();
        try {
            em.getTransaction().begin();
            
            TypedQuery<Dragon> dragonQuery = em.createQuery(
                "SELECT d FROM Dragon d WHERE d.cave.id = :caveId", Dragon.class);
            dragonQuery.setParameter("caveId", id);
            List<Dragon> dragons = dragonQuery.getResultList();
            
            for (Dragon dragon : dragons) {
                dragon.setCave(null);
                em.merge(dragon);
            }
            
            DragonCave cave = em.find(DragonCave.class, id);
            if (cave != null) {
                em.remove(cave);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}