/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.crm;

import ams.ais.entity.FlightScheduleBookingClass;
import ams.aps.entity.FlightSchedule;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
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

    //Passenger details
    private List<String> titles = new ArrayList<>();
    private List<String> genders = new ArrayList<>();
    /**
     * Creates a new instance of BookingBacking
     */
    @PostConstruct
    public void init() {
        setTitleList();
        setGenders();
    }
    
    public BookingBacking() {
    }

    public void setTitleList() {
        titles.add("Ms");
        titles.add("Mrs");
        titles.add("Mr");
        titles.add("Ms Dr.");
        titles.add("Ms Prof.");
        titles.add("Mr Dr.");
        titles.add("Mr Prof.");
    }

    public void setGenders() {
        genders.add("Male");
        genders.add("Female");
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

    public List<String> getTitles() {
        return titles;
    }

    public void setTitles(List<String> titles) {
        this.titles = titles;
    }

    public List<String> getGenders() {
        return genders;
    }

    public void setGenders(List<String> genders) {
        this.genders = genders;
    }
    
    
}
