/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import controllers.exceptions.IllegalOrphanException;
import controllers.exceptions.NonexistentEntityException;
import entities.Phone;
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
public class PhoneJpaController implements Serializable {

    public PhoneJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Phone phone) {
        if (phone.getUserCollection() == null) {
            phone.setUserCollection(new ArrayList<User>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<User> attachedUserCollection = new ArrayList<User>();
            for (User userCollectionUserToAttach : phone.getUserCollection()) {
                userCollectionUserToAttach = em.getReference(userCollectionUserToAttach.getClass(), userCollectionUserToAttach.getId());
                attachedUserCollection.add(userCollectionUserToAttach);
            }
            phone.setUserCollection(attachedUserCollection);
            em.persist(phone);
            for (User userCollectionUser : phone.getUserCollection()) {
                Phone oldPhoneIdOfUserCollectionUser = userCollectionUser.getPhoneId();
                userCollectionUser.setPhoneId(phone);
                userCollectionUser = em.merge(userCollectionUser);
                if (oldPhoneIdOfUserCollectionUser != null) {
                    oldPhoneIdOfUserCollectionUser.getUserCollection().remove(userCollectionUser);
                    oldPhoneIdOfUserCollectionUser = em.merge(oldPhoneIdOfUserCollectionUser);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Phone phone) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Phone persistentPhone = em.find(Phone.class, phone.getId());
            Collection<User> userCollectionOld = persistentPhone.getUserCollection();
            Collection<User> userCollectionNew = phone.getUserCollection();
            List<String> illegalOrphanMessages = null;
            for (User userCollectionOldUser : userCollectionOld) {
                if (!userCollectionNew.contains(userCollectionOldUser)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain User " + userCollectionOldUser + " since its phoneId field is not nullable.");
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
            phone.setUserCollection(userCollectionNew);
            phone = em.merge(phone);
            for (User userCollectionNewUser : userCollectionNew) {
                if (!userCollectionOld.contains(userCollectionNewUser)) {
                    Phone oldPhoneIdOfUserCollectionNewUser = userCollectionNewUser.getPhoneId();
                    userCollectionNewUser.setPhoneId(phone);
                    userCollectionNewUser = em.merge(userCollectionNewUser);
                    if (oldPhoneIdOfUserCollectionNewUser != null && !oldPhoneIdOfUserCollectionNewUser.equals(phone)) {
                        oldPhoneIdOfUserCollectionNewUser.getUserCollection().remove(userCollectionNewUser);
                        oldPhoneIdOfUserCollectionNewUser = em.merge(oldPhoneIdOfUserCollectionNewUser);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = phone.getId();
                if (findPhone(id) == null) {
                    throw new NonexistentEntityException("The phone with id " + id + " no longer exists.");
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
            Phone phone;
            try {
                phone = em.getReference(Phone.class, id);
                phone.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The phone with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<User> userCollectionOrphanCheck = phone.getUserCollection();
            for (User userCollectionOrphanCheckUser : userCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Phone (" + phone + ") cannot be destroyed since the User " + userCollectionOrphanCheckUser + " in its userCollection field has a non-nullable phoneId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(phone);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Phone> findPhoneEntities() {
        return findPhoneEntities(true, -1, -1);
    }

    public List<Phone> findPhoneEntities(int maxResults, int firstResult) {
        return findPhoneEntities(false, maxResults, firstResult);
    }

    private List<Phone> findPhoneEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Phone.class));
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

    public Phone findPhone(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Phone.class, id);
        } finally {
            em.close();
        }
    }

    public int getPhoneCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Phone> rt = cq.from(Phone.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
