/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

/**
 *
 * @author Lewis
 */
@Entity
public class Pairing implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pairingId;
    private String pairingCode;
    private Integer cabinCrewQuota;
    private Integer cockpitCrewQuota;
    private Double layoverTime;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private List<FlightDuty> flightDuties;

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private List<BiddingSession> biddingSession;

    public Long getPairingId() {
        return pairingId;
    }

    public void setPairingId(Long pairingId) {
        this.pairingId = pairingId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pairingId != null ? pairingId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the pairingId fields are not set
        if (!(object instanceof Pairing)) {
            return false;
        }
        Pairing other = (Pairing) object;
        if ((this.pairingId == null && other.pairingId != null) || (this.pairingId != null && !this.pairingId.equals(other.pairingId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.afos.entity.Pairing[ pairingId=" + pairingId + " ]";
    }

    /**
     * @return the pairingCode
     */
    public String getPairingCode() {
        return pairingCode;
    }

    /**
     * @param pairingCode the pairingCode to set
     */
    public void setPairingCode(String pairingCode) {
        this.pairingCode = pairingCode;
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
     * @return the layoverTime
     */
    public Double getLayoverTime() {
        return layoverTime;
    }

    /**
     * @param layoverTime the layoverTime to set
     */
    public void setLayoverTime(Double layoverTime) {
        this.layoverTime = layoverTime;
    }

    /**
     * @return the flightDuties
     */
    public List<FlightDuty> getFlightDuties() {
        return flightDuties;
    }

    /**
     * @param flightDuties the flightDuties to set
     */
    public void setFlightDuties(List<FlightDuty> flightDuties) {
        this.flightDuties = flightDuties;
    }

    /**
     * @return the biddingSession
     */
    public List<BiddingSession> getBiddingSession() {
        return biddingSession;
    }

    /**
     * @param biddingSession the biddingSession to set
     */
    public void setBiddingSession(List<BiddingSession> biddingSession) {
        this.biddingSession = biddingSession;
    }

}
