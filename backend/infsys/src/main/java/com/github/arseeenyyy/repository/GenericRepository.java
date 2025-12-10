package com.github.arseeenyyy.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import com.github.arseeenyyy.config.DatabaseManager;

@ApplicationScoped
public abstract class GenericRepository<T, ID> {
    
    private final Class<T> entityClass;
    
    @SuppressWarnings("unchecked")
    public GenericRepository() {
        Class<?> clazz = getClass();
        
        while (clazz != null && !(clazz.getGenericSuperclass() instanceof ParameterizedType)) {
            clazz = clazz.getSuperclass();
        }
        
        if (clazz != null) {
            ParameterizedType genericSuperclass = (ParameterizedType) clazz.getGenericSuperclass();
            this.entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
        } else {
            throw new IllegalStateException("Cannot determine entity class for repository");
        }
    }
    
    protected EntityManager getEntityManager() {
        return DatabaseManager.getEntityManager();
    }
    
    public T findById(ID id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(entityClass, id);
        } finally {
            em.close();
        }
    }
    
    public List<T> findAll() {
        EntityManager em = getEntityManager();
        try {
            // Кэшируем запрос
            return em.createQuery(
                "SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
        } finally {
            em.close();
        }
    }
    
    public T save(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return entity;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    public T update(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T merged = em.merge(entity);
            em.getTransaction().commit();
            return merged;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    public void delete(ID id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
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
    
    public long count() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e", Long.class)
                .getSingleResult();
        } finally {
            em.close();
        }
    }
}