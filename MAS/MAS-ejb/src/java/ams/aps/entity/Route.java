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
import javax.persistence.OneToOne;

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
    
    private String status;
    @ManyToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER, mappedBy="routes")
    private Collection<Leg> legs = new ArrayList<Leg>();
    @OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER, mappedBy="route")
    private Collection<Flight> flights = new ArrayList<Flight>();
    @OneToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private Route returnRoute;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    /**
     * @return the legs
     */
    public Collection<Leg> getLegs() {
        return legs;
    }

    /**
     * @param legs the legs to set
     */
    public void setLegs(Collection<Leg> legs) {
        this.legs = legs;
    }

    /**
     * @return the flights
     */
    public Collection<Flight> getFlights() {
        return flights;
    }

    /**
     * @param flights the flights to set
     */
    public void setFlights(Collection<Flight> flights) {
        this.flights = flights;
    }

    /**
     * @return the returnRoute
     */
    public Route getReturnRoute() {
        return returnRoute;
    }

    /**
     * @param returnRoute the returnRoute to set
     */
    public void setReturnRoute(Route returnRoute) {
        this.returnRoute = returnRoute;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
}
