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
public class RouteCompareHelper {

    private double distance;
    private double minFlyingTime;
    private double maxFlyingTime;
    private String origin;
    private String destination;
    private String stops;
    private String type;
    
    public RouteCompareHelper() {
    }

    
    public RouteCompareHelper(String type, String origin, String stops, String destination, double distance, double minFlyingTime, double maxFlyingTime) {
        this.distance = distance;
        this.origin = origin;
        this.destination = destination;
        this.stops = stops;
        this.maxFlyingTime = maxFlyingTime;
        this.minFlyingTime = minFlyingTime;
        this.type = type;
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
    public String getStops() {
        return stops;
    }

    /**
     * @param stops the stops to set
     */
    public void setStops(String stops) {
        this.stops = stops;
    }

    /**
     * @return the distance
     */
    public double getDistance() {
        return distance;
    }

    /**
     * @param distance the distance to set
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * @return the minFlyingTime
     */
    public double getMinFlyingTime() {
        return minFlyingTime;
    }

    /**
     * @param minFlyingTime the minFlyingTime to set
     */
    public void setMinFlyingTime(double minFlyingTime) {
        this.minFlyingTime = minFlyingTime;
    }

    /**
     * @return the maxFlyingTime
     */
    public double getMaxFlyingTime() {
        return maxFlyingTime;
    }

    /**
     * @param maxFlyingTime the maxFlyingTime to set
     */
    public void setMaxFlyingTime(double maxFlyingTime) {
        this.maxFlyingTime = maxFlyingTime;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
    
    
}