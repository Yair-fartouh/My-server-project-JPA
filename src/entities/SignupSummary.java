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
@Table(name = "signup_summary")
@NamedQueries({
    @NamedQuery(name = "SignupSummary.findAll", query = "SELECT s FROM SignupSummary s"),
    @NamedQuery(name = "SignupSummary.findById", query = "SELECT s FROM SignupSummary s WHERE s.id = :id"),
    @NamedQuery(name = "SignupSummary.findBySignupDatetime", query = "SELECT s FROM SignupSummary s WHERE s.signupDatetime = :signupDatetime"),
    @NamedQuery(name = "SignupSummary.findBySignupCount", query = "SELECT s FROM SignupSummary s WHERE s.signupCount = :signupCount")})
public class SignupSummary implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "signup_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date signupDatetime;
    @Basic(optional = false)
    @Column(name = "signup_count")
    private int signupCount;

    public SignupSummary() {
    }

    public SignupSummary(Integer id) {
        this.id = id;
    }

    public SignupSummary(Integer id, Date signupDatetime, int signupCount) {
        this.id = id;
        this.signupDatetime = signupDatetime;
        this.signupCount = signupCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getSignupDatetime() {
        return signupDatetime;
    }

    public void setSignupDatetime(Date signupDatetime) {
        this.signupDatetime = signupDatetime;
    }

    public int getSignupCount() {
        return signupCount;
    }

    public void setSignupCount(int signupCount) {
        this.signupCount = signupCount;
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
        if (!(object instanceof SignupSummary)) {
            return false;
        }
        SignupSummary other = (SignupSummary) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.SignupSummary[ id=" + id + " ]";
    }
    
}
