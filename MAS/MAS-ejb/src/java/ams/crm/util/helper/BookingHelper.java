/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.util.helper;

import ams.ais.entity.FlightScheduleBookingClass;
import ams.ars.entity.Booking;
import ams.ars.entity.PricingItem;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author weiming
 */
public class BookingHelper implements Serializable{
    private Booking booking;
    private List<CustomerHelper> customers;
    private List<CustomerHelper> adults;
    private List<CustomerHelper> children;
    private String channel;
    private List<FlightScheduleBookingClass> flightSchedBookingClses;
    
    private List<PricingItem> pricingItems;

    private double totalPrice;
    private String promoCode;
    
    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public List<CustomerHelper> getCustomers() {
        return customers;
    }

    public void setCustomers(List<CustomerHelper> customers) {
        this.customers = customers;
    }

    public List<CustomerHelper> getChildren() {
        return children;
    }

    public void setChildren(List<CustomerHelper> children) {
        this.children = children;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public List<FlightScheduleBookingClass> getFlightSchedBookingClses() {
        return flightSchedBookingClses;
    }

    public void setFlightSchedBookingClses(List<FlightScheduleBookingClass> flightSchedBookingClses) {
        this.flightSchedBookingClses = flightSchedBookingClses;
    }

    public List<PricingItem> getPricingItems() {
        return pricingItems;
    }

    public void setPricingItems(List<PricingItem> pricingItems) {
        this.pricingItems = pricingItems;
    }


    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<CustomerHelper> getAdults() {
        return adults;
    }

    public void setAdults(List<CustomerHelper> adults) {
        this.adults = adults;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }
}
