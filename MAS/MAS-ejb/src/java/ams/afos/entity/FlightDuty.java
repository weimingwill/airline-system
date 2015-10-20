/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.entity;

import ams.aps.entity.FlightSchedule;
import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.OneToOne;

/**
 *
 * @author Lewis
 */
@Embeddable
public class FlightDuty implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToOne
    private FlightSchedule flightSchedule;

    private Double flyingDistInKm;
    private Double flyingTimeInHrs;
    private Double sitTimeInHrs;
    private Integer dutySeq;

    /**
     * @return the flightSchedule
     */
    public FlightSchedule getFlightSchedule() {
        return flightSchedule;
    }

    /**
     * @param flightSchedule the flightSchedule to set
     */
    public void setFlightSchedule(FlightSchedule flightSchedule) {
        this.flightSchedule = flightSchedule;
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
     * @return the dutySeq
     */
    public Integer getDutySeq() {
        return dutySeq;
    }

    /**
     * @param dutySeq the dutySeq to set
     */
    public void setDutySeq(Integer dutySeq) {
        this.dutySeq = dutySeq;
    }

}
