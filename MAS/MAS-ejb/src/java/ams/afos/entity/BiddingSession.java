/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.entity;

import ams.crm.entity.PrivilegeValue;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Lewis
 */
@Entity
public class BiddingSession implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String status;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date startTime;
    private Double remainingHrs;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date createdTime;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, mappedBy = "biddingSession")
    private List<Pairing> pairings;

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, mappedBy="biddingSessions")
    private List<FlightCrew> flightCrews;

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
        if (!(object instanceof BiddingSession)) {
            return false;
        }
        BiddingSession other = (BiddingSession) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.afos.entity.BiddingSession[ id=" + id + " ]";
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
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the remainingHrs
     */
    public Double getRemainingHrs() {
        return remainingHrs;
    }

    /**
     * @param remainingHrs the remainingHrs to set
     */
    public void setRemainingHrs(Double remainingHrs) {
        this.remainingHrs = remainingHrs;
    }

    /**
     * @return the createdTime
     */
    public Date getCreatedTime() {
        return createdTime;
    }

    /**
     * @param createdTime the createdTime to set
     */
    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    /**
     * @return the pairings
     */
    public List<Pairing> getPairings() {
        return pairings;
    }

    /**
     * @param pairings the pairings to set
     */
    public void setPairings(List<Pairing> pairings) {
        this.pairings = pairings;
    }

    /**
     * @return the flightCrews
     */
    public List<FlightCrew> getFlightCrews() {
        return flightCrews;
    }

    /**
     * @param flightCrews the flightCrews to set
     */
    public void setFlightCrews(List<FlightCrew> flightCrews) {
        this.flightCrews = flightCrews;
    }

}
