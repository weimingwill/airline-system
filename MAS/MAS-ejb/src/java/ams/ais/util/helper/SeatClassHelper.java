/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.util.helper;

import ams.ais.entity.BookingClass;
import java.util.List;

/**
 *
 * @author winga_000
 */
public class SeatClassHelper {

    private String cabinClassName;
    private String ticketFamilyName;
    private List<BookingClass> bookingClasses;

    public SeatClassHelper(String cabinClassName, String ticketFamilyName) {
        this.cabinClassName = cabinClassName;
        this.ticketFamilyName = ticketFamilyName;
    }

    public SeatClassHelper(String cabinClassName, String ticketFamilyName, List<BookingClass> bookingClasses) {
        this.cabinClassName = cabinClassName;
        this.ticketFamilyName = ticketFamilyName;
        this.bookingClasses = bookingClasses;
    }
    
    public String getCabinClassName() {
        return cabinClassName;
    }

    public void setCabinClassName(String cabinClassName) {
        this.cabinClassName = cabinClassName;
    }

    public String getTicketFamilyName() {
        return ticketFamilyName;
    }

    public void setTicketFamilyName(String ticketFamilyName) {
        this.ticketFamilyName = ticketFamilyName;
    }

    public List<BookingClass> getBookingClasses() {
        return bookingClasses;
    }

    public void setBookingClasses(List<BookingClass> bookingClasses) {
        this.bookingClasses = bookingClasses;
    }
    
}
