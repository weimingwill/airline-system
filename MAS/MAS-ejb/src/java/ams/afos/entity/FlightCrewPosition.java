/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Lewis
 */
@Entity
public class FlightCrewPosition implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;
    private Integer rank;
    private Double maxMonthlyFlyingHrs;
    private Integer maxTotalMonthlyDaysOff;
    private Double maxDutyPeriodHrs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(object instanceof FlightCrewPosition)) {
            return false;
        }
        FlightCrewPosition other = (FlightCrewPosition) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.afos.entity.FlightCrewPosition[ id=" + id + " ]";
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the rank
     */
    public Integer getRank() {
        return rank;
    }

    /**
     * @param rank the rank to set
     */
    public void setRank(Integer rank) {
        this.rank = rank;
    }

    /**
     * @return the maxMonthlyFlyingHrs
     */
    public Double getMaxMonthlyFlyingHrs() {
        return maxMonthlyFlyingHrs;
    }

    /**
     * @param maxMonthlyFlyingHrs the maxMonthlyFlyingHrs to set
     */
    public void setMaxMonthlyFlyingHrs(Double maxMonthlyFlyingHrs) {
        this.maxMonthlyFlyingHrs = maxMonthlyFlyingHrs;
    }

    /**
     * @return the maxDutyPeriodHrs
     */
    public Double getMaxDutyPeriodHrs() {
        return maxDutyPeriodHrs;
    }

    /**
     * @param maxDutyPeriodHrs the maxDutyPeriodHrs to set
     */
    public void setMaxDutyPeriodHrs(Double maxDutyPeriodHrs) {
        this.maxDutyPeriodHrs = maxDutyPeriodHrs;
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

    /**
     * @return the maxTotalMonthlyDaysOff
     */
    public Integer getMaxTotalMonthlyDaysOff() {
        return maxTotalMonthlyDaysOff;
    }

    /**
     * @param maxTotalMonthlyDaysOff the maxTotalMonthlyDaysOff to set
     */
    public void setMaxTotalMonthlyDaysOff(Integer maxTotalMonthlyDaysOff) {
        this.maxTotalMonthlyDaysOff = maxTotalMonthlyDaysOff;
    }

}
