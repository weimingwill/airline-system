/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.entity;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author ChuningLiu
 */
@Entity
public class Leg implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long legId;
    
    @ManyToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private Airport departAirport;
    @ManyToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private Airport arrivalAirport;
    
    public Long getLegId() {
        return legId;
    }

    public void setLegId(Long legId) {
        this.legId = legId;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (legId != null ? legId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Leg)) {
            return false;
        }
        Leg other = (Leg) object;
        if ((this.legId == null && other.legId != null) || (this.legId != null && !this.legId.equals(other.legId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.aps.entity.Leg[ id=" + legId + " ]";
    }

    /**
     * @return the departAirport
     */
    public Airport getDepartAirport() {
        return departAirport;
    }

    /**
     * @param departAirport the departAirport to set
     */
    public void setDepartAirport(Airport departAirport) {
        this.departAirport = departAirport;
    }

    /**
     * @return the arrivalAirport
     */
    public Airport getArrivalAirport() {
        return arrivalAirport;
    }

    /**
     * @param arrivalAirport the arrivalAirport to set
     */
    public void setArrivalAirport(Airport arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }
    
}
