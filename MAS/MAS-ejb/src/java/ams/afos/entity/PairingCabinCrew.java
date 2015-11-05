/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.entity;

import ams.afos.entity.helper.PairingCrewId;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Lewis
 */
@Entity
@Table(name = "PAIRINGCABINCREW")
public class PairingCabinCrew implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    private PairingCrewId pairingCrewId;

    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "FLIGHTCREWID", referencedColumnName = "SYSTEMUSERID")
    private FlightCrew cabinCrew;

    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "PAIRINGID", referencedColumnName = "PAIRINGID")
    private Pairing pairing;

    @Column(name = "STATUS")
    private String status;
    
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastUpdateTime;

    /**
     * @return the pairingCrewId
     */
    public PairingCrewId getPairingCrewId() {
        return pairingCrewId;
    }

    /**
     * @param pairingCrewId the pairingCrewId to set
     */
    public void setPairingCrewId(PairingCrewId pairingCrewId) {
        this.pairingCrewId = pairingCrewId;
    }

    /**
     * @return the pairing
     */
    public Pairing getPairing() {
        return pairing;
    }

    /**
     * @param pairing the pairing to set
     */
    public void setPairing(Pairing pairing) {
        this.pairing = pairing;
    }

    /**
     * @return the cabinCrew
     */
    public FlightCrew getCabinCrew() {
        return cabinCrew;
    }

    /**
     * @param cabinCrew the cabinCrew to set
     */
    public void setCabinCrew(FlightCrew cabinCrew) {
        this.cabinCrew = cabinCrew;
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
     * @return the lastUpdateTime
     */
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * @param lastUpdateTime the lastUpdateTime to set
     */
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

}
