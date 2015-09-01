/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author ChuningLiu
 */
@Entity
public class Airport implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String airportCode;
    private String airportName;
    private Boolean isHub;
    
    @OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER, mappedBy="departure")
    private Collection<Leg> departures = new ArrayList<Leg>();
    
    @OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER, mappedBy="destination")
    private Collection<Leg> arrivals = new ArrayList<Leg>();
    
    @ManyToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private City city;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public String getAirportName() {
        return airportName;
    }

    public Boolean getIsHub() {
        return isHub;
    }

    public City getCity() {
        return city;
    }

    public Collection<Leg> getDepartures() {
        return departures;
    }

    public Collection<Leg> getArrivals() {
        return arrivals;
    }

    public void setDepartures(Collection<Leg> departures) {
        this.departures = departures;
    }

    public void setArrivals(Collection<Leg> arrivals) {
        this.arrivals = arrivals;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public void setIsHub(Boolean isHub) {
        this.isHub = isHub;
    }

    public void setCity(City city) {
        this.city = city;
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
        if (!(object instanceof Airport)) {
            return false;
        }
        Airport other = (Airport) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.aps.entity.Airport[ id=" + id + " ]";
    }
    
}
