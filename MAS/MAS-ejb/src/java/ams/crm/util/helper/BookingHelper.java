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
    private List<Customer> customers;

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }
}
