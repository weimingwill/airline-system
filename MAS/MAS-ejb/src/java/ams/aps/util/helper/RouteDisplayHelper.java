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

    private String origin;
    private String destination;
    private String legs;

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

}
