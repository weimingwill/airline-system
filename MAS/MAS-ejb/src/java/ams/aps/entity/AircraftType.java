/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author ChuningLiu
 */
@Entity
public class AircraftType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String typeCode;
    private String typeFamily;
    private String mfdBy;
    private Float rangeInKm;
    private Integer typicalSeating;
    private Integer maxSeating;
    private Float maxFuelCapacity;
    private Float maxMachNo;
    private Float maxTakeOffWeight;
    private Float maxZeroFuelWeight;
    private Float overallLengthInM;
    private Float wingspanInM;
    private Float heightInM;
    private Float trackInM;
    private Float wheelbaseInM;

    @OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER, mappedBy="aircraftType")
    private Collection<Aircraft> aircrafts = new ArrayList<Aircraft>();
    

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
        if (!(object instanceof AircraftType)) {
            return false;
        }
        AircraftType other = (AircraftType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.aps.entity.AircraftType[ id=" + id + " ]";
    }

    /**
     * @return the typeCode
     */
    public String getTypeCode() {
        return typeCode;
    }

    /**
     * @param typeCode the typeCode to set
     */
    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    /**
     * @return the typeFamily
     */
    public String getTypeFamily() {
        return typeFamily;
    }

    /**
     * @param typeFamily the typeFamily to set
     */
    public void setTypeFamily(String typeFamily) {
        this.typeFamily = typeFamily;
    }

    /**
     * @return the mfdBy
     */
    public String getMfdBy() {
        return mfdBy;
    }

    /**
     * @param mfdBy the mfdBy to set
     */
    public void setMfdBy(String mfdBy) {
        this.mfdBy = mfdBy;
    }

    /**
     * @return the rangeInKm
     */
    public Float getRangeInKm() {
        return rangeInKm;
    }

    /**
     * @param rangeInKm the rangeInKm to set
     */
    public void setRangeInKm(Float rangeInKm) {
        this.rangeInKm = rangeInKm;
    }

    /**
     * @return the typicalSeating
     */
    public Integer getTypicalSeating() {
        return typicalSeating;
    }

    /**
     * @param typicalSeating the typicalSeating to set
     */
    public void setTypicalSeating(Integer typicalSeating) {
        this.typicalSeating = typicalSeating;
    }

    /**
     * @return the maxSeating
     */
    public Integer getMaxSeating() {
        return maxSeating;
    }

    /**
     * @param maxSeating the maxSeating to set
     */
    public void setMaxSeating(Integer maxSeating) {
        this.maxSeating = maxSeating;
    }

    /**
     * @return the maxFuelCapacity
     */
    public Float getMaxFuelCapacity() {
        return maxFuelCapacity;
    }

    /**
     * @param maxFuelCapacity the maxFuelCapacity to set
     */
    public void setMaxFuelCapacity(Float maxFuelCapacity) {
        this.maxFuelCapacity = maxFuelCapacity;
    }

    /**
     * @return the maxMachNo
     */
    public Float getMaxMachNo() {
        return maxMachNo;
    }

    /**
     * @param maxMachNo the maxMachNo to set
     */
    public void setMaxMachNo(Float maxMachNo) {
        this.maxMachNo = maxMachNo;
    }

    /**
     * @return the maxTakeOffWeight
     */
    public Float getMaxTakeOffWeight() {
        return maxTakeOffWeight;
    }

    /**
     * @param maxTakeOffWeight the maxTakeOffWeight to set
     */
    public void setMaxTakeOffWeight(Float maxTakeOffWeight) {
        this.maxTakeOffWeight = maxTakeOffWeight;
    }

    /**
     * @return the maxZeroFuelWeight
     */
    public Float getMaxZeroFuelWeight() {
        return maxZeroFuelWeight;
    }

    /**
     * @param maxZeroFuelWeight the maxZeroFuelWeight to set
     */
    public void setMaxZeroFuelWeight(Float maxZeroFuelWeight) {
        this.maxZeroFuelWeight = maxZeroFuelWeight;
    }

    /**
     * @return the overallLengthInM
     */
    public Float getOverallLengthInM() {
        return overallLengthInM;
    }

    /**
     * @param overallLengthInM the overallLengthInM to set
     */
    public void setOverallLengthInM(Float overallLengthInM) {
        this.overallLengthInM = overallLengthInM;
    }

    /**
     * @return the wingspanInM
     */
    public Float getWingspanInM() {
        return wingspanInM;
    }

    /**
     * @param wingspanInM the wingspanInM to set
     */
    public void setWingspanInM(Float wingspanInM) {
        this.wingspanInM = wingspanInM;
    }

    /**
     * @return the heightInM
     */
    public Float getHeightInM() {
        return heightInM;
    }

    /**
     * @param heightInM the heightInM to set
     */
    public void setHeightInM(Float heightInM) {
        this.heightInM = heightInM;
    }

    /**
     * @return the trackInM
     */
    public Float getTrackInM() {
        return trackInM;
    }

    /**
     * @param trackInM the trackInM to set
     */
    public void setTrackInM(Float trackInM) {
        this.trackInM = trackInM;
    }

    /**
     * @return the wheelbaseInM
     */
    public Float getWheelbaseInM() {
        return wheelbaseInM;
    }

    /**
     * @param wheelbaseInM the wheelbaseInM to set
     */
    public void setWheelbaseInM(Float wheelbaseInM) {
        this.wheelbaseInM = wheelbaseInM;
    }

    /**
     * @return the aircrafts
     */
    public Collection<Aircraft> getAircrafts() {
        return aircrafts;
    }

    /**
     * @param aircrafts the aircrafts to set
     */
    public void setAircrafts(Collection<Aircraft> aircrafts) {
        this.aircrafts = aircrafts;
    }
    
}
