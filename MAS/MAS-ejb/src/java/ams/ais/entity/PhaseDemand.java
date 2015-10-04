/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Tongtong
 */
@Entity
public class PhaseDemand implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int daysBeforeDeparture;
    private float demandMean;
    private float demandDev;
    private boolean isDeleted;
    
    public void create(int daysBeforeDeparture, float demandMean, float demandDev){
        this.daysBeforeDeparture = daysBeforeDeparture;
        this.demandMean = demandMean;
        this.demandDev = demandDev;
        this.isDeleted = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDaysBeforeDeparture() {
        return daysBeforeDeparture;
    }

    public void setDaysBeforeDeparture(int daysBeforeDeparture) {
        this.daysBeforeDeparture = daysBeforeDeparture;
    }

    public float getDemandMean() {
        return demandMean;
    }

    public void setDemandMean(float demandMean) {
        this.demandMean = demandMean;
    }

    public float getDemandDev() {
        return demandDev;
    }

    public void setDemandDev(float demandDev) {
        this.demandDev = demandDev;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PhaseDemand)) {
            return false;
        }
        PhaseDemand other = (PhaseDemand) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.ais.entity.PhaseDemand[ id=" + id + " ]";
    }
    
}
