/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ars.entity;

import ams.ais.entity.FlightScheduleBookingClass;
import ams.crm.entity.Customer;
import ams.dcs.entity.CheckInLuggage;
import ams.dcs.entity.Luggage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 *
 * @author Bowen
 */
@Entity
public class AirTicket implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String status;
    
    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Booking booking;
    
    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private List<AddOn> addOns = new ArrayList<>();
    
    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Luggage purchasedLuggage;
    
    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private List<CheckInLuggage> luggages = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private List<PricingItem> pricingItems = new ArrayList<>();
    
    @OneToOne(cascade={CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private Seat seat;
    
    @OneToOne(cascade={CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private BoardingPass boardingPass;

    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Customer customer;
    
    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private FlightScheduleBookingClass flightSchedBookingClass;
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AirTicket)) {
            return false;
        }
        AirTicket other = (AirTicket) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.ars_crm.entity.Airticket[ id=" + id + " ]";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public List<AddOn> getAddOns() {
        return addOns;
    }

    public void setAddOns(List<AddOn> addOns) {
        this.addOns = addOns;
    }

    public List<PricingItem> getPricingItems() {
        return pricingItems;
    }

    public void setPricingItems(List<PricingItem> pricingItems) {
        this.pricingItems = pricingItems;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public BoardingPass getBoardingPass() {
        return boardingPass;
    }

    public void setBoardingPass(BoardingPass boardingPass) {
        this.boardingPass = boardingPass;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the luggages
     */
    public List<CheckInLuggage> getLuggages() {
        return luggages;
    }

    /**
     * @param luggages the luggages to set
     */
    public void setLuggages(List<CheckInLuggage> luggages) {
        this.luggages = luggages;
    }

    /**
     * @return the flightSchedBookingClass
     */
    public FlightScheduleBookingClass getFlightSchedBookingClass() {
        return flightSchedBookingClass;
    }

    /**
     * @param flightSchedBookingClass the flightSchedBookingClass to set
     */
    public void setFlightSchedBookingClass(FlightScheduleBookingClass flightSchedBookingClass) {
        this.flightSchedBookingClass = flightSchedBookingClass;
    }

    /**
     * @return the purchasedLuggage
     */
    public Luggage getPurchasedLuggage() {
        return purchasedLuggage;
    }

    /**
     * @param purchasedLuggage the purchasedLuggage to set
     */
    public void setPurchasedLuggage(Luggage purchasedLuggage) {
        this.purchasedLuggage = purchasedLuggage;
    }



}
