/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;

/**
 *
 * @author Lewis
 */
@Entity
public class Pairing implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String pairingCode;
    private Integer cabinCrewQuota;
    private Integer cockpitCrewQuota;
    private Double layoverTime;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(joinColumns = @JoinColumn(name = "FLIGHTDUTYID"))
    private List<FlightDuty> flightDuties;

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private List<FlightCrew> cabinCrews;

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private List<FlightCrew> cockpitCrews;

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
        if (!(object instanceof Pairing)) {
            return false;
        }
        Pairing other = (Pairing) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.afos.entity.Pairing[ id=" + id + " ]";
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
     * @return the cabinCrews
     */
    public List<FlightCrew> getCabinCrews() {
        return cabinCrews;
    }

    /**
     * @param cabinCrews the cabinCrews to set
     */
    public void setCabinCrews(List<FlightCrew> cabinCrews) {
        this.cabinCrews = cabinCrews;
    }

    /**
     * @return the cockpitCrews
     */
    public List<FlightCrew> getCockpitCrews() {
        return cockpitCrews;
    }

    /**
     * @param cockpitCrews the cockpitCrews to set
     */
    public void setCockpitCrews(List<FlightCrew> cockpitCrews) {
        this.cockpitCrews = cockpitCrews;
    }

}
