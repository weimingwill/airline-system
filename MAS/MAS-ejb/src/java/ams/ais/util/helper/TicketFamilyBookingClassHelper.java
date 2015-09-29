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
public class TicketFamilyBookingClassHelper {
    private String ticketFamilyName;
    private List<BookingClass> bookingClass;

    public TicketFamilyBookingClassHelper(String ticketFamilyName, List<BookingClass> bookingClass) {
        this.ticketFamilyName = ticketFamilyName;
        this.bookingClass = bookingClass;
    }

    public String getTicketFamilyName() {
        return ticketFamilyName;
    }

    public void setTicketFamilyName(String ticketFamilyName) {
        this.ticketFamilyName = ticketFamilyName;
    }

    public List<BookingClass> getBookingClass() {
        return bookingClass;
    }

    public void setBookingClass(List<BookingClass> bookingClass) {
        this.bookingClass = bookingClass;
    }
    
}
