/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import controllers.exceptions.NonexistentEntityException;
import entities.LoginAttempts;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entities.User;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Yair
 */
public class LoginAttemptsJpaController implements Serializable {

    public LoginAttemptsJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(LoginAttempts loginAttempts) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User userId = loginAttempts.getUserId();
            if (userId != null) {
                userId = em.getReference(userId.getClass(), userId.getId());
                loginAttempts.setUserId(userId);
            }
            em.persist(loginAttempts);
            if (userId != null) {
                userId.getLoginAttemptsCollection().add(loginAttempts);
                userId = em.merge(userId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(LoginAttempts loginAttempts) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            LoginAttempts persistentLoginAttempts = em.find(LoginAttempts.class, loginAttempts.getId());
            User userIdOld = persistentLoginAttempts.getUserId();
            User userIdNew = loginAttempts.getUserId();
            if (userIdNew != null) {
                userIdNew = em.getReference(userIdNew.getClass(), userIdNew.getId());
                loginAttempts.setUserId(userIdNew);
            }
            loginAttempts = em.merge(loginAttempts);
            if (userIdOld != null && !userIdOld.equals(userIdNew)) {
                userIdOld.getLoginAttemptsCollection().remove(loginAttempts);
                userIdOld = em.merge(userIdOld);
            }
            if (userIdNew != null && !userIdNew.equals(userIdOld)) {
                userIdNew.getLoginAttemptsCollection().add(loginAttempts);
                userIdNew = em.merge(userIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = loginAttempts.getId();
                if (findLoginAttempts(id) == null) {
                    throw new NonexistentEntityException("The loginAttempts with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            LoginAttempts loginAttempts;
            try {
                loginAttempts = em.getReference(LoginAttempts.class, id);
                loginAttempts.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The loginAttempts with id " + id + " no longer exists.", enfe);
            }
            User userId = loginAttempts.getUserId();
            if (userId != null) {
                userId.getLoginAttemptsCollection().remove(loginAttempts);
                userId = em.merge(userId);
            }
            em.remove(loginAttempts);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<LoginAttempts> findLoginAttemptsEntities() {
        return findLoginAttemptsEntities(true, -1, -1);
    }

    public List<LoginAttempts> findLoginAttemptsEntities(int maxResults, int firstResult) {
        return findLoginAttemptsEntities(false, maxResults, firstResult);
    }

    private List<LoginAttempts> findLoginAttemptsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(LoginAttempts.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public LoginAttempts findLoginAttempts(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(LoginAttempts.class, id);
        } finally {
            em.close();
        }
    }

    public int getLoginAttemptsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<LoginAttempts> rt = cq.from(LoginAttempts.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
