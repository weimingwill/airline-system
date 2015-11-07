/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.util.helper;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Lewis
 */
public class RetireAircraftFilterHelper {

    private float minLifespan;
    private float maxLifespan;
    private Date fromAddOnDate;
    private float minFuelCapacity;
    private float minAvgFuelUsage;
    private int timesOfMaint;
    private List<String> typeOfMaint;
    private int numOfFlightCycle;
    private float maxTotalFlightDist;

    public RetireAircraftFilterHelper() {
    }

    /**
     * @return the fromAddOnDate
     */
    public Date getFromAddOnDate() {
        return fromAddOnDate;
    }

    /**
     * @param fromAddOnDate the fromAddOnDate to set
     */
    public void setFromAddOnDate(Date fromAddOnDate) {
        this.fromAddOnDate = fromAddOnDate;
    }

    /**
     * @return the minFuelCapacity
     */
    public float getMinFuelCapacity() {
        return minFuelCapacity;
    }

    /**
     * @param minFuelCapacity the minFuelCapacity to set
     */
    public void setMinFuelCapacity(float minFuelCapacity) {
        this.minFuelCapacity = minFuelCapacity;
    }

    /**
     * @return the minAvgFuelUsage
     */
    public float getMinAvgFuelUsage() {
        return minAvgFuelUsage;
    }

    /**
     * @param minAvgFuelUsage the minAvgFuelUsage to set
     */
    public void setMinAvgFuelUsage(float minAvgFuelUsage) {
        this.minAvgFuelUsage = minAvgFuelUsage;
    }

    /**
     * @return the timesOfMaint
     */
    public int getTimesOfMaint() {
        return timesOfMaint;
    }

    /**
     * @param timesOfMaint the timesOfMaint to set
     */
    public void setTimesOfMaint(int timesOfMaint) {
        this.timesOfMaint = timesOfMaint;
    }

    /**
     * @return the numOfFlightCycle
     */
    public int getNumOfFlightCycle() {
        return numOfFlightCycle;
    }

    /**
     * @param numOfFlightCycle the numOfFlightCycle to set
     */
    public void setNumOfFlightCycle(int numOfFlightCycle) {
        this.numOfFlightCycle = numOfFlightCycle;
    }

    /**
     * @return the maxTotalFlightDist
     */
    public float getMaxTotalFlightDist() {
        return maxTotalFlightDist;
    }

    /**
     * @param maxTotalFlightDist the maxTotalFlightDist to set
     */
    public void setMaxTotalFlightDist(float maxTotalFlightDist) {
        this.maxTotalFlightDist = maxTotalFlightDist;
    }

    /**
     * @return the typeOfMaint
     */
    public List<String> getTypeOfMaint() {
        return typeOfMaint;
    }

    /**
     * @param typeOfMaint the typeOfMaint to set
     */
    public void setTypeOfMaint(List<String> typeOfMaint) {
        this.typeOfMaint = typeOfMaint;
    }

    /**
     * @return the minLifespan
     */
    public float getMinLifespan() {
        return minLifespan;
    }

    /**
     * @param minLifespan the minLifespan to set
     */
    public void setMinLifespan(float minLifespan) {
        this.minLifespan = minLifespan;
    }

    /**
     * @return the maxLifespan
     */
    public float getMaxLifespan() {
        return maxLifespan;
    }

    /**
     * @param maxLifespan the maxLifespan to set
     */
    public void setMaxLifespan(float maxLifespan) {
        this.maxLifespan = maxLifespan;
    }

}
