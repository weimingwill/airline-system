/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.entity;

import ams.aps.helper.RouteLegId;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 *
 * @author Lewis
 */
@Entity
@IdClass(RouteLegId.class)
@Table(name = "AIRCRAFT_CABINCLASS")
public class RouteLeg implements Serializable {
    @Id
    private Long routeId;
    @Id 
    private Long legId;
    
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "ROUTEID", referencedColumnName="ROUTEID")
    private Route route;
    
    @OneToOne
    @PrimaryKeyJoinColumn(name = "LEGID", referencedColumnName="LEGID")
    private Leg leg;

    @Column(name = "LEGSEQ") 
    private int legSeq;

    /**
     * @return the routeId
     */
    public Long getRouteId() {
        return routeId;
    }

    /**
     * @param routeId the routeId to set
     */
    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    /**
     * @return the legId
     */
    public Long getLegId() {
        return legId;
    }

    /**
     * @param legId the legId to set
     */
    public void setLegId(Long legId) {
        this.legId = legId;
    }

    /**
     * @return the route
     */
    public Route getRoute() {
        return route;
    }

    /**
     * @param route the route to set
     */
    public void setRoute(Route route) {
        this.route = route;
    }

    /**
     * @return the leg
     */
    public Leg getLeg() {
        return leg;
    }

    /**
     * @param leg the leg to set
     */
    public void setLeg(Leg leg) {
        this.leg = leg;
    }

    /**
     * @return the legSeq
     */
    public int getLegSeq() {
        return legSeq;
    }

    /**
     * @param legSeq the legSeq to set
     */
    public void setLegSeq(int legSeq) {
        this.legSeq = legSeq;
    }
    
}
