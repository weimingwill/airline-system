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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Lewis
 */
@Entity
public class SwappingRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date createdTime;
    private String remark;
    private String status;
    
    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Pairing chosenPairing;
    
    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Pairing targetPairing;
    
    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private FlightCrew sender;
    
    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private FlightCrew receiver;

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
        if (!(object instanceof SwappingRequest)) {
            return false;
        }
        SwappingRequest other = (SwappingRequest) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.afos.entity.SwappingRequest[ id=" + id + " ]";
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
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark the remark to set
     */
    public void setRemark(String remark) {
        this.remark = remark;
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
     * @return the chosenPairing
     */
    public Pairing getChosenPairing() {
        return chosenPairing;
    }

    /**
     * @param chosenPairing the chosenPairing to set
     */
    public void setChosenPairing(Pairing chosenPairing) {
        this.chosenPairing = chosenPairing;
    }

    /**
     * @return the targetPairing
     */
    public Pairing getTargetPairing() {
        return targetPairing;
    }

    /**
     * @param targetPairing the targetPairing to set
     */
    public void setTargetPairing(Pairing targetPairing) {
        this.targetPairing = targetPairing;
    }

    /**
     * @return the sender
     */
    public FlightCrew getSender() {
        return sender;
    }

    /**
     * @param sender the sender to set
     */
    public void setSender(FlightCrew sender) {
        this.sender = sender;
    }

    /**
     * @return the receiver
     */
    public FlightCrew getReceiver() {
        return receiver;
    }

    /**
     * @param receiver the receiver to set
     */
    public void setReceiver(FlightCrew receiver) {
        this.receiver = receiver;
    }
    
}
