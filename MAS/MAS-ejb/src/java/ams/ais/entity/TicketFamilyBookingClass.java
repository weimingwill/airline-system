/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.entity;

import ams.ais.helper.TicketFamilyBookingClassId;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.Table;

/**
 *
 * @author winga_000
 */
@Entity
@Table(name = "TICKETFAMILY_BOOKINGCLASS")
public class TicketFamilyBookingClass implements Serializable {

    @EmbeddedId
    private TicketFamilyBookingClassId ticketFamilyBookingClassId;

//    @MapsId("cabinClassTicketFamilyId")
    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumns({
        @PrimaryKeyJoinColumn(name = "AIRCRAFTID", referencedColumnName = "AIRCRAFTID"),
        @PrimaryKeyJoinColumn(name = "CABINCLASSID", referencedColumnName = "CABINCLASSID"),
        @PrimaryKeyJoinColumn(name = "TICKETFAMILYID", referencedColumnName = "TICKETFAMILYID")
    })
    private CabinClassTicketFamily cabinClassTicketFamily;

//    @MapsId("bookingClassId")
    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "BOOKINGCLASSID", referencedColumnName = "BOOKINGCLASSID")
    private BookingClass bookingClass;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private List<SeatAllocationHistory> seatAllocationHistorys;


    @Column(name = "SEATQTY")
    private int seatQty;

    public TicketFamilyBookingClassId getTicketFamilyBookingClassId() {
        return ticketFamilyBookingClassId;
    }

    public List<SeatAllocationHistory> getSeatAllocationHistorys() {
        return seatAllocationHistorys;
    }

    public void setSeatAllocationHistorys(List<SeatAllocationHistory> seatAllocationHistorys) {
        this.seatAllocationHistorys = seatAllocationHistorys;
    }
    
    public void setTicketFamilyBookingClassId(TicketFamilyBookingClassId ticketFamilyBookingClassId) {
        this.ticketFamilyBookingClassId = ticketFamilyBookingClassId;
    }

    public CabinClassTicketFamily getCabinClassTicketFamily() {
        return cabinClassTicketFamily;
    }

    public void setCabinClassTicketFamily(CabinClassTicketFamily cabinClassTicketFamily) {
        this.cabinClassTicketFamily = cabinClassTicketFamily;
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

}
