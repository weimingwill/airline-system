/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.util.helper;

import ams.ais.entity.CabinClass;
import java.util.List;

/**
 *
 * @author winga_000
 */
public class FlightSchCabinClsTicFamBookingClsHelper {
    private CabinClass cabinClass;
    private int seatQty;
    private List<TicketFamilyBookingClassHelper> ticketFamilyBookingClassHelpers;

    public FlightSchCabinClsTicFamBookingClsHelper() {
    }

    public FlightSchCabinClsTicFamBookingClsHelper(CabinClass cabinClass, int seatQty, List<TicketFamilyBookingClassHelper> ticketFamilyBookingClassHelpers) {
        this.cabinClass = cabinClass;
        this.seatQty = seatQty;
        this.ticketFamilyBookingClassHelpers = ticketFamilyBookingClassHelpers;
    }

    public CabinClass getCabinClass() {
        return cabinClass;
    }

    public void setCabinClass(CabinClass cabinClass) {
        this.cabinClass = cabinClass;
    }

    public List<TicketFamilyBookingClassHelper> getTicketFamilyBookingClassHelpers() {
        return ticketFamilyBookingClassHelpers;
    }

    public void setTicketFamilyBookingClassHelpers(List<TicketFamilyBookingClassHelper> ticketFamilyBookingClassHelpers) {
        this.ticketFamilyBookingClassHelpers = ticketFamilyBookingClassHelpers;
    }

    public int getSeatQty() {
        return seatQty;
    }

    public void setSeatQty(int seatQty) {
        this.seatQty = seatQty;
    }
}
