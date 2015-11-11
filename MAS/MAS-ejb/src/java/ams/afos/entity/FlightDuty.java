/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.entity;

import ams.aps.entity.FlightSchedule;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Lewis
 */
@Entity
public class FlightDuty implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToMany
    private List<FlightSchedule> flightSchedules;

    private Double flyingDistInKm;
    private Double flyingTimeInHrs;
    private Double sitTimeInHrs;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date reportTime;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date dismissTime;
    private Integer cabinCrewQuota;
    private Integer cockpitCrewQuota;
    @Temporal(value = TemporalType.DATE)
    private Date appliedPeriod;

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
        if (!(object instanceof FlightDuty)) {
            return false;
        }
        FlightDuty other = (FlightDuty) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.afos.entity.FlightDuty[ id=" + id + " ]";
    }

    /**
     * @return the flightSchedules
     */
    public List<FlightSchedule> getFlightSchedules() {
        return flightSchedules;
    }

    /**
     * @param flightSchedules the flightSchedules to set
     */
    public void setFlightSchedules(List<FlightSchedule> flightSchedules) {
        this.flightSchedules = flightSchedules;
    }

    /**
     * @return the flyingDistInKm
     */
    public Double getFlyingDistInKm() {
        return flyingDistInKm;
    }

    /**
     * @param flyingDistInKm the flyingDistInKm to set
     */
    public void setFlyingDistInKm(Double flyingDistInKm) {
        this.flyingDistInKm = flyingDistInKm;
    }

    /**
     * @return the flyingTimeInHrs
     */
    public Double getFlyingTimeInHrs() {
        return flyingTimeInHrs;
    }

    /**
     * @param flyingTimeInHrs the flyingTimeInHrs to set
     */
    public void setFlyingTimeInHrs(Double flyingTimeInHrs) {
        this.flyingTimeInHrs = flyingTimeInHrs;
    }

    /**
     * @return the sitTimeInHrs
     */
    public Double getSitTimeInHrs() {
        return sitTimeInHrs;
    }

    /**
     * @param sitTimeInHrs the sitTimeInHrs to set
     */
    public void setSitTimeInHrs(Double sitTimeInHrs) {
        this.sitTimeInHrs = sitTimeInHrs;
    }

    /**
     * @return the cabinCrewQuota
     */
    public Integer getCabinCrewQuota() {
        return cabinCrewQuota;
    }

    /**
     * @param cabinCrewQuota the cabinCrewQuota to set
     */
    public void setCabinCrewQuota(Integer cabinCrewQuota) {
        this.cabinCrewQuota = cabinCrewQuota;
    }

    /**
     * @return the cockpitCrewQuota
     */
    public Integer getCockpitCrewQuota() {
        return cockpitCrewQuota;
    }

    /**
     * @param cockpitCrewQuota the cockpitCrewQuota to set
     */
    public void setCockpitCrewQuota(Integer cockpitCrewQuota) {
        this.cockpitCrewQuota = cockpitCrewQuota;
    }

    /**
     * @return the appliedPeriod
     */
    public Date getAppliedPeriod() {
        return appliedPeriod;
    }

    /**
     * @param appliedPeriod the appliedPeriod to set
     */
    public void setAppliedPeriod(Date appliedPeriod) {
        this.appliedPeriod = appliedPeriod;
    }

    /**
     * @return the reportTime
     */
    public Date getReportTime() {
        return reportTime;
    }

    /**
     * @param reportTime the reportTime to set
     */
    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }

    /**
     * @return the dismissTime
     */
    public Date getDismissTime() {
        return dismissTime;
    }

    /**
     * @param dismissTime the dismissTime to set
     */
    public void setDismissTime(Date dismissTime) {
        this.dismissTime = dismissTime;
    }

}
