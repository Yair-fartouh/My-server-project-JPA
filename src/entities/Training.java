/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
import java.util.Collection;
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

/**
 *
 * @author Yair
 */
@Entity
@Table(name = "training")
@NamedQueries({
    @NamedQuery(name = "Training.findAll", query = "SELECT t FROM Training t"),
    @NamedQuery(name = "Training.findById", query = "SELECT t FROM Training t WHERE t.id = :id"),
    @NamedQuery(name = "Training.findByNameOfTraining", query = "SELECT t FROM Training t WHERE t.nameOfTraining = :nameOfTraining")})
public class Training implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "nameOfTraining")
    private String nameOfTraining;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "trainingId")
    private Collection<Volunteer> volunteerCollection;

    public Training() {
    }

    public Training(Integer id) {
        this.id = id;
    }

    public Training(Integer id, String nameOfTraining) {
        this.id = id;
        this.nameOfTraining = nameOfTraining;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNameOfTraining() {
        return nameOfTraining;
    }

    public void setNameOfTraining(String nameOfTraining) {
        this.nameOfTraining = nameOfTraining;
    }

    public Collection<Volunteer> getVolunteerCollection() {
        return volunteerCollection;
    }

    public void setVolunteerCollection(Collection<Volunteer> volunteerCollection) {
        this.volunteerCollection = volunteerCollection;
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
        if (!(object instanceof Training)) {
            return false;
        }
        Training other = (Training) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Training[ id=" + id + " ]";
    }
    
}
