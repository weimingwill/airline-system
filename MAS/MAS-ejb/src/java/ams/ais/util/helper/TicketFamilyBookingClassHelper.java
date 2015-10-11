/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.util.helper;

import ams.ais.entity.BookingClass;
import ams.ais.entity.TicketFamily;
import java.util.List;

/**
 *
 * @author winga_000
 */
public class TicketFamilyBookingClassHelper {
    private TicketFamily ticketFamily;
    private int seatQty;
    private float price;
    private List<BookingClassHelper> bookingClassHelpers;
    private List<BookingClass> bookingClasses;

    

    public TicketFamilyBookingClassHelper() {
    }

    public TicketFamilyBookingClassHelper(TicketFamily ticketFamily, int seatQty, List<BookingClassHelper> bookingClassHelpers) {
        this.ticketFamily = ticketFamily;
        this.seatQty = seatQty;
        this.bookingClassHelpers = bookingClassHelpers;
    }

    public TicketFamily getTicketFamily() {
        return ticketFamily;
    }

    public void setTicketFamily(TicketFamily ticketFamily) {
        this.ticketFamily = ticketFamily;
    }

    public List<BookingClassHelper> getBookingClassHelpers() {
        return bookingClassHelpers;
    }

    public void setBookingClassHelpers(List<BookingClassHelper> bookingClassHelpers) {
        this.bookingClassHelpers = bookingClassHelpers;
    }

    public int getSeatQty() {
        return seatQty;
    }

    public void setSeatQty(int seatQty) {
        this.seatQty = seatQty;
    }

    public List<BookingClass> getBookingClasses() {
        return bookingClasses;
    }

    public void setBookingClasses(List<BookingClass> bookingClasses) {
        this.bookingClasses = bookingClasses;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
