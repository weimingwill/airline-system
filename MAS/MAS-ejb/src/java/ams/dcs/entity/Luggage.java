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
 * @author ChuningLiu
 */
@Entity
public class Luggage extends AddOn implements Serializable {

    private Double weight;

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
