/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.entity;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.sql.Date;
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
public class FlightSchedule implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String departTerminal;
    private String departGate;
    private Date departDatel;
    private Time departTime;
    private String arrivalTerminal;
    private String arrivalGate;
    private Date arrivalDatel;
    private Time arrivalTime;
   
    @ManyToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private Aircraft aircraft;
    @ManyToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private Leg leg;
    @ManyToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private Flight flight;
    
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
        if (!(object instanceof FlightSchedule)) {
            return false;
        }
        FlightSchedule other = (FlightSchedule) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.aps.entity.FlightSchedule[ id=" + id + " ]";
    }

    /**
     * @return the departTerminal
     */
    public String getDepartTerminal() {
        return departTerminal;
    }

    /**
     * @param departTerminal the departTerminal to set
     */
    public void setDepartTerminal(String departTerminal) {
        this.departTerminal = departTerminal;
    }

    /**
     * @return the departGate
     */
    public String getDepartGate() {
        return departGate;
    }

    /**
     * @param departGate the departGate to set
     */
    public void setDepartGate(String departGate) {
        this.departGate = departGate;
    }

    /**
     * @return the departDatel
     */
    public Date getDepartDatel() {
        return departDatel;
    }

    /**
     * @param departDatel the departDatel to set
     */
    public void setDepartDatel(Date departDatel) {
        this.departDatel = departDatel;
    }

    /**
     * @return the departTime
     */
    public Time getDepartTime() {
        return departTime;
    }

    /**
     * @param departTime the departTime to set
     */
    public void setDepartTime(Time departTime) {
        this.departTime = departTime;
    }

    /**
     * @return the arrivalTerminal
     */
    public String getArrivalTerminal() {
        return arrivalTerminal;
    }

    /**
     * @param arrivalTerminal the arrivalTerminal to set
     */
    public void setArrivalTerminal(String arrivalTerminal) {
        this.arrivalTerminal = arrivalTerminal;
    }

    /**
     * @return the arrivalGate
     */
    public String getArrivalGate() {
        return arrivalGate;
    }

    /**
     * @param arrivalGate the arrivalGate to set
     */
    public void setArrivalGate(String arrivalGate) {
        this.arrivalGate = arrivalGate;
    }

    /**
     * @return the arrivalDatel
     */
    public Date getArrivalDatel() {
        return arrivalDatel;
    }

    /**
     * @param arrivalDatel the arrivalDatel to set
     */
    public void setArrivalDatel(Date arrivalDatel) {
        this.arrivalDatel = arrivalDatel;
    }

    /**
     * @return the arrivalTime
     */
    public Time getArrivalTime() {
        return arrivalTime;
    }

    /**
     * @param arrivalTime the arrivalTime to set
     */
    public void setArrivalTime(Time arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    /**
     * @return the aircraft
     */
    public Aircraft getAircraft() {
        return aircraft;
    }

    /**
     * @param aircraft the aircraft to set
     */
    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }

    /**
     * @return the leg
     */
    public Leg getLeg() {
        return leg;
    }

    /**
     * @param leg the leg to set
     */
    public void setLeg(Leg leg) {
        this.leg = leg;
    }

    /**
     * @return the flight
     */
    public Flight getFlight() {
        return flight;
    }

    /**
     * @param flight the flight to set
     */
    public void setFlight(Flight flight) {
        this.flight = flight;
    }
    
}
