/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.dcs.entity;

import ams.ars.entity.AddOn;
import java.io.Serializable;
import javax.persistence.Entity;

/**
 *
 * @author Lewis
 */
@Entity
public class Luggage extends AddOn implements Serializable {

    private static final long serialVersionUID = 1L;

    private Double maxWeight;

    /**
     * @return the maxWeight
     */
    public Double getMaxWeight() {
        return maxWeight;
    }

    /**
     * @param maxWeight the maxWeight to set
     */
    public void setMaxWeight(Double maxWeight) {
        this.maxWeight = maxWeight;
    }

}
