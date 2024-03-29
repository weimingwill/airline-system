/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.entity;

import ams.afos.entity.Checklist;
import ams.ais.entity.FlightScheduleBookingClass;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author ChuningLiu
 */
@Entity
public class FlightSchedule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightScheduleId;
    private String departTerminal;
    private String departGate;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date departDate;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date actualDepartDate;
    private String arrivalTerminal;
    private String arrivalGate;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date arrivalDate;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date actualArrivalDate;
    private Boolean deleted;
    private Boolean completed;
    private Boolean seatAllocated;
    private Boolean priced;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date createdTime;
    private String status;
    private Double turnoverTime;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, mappedBy = "flightSchedule")
    private List<FlightScheduleBookingClass> flightScheduleBookingClasses = new ArrayList<>();
    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Aircraft aircraft;
    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Leg leg;
    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Flight flight;
    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private FlightSchedule preFlightSched;
    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private FlightSchedule nextFlightSched;
    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private List<FlightScheduleSeat> flightSchedSeats;
    @ManyToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private List<Checklist> checklists;
    
    public Long getFlightScheduleId() {
        return flightScheduleId;
    }

    public void setFlightScheduleId(Long flightScheduleId) {
        this.flightScheduleId = flightScheduleId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (flightScheduleId != null ? flightScheduleId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the flightScheduleId fields are not set
        if (!(object instanceof FlightSchedule)) {
            return false;
        }
        FlightSchedule other = (FlightSchedule) object;
        if ((this.flightScheduleId == null && other.flightScheduleId != null) || (this.flightScheduleId != null && !this.flightScheduleId.equals(other.flightScheduleId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.aps.entity.FlightSchedule[ flightScheduleId=" + flightScheduleId + " ]";
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

    public Date getDepartDate() {
        return departDate;
    }

    public void setDepartDate(Date departDate) {
        this.departDate = departDate;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public List<FlightScheduleBookingClass> getFlightScheduleBookingClasses() {
        return flightScheduleBookingClasses;
    }

    public void setFlightScheduleBookingClasses(List<FlightScheduleBookingClass> flightScheduleBookingClasses) {
        this.flightScheduleBookingClasses = flightScheduleBookingClasses;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Boolean getSeatAllocated() {
        return seatAllocated;
    }

    public void setSeatAllocated(Boolean seatAllocated) {
        this.seatAllocated = seatAllocated;
    }

    public Boolean getPriced() {
        return priced;
    }

    public void setPriced(Boolean priced) {
        this.priced = priced;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Double getTurnoverTime() {
        return turnoverTime;
    }

    public void setTurnoverTime(Double turnoverTime) {
        this.turnoverTime = turnoverTime;
    }

    public FlightSchedule getPreFlightSched() {
        return preFlightSched;
    }

    public void setPreFlightSched(FlightSchedule preFlightSched) {
        this.preFlightSched = preFlightSched;
    }

    public FlightSchedule getNextFlightSched() {
        return nextFlightSched;
    }

    public void setNextFlightSched(FlightSchedule nextFlightSched) {
        this.nextFlightSched = nextFlightSched;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<FlightScheduleSeat> getFlightSchedSeats() {
        return flightSchedSeats;
    }

    public void setFlightSchedSeats(List<FlightScheduleSeat> flightSchedSeats) {
        this.flightSchedSeats = flightSchedSeats;
    }

    /**
     * @return the actualDepartDate
     */
    public Date getActualDepartDate() {
        return actualDepartDate;
    }

    /**
     * @param actualDepartDate the actualDepartDate to set
     */
    public void setActualDepartDate(Date actualDepartDate) {
        this.actualDepartDate = actualDepartDate;
    }

    /**
     * @return the actualArrivalDate
     */
    public Date getActualArrivalDate() {
        return actualArrivalDate;
    }

    /**
     * @param actualArrivalDate the actualArrivalDate to set
     */
    public void setActualArrivalDate(Date actualArrivalDate) {
        this.actualArrivalDate = actualArrivalDate;
    }

    /**
     * @return the checklists
     */
    public List<Checklist> getChecklists() {
        return checklists;
    }

    /**
     * @param checklists the checklists to set
     */
    public void setChecklists(List<Checklist> checklists) {
        this.checklists = checklists;
    }

}
