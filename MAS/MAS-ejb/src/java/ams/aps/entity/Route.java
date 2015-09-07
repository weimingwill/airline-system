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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author ChuningLiu
 */
@Entity
public class Route implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER, mappedBy="routes")
    private Collection<Leg> legs = new ArrayList<Leg>();
    @OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER, mappedBy="route")
    private Collection<Flight> flights = new ArrayList<Flight>();
    @ManyToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private Airport departure;
    @ManyToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private Airport destination;
    
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLegs(Collection<Leg> legs) {
        this.legs = legs;
    }

    public void setFlights(Collection<Flight> flights) {
        this.flights = flights;
    }

    public void setDeparture(Airport departure) {
        this.departure = departure;
    }

    public void setDestination(Airport destination) {
        this.destination = destination;
    }

    public Collection<Leg> getLegs() {
        return legs;
    }

    public Collection<Flight> getFlights() {
        return flights;
    }

    public Airport getDeparture() {
        return departure;
    }

    public Airport getDestination() {
        return destination;
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
        if (!(object instanceof Route)) {
            return false;
        }
        Route other = (Route) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.aps.entity.Route[ id=" + id + " ]";
    }
    
}
