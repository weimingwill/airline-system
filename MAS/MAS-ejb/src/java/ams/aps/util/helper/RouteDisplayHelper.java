/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.util.helper;

/**
 *
 * @author ChuningLiu
 */
public class RouteDisplayHelper {

    private Long id;
    private String origin;
    private String destination;
    private String legs;
    private Long returnRouteId;
    private Double totalDistance;
    private Double totalDuration;

    public RouteDisplayHelper() {
    }

    public RouteDisplayHelper(String origin, String destination, String legs) {
        this.origin = origin;
        this.destination = destination;
        this.legs = legs;
    }

    /**
     * @return the origin
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * @param origin the origin to set
     */
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
     * @return the destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * @param destination the destination to set
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * @return the legs
     */
    public String getLegs() {
        return legs;
    }

    /**
     * @param legs the legs to set
     */
    public void setLegs(String legs) {
        this.legs = legs;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the returnRouteId
     */
    public Long getReturnRouteId() {
        return returnRouteId;
    }

    /**
     * @param returnRouteId the returnRouteId to set
     */
    public void setReturnRouteId(Long returnRouteId) {
        this.returnRouteId = returnRouteId;
    }

    /**
     * @return the totalDistance
     */
    public Double getTotalDistance() {
        return totalDistance;
    }

    /**
     * @param totalDistance the totalDistance to set
     */
    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }

    /**
     * @return the totalDuration
     */
    public Double getTotalDuration() {
        return totalDuration;
    }

    /**
     * @param totalDuration the totalDuration to set
     */
    public void setTotalDuration(Double totalDuration) {
        this.totalDuration = totalDuration;
    }

}
