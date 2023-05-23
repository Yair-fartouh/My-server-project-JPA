/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import controllers.exceptions.IllegalOrphanException;
import controllers.exceptions.NonexistentEntityException;
import entities.LastName;
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
public class LastNameJpaController implements Serializable {

    public LastNameJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(LastName lastName) {
        if (lastName.getUserCollection() == null) {
            lastName.setUserCollection(new ArrayList<User>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<User> attachedUserCollection = new ArrayList<User>();
            for (User userCollectionUserToAttach : lastName.getUserCollection()) {
                userCollectionUserToAttach = em.getReference(userCollectionUserToAttach.getClass(), userCollectionUserToAttach.getId());
                attachedUserCollection.add(userCollectionUserToAttach);
            }
            lastName.setUserCollection(attachedUserCollection);
            em.persist(lastName);
            for (User userCollectionUser : lastName.getUserCollection()) {
                LastName oldLastNameidOfUserCollectionUser = userCollectionUser.getLastNameid();
                userCollectionUser.setLastNameid(lastName);
                userCollectionUser = em.merge(userCollectionUser);
                if (oldLastNameidOfUserCollectionUser != null) {
                    oldLastNameidOfUserCollectionUser.getUserCollection().remove(userCollectionUser);
                    oldLastNameidOfUserCollectionUser = em.merge(oldLastNameidOfUserCollectionUser);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(LastName lastName) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            LastName persistentLastName = em.find(LastName.class, lastName.getId());
            Collection<User> userCollectionOld = persistentLastName.getUserCollection();
            Collection<User> userCollectionNew = lastName.getUserCollection();
            List<String> illegalOrphanMessages = null;
            for (User userCollectionOldUser : userCollectionOld) {
                if (!userCollectionNew.contains(userCollectionOldUser)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain User " + userCollectionOldUser + " since its lastNameid field is not nullable.");
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
            lastName.setUserCollection(userCollectionNew);
            lastName = em.merge(lastName);
            for (User userCollectionNewUser : userCollectionNew) {
                if (!userCollectionOld.contains(userCollectionNewUser)) {
                    LastName oldLastNameidOfUserCollectionNewUser = userCollectionNewUser.getLastNameid();
                    userCollectionNewUser.setLastNameid(lastName);
                    userCollectionNewUser = em.merge(userCollectionNewUser);
                    if (oldLastNameidOfUserCollectionNewUser != null && !oldLastNameidOfUserCollectionNewUser.equals(lastName)) {
                        oldLastNameidOfUserCollectionNewUser.getUserCollection().remove(userCollectionNewUser);
                        oldLastNameidOfUserCollectionNewUser = em.merge(oldLastNameidOfUserCollectionNewUser);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = lastName.getId();
                if (findLastName(id) == null) {
                    throw new NonexistentEntityException("The lastName with id " + id + " no longer exists.");
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
            LastName lastName;
            try {
                lastName = em.getReference(LastName.class, id);
                lastName.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The lastName with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<User> userCollectionOrphanCheck = lastName.getUserCollection();
            for (User userCollectionOrphanCheckUser : userCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This LastName (" + lastName + ") cannot be destroyed since the User " + userCollectionOrphanCheckUser + " in its userCollection field has a non-nullable lastNameid field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(lastName);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<LastName> findLastNameEntities() {
        return findLastNameEntities(true, -1, -1);
    }

    public List<LastName> findLastNameEntities(int maxResults, int firstResult) {
        return findLastNameEntities(false, maxResults, firstResult);
    }

    private List<LastName> findLastNameEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(LastName.class));
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

    public LastName findLastName(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(LastName.class, id);
        } finally {
            em.close();
        }
    }

    public int getLastNameCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<LastName> rt = cq.from(LastName.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
