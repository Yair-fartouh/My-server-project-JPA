/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Yair
 */
@Entity
@Table(name = "login_attempts")
@NamedQueries({
    @NamedQuery(name = "LoginAttempts.findAll", query = "SELECT l FROM LoginAttempts l"),
    @NamedQuery(name = "LoginAttempts.findById", query = "SELECT l FROM LoginAttempts l WHERE l.id = :id"),
    @NamedQuery(name = "LoginAttempts.findByIpAddress", query = "SELECT l FROM LoginAttempts l WHERE l.ipAddress = :ipAddress"),
    @NamedQuery(name = "LoginAttempts.findByAttemptTime", query = "SELECT l FROM LoginAttempts l WHERE l.attemptTime = :attemptTime")})
public class LoginAttempts implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "ipAddress")
    private String ipAddress;
    @Basic(optional = false)
    @Column(name = "attempt_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date attemptTime;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User userId;

    public LoginAttempts() {
    }

    public LoginAttempts(Integer id) {
        this.id = id;
    }

    public LoginAttempts(Integer id, String ipAddress, Date attemptTime) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.attemptTime = attemptTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Date getAttemptTime() {
        return attemptTime;
    }

    public void setAttemptTime(Date attemptTime) {
        this.attemptTime = attemptTime;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
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
        if (!(object instanceof LoginAttempts)) {
            return false;
        }
        LoginAttempts other = (LoginAttempts) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.LoginAttempts[ id=" + id + " ]";
    }
    
}
