/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.util.helper;

import ams.ars.entity.Booking;
import ams.crm.entity.Customer;
import java.util.List;

/**
 *
 * @author weiming
 */
public class BookingHelper {
    private Booking booking;
    private List<Customer> adults;
    private List<Customer> children;

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public List<Customer> getAdults() {
        return adults;
    }

    public void setAdults(List<Customer> adults) {
        this.adults = adults;
    }

    public List<Customer> getChildren() {
        return children;
    }

    public void setChildren(List<Customer> children) {
        this.children = children;
    }


}
