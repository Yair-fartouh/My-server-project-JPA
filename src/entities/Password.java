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
@Table(name = "password")
@NamedQueries({
    @NamedQuery(name = "Password.findAll", query = "SELECT p FROM Password p"),
    @NamedQuery(name = "Password.findById", query = "SELECT p FROM Password p WHERE p.id = :id"),
    @NamedQuery(name = "Password.findByPassword", query = "SELECT p FROM Password p WHERE p.password = :password"),
    @NamedQuery(name = "Password.findBySalt", query = "SELECT p FROM Password p WHERE p.salt = :salt"),
    @NamedQuery(name = "Password.findByPasswordEffectiveDate", query = "SELECT p FROM Password p WHERE p.passwordEffectiveDate = :passwordEffectiveDate"),
    @NamedQuery(name = "Password.findByIsActive", query = "SELECT p FROM Password p WHERE p.isActive = :isActive")})
public class Password implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "password")
    private String password;
    @Basic(optional = false)
    @Column(name = "salt")
    private String salt;
    @Basic(optional = false)
    @Column(name = "passwordEffectiveDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date passwordEffectiveDate;
    @Basic(optional = false)
    @Column(name = "isActive")
    private boolean isActive;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "passwordId")
    private Collection<User> userCollection;

    public Password() {
    }

    public Password(Integer id) {
        this.id = id;
    }

    public Password(Integer id, String password, String salt, Date passwordEffectiveDate, boolean isActive) {
        this.id = id;
        this.password = password;
        this.salt = salt;
        this.passwordEffectiveDate = passwordEffectiveDate;
        this.isActive = isActive;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Date getPasswordEffectiveDate() {
        return passwordEffectiveDate;
    }

    public void setPasswordEffectiveDate(Date passwordEffectiveDate) {
        this.passwordEffectiveDate = passwordEffectiveDate;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Collection<User> getUserCollection() {
        return userCollection;
    }

    public void setUserCollection(Collection<User> userCollection) {
        this.userCollection = userCollection;
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
        if (!(object instanceof Password)) {
            return false;
        }
        Password other = (Password) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Password[ id=" + id + " ]";
    }
    
}
