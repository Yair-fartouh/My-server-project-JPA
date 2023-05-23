/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import controllers.exceptions.IllegalOrphanException;
import controllers.exceptions.NonexistentEntityException;
import entities.FirstName;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entities.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Yair
 */
public class FirstNameJpaController implements Serializable {

    public FirstNameJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(FirstName firstName) {
        if (firstName.getUserCollection() == null) {
            firstName.setUserCollection(new ArrayList<User>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<User> attachedUserCollection = new ArrayList<User>();
            for (User userCollectionUserToAttach : firstName.getUserCollection()) {
                userCollectionUserToAttach = em.getReference(userCollectionUserToAttach.getClass(), userCollectionUserToAttach.getId());
                attachedUserCollection.add(userCollectionUserToAttach);
            }
            firstName.setUserCollection(attachedUserCollection);
            em.persist(firstName);
            for (User userCollectionUser : firstName.getUserCollection()) {
                FirstName oldFirstNameidOfUserCollectionUser = userCollectionUser.getFirstNameid();
                userCollectionUser.setFirstNameid(firstName);
                userCollectionUser = em.merge(userCollectionUser);
                if (oldFirstNameidOfUserCollectionUser != null) {
                    oldFirstNameidOfUserCollectionUser.getUserCollection().remove(userCollectionUser);
                    oldFirstNameidOfUserCollectionUser = em.merge(oldFirstNameidOfUserCollectionUser);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(FirstName firstName) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            FirstName persistentFirstName = em.find(FirstName.class, firstName.getId());
            Collection<User> userCollectionOld = persistentFirstName.getUserCollection();
            Collection<User> userCollectionNew = firstName.getUserCollection();
            List<String> illegalOrphanMessages = null;
            for (User userCollectionOldUser : userCollectionOld) {
                if (!userCollectionNew.contains(userCollectionOldUser)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain User " + userCollectionOldUser + " since its firstNameid field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<User> attachedUserCollectionNew = new ArrayList<User>();
            for (User userCollectionNewUserToAttach : userCollectionNew) {
                userCollectionNewUserToAttach = em.getReference(userCollectionNewUserToAttach.getClass(), userCollectionNewUserToAttach.getId());
                attachedUserCollectionNew.add(userCollectionNewUserToAttach);
            }
            userCollectionNew = attachedUserCollectionNew;
            firstName.setUserCollection(userCollectionNew);
            firstName = em.merge(firstName);
            for (User userCollectionNewUser : userCollectionNew) {
                if (!userCollectionOld.contains(userCollectionNewUser)) {
                    FirstName oldFirstNameidOfUserCollectionNewUser = userCollectionNewUser.getFirstNameid();
                    userCollectionNewUser.setFirstNameid(firstName);
                    userCollectionNewUser = em.merge(userCollectionNewUser);
                    if (oldFirstNameidOfUserCollectionNewUser != null && !oldFirstNameidOfUserCollectionNewUser.equals(firstName)) {
                        oldFirstNameidOfUserCollectionNewUser.getUserCollection().remove(userCollectionNewUser);
                        oldFirstNameidOfUserCollectionNewUser = em.merge(oldFirstNameidOfUserCollectionNewUser);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = firstName.getId();
                if (findFirstName(id) == null) {
                    throw new NonexistentEntityException("The firstName with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            FirstName firstName;
            try {
                firstName = em.getReference(FirstName.class, id);
                firstName.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The firstName with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<User> userCollectionOrphanCheck = firstName.getUserCollection();
            for (User userCollectionOrphanCheckUser : userCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This FirstName (" + firstName + ") cannot be destroyed since the User " + userCollectionOrphanCheckUser + " in its userCollection field has a non-nullable firstNameid field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(firstName);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<FirstName> findFirstNameEntities() {
        return findFirstNameEntities(true, -1, -1);
    }

    public List<FirstName> findFirstNameEntities(int maxResults, int firstResult) {
        return findFirstNameEntities(false, maxResults, firstResult);
    }

    private List<FirstName> findFirstNameEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(FirstName.class));
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

    public FirstName findFirstName(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(FirstName.class, id);
        } finally {
            em.close();
        }
    }

    public int getFirstNameCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<FirstName> rt = cq.from(FirstName.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
