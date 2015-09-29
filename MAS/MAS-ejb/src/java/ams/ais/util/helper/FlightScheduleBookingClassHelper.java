/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.util.helper;

import ams.ais.entity.CabinClass;
import ams.ais.entity.TicketFamily;
import java.util.List;

/**
 *
 * @author winga_000
 */
public class FlightScheduleBookingClassHelper {
    private int id;
    private CabinClass cabinClass;
    private List<TicketFamily> ticketFamilys;
    private boolean haveTicketFamily;
//    private List<SeatClassHelper> ticketFamilyBookingClassHelpers;

    public FlightScheduleBookingClassHelper() {
    }

    
    public FlightScheduleBookingClassHelper(int id, CabinClass cabinClass) {
        this.id = id;
        this.cabinClass = cabinClass;
    }    

    public FlightScheduleBookingClassHelper(int id, CabinClass cabinClass, List<TicketFamily> ticketFamilys) {
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

//    public List<SeatClassHelper> getTicketFamilyBookingClassHelpers() {
//        return ticketFamilyBookingClassHelpers;
//    }
//
//    public void setTicketFamilyBookingClassHelpers(List<SeatClassHelper> ticketFamilyBookingClassHelpers) {
//        this.ticketFamilyBookingClassHelpers = ticketFamilyBookingClassHelpers;
//    }
    
    
}
