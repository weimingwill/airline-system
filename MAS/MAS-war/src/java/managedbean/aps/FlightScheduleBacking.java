/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import ams.aps.entity.Aircraft;
import ams.aps.entity.FlightSchedule;
import ams.aps.session.FlightSchedulingSessionLocal;
import ams.aps.util.helper.GetFlightSchedMethod;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.extensions.model.timeline.TimelineEvent;
import org.primefaces.extensions.model.timeline.TimelineModel;

/**
 *
 * @author weiming
 */
@Named(value = "flightScheduleBacking")
@ViewScoped
public class FlightScheduleBacking implements Serializable{

    @EJB
    private FlightSchedulingSessionLocal flightSchedulingSession;

    private List<Aircraft> aircrafts;
    private List<Aircraft> selectedAircrafts;
    private List<FlightSchedule> flightSchedules = new ArrayList<>();
    private Date startDate;
    private Date endDate;

    private TimelineModel model;
    private long zoomMin;
    private long zoomMax;

    @PostConstruct
    public void init() {
        initializeDate();
        initializeFlightSchedule();
        display();
    }

    /**
     * Creates a new instance of FligtScheduleBacking
     */
    public FlightScheduleBacking() {
    }

    public void initializeDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        startDate = calendar.getTime();
        calendar.add(Calendar.YEAR, 1);
        endDate = calendar.getTime();
        setZoomMin(1000L * 60 * 60 * 12); //Visible interval is limited to a minimum of 8 hours
    }

    public void initializeFlightSchedule() {
        Set<FlightSchedule> flightSchedHs = new HashSet<>();
        for (Aircraft aircraft : flightSchedulingSession.getAllAircrafts()) {
            flightSchedHs.addAll(flightSchedulingSession.getFlightSchedulesByTailNoAndTime(aircraft.getTailNo(), startDate, endDate, GetFlightSchedMethod.DISPLAY));
        }
        flightSchedules.addAll(flightSchedHs);
    }

    public void applyFilters() {
        Set<FlightSchedule> flightSchedHs = new HashSet<>();
        for (Aircraft aircraft : selectedAircrafts) {
            flightSchedHs.addAll(flightSchedulingSession.getFlightSchedulesByTailNoAndTime(aircraft.getTailNo(), startDate, endDate, GetFlightSchedMethod.DISPLAY));
        }
        flightSchedules.addAll(flightSchedHs);
    }

    public void resetFilters() {
        selectedAircrafts = new ArrayList<>();
    }

    public void display() {
        model = new TimelineModel();
        for (FlightSchedule flightSchedule : flightSchedules) {
            if (flightSchedules.isEmpty()) {
                model.add(new TimelineEvent(null, new Date(), new Date(), true, flightSchedule.getAircraft().getTailNo()));
            }
            if (flightSchedule.getPreFlightSched() == null) {
                System.out.println("Flight Schedule: " + flightSchedule);
                flightSchedulingSession.setRouteFlightSchedule(flightSchedule);
                model.add(new TimelineEvent(flightSchedule, flightSchedule.getDepartDate(), flightSchedule.getArrivalDate(), true, flightSchedule.getAircraft().getTailNo()));
            }
        }
    }

    //
    //Getter and Setter
    //
    public List<Aircraft> getAircrafts() {
        return aircrafts;
    }

    public void setAircrafts(List<Aircraft> aircrafts) {
        this.aircrafts = aircrafts;
    }

    public List<Aircraft> getSelectedAircrafts() {
        return selectedAircrafts;
    }

    public void setSelectedAircrafts(List<Aircraft> selectedAircrafts) {
        this.selectedAircrafts = selectedAircrafts;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public TimelineModel getModel() {
        return model;
    }

    public void setModel(TimelineModel model) {
        this.model = model;
    }

    public long getZoomMin() {
        return zoomMin;
    }

    public void setZoomMin(long zoomMin) {
        this.zoomMin = zoomMin;
    }

    public long getZoomMax() {
        return zoomMax;
    }

    public void setZoomMax(long zoomMax) {
        this.zoomMax = zoomMax;
    }

}
