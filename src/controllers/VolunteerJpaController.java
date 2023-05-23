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
import entities.Location;
import entities.Training;
import entities.User;
import entities.Volunteer;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Yair
 */
public class VolunteerJpaController implements Serializable {

    public VolunteerJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Volunteer volunteer) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Location locationId = volunteer.getLocationId();
            if (locationId != null) {
                locationId = em.getReference(locationId.getClass(), locationId.getId());
                volunteer.setLocationId(locationId);
            }
            Training trainingId = volunteer.getTrainingId();
            if (trainingId != null) {
                trainingId = em.getReference(trainingId.getClass(), trainingId.getId());
                volunteer.setTrainingId(trainingId);
            }
            User userId = volunteer.getUserId();
            if (userId != null) {
                userId = em.getReference(userId.getClass(), userId.getId());
                volunteer.setUserId(userId);
            }
            em.persist(volunteer);
            if (locationId != null) {
                locationId.getVolunteerCollection().add(volunteer);
                locationId = em.merge(locationId);
            }
            if (trainingId != null) {
                trainingId.getVolunteerCollection().add(volunteer);
                trainingId = em.merge(trainingId);
            }
            if (userId != null) {
                userId.getVolunteerCollection().add(volunteer);
                userId = em.merge(userId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Volunteer volunteer) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Volunteer persistentVolunteer = em.find(Volunteer.class, volunteer.getId());
            Location locationIdOld = persistentVolunteer.getLocationId();
            Location locationIdNew = volunteer.getLocationId();
            Training trainingIdOld = persistentVolunteer.getTrainingId();
            Training trainingIdNew = volunteer.getTrainingId();
            User userIdOld = persistentVolunteer.getUserId();
            User userIdNew = volunteer.getUserId();
            if (locationIdNew != null) {
                locationIdNew = em.getReference(locationIdNew.getClass(), locationIdNew.getId());
                volunteer.setLocationId(locationIdNew);
            }
            if (trainingIdNew != null) {
                trainingIdNew = em.getReference(trainingIdNew.getClass(), trainingIdNew.getId());
                volunteer.setTrainingId(trainingIdNew);
            }
            if (userIdNew != null) {
                userIdNew = em.getReference(userIdNew.getClass(), userIdNew.getId());
                volunteer.setUserId(userIdNew);
            }
            volunteer = em.merge(volunteer);
            if (locationIdOld != null && !locationIdOld.equals(locationIdNew)) {
                locationIdOld.getVolunteerCollection().remove(volunteer);
                locationIdOld = em.merge(locationIdOld);
            }
            if (locationIdNew != null && !locationIdNew.equals(locationIdOld)) {
                locationIdNew.getVolunteerCollection().add(volunteer);
                locationIdNew = em.merge(locationIdNew);
            }
            if (trainingIdOld != null && !trainingIdOld.equals(trainingIdNew)) {
                trainingIdOld.getVolunteerCollection().remove(volunteer);
                trainingIdOld = em.merge(trainingIdOld);
            }
            if (trainingIdNew != null && !trainingIdNew.equals(trainingIdOld)) {
                trainingIdNew.getVolunteerCollection().add(volunteer);
                trainingIdNew = em.merge(trainingIdNew);
            }
            if (userIdOld != null && !userIdOld.equals(userIdNew)) {
                userIdOld.getVolunteerCollection().remove(volunteer);
                userIdOld = em.merge(userIdOld);
            }
            if (userIdNew != null && !userIdNew.equals(userIdOld)) {
                userIdNew.getVolunteerCollection().add(volunteer);
                userIdNew = em.merge(userIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = volunteer.getId();
                if (findVolunteer(id) == null) {
                    throw new NonexistentEntityException("The volunteer with id " + id + " no longer exists.");
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
            Volunteer volunteer;
            try {
                volunteer = em.getReference(Volunteer.class, id);
                volunteer.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The volunteer with id " + id + " no longer exists.", enfe);
            }
            Location locationId = volunteer.getLocationId();
            if (locationId != null) {
                locationId.getVolunteerCollection().remove(volunteer);
                locationId = em.merge(locationId);
            }
            Training trainingId = volunteer.getTrainingId();
            if (trainingId != null) {
                trainingId.getVolunteerCollection().remove(volunteer);
                trainingId = em.merge(trainingId);
            }
            User userId = volunteer.getUserId();
            if (userId != null) {
                userId.getVolunteerCollection().remove(volunteer);
                userId = em.merge(userId);
            }
            em.remove(volunteer);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Volunteer> findVolunteerEntities() {
        return findVolunteerEntities(true, -1, -1);
    }

    public List<Volunteer> findVolunteerEntities(int maxResults, int firstResult) {
        return findVolunteerEntities(false, maxResults, firstResult);
    }

    private List<Volunteer> findVolunteerEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Volunteer.class));
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

    public Volunteer findVolunteer(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Volunteer.class, id);
        } finally {
            em.close();
        }
    }

    public int getVolunteerCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Volunteer> rt = cq.from(Volunteer.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
