/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import controllers.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entities.User;
import entities.UserPermission;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Yair
 */
public class UserPermissionJpaController implements Serializable {

    public UserPermissionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(UserPermission userPermission) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User userId = userPermission.getUserId();
            if (userId != null) {
                userId = em.getReference(userId.getClass(), userId.getId());
                userPermission.setUserId(userId);
            }
            em.persist(userPermission);
            if (userId != null) {
                userId.getUserPermissionCollection().add(userPermission);
                userId = em.merge(userId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(UserPermission userPermission) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserPermission persistentUserPermission = em.find(UserPermission.class, userPermission.getId());
            User userIdOld = persistentUserPermission.getUserId();
            User userIdNew = userPermission.getUserId();
            if (userIdNew != null) {
                userIdNew = em.getReference(userIdNew.getClass(), userIdNew.getId());
                userPermission.setUserId(userIdNew);
            }
            userPermission = em.merge(userPermission);
            if (userIdOld != null && !userIdOld.equals(userIdNew)) {
                userIdOld.getUserPermissionCollection().remove(userPermission);
                userIdOld = em.merge(userIdOld);
            }
            if (userIdNew != null && !userIdNew.equals(userIdOld)) {
                userIdNew.getUserPermissionCollection().add(userPermission);
                userIdNew = em.merge(userIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = userPermission.getId();
                if (findUserPermission(id) == null) {
                    throw new NonexistentEntityException("The userPermission with id " + id + " no longer exists.");
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
            UserPermission userPermission;
            try {
                userPermission = em.getReference(UserPermission.class, id);
                userPermission.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The userPermission with id " + id + " no longer exists.", enfe);
            }
            User userId = userPermission.getUserId();
            if (userId != null) {
                userId.getUserPermissionCollection().remove(userPermission);
                userId = em.merge(userId);
            }
            em.remove(userPermission);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<UserPermission> findUserPermissionEntities() {
        return findUserPermissionEntities(true, -1, -1);
    }

    public List<UserPermission> findUserPermissionEntities(int maxResults, int firstResult) {
        return findUserPermissionEntities(false, maxResults, firstResult);
    }

    private List<UserPermission> findUserPermissionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(UserPermission.class));
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

    public UserPermission findUserPermission(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(UserPermission.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserPermissionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<UserPermission> rt = cq.from(UserPermission.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
