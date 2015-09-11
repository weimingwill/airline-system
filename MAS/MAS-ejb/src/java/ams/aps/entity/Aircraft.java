/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.entity;

import java.io.Serializable;
import java.sql.Date;
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
public class Aircraft implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tailNo;
    private Float lifetime;
    private String status;
    private String source;
    private Float cost;
    private Date addOnDate;
    private Float avgUnitOilUsage;
    
    @OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER, mappedBy="aircraft")
    private Collection<FlightSchedule> flightSchedules = new ArrayList<FlightSchedule>();
    @ManyToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private AircraftType aircraftType;
    @ManyToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private SeatConfig seatConfig;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FlightSchedule getFlightSchedule() {
        return flightSchedule;
    }

    public AircraftType getAircraftType() {
        return aircraftType;
    }

    public SeatConfig getSeatConfig() {
        return seatConfig;
    }

    public String getTailNo() {
        return tailNo;
    }

    public Float getLifeTime() {
        return lifeTime;
    }

    public Date getBoughtOn() {
        return boughtOn;
    }

    public String getStatus() {
        return status;
    }

    public String getSource() {
        return source;
    }

    public void setTailNo(String tailNo) {
        this.tailNo = tailNo;
    }

    public void setLifeTime(Float lifeTime) {
        this.lifeTime = lifeTime;
    }

    public void setBoughtOn(Date boughtOn) {
        this.boughtOn = boughtOn;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setFlightSchedule(FlightSchedule flightSchedule) {
        this.flightSchedule = flightSchedule;
    }

    public void setAircraftType(AircraftType aircraftType) {
        this.aircraftType = aircraftType;
    }

    public void setSeatConfig(SeatConfig seatConfig) {
        this.seatConfig = seatConfig;
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
        if (!(object instanceof Aircraft)) {
            return false;
        }
        Aircraft other = (Aircraft) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.aps.entity.Aircraft[ id=" + id + " ]";
    }
    
}
