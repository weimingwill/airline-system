/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.entity;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import mas.common.entity.SystemUser;

/**
 *
 * @author Lewis
 */
@Entity
public class FlightCrew extends SystemUser implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String flightCrewID;
    private Double totalFlyingTime;
    private Double totalFlyingDist;
    
    @ManyToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private FlightCrewPosition position;

    /**
     * @return the totalFlyingTime
     */
    public Double getTotalFlyingTime() {
        return totalFlyingTime;
    }

    /**
     * @param totalFlyingTime the totalFlyingTime to set
     */
    public void setTotalFlyingTime(Double totalFlyingTime) {
        this.totalFlyingTime = totalFlyingTime;
    }

    /**
     * @return the totalFlyingDist
     */
    public Double getTotalFlyingDist() {
        return totalFlyingDist;
    }

    /**
     * @param totalFlyingDist the totalFlyingDist to set
     */
    public void setTotalFlyingDist(Double totalFlyingDist) {
        this.totalFlyingDist = totalFlyingDist;
    }

    /**
     * @return the position
     */
    public FlightCrewPosition getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(FlightCrewPosition position) {
        this.position = position;
    }

    
}
