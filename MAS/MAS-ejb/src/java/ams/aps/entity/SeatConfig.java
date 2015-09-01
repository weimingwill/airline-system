/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author ChuningLiu
 */
@Entity
public class SeatConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cabinClassType;
    private Integer count;
    
    @ManyToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private AircraftType aircraftType;
    @OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER, mappedBy="seatConfig")
    private Collection<Aircraft> aircraft = new ArrayList<Aircraft>();
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCabinClassType() {
        return cabinClassType;
    }

    public Integer getCount() {
        return count;
    }

    public AircraftType getAircraftType() {
        return aircraftType;
    }

    public Collection<Aircraft> getAircraft() {
        return aircraft;
    }

    public void setAircraft(Collection<Aircraft> aircraft) {
        this.aircraft = aircraft;
    }

    public void setCabinClassType(String cabinClassType) {
        this.cabinClassType = cabinClassType;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setAircraftType(AircraftType aircraftType) {
        this.aircraftType = aircraftType;
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
        if (!(object instanceof SeatConfig)) {
            return false;
        }
        SeatConfig other = (SeatConfig) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.aps.entity.SeatConfig[ id=" + id + " ]";
    }
    
}
