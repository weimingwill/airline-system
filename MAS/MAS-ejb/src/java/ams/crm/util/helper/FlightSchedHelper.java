/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.util.helper;

import ams.ais.entity.FlightScheduleBookingClass;
import ams.ais.util.helper.FlightSchedBookingClsHelper;
import ams.aps.entity.FlightSchedule;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author weiming
 */
public class FlightSchedHelper implements Serializable{
    private FlightSchedule flightSched;
    private FlightSchedule nextFlightSched;
    private List<FlightScheduleBookingClass> flightSchedBookingClses;
    private List<FlightSchedBookingClsHelper> fbHelpers;
    private String totalDur;

    public FlightSchedule getFlightSched() {
        return flightSched;
    }

    public void setFlightSched(FlightSchedule flightSched) {
        this.flightSched = flightSched;
    }

    public List<FlightScheduleBookingClass> getFlightSchedBookingClses() {
        return flightSchedBookingClses;
    }

    public void setFlightSchedBookingClses(List<FlightScheduleBookingClass> flightSchedBookingClses) {
        this.flightSchedBookingClses = flightSchedBookingClses;
    }

    public List<FlightSchedBookingClsHelper> getFbHelpers() {
        return fbHelpers;
    }

    public void setFbHelpers(List<FlightSchedBookingClsHelper> fbHelpers) {
        this.fbHelpers = fbHelpers;
    }

    public String getTotalDur() {
        return totalDur;
    }

    public void setTotalDur(String totalDur) {
        this.totalDur = totalDur;
    }

    public FlightSchedule getNextFlightSched() {
        return nextFlightSched;
    }

    public void setNextFlightSched(FlightSchedule nextFlightSched) {
        this.nextFlightSched = nextFlightSched;
    }
}
