/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.entity;

import ams.aps.entity.FlightSchedule;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Lewis
 */
@Embeddable
@Table(name = "PAIRING_FLIGHTDUTIES")
public class FlightDuty implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToMany
    private List<FlightSchedule> flightSchedules;

    private Double flyingDistInKm;
    private Double flyingTimeInHrs;
    private Double sitTimeInHrs;
    private Integer cabinCrewQuota;
    private Integer cockpitCrewQuota;

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

}
