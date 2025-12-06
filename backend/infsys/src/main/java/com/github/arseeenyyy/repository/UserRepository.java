package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.models.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;


@ApplicationScoped
public class UserRepository extends GenericRepository<User, Long> {
    
    public User findByUsername(String username) {
        var em = getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null; 
        } finally {
            em.close();
        }
    }
}