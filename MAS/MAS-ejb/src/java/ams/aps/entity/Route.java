/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
    private Long routeId;
    private boolean deleted;
    @OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER, mappedBy="route")
    private List<Flight> flights = new ArrayList<>();
    @OneToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private Route returnRoute;

    @OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER, mappedBy="route")
    private List<RouteLeg> routeLegs = new ArrayList<>();

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (routeId != null ? routeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Route)) {
            return false;
        }
        Route other = (Route) object;
        if ((this.routeId == null && other.routeId != null) || (this.routeId != null && !this.routeId.equals(other.routeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.aps.entity.Route[ id=" + routeId + " ]";
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    public List<RouteLeg> getRouteLegs() {
        return routeLegs;
    }

    public void setRouteLegs(List<RouteLeg> routeLegs) {
        this.routeLegs = routeLegs;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
}
