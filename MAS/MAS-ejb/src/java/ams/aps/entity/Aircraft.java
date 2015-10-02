/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.entity;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author ChuningLiu
 */
@Entity
public class Aircraft implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aircraftId;
    private String tailNo;
    private Float lifetime;
    private String status;
    private String source;
    private Float cost;
    private Date addOnDate;
    private Float avgUnitOilUsage;
    private boolean isScheduled;
    
    @OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER, mappedBy="aircraft")
    private List<AircraftCabinClass> aircraftCabinClasses = new ArrayList<>();
            
    
    @OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER, mappedBy="aircraft")
    private List<FlightSchedule> flightSchedules = new ArrayList<>();
    @ManyToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private AircraftType aircraftType;

    public Long getAircraftId() {
        return aircraftId;
    }

    public void setAircraftId(Long aircraftId) {
        this.aircraftId = aircraftId;
    }
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (aircraftId != null ? aircraftId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the aircraftId fields are not set
        if (!(object instanceof Aircraft)) {
            return false;
        }
        Aircraft other = (Aircraft) object;
        if ((this.aircraftId == null && other.aircraftId != null) || (this.aircraftId != null && !this.aircraftId.equals(other.aircraftId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.aps.entity.Aircraft[ aircraftId=" + aircraftId + " ]";
    }

    /**
     * @return the tailNo
     */
    public String getTailNo() {
        return tailNo;
    }

    /**
     * @param tailNo the tailNo to set
     */
    public void setTailNo(String tailNo) {
        this.tailNo = tailNo;
    }

    /**
     * @return the lifetime
     */
    public Float getLifetime() {
        return lifetime;
    }

    /**
     * @param lifetime the lifetime to set
     */
    public void setLifetime(Float lifetime) {
        this.lifetime = lifetime;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return the cost
     */
    public Float getCost() {
        return cost;
    }

    /**
     * @param cost the cost to set
     */
    public void setCost(Float cost) {
        this.cost = cost;
    }

    /**
     * @return the addOnDate
     */
    public Date getAddOnDate() {
        return addOnDate;
    }

    /**
     * @param addOnDate the addOnDate to set
     */
    public void setAddOnDate(Date addOnDate) {
        this.addOnDate = addOnDate;
    }

    /**
     * @return the avgUnitOilUsage
     */
    public Float getAvgUnitOilUsage() {
        return avgUnitOilUsage;
    }

    /**
     * @param avgUnitOilUsage the avgUnitOilUsage to set
     */
    public void setAvgUnitOilUsage(Float avgUnitOilUsage) {
        this.avgUnitOilUsage = avgUnitOilUsage;
    }

    /**
     * @return the aircraftType
     */
    public AircraftType getAircraftType() {
        return aircraftType;
    }

    /**
     * @param aircraftType the aircraftType to set
     */
    public void setAircraftType(AircraftType aircraftType) {
        this.aircraftType = aircraftType;
    }

    public List<AircraftCabinClass> getAircraftCabinClasses() {
        return aircraftCabinClasses;
    }

    public void setAircraftCabinClasses(List<AircraftCabinClass> aircraftCabinClasses) {
        this.aircraftCabinClasses = aircraftCabinClasses;
    }

    public List<FlightSchedule> getFlightSchedules() {
        return flightSchedules;
    }

    public void setFlightSchedules(List<FlightSchedule> flightSchedules) {
        this.flightSchedules = flightSchedules;
    }

    public boolean isIsScheduled() {
        return isScheduled;
    }

    public void setIsScheduled(boolean isScheduled) {
        this.isScheduled = isScheduled;
    }

    
}
