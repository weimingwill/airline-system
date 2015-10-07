/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.util.helper;

import ams.aps.entity.Airport;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author Lewis
 */
public class RouteHelper {

    private Long id;
    private Airport origin;
    private Airport destination;
    private TreeMap<Integer, Airport> stopovers;
    private Long returnRouteId;

    public RouteHelper() {
    }

    public List<Airport> getStopoverList() {
        if (this.stopovers != null) {
            List<Airport> stopoverList = new ArrayList();
            for (int i = 0; i < this.stopovers.size(); i++) {
                stopoverList.add(this.stopovers.get(i));
            }
            return stopoverList;
        } else {
            return null;
        }
    }

    /**
     * @return the origin
     */
    public Airport getOrigin() {
        return origin;
    }

    /**
     * @param origin the origin to set
     */
    public void setOrigin(Airport origin) {
        this.origin = origin;
    }

    /**
     * @return the destination
     */
    public Airport getDestination() {
        return destination;
    }

    /**
     * @param destination the destination to set
     */
    public void setDestination(Airport destination) {
        this.destination = destination;
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
     * @return the stopovers
     */
    public TreeMap<Integer, Airport> getStopovers() {
        return stopovers;
    }

    /**
     * @param stopovers the stopovers to set
     */
    public void setStopovers(TreeMap<Integer, Airport> stopovers) {
        this.stopovers = stopovers;
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

}
