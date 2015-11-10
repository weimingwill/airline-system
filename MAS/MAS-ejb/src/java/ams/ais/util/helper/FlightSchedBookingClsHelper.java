/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.util.helper;

import ams.ais.entity.CabinClass;
import ams.ais.entity.FlightScheduleBookingClass;
import ams.ais.entity.TicketFamily;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author winga_000
 */
public class FlightSchedBookingClsHelper implements Serializable{
    private int id;
    private CabinClass cabinClass;
    private List<TicketFamily> ticketFamilys;
    private boolean haveTicketFamily;
//    private List<SeatClassHelper> ticketFamilyBookingClassHelpers;

    private FlightScheduleBookingClass flightSchedBookingCls;
    private int remainedSeatQty;
    private boolean available;
    private boolean showLeftSeatQty;
    private TicketFamily ticketFamily;
    private float price;
    
    public FlightSchedBookingClsHelper() {
    }

    
    public FlightSchedBookingClsHelper(int id, CabinClass cabinClass) {
        this.id = id;
        this.cabinClass = cabinClass;
    }    

    public FlightSchedBookingClsHelper(int id, CabinClass cabinClass, List<TicketFamily> ticketFamilys) {
        this.id = id;
        this.cabinClass = cabinClass;
        this.ticketFamilys = ticketFamilys;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isHaveTicketFamily() {
        return haveTicketFamily;
    }

    public void setHaveTicketFamily(boolean haveTicketFamily) {
        this.haveTicketFamily = haveTicketFamily;
    }
    
    public CabinClass getCabinClass() {
        return cabinClass;
    }

    public void setCabinClass(CabinClass cabinClass) {
        this.cabinClass = cabinClass;
    }

    public List<TicketFamily> getTicketFamilys() {
        return ticketFamilys;
    }

    public void setTicketFamilys(List<TicketFamily> ticketFamilys) {
        this.ticketFamilys = ticketFamilys;
    }

    public int getRemainedSeatQty() {
        return remainedSeatQty;
    }

    public void setRemainedSeatQty(int remainedSeatQty) {
        this.remainedSeatQty = remainedSeatQty;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public FlightScheduleBookingClass getFlightSchedBookingCls() {
        return flightSchedBookingCls;
    }

    public void setFlightSchedBookingCls(FlightScheduleBookingClass flightSchedBookingCls) {
        this.flightSchedBookingCls = flightSchedBookingCls;
    }

    public boolean isShowLeftSeatQty() {
        return showLeftSeatQty;
    }

    public void setShowLeftSeatQty(boolean showLeftSeatQty) {
        this.showLeftSeatQty = showLeftSeatQty;
    }

    public TicketFamily getTicketFamily() {
        return ticketFamily;
    }

    public void setTicketFamily(TicketFamily ticketFamily) {
        this.ticketFamily = ticketFamily;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    

    
}
