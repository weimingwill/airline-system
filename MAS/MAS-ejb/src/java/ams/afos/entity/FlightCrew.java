/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import mas.common.entity.SystemUser;

/**
 *
 * @author Lewis
 */
@Entity
public class FlightCrew extends SystemUser implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String flightCrewID;
    private Integer age;
    private String gender;
    private Double totalFlyingTime;
    private Double totalFlyingDist;
    @Temporal(value = TemporalType.DATE)
    private Date dateJoined;
    
    @ManyToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private FlightCrewPosition position;

    /**
     * @return the totalFlyingTime
     */
    public Double getTotalFlyingTime() {
        return totalFlyingTime;
    }

    /**
     * @param totalFlyingTime the totalFlyingTime to set
     */
    public void setTotalFlyingTime(Double totalFlyingTime) {
        this.totalFlyingTime = totalFlyingTime;
    }

    /**
     * @return the totalFlyingDist
     */
    public Double getTotalFlyingDist() {
        return totalFlyingDist;
    }

    /**
     * @param totalFlyingDist the totalFlyingDist to set
     */
    public void setTotalFlyingDist(Double totalFlyingDist) {
        this.totalFlyingDist = totalFlyingDist;
    }

    /**
     * @return the position
     */
    public FlightCrewPosition getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(FlightCrewPosition position) {
        this.position = position;
    }

    /**
     * @return the flightCrewID
     */
    public String getFlightCrewID() {
        return flightCrewID;
    }

    /**
     * @param flightCrewID the flightCrewID to set
     */
    public void setFlightCrewID(String flightCrewID) {
        this.flightCrewID = flightCrewID;
    }

    /**
     * @return the gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * @param gender the gender to set
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * @return the age
     */
    public Integer getAge() {
        return age;
    }

    /**
     * @param age the age to set
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * @return the dateJoined
     */
    public Date getDateJoined() {
        return dateJoined;
    }

    /**
     * @param dateJoined the dateJoined to set
     */
    public void setDateJoined(Date dateJoined) {
        this.dateJoined = dateJoined;
    }

}
