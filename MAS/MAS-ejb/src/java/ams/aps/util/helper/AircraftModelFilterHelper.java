/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.util.helper;

import java.util.List;

/**
 *
 * @author Lewis
 */
public class AircraftModelFilterHelper {

    private List<String> manufacturers;
    private List<String> typeFamilies;
    private int minMaxSeating;
    private int maxMaxSeating;
    private float minApproxPrice;
    private float maxApproxPrice;
    private float minFuelCostPerKm;
    private float maxFuelCostPerKm;
    private float minRange;
    private float maxRange;
    private float minPayload;
    private float maxPayload;
    private float minMaxMachNum;
    private float maxMaxMachNum;
    private float minFuelCapacity;
    private float maxFuelCapacity;

    public AircraftModelFilterHelper() {
    }

    /**
     * @return the minMaxSeating
     */
    public int getMinMaxSeating() {
        return minMaxSeating;
    }

    /**
     * @param minMaxSeating the minMaxSeating to set
     */
    public void setMinMaxSeating(int minMaxSeating) {
        this.minMaxSeating = minMaxSeating;
    }

    /**
     * @return the maxMaxSeating
     */
    public int getMaxMaxSeating() {
        return maxMaxSeating;
    }

    /**
     * @param maxMaxSeating the maxMaxSeating to set
     */
    public void setMaxMaxSeating(int maxMaxSeating) {
        this.maxMaxSeating = maxMaxSeating;
    }

    /**
     * @return the minApproxPrice
     */
    public float getMinApproxPrice() {
        return minApproxPrice;
    }

    /**
     * @param minApproxPrice the minApproxPrice to set
     */
    public void setMinApproxPrice(float minApproxPrice) {
        this.minApproxPrice = minApproxPrice;
    }

    /**
     * @return the maxApproxPrice
     */
    public float getMaxApproxPrice() {
        return maxApproxPrice;
    }

    /**
     * @param maxApproxPrice the maxApproxPrice to set
     */
    public void setMaxApproxPrice(float maxApproxPrice) {
        this.maxApproxPrice = maxApproxPrice;
    }

    /**
     * @return the minFuelCostPerKm
     */
    public float getMinFuelCostPerKm() {
        return minFuelCostPerKm;
    }

    /**
     * @param minFuelCostPerKm the minFuelCostPerKm to set
     */
    public void setMinFuelCostPerKm(float minFuelCostPerKm) {
        this.minFuelCostPerKm = minFuelCostPerKm;
    }

    /**
     * @return the maxFuelCostPerKm
     */
    public float getMaxFuelCostPerKm() {
        return maxFuelCostPerKm;
    }

    /**
     * @param maxFuelCostPerKm the maxFuelCostPerKm to set
     */
    public void setMaxFuelCostPerKm(float maxFuelCostPerKm) {
        this.maxFuelCostPerKm = maxFuelCostPerKm;
    }

    /**
     * @return the minRange
     */
    public float getMinRange() {
        return minRange;
    }

    /**
     * @param minRange the minRange to set
     */
    public void setMinRange(float minRange) {
        this.minRange = minRange;
    }

    /**
     * @return the maxRange
     */
    public float getMaxRange() {
        return maxRange;
    }

    /**
     * @param maxRange the maxRange to set
     */
    public void setMaxRange(float maxRange) {
        this.maxRange = maxRange;
    }

    /**
     * @return the minPayload
     */
    public float getMinPayload() {
        return minPayload;
    }

    /**
     * @param minPayload the minPayload to set
     */
    public void setMinPayload(float minPayload) {
        this.minPayload = minPayload;
    }

    /**
     * @return the maxPayload
     */
    public float getMaxPayload() {
        return maxPayload;
    }

    /**
     * @param maxPayload the maxPayload to set
     */
    public void setMaxPayload(float maxPayload) {
        this.maxPayload = maxPayload;
    }

    /**
     * @return the minMaxMachNum
     */
    public float getMinMaxMachNum() {
        return minMaxMachNum;
    }

    /**
     * @param minMaxMachNum the minMaxMachNum to set
     */
    public void setMinMaxMachNum(float minMaxMachNum) {
        this.minMaxMachNum = minMaxMachNum;
    }

    /**
     * @return the maxMaxMachNum
     */
    public float getMaxMaxMachNum() {
        return maxMaxMachNum;
    }

    /**
     * @param maxMaxMachNum the maxMaxMachNum to set
     */
    public void setMaxMaxMachNum(float maxMaxMachNum) {
        this.maxMaxMachNum = maxMaxMachNum;
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
     * @return the maxFuelCapacity
     */
    public float getMaxFuelCapacity() {
        return maxFuelCapacity;
    }

    /**
     * @param maxFuelCapacity the maxFuelCapacity to set
     */
    public void setMaxFuelCapacity(float maxFuelCapacity) {
        this.maxFuelCapacity = maxFuelCapacity;
    }

    /**
     * @return the manufacturers
     */
    public List<String> getManufacturers() {
        return manufacturers;
    }

    /**
     * @param manufacturers the manufacturers to set
     */
    public void setManufacturers(List<String> manufacturers) {
        this.manufacturers = manufacturers;
    }

    /**
     * @return the typeFamilies
     */
    public List<String> getTypeFamilies() {
        return typeFamilies;
    }

    /**
     * @param typeFamilies the typeFamilies to set
     */
    public void setTypeFamilies(List<String> typeFamilies) {
        this.typeFamilies = typeFamilies;
    }

}
