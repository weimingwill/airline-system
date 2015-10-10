/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.util.helper;

import ams.aps.entity.Airport;

/**
 *
 * @author Lewis
 */
public class LegHelper {

    private Long legId;
    private Airport departure;
    private Airport arrival;
    private Float distance;
    private Float duration;

    public LegHelper() {
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
     * @return the departure
     */
    public Airport getDeparture() {
        return departure;
    }

    /**
     * @param departure the departure to set
     */
    public void setDeparture(Airport departure) {
        this.departure = departure;
    }

    /**
     * @return the arrival
     */
    public Airport getArrival() {
        return arrival;
    }

    /**
     * @param arrival the arrival to set
     */
    public void setArrival(Airport arrival) {
        this.arrival = arrival;
    }

    /**
     * @return the distance
     */
    public Float getDistance() {
        return distance;
    }

    /**
     * @param distance the distance to set
     */
    public void setDistance(Float distance) {
        this.distance = distance;
    }

    /**
     * @return the duration
     */
    public Float getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(Float duration) {
        this.duration = duration;
    }

}
