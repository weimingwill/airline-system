/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
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
    @Temporal(value = TemporalType.DATE)
    private Date dob;
    private String gender;
    private Double totalFlyingTime;
    private Double totalFlyingDist;
    private String base;
    @Temporal(value = TemporalType.DATE)
    private Date dateJoined;

    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private FlightCrewPosition position;

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private List<BiddingSession> biddingSessions;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(joinColumns = @JoinColumn(name = "LANGUAGESID"))
    private List<Languages> languages;

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

    /**
     * @return the dob
     */
    public Date getDob() {
        return dob;
    }

    /**
     * @param dob the dob to set
     */
    public void setDob(Date dob) {
        this.dob = dob;
    }

    /**
     * @return the languages
     */
    public List<Languages> getLanguages() {
        return languages;
    }

    /**
     * @param languages the languages to set
     */
    public void setLanguages(List<Languages> languages) {
        this.languages = languages;
    }

    /**
     * @return the base
     */
    public String getBase() {
        return base;
    }

    /**
     * @param base the base to set
     */
    public void setBase(String base) {
        this.base = base;
    }

    /**
     * @return the biddingSessions
     */
    public List<BiddingSession> getBiddingSessions() {
        return biddingSessions;
    }

    /**
     * @param biddingSessions the biddingSessions to set
     */
    public void setBiddingSessions(List<BiddingSession> biddingSessions) {
        this.biddingSessions = biddingSessions;
    }

}
