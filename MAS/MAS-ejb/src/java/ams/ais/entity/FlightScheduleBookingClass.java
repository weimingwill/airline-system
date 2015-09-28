/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.entity;

import ams.ais.helper.FlightScheduleBookingClassId;
import ams.aps.entity.FlightSchedule;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 *
 * @author winga_000
 */
@Entity
@IdClass(FlightScheduleBookingClassId.class)
@Table(name = "FLIGHTSCHEDULE_BOOKINGCLASS")
public class FlightScheduleBookingClass implements Serializable {

    @Id
    private Long flightScheduleId;
    @Id
    private Long bookingClassId;

    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "FLIGHTSCHEDULEID", referencedColumnName = "FLIGHTSCHEDULEID")
    private FlightSchedule flightSchedule;

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "BOOKINGCLASSID", referencedColumnName = "BOOKINGCLASSID")
    private BookingClass bookingClass;
    
    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, mappedBy = "flightScheduleBookingClass")
    @Column(name = "SEATALLOCATIONHISTORY")
    private List<SeatAllocationHistory> seatAllocationHistory = new ArrayList<>();


    @Column(name = "SEATQTY")
    private int seatQty;
    
    @Column(name = "PRICE")
    private float price;
    
    @Column(name = "PRICECOEFFICIENT")
    private float priceCoefficient;

    @Column(name = "DEMANDMEAN")
    private float demandMean;
    
    @Column(name = "DEMANDDEV")
    private float demandDev;

    public Long getFlightScheduleId() {
        return flightScheduleId;
    }

    public void setFlightScheduleId(Long flightScheduleId) {
        this.flightScheduleId = flightScheduleId;
    }

    public Long getBookingClassId() {
        return bookingClassId;
    }

    public void setBookingClassId(Long bookingClassId) {
        this.bookingClassId = bookingClassId;
    }

    public FlightSchedule getFlightSchedule() {
        return flightSchedule;
    }

    public void setFlightSchedule(FlightSchedule flightSchedule) {
        this.flightSchedule = flightSchedule;
    }

    public BookingClass getBookingClass() {
        return bookingClass;
    }

    public void setBookingClass(BookingClass bookingClass) {
        this.bookingClass = bookingClass;
    }

    public int getSeatQty() {
        return seatQty;
    }

    public void setSeatQty(int seatQty) {
        this.seatQty = seatQty;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getPriceCoefficient() {
        return priceCoefficient;
    }

    public void setPriceCoefficient(float priceCoefficient) {
        this.priceCoefficient = priceCoefficient;
    }

    public float getDemandMean() {
        return demandMean;
    }

    public void setDemandMean(float demandMean) {
        this.demandMean = demandMean;
    }

    public float getDemandDev() {
        return demandDev;
    }

    public void setDemandDev(float demandDev) {
        this.demandDev = demandDev;
    }

    public List<SeatAllocationHistory> getSeatAllocationHistory() {
        return seatAllocationHistory;
    }

    public void setSeatAllocationHistory(List<SeatAllocationHistory> seatAllocationHistory) {
        this.seatAllocationHistory = seatAllocationHistory;
    }
    
    
    
}
