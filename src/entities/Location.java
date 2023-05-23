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
@Table(name = "location")
@NamedQueries({
    @NamedQuery(name = "Location.findAll", query = "SELECT l FROM Location l"),
    @NamedQuery(name = "Location.findById", query = "SELECT l FROM Location l WHERE l.id = :id"),
    @NamedQuery(name = "Location.findByUserId", query = "SELECT l FROM Location l WHERE l.userId = :userId"),
    @NamedQuery(name = "Location.findByLat", query = "SELECT l FROM Location l WHERE l.lat = :lat"),
    @NamedQuery(name = "Location.findByLng", query = "SELECT l FROM Location l WHERE l.lng = :lng"),
    @NamedQuery(name = "Location.findByUpdateFrom", query = "SELECT l FROM Location l WHERE l.updateFrom = :updateFrom")})
public class Location implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "user_id")
    private int userId;
    @Basic(optional = false)
    @Column(name = "LAT")
    private double lat;
    @Basic(optional = false)
    @Column(name = "LNG")
    private double lng;
    @Basic(optional = false)
    @Column(name = "updateFrom")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateFrom;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "locationId")
    private Collection<Volunteer> volunteerCollection;

    public Location() {
    }

    public Location(Integer id) {
        this.id = id;
    }

    public Location(Integer id, int userId, double lat, double lng, Date updateFrom) {
        this.id = id;
        this.userId = userId;
        this.lat = lat;
        this.lng = lng;
        this.updateFrom = updateFrom;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Date getUpdateFrom() {
        return updateFrom;
    }

    public void setUpdateFrom(Date updateFrom) {
        this.updateFrom = updateFrom;
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
        if (!(object instanceof Location)) {
            return false;
        }
        Location other = (Location) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Location[ id=" + id + " ]";
    }
    
}
