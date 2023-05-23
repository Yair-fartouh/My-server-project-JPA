/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import controllers.exceptions.IllegalOrphanException;
import controllers.exceptions.NonexistentEntityException;
import entities.Location;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entities.Volunteer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Yair
 */
public class LocationJpaController implements Serializable {

    public LocationJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Location location) {
        if (location.getVolunteerCollection() == null) {
            location.setVolunteerCollection(new ArrayList<Volunteer>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Volunteer> attachedVolunteerCollection = new ArrayList<Volunteer>();
            for (Volunteer volunteerCollectionVolunteerToAttach : location.getVolunteerCollection()) {
                volunteerCollectionVolunteerToAttach = em.getReference(volunteerCollectionVolunteerToAttach.getClass(), volunteerCollectionVolunteerToAttach.getId());
                attachedVolunteerCollection.add(volunteerCollectionVolunteerToAttach);
            }
            location.setVolunteerCollection(attachedVolunteerCollection);
            em.persist(location);
            for (Volunteer volunteerCollectionVolunteer : location.getVolunteerCollection()) {
                Location oldLocationIdOfVolunteerCollectionVolunteer = volunteerCollectionVolunteer.getLocationId();
                volunteerCollectionVolunteer.setLocationId(location);
                volunteerCollectionVolunteer = em.merge(volunteerCollectionVolunteer);
                if (oldLocationIdOfVolunteerCollectionVolunteer != null) {
                    oldLocationIdOfVolunteerCollectionVolunteer.getVolunteerCollection().remove(volunteerCollectionVolunteer);
                    oldLocationIdOfVolunteerCollectionVolunteer = em.merge(oldLocationIdOfVolunteerCollectionVolunteer);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Location location) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Location persistentLocation = em.find(Location.class, location.getId());
            Collection<Volunteer> volunteerCollectionOld = persistentLocation.getVolunteerCollection();
            Collection<Volunteer> volunteerCollectionNew = location.getVolunteerCollection();
            List<String> illegalOrphanMessages = null;
            for (Volunteer volunteerCollectionOldVolunteer : volunteerCollectionOld) {
                if (!volunteerCollectionNew.contains(volunteerCollectionOldVolunteer)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Volunteer " + volunteerCollectionOldVolunteer + " since its locationId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Volunteer> attachedVolunteerCollectionNew = new ArrayList<Volunteer>();
            for (Volunteer volunteerCollectionNewVolunteerToAttach : volunteerCollectionNew) {
                volunteerCollectionNewVolunteerToAttach = em.getReference(volunteerCollectionNewVolunteerToAttach.getClass(), volunteerCollectionNewVolunteerToAttach.getId());
                attachedVolunteerCollectionNew.add(volunteerCollectionNewVolunteerToAttach);
            }
            volunteerCollectionNew = attachedVolunteerCollectionNew;
            location.setVolunteerCollection(volunteerCollectionNew);
            location = em.merge(location);
            for (Volunteer volunteerCollectionNewVolunteer : volunteerCollectionNew) {
                if (!volunteerCollectionOld.contains(volunteerCollectionNewVolunteer)) {
                    Location oldLocationIdOfVolunteerCollectionNewVolunteer = volunteerCollectionNewVolunteer.getLocationId();
                    volunteerCollectionNewVolunteer.setLocationId(location);
                    volunteerCollectionNewVolunteer = em.merge(volunteerCollectionNewVolunteer);
                    if (oldLocationIdOfVolunteerCollectionNewVolunteer != null && !oldLocationIdOfVolunteerCollectionNewVolunteer.equals(location)) {
                        oldLocationIdOfVolunteerCollectionNewVolunteer.getVolunteerCollection().remove(volunteerCollectionNewVolunteer);
                        oldLocationIdOfVolunteerCollectionNewVolunteer = em.merge(oldLocationIdOfVolunteerCollectionNewVolunteer);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = location.getId();
                if (findLocation(id) == null) {
                    throw new NonexistentEntityException("The location with id " + id + " no longer exists.");
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
            Location location;
            try {
                location = em.getReference(Location.class, id);
                location.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The location with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Volunteer> volunteerCollectionOrphanCheck = location.getVolunteerCollection();
            for (Volunteer volunteerCollectionOrphanCheckVolunteer : volunteerCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Location (" + location + ") cannot be destroyed since the Volunteer " + volunteerCollectionOrphanCheckVolunteer + " in its volunteerCollection field has a non-nullable locationId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(location);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Location> findLocationEntities() {
        return findLocationEntities(true, -1, -1);
    }

    public List<Location> findLocationEntities(int maxResults, int firstResult) {
        return findLocationEntities(false, maxResults, firstResult);
    }

    private List<Location> findLocationEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Location.class));
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

    public Location findLocation(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Location.class, id);
        } finally {
            em.close();
        }
    }

    public int getLocationCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Location> rt = cq.from(Location.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
