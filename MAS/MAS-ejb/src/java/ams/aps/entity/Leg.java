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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author ChuningLiu
 */
@Entity
public class Leg implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    @JoinTable
    private Collection<Route> routes = new ArrayList<Route>();
    @ManyToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private Airport departure;
    @ManyToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private Airport destination;
    @OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER, mappedBy="leg")
    private Collection<FlightSchedule> flightSchdules = new ArrayList<FlightSchedule>();
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Collection<Route> getRoutes() {
        return routes;
    }

    public Airport getDeparture() {
        return departure;
    }

    public Airport getDestination() {
        return destination;
    }

    public Collection<FlightSchedule> getFlightSchdules() {
        return flightSchdules;
    }

    public void setRoutes(Collection<Route> routes) {
        this.routes = routes;
    }

    public void setDeparture(Airport departure) {
        this.departure = departure;
    }

    public void setDestination(Airport destination) {
        this.destination = destination;
    }

    public void setFlightSchdules(Collection<FlightSchedule> flightSchdules) {
        this.flightSchdules = flightSchdules;
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
        if (!(object instanceof Leg)) {
            return false;
        }
        Leg other = (Leg) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.aps.entity.Leg[ id=" + id + " ]";
    }
    
}
