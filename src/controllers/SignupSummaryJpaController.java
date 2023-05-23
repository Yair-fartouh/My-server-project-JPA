/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import controllers.exceptions.NonexistentEntityException;
import entities.SignupSummary;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Yair
 */
public class SignupSummaryJpaController implements Serializable {

    public SignupSummaryJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(SignupSummary signupSummary) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(signupSummary);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(SignupSummary signupSummary) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            signupSummary = em.merge(signupSummary);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = signupSummary.getId();
                if (findSignupSummary(id) == null) {
                    throw new NonexistentEntityException("The signupSummary with id " + id + " no longer exists.");
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
            SignupSummary signupSummary;
            try {
                signupSummary = em.getReference(SignupSummary.class, id);
                signupSummary.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The signupSummary with id " + id + " no longer exists.", enfe);
            }
            em.remove(signupSummary);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<SignupSummary> findSignupSummaryEntities() {
        return findSignupSummaryEntities(true, -1, -1);
    }

    public List<SignupSummary> findSignupSummaryEntities(int maxResults, int firstResult) {
        return findSignupSummaryEntities(false, maxResults, firstResult);
    }

    private List<SignupSummary> findSignupSummaryEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(SignupSummary.class));
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

    public SignupSummary findSignupSummary(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SignupSummary.class, id);
        } finally {
            em.close();
        }
    }

    public int getSignupSummaryCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<SignupSummary> rt = cq.from(SignupSummary.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
