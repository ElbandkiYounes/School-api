package com.school_1.api.User;

import com.school_1.api.User.models.User;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
@LocalBean
public class UserEJB {
    @PersistenceContext
    private EntityManager entityManager;

    public User saveUser(User user) {
        entityManager.persist(user);
        return user;
    }

    public User findUserByEmail(String email) {
        try {
            return entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public User findUserById(Long userId) {
        return entityManager.find(User.class, userId);
    }

    public List<User> findAllProfesseurs() {
        return entityManager.createQuery("SELECT u FROM User u WHERE u.role = 'PROFESSEUR'", User.class)
                .getResultList();
    }

    public User findUserByVerificationToken(String token) {
        try {
            return entityManager.createQuery("SELECT u FROM User u WHERE u.verificationToken = :token", User.class)
                    .setParameter("token", token)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void updateUser(User user) {
        entityManager.merge(user);
    }

}
