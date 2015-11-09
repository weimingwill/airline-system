/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.crm;

import ams.ais.entity.FlightScheduleBookingClass;
import ams.aps.entity.FlightSchedule;
import java.io.Serializable;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author weiming
 */
@Named(value = "bookingBacking")
@ViewScoped
public class BookingBacking implements Serializable {
    
    private FlightSchedule flightSchedule;
    private FlightScheduleBookingClass selectedFlightSchedBookignCls;

    /**
     * Creates a new instance of BookingBacking
     */
    public BookingBacking() {
    }

    public FlightSchedule getFlightSchedule() {
        return flightSchedule;
    }

    public void setFlightSchedule(FlightSchedule flightSchedule) {
        this.flightSchedule = flightSchedule;
    }

    public FlightScheduleBookingClass getSelectedFlightSchedBookignCls() {
        return selectedFlightSchedBookignCls;
    }

    public void setSelectedFlightSchedBookignCls(FlightScheduleBookingClass selectedFlightSchedBookignCls) {
        this.selectedFlightSchedBookignCls = selectedFlightSchedBookignCls;
    }
    
    
    
}
