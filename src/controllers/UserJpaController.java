/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import controllers.exceptions.IllegalOrphanException;
import controllers.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entities.Address;
import entities.Email;
import entities.FirstName;
import entities.LastName;
import entities.Password;
import entities.Phone;
import entities.UserPermission;
import java.util.ArrayList;
import java.util.Collection;
import entities.LoginAttempts;
import entities.User;
import entities.Volunteer;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Yair
 */
public class UserJpaController implements Serializable {

    public UserJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(User user) {
        if (user.getUserPermissionCollection() == null) {
            user.setUserPermissionCollection(new ArrayList<UserPermission>());
        }
        if (user.getLoginAttemptsCollection() == null) {
            user.setLoginAttemptsCollection(new ArrayList<LoginAttempts>());
        }
        if (user.getVolunteerCollection() == null) {
            user.setVolunteerCollection(new ArrayList<Volunteer>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Address addressId = user.getAddressId();
            if (addressId != null) {
                addressId = em.getReference(addressId.getClass(), addressId.getId());
                user.setAddressId(addressId);
            }
            Email emailId = user.getEmailId();
            if (emailId != null) {
                emailId = em.getReference(emailId.getClass(), emailId.getId());
                user.setEmailId(emailId);
            }
            FirstName firstNameid = user.getFirstNameid();
            if (firstNameid != null) {
                firstNameid = em.getReference(firstNameid.getClass(), firstNameid.getId());
                user.setFirstNameid(firstNameid);
            }
            LastName lastNameid = user.getLastNameid();
            if (lastNameid != null) {
                lastNameid = em.getReference(lastNameid.getClass(), lastNameid.getId());
                user.setLastNameid(lastNameid);
            }
            Password passwordId = user.getPasswordId();
            if (passwordId != null) {
                passwordId = em.getReference(passwordId.getClass(), passwordId.getId());
                user.setPasswordId(passwordId);
            }
            Phone phoneId = user.getPhoneId();
            if (phoneId != null) {
                phoneId = em.getReference(phoneId.getClass(), phoneId.getId());
                user.setPhoneId(phoneId);
            }
            Collection<UserPermission> attachedUserPermissionCollection = new ArrayList<UserPermission>();
            for (UserPermission userPermissionCollectionUserPermissionToAttach : user.getUserPermissionCollection()) {
                userPermissionCollectionUserPermissionToAttach = em.getReference(userPermissionCollectionUserPermissionToAttach.getClass(), userPermissionCollectionUserPermissionToAttach.getId());
                attachedUserPermissionCollection.add(userPermissionCollectionUserPermissionToAttach);
            }
            user.setUserPermissionCollection(attachedUserPermissionCollection);
            Collection<LoginAttempts> attachedLoginAttemptsCollection = new ArrayList<LoginAttempts>();
            for (LoginAttempts loginAttemptsCollectionLoginAttemptsToAttach : user.getLoginAttemptsCollection()) {
                loginAttemptsCollectionLoginAttemptsToAttach = em.getReference(loginAttemptsCollectionLoginAttemptsToAttach.getClass(), loginAttemptsCollectionLoginAttemptsToAttach.getId());
                attachedLoginAttemptsCollection.add(loginAttemptsCollectionLoginAttemptsToAttach);
            }
            user.setLoginAttemptsCollection(attachedLoginAttemptsCollection);
            Collection<Volunteer> attachedVolunteerCollection = new ArrayList<Volunteer>();
            for (Volunteer volunteerCollectionVolunteerToAttach : user.getVolunteerCollection()) {
                volunteerCollectionVolunteerToAttach = em.getReference(volunteerCollectionVolunteerToAttach.getClass(), volunteerCollectionVolunteerToAttach.getId());
                attachedVolunteerCollection.add(volunteerCollectionVolunteerToAttach);
            }
            user.setVolunteerCollection(attachedVolunteerCollection);
            em.persist(user);
            if (addressId != null) {
                addressId.getUserCollection().add(user);
                addressId = em.merge(addressId);
            }
            if (emailId != null) {
                emailId.getUserCollection().add(user);
                emailId = em.merge(emailId);
            }
            if (firstNameid != null) {
                firstNameid.getUserCollection().add(user);
                firstNameid = em.merge(firstNameid);
            }
            if (lastNameid != null) {
                lastNameid.getUserCollection().add(user);
                lastNameid = em.merge(lastNameid);
            }
            if (passwordId != null) {
                passwordId.getUserCollection().add(user);
                passwordId = em.merge(passwordId);
            }
            if (phoneId != null) {
                phoneId.getUserCollection().add(user);
                phoneId = em.merge(phoneId);
            }
            for (UserPermission userPermissionCollectionUserPermission : user.getUserPermissionCollection()) {
                User oldUserIdOfUserPermissionCollectionUserPermission = userPermissionCollectionUserPermission.getUserId();
                userPermissionCollectionUserPermission.setUserId(user);
                userPermissionCollectionUserPermission = em.merge(userPermissionCollectionUserPermission);
                if (oldUserIdOfUserPermissionCollectionUserPermission != null) {
                    oldUserIdOfUserPermissionCollectionUserPermission.getUserPermissionCollection().remove(userPermissionCollectionUserPermission);
                    oldUserIdOfUserPermissionCollectionUserPermission = em.merge(oldUserIdOfUserPermissionCollectionUserPermission);
                }
            }
            for (LoginAttempts loginAttemptsCollectionLoginAttempts : user.getLoginAttemptsCollection()) {
                User oldUserIdOfLoginAttemptsCollectionLoginAttempts = loginAttemptsCollectionLoginAttempts.getUserId();
                loginAttemptsCollectionLoginAttempts.setUserId(user);
                loginAttemptsCollectionLoginAttempts = em.merge(loginAttemptsCollectionLoginAttempts);
                if (oldUserIdOfLoginAttemptsCollectionLoginAttempts != null) {
                    oldUserIdOfLoginAttemptsCollectionLoginAttempts.getLoginAttemptsCollection().remove(loginAttemptsCollectionLoginAttempts);
                    oldUserIdOfLoginAttemptsCollectionLoginAttempts = em.merge(oldUserIdOfLoginAttemptsCollectionLoginAttempts);
                }
            }
            for (Volunteer volunteerCollectionVolunteer : user.getVolunteerCollection()) {
                User oldUserIdOfVolunteerCollectionVolunteer = volunteerCollectionVolunteer.getUserId();
                volunteerCollectionVolunteer.setUserId(user);
                volunteerCollectionVolunteer = em.merge(volunteerCollectionVolunteer);
                if (oldUserIdOfVolunteerCollectionVolunteer != null) {
                    oldUserIdOfVolunteerCollectionVolunteer.getVolunteerCollection().remove(volunteerCollectionVolunteer);
                    oldUserIdOfVolunteerCollectionVolunteer = em.merge(oldUserIdOfVolunteerCollectionVolunteer);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(User user) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User persistentUser = em.find(User.class, user.getId());
            Address addressIdOld = persistentUser.getAddressId();
            Address addressIdNew = user.getAddressId();
            Email emailIdOld = persistentUser.getEmailId();
            Email emailIdNew = user.getEmailId();
            FirstName firstNameidOld = persistentUser.getFirstNameid();
            FirstName firstNameidNew = user.getFirstNameid();
            LastName lastNameidOld = persistentUser.getLastNameid();
            LastName lastNameidNew = user.getLastNameid();
            Password passwordIdOld = persistentUser.getPasswordId();
            Password passwordIdNew = user.getPasswordId();
            Phone phoneIdOld = persistentUser.getPhoneId();
            Phone phoneIdNew = user.getPhoneId();
            Collection<UserPermission> userPermissionCollectionOld = persistentUser.getUserPermissionCollection();
            Collection<UserPermission> userPermissionCollectionNew = user.getUserPermissionCollection();
            Collection<LoginAttempts> loginAttemptsCollectionOld = persistentUser.getLoginAttemptsCollection();
            Collection<LoginAttempts> loginAttemptsCollectionNew = user.getLoginAttemptsCollection();
            Collection<Volunteer> volunteerCollectionOld = persistentUser.getVolunteerCollection();
            Collection<Volunteer> volunteerCollectionNew = user.getVolunteerCollection();
            List<String> illegalOrphanMessages = null;
            for (UserPermission userPermissionCollectionOldUserPermission : userPermissionCollectionOld) {
                if (!userPermissionCollectionNew.contains(userPermissionCollectionOldUserPermission)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain UserPermission " + userPermissionCollectionOldUserPermission + " since its userId field is not nullable.");
                }
            }
            for (LoginAttempts loginAttemptsCollectionOldLoginAttempts : loginAttemptsCollectionOld) {
                if (!loginAttemptsCollectionNew.contains(loginAttemptsCollectionOldLoginAttempts)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain LoginAttempts " + loginAttemptsCollectionOldLoginAttempts + " since its userId field is not nullable.");
                }
            }
            for (Volunteer volunteerCollectionOldVolunteer : volunteerCollectionOld) {
                if (!volunteerCollectionNew.contains(volunteerCollectionOldVolunteer)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Volunteer " + volunteerCollectionOldVolunteer + " since its userId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (addressIdNew != null) {
                addressIdNew = em.getReference(addressIdNew.getClass(), addressIdNew.getId());
                user.setAddressId(addressIdNew);
            }
            if (emailIdNew != null) {
                emailIdNew = em.getReference(emailIdNew.getClass(), emailIdNew.getId());
                user.setEmailId(emailIdNew);
            }
            if (firstNameidNew != null) {
                firstNameidNew = em.getReference(firstNameidNew.getClass(), firstNameidNew.getId());
                user.setFirstNameid(firstNameidNew);
            }
            if (lastNameidNew != null) {
                lastNameidNew = em.getReference(lastNameidNew.getClass(), lastNameidNew.getId());
                user.setLastNameid(lastNameidNew);
            }
            if (passwordIdNew != null) {
                passwordIdNew = em.getReference(passwordIdNew.getClass(), passwordIdNew.getId());
                user.setPasswordId(passwordIdNew);
            }
            if (phoneIdNew != null) {
                phoneIdNew = em.getReference(phoneIdNew.getClass(), phoneIdNew.getId());
                user.setPhoneId(phoneIdNew);
            }
            Collection<UserPermission> attachedUserPermissionCollectionNew = new ArrayList<UserPermission>();
            for (UserPermission userPermissionCollectionNewUserPermissionToAttach : userPermissionCollectionNew) {
                userPermissionCollectionNewUserPermissionToAttach = em.getReference(userPermissionCollectionNewUserPermissionToAttach.getClass(), userPermissionCollectionNewUserPermissionToAttach.getId());
                attachedUserPermissionCollectionNew.add(userPermissionCollectionNewUserPermissionToAttach);
            }
            userPermissionCollectionNew = attachedUserPermissionCollectionNew;
            user.setUserPermissionCollection(userPermissionCollectionNew);
            Collection<LoginAttempts> attachedLoginAttemptsCollectionNew = new ArrayList<LoginAttempts>();
            for (LoginAttempts loginAttemptsCollectionNewLoginAttemptsToAttach : loginAttemptsCollectionNew) {
                loginAttemptsCollectionNewLoginAttemptsToAttach = em.getReference(loginAttemptsCollectionNewLoginAttemptsToAttach.getClass(), loginAttemptsCollectionNewLoginAttemptsToAttach.getId());
                attachedLoginAttemptsCollectionNew.add(loginAttemptsCollectionNewLoginAttemptsToAttach);
            }
            loginAttemptsCollectionNew = attachedLoginAttemptsCollectionNew;
            user.setLoginAttemptsCollection(loginAttemptsCollectionNew);
            Collection<Volunteer> attachedVolunteerCollectionNew = new ArrayList<Volunteer>();
            for (Volunteer volunteerCollectionNewVolunteerToAttach : volunteerCollectionNew) {
                volunteerCollectionNewVolunteerToAttach = em.getReference(volunteerCollectionNewVolunteerToAttach.getClass(), volunteerCollectionNewVolunteerToAttach.getId());
                attachedVolunteerCollectionNew.add(volunteerCollectionNewVolunteerToAttach);
            }
            volunteerCollectionNew = attachedVolunteerCollectionNew;
            user.setVolunteerCollection(volunteerCollectionNew);
            user = em.merge(user);
            if (addressIdOld != null && !addressIdOld.equals(addressIdNew)) {
                addressIdOld.getUserCollection().remove(user);
                addressIdOld = em.merge(addressIdOld);
            }
            if (addressIdNew != null && !addressIdNew.equals(addressIdOld)) {
                addressIdNew.getUserCollection().add(user);
                addressIdNew = em.merge(addressIdNew);
            }
            if (emailIdOld != null && !emailIdOld.equals(emailIdNew)) {
                emailIdOld.getUserCollection().remove(user);
                emailIdOld = em.merge(emailIdOld);
            }
            if (emailIdNew != null && !emailIdNew.equals(emailIdOld)) {
                emailIdNew.getUserCollection().add(user);
                emailIdNew = em.merge(emailIdNew);
            }
            if (firstNameidOld != null && !firstNameidOld.equals(firstNameidNew)) {
                firstNameidOld.getUserCollection().remove(user);
                firstNameidOld = em.merge(firstNameidOld);
            }
            if (firstNameidNew != null && !firstNameidNew.equals(firstNameidOld)) {
                firstNameidNew.getUserCollection().add(user);
                firstNameidNew = em.merge(firstNameidNew);
            }
            if (lastNameidOld != null && !lastNameidOld.equals(lastNameidNew)) {
                lastNameidOld.getUserCollection().remove(user);
                lastNameidOld = em.merge(lastNameidOld);
            }
            if (lastNameidNew != null && !lastNameidNew.equals(lastNameidOld)) {
                lastNameidNew.getUserCollection().add(user);
                lastNameidNew = em.merge(lastNameidNew);
            }
            if (passwordIdOld != null && !passwordIdOld.equals(passwordIdNew)) {
                passwordIdOld.getUserCollection().remove(user);
                passwordIdOld = em.merge(passwordIdOld);
            }
            if (passwordIdNew != null && !passwordIdNew.equals(passwordIdOld)) {
                passwordIdNew.getUserCollection().add(user);
                passwordIdNew = em.merge(passwordIdNew);
            }
            if (phoneIdOld != null && !phoneIdOld.equals(phoneIdNew)) {
                phoneIdOld.getUserCollection().remove(user);
                phoneIdOld = em.merge(phoneIdOld);
            }
            if (phoneIdNew != null && !phoneIdNew.equals(phoneIdOld)) {
                phoneIdNew.getUserCollection().add(user);
                phoneIdNew = em.merge(phoneIdNew);
            }
            for (UserPermission userPermissionCollectionNewUserPermission : userPermissionCollectionNew) {
                if (!userPermissionCollectionOld.contains(userPermissionCollectionNewUserPermission)) {
                    User oldUserIdOfUserPermissionCollectionNewUserPermission = userPermissionCollectionNewUserPermission.getUserId();
                    userPermissionCollectionNewUserPermission.setUserId(user);
                    userPermissionCollectionNewUserPermission = em.merge(userPermissionCollectionNewUserPermission);
                    if (oldUserIdOfUserPermissionCollectionNewUserPermission != null && !oldUserIdOfUserPermissionCollectionNewUserPermission.equals(user)) {
                        oldUserIdOfUserPermissionCollectionNewUserPermission.getUserPermissionCollection().remove(userPermissionCollectionNewUserPermission);
                        oldUserIdOfUserPermissionCollectionNewUserPermission = em.merge(oldUserIdOfUserPermissionCollectionNewUserPermission);
                    }
                }
            }
            for (LoginAttempts loginAttemptsCollectionNewLoginAttempts : loginAttemptsCollectionNew) {
                if (!loginAttemptsCollectionOld.contains(loginAttemptsCollectionNewLoginAttempts)) {
                    User oldUserIdOfLoginAttemptsCollectionNewLoginAttempts = loginAttemptsCollectionNewLoginAttempts.getUserId();
                    loginAttemptsCollectionNewLoginAttempts.setUserId(user);
                    loginAttemptsCollectionNewLoginAttempts = em.merge(loginAttemptsCollectionNewLoginAttempts);
                    if (oldUserIdOfLoginAttemptsCollectionNewLoginAttempts != null && !oldUserIdOfLoginAttemptsCollectionNewLoginAttempts.equals(user)) {
                        oldUserIdOfLoginAttemptsCollectionNewLoginAttempts.getLoginAttemptsCollection().remove(loginAttemptsCollectionNewLoginAttempts);
                        oldUserIdOfLoginAttemptsCollectionNewLoginAttempts = em.merge(oldUserIdOfLoginAttemptsCollectionNewLoginAttempts);
                    }
                }
            }
            for (Volunteer volunteerCollectionNewVolunteer : volunteerCollectionNew) {
                if (!volunteerCollectionOld.contains(volunteerCollectionNewVolunteer)) {
                    User oldUserIdOfVolunteerCollectionNewVolunteer = volunteerCollectionNewVolunteer.getUserId();
                    volunteerCollectionNewVolunteer.setUserId(user);
                    volunteerCollectionNewVolunteer = em.merge(volunteerCollectionNewVolunteer);
                    if (oldUserIdOfVolunteerCollectionNewVolunteer != null && !oldUserIdOfVolunteerCollectionNewVolunteer.equals(user)) {
                        oldUserIdOfVolunteerCollectionNewVolunteer.getVolunteerCollection().remove(volunteerCollectionNewVolunteer);
                        oldUserIdOfVolunteerCollectionNewVolunteer = em.merge(oldUserIdOfVolunteerCollectionNewVolunteer);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = user.getId();
                if (findUser(id) == null) {
                    throw new NonexistentEntityException("The user with id " + id + " no longer exists.");
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
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<UserPermission> userPermissionCollectionOrphanCheck = user.getUserPermissionCollection();
            for (UserPermission userPermissionCollectionOrphanCheckUserPermission : userPermissionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the UserPermission " + userPermissionCollectionOrphanCheckUserPermission + " in its userPermissionCollection field has a non-nullable userId field.");
            }
            Collection<LoginAttempts> loginAttemptsCollectionOrphanCheck = user.getLoginAttemptsCollection();
            for (LoginAttempts loginAttemptsCollectionOrphanCheckLoginAttempts : loginAttemptsCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the LoginAttempts " + loginAttemptsCollectionOrphanCheckLoginAttempts + " in its loginAttemptsCollection field has a non-nullable userId field.");
            }
            Collection<Volunteer> volunteerCollectionOrphanCheck = user.getVolunteerCollection();
            for (Volunteer volunteerCollectionOrphanCheckVolunteer : volunteerCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the Volunteer " + volunteerCollectionOrphanCheckVolunteer + " in its volunteerCollection field has a non-nullable userId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Address addressId = user.getAddressId();
            if (addressId != null) {
                addressId.getUserCollection().remove(user);
                addressId = em.merge(addressId);
            }
            Email emailId = user.getEmailId();
            if (emailId != null) {
                emailId.getUserCollection().remove(user);
                emailId = em.merge(emailId);
            }
            FirstName firstNameid = user.getFirstNameid();
            if (firstNameid != null) {
                firstNameid.getUserCollection().remove(user);
                firstNameid = em.merge(firstNameid);
            }
            LastName lastNameid = user.getLastNameid();
            if (lastNameid != null) {
                lastNameid.getUserCollection().remove(user);
                lastNameid = em.merge(lastNameid);
            }
            Password passwordId = user.getPasswordId();
            if (passwordId != null) {
                passwordId.getUserCollection().remove(user);
                passwordId = em.merge(passwordId);
            }
            Phone phoneId = user.getPhoneId();
            if (phoneId != null) {
                phoneId.getUserCollection().remove(user);
                phoneId = em.merge(phoneId);
            }
            em.remove(user);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<User> findUserEntities() {
        return findUserEntities(true, -1, -1);
    }

    public List<User> findUserEntities(int maxResults, int firstResult) {
        return findUserEntities(false, maxResults, firstResult);
    }

    private List<User> findUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
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

    public User findUser(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<User> rt = cq.from(User.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
