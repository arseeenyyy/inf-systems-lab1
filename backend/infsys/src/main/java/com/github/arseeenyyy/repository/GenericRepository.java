package com.github.arseeenyyy.repository;

import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;
import com.github.arseeenyyy.config.DataSourceConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.lang.reflect.ParameterizedType;


public abstract class GenericRepository<T, ID> {
    private static EntityManagerFactory emf;
    private final Class<T> entityClass;

    static {
        try {
            DataSource dataSource = DataSourceConfig.getDataSource();
            Properties props = new Properties();
            props.put("jakarta.persistence.nonJtaDataSource", dataSource);
            emf = Persistence.createEntityManagerFactory("default", props);

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }
    @SuppressWarnings("unchecked")
    public GenericRepository() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass())
                .getActualTypeArguments()[0];
        System.out.println("repository created for entity: " + entityClass.getSimpleName());
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
        } catch (Exception exception) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw exception;
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
