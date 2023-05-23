/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import controllers.exceptions.IllegalOrphanException;
import controllers.exceptions.NonexistentEntityException;
import entities.Training;
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
public class TrainingJpaController implements Serializable {

    public TrainingJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Training training) {
        if (training.getVolunteerCollection() == null) {
            training.setVolunteerCollection(new ArrayList<Volunteer>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Volunteer> attachedVolunteerCollection = new ArrayList<Volunteer>();
            for (Volunteer volunteerCollectionVolunteerToAttach : training.getVolunteerCollection()) {
                volunteerCollectionVolunteerToAttach = em.getReference(volunteerCollectionVolunteerToAttach.getClass(), volunteerCollectionVolunteerToAttach.getId());
                attachedVolunteerCollection.add(volunteerCollectionVolunteerToAttach);
            }
            training.setVolunteerCollection(attachedVolunteerCollection);
            em.persist(training);
            for (Volunteer volunteerCollectionVolunteer : training.getVolunteerCollection()) {
                Training oldTrainingIdOfVolunteerCollectionVolunteer = volunteerCollectionVolunteer.getTrainingId();
                volunteerCollectionVolunteer.setTrainingId(training);
                volunteerCollectionVolunteer = em.merge(volunteerCollectionVolunteer);
                if (oldTrainingIdOfVolunteerCollectionVolunteer != null) {
                    oldTrainingIdOfVolunteerCollectionVolunteer.getVolunteerCollection().remove(volunteerCollectionVolunteer);
                    oldTrainingIdOfVolunteerCollectionVolunteer = em.merge(oldTrainingIdOfVolunteerCollectionVolunteer);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Training training) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Training persistentTraining = em.find(Training.class, training.getId());
            Collection<Volunteer> volunteerCollectionOld = persistentTraining.getVolunteerCollection();
            Collection<Volunteer> volunteerCollectionNew = training.getVolunteerCollection();
            List<String> illegalOrphanMessages = null;
            for (Volunteer volunteerCollectionOldVolunteer : volunteerCollectionOld) {
                if (!volunteerCollectionNew.contains(volunteerCollectionOldVolunteer)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Volunteer " + volunteerCollectionOldVolunteer + " since its trainingId field is not nullable.");
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
            training.setVolunteerCollection(volunteerCollectionNew);
            training = em.merge(training);
            for (Volunteer volunteerCollectionNewVolunteer : volunteerCollectionNew) {
                if (!volunteerCollectionOld.contains(volunteerCollectionNewVolunteer)) {
                    Training oldTrainingIdOfVolunteerCollectionNewVolunteer = volunteerCollectionNewVolunteer.getTrainingId();
                    volunteerCollectionNewVolunteer.setTrainingId(training);
                    volunteerCollectionNewVolunteer = em.merge(volunteerCollectionNewVolunteer);
                    if (oldTrainingIdOfVolunteerCollectionNewVolunteer != null && !oldTrainingIdOfVolunteerCollectionNewVolunteer.equals(training)) {
                        oldTrainingIdOfVolunteerCollectionNewVolunteer.getVolunteerCollection().remove(volunteerCollectionNewVolunteer);
                        oldTrainingIdOfVolunteerCollectionNewVolunteer = em.merge(oldTrainingIdOfVolunteerCollectionNewVolunteer);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = training.getId();
                if (findTraining(id) == null) {
                    throw new NonexistentEntityException("The training with id " + id + " no longer exists.");
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
            Training training;
            try {
                training = em.getReference(Training.class, id);
                training.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The training with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Volunteer> volunteerCollectionOrphanCheck = training.getVolunteerCollection();
            for (Volunteer volunteerCollectionOrphanCheckVolunteer : volunteerCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Training (" + training + ") cannot be destroyed since the Volunteer " + volunteerCollectionOrphanCheckVolunteer + " in its volunteerCollection field has a non-nullable trainingId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(training);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Training> findTrainingEntities() {
        return findTrainingEntities(true, -1, -1);
    }

    public List<Training> findTrainingEntities(int maxResults, int firstResult) {
        return findTrainingEntities(false, maxResults, firstResult);
    }

    private List<Training> findTrainingEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Training.class));
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

    public Training findTraining(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Training.class, id);
        } finally {
            em.close();
        }
    }

    public int getTrainingCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Training> rt = cq.from(Training.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
