package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.config.DataSourceConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javax.sql.DataSource;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Properties;

public abstract class GenericRepository<T, ID> {
    private static EntityManagerFactory emf;
    private final Class<T> entityClass;

    static {
        try {
            DataSource dataSource = DataSourceConfig.getDataSource();
            Properties props = new Properties();
            props.put("jakarta.persistence.nonJtaDataSource", dataSource);
            emf = Persistence.createEntityManagerFactory("default", props);
            
        } catch (Exception e) {
            System.out.println("Error initializing EntityManagerFactory: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("JPA initialization failed", e);
        }
    }

    @SuppressWarnings("unchecked")
    public GenericRepository() {
        Class<?> clazz = getClass();
        while (clazz != null && !(clazz.getGenericSuperclass() instanceof ParameterizedType)) {
            clazz = clazz.getSuperclass();
        }
        
        if (clazz != null && clazz.getGenericSuperclass() instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) clazz.getGenericSuperclass();
            this.entityClass = (Class<T>) type.getActualTypeArguments()[0];
        } else {
            this.entityClass = (Class<T>) Object.class;
        }
    }

    protected EntityManager getEntityManager() {
        return emf.createEntityManager();
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
            return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public T update(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T updated = em.merge(entity);
            em.getTransaction().commit();
            return updated;
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
            return em.createQuery("SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e", Long.class)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
}