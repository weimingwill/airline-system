/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.dcs.entity;

import ams.aps.entity.Airport;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 *
 * @author ChuningLiu
 */
@Entity
public class LuggageTag implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String luggageNo;
    private double weight;
    private String status;
    private String notes;
    
    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Airport origin;
    
    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Airport destination;
    
    

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
        if (!(object instanceof LuggageTag)) {
            return false;
        }
        LuggageTag other = (LuggageTag) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.dcs.entity.LuggageTag[ id=" + id + " ]";
    }

    /**
     * @return the luggageNo
     */
    public String getLuggageNo() {
        return luggageNo;
    }

    /**
     * @param luggageNo the luggageNo to set
     */
    public void setLuggageNo(String luggageNo) {
        this.luggageNo = luggageNo;
    }

    /**
     * @return the weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * @param weight the weight to set
     */
    public void setWeight(double weight) {
        this.weight = weight;
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
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * @param notes the notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * @return the origin
     */
    public Airport getOrigin() {
        return origin;
    }

    /**
     * @param origin the origin to set
     */
    public void setOrigin(Airport origin) {
        this.origin = origin;
    }

    /**
     * @return the destination
     */
    public Airport getDestination() {
        return destination;
    }

    /**
     * @param destination the destination to set
     */
    public void setDestination(Airport destination) {
        this.destination = destination;
    }
    
}
