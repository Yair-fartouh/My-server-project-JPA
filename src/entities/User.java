/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Yair
 */
@Entity
@Table(name = "user")
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findById", query = "SELECT u FROM User u WHERE u.id = :id"),
    @NamedQuery(name = "User.findByBirthDate", query = "SELECT u FROM User u WHERE u.birthDate = :birthDate"),
    @NamedQuery(name = "User.findByIsDeleted", query = "SELECT u FROM User u WHERE u.isDeleted = :isDeleted")})
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "birthDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date birthDate;
    @Basic(optional = false)
    @Column(name = "isDeleted")
    private boolean isDeleted;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId")
    private Collection<UserPermission> userPermissionCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId")
    private Collection<LoginAttempts> loginAttemptsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId")
    private Collection<Volunteer> volunteerCollection;
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Address addressId;
    @JoinColumn(name = "email_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Email emailId;
    @JoinColumn(name = "firstName_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private FirstName firstNameid;
    @JoinColumn(name = "lastName_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private LastName lastNameid;
    @JoinColumn(name = "password_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Password passwordId;
    @JoinColumn(name = "phone_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Phone phoneId;

    public User() {
    }

    public User(Integer id) {
        this.id = id;
    }

    public User(Integer id, Date birthDate, boolean isDeleted) {
        this.id = id;
        this.birthDate = birthDate;
        this.isDeleted = isDeleted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Collection<UserPermission> getUserPermissionCollection() {
        return userPermissionCollection;
    }

    public void setUserPermissionCollection(Collection<UserPermission> userPermissionCollection) {
        this.userPermissionCollection = userPermissionCollection;
    }

    public Collection<LoginAttempts> getLoginAttemptsCollection() {
        return loginAttemptsCollection;
    }

    public void setLoginAttemptsCollection(Collection<LoginAttempts> loginAttemptsCollection) {
        this.loginAttemptsCollection = loginAttemptsCollection;
    }

    public Collection<Volunteer> getVolunteerCollection() {
        return volunteerCollection;
    }

    public void setVolunteerCollection(Collection<Volunteer> volunteerCollection) {
        this.volunteerCollection = volunteerCollection;
    }

    public Address getAddressId() {
        return addressId;
    }

    public void setAddressId(Address addressId) {
        this.addressId = addressId;
    }

    public Email getEmailId() {
        return emailId;
    }

    public void setEmailId(Email emailId) {
        this.emailId = emailId;
    }

    public FirstName getFirstNameid() {
        return firstNameid;
    }

    public void setFirstNameid(FirstName firstNameid) {
        this.firstNameid = firstNameid;
    }

    public LastName getLastNameid() {
        return lastNameid;
    }

    public void setLastNameid(LastName lastNameid) {
        this.lastNameid = lastNameid;
    }

    public Password getPasswordId() {
        return passwordId;
    }

    public void setPasswordId(Password passwordId) {
        this.passwordId = passwordId;
    }

    public Phone getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(Phone phoneId) {
        this.phoneId = phoneId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.User[ id=" + id + " ]";
    }
    
}
