/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftType;
import ams.aps.entity.Flight;
import ams.aps.entity.FlightSchedule;
import ams.aps.session.FlightSchedulingSessionLocal;
import ams.aps.session.RoutePlanningSessionLocal;
import ams.aps.util.exception.ExistSuchFlightScheduleException;
import ams.aps.util.exception.NoMoreUnscheduledFlightException;
import ams.aps.util.exception.NoSelectAircraftException;
import ams.aps.util.exception.NoSuchAircraftException;
import ams.aps.util.exception.NoSuchFlightException;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import ams.aps.util.exception.NoSuchRouteException;
import ams.aps.util.helper.FlightSchedMethod;
import ams.aps.util.helper.RouteHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import managedbean.application.MsgController;
import org.primefaces.context.RequestContext;
import org.primefaces.extensions.component.timeline.TimelineUpdater;
import org.primefaces.extensions.event.timeline.TimelineModificationEvent;
import org.primefaces.extensions.model.timeline.TimelineEvent;
import org.primefaces.extensions.model.timeline.TimelineGroup;
import org.primefaces.extensions.model.timeline.TimelineModel;

/**
 *
 * @author weiming
 */
@Named(value = "flightScheduleBacking")
@ViewScoped
public class FlightScheduleBacking implements Serializable {

    @Inject
    MsgController msgController;

    @EJB
    private FlightSchedulingSessionLocal flightSchedulingSession;
    @EJB
    private RoutePlanningSessionLocal routePlanningSession;

    private List<Aircraft> aircrafts = new ArrayList<>();
    private List<Aircraft> selectedAircrafts = new ArrayList<>();
    private Aircraft selectedAircraft;
    private List<FlightSchedule> flightSchedules = new ArrayList<>();
    private Date startDate;
    private Date endDate;

    private String deptAirport = new String();
    private String arriveAirport = new String();
    private String flightNo;
    private Date deptDate;
    private Date arrDate;
    private TimelineEvent event = new TimelineEvent();
    private Date calendarMinDate;
    private Date calendarMaxDate;
    private int fixedEndHour;
    private int fixedEndMinute;
    private Date fixedEndDate;
    private double routeDuration;

    private TimelineModel model;
    private long zoomMin;
    private long zoomMax;
    private FlightSchedule oldFlightSched;

    @PostConstruct
    public void init() {
        initializeDate();
        initializeAircraft();
        setTimelineFlightSched();
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
        calendar.add(Calendar.YEAR, 2);
        endDate = calendar.getTime();
        setZoomMin(1000L * 60 * 60 * 12); //Visible interval is limited to a minimum of 8 hours
    }

    public void initializeAircraft() {
        model = new TimelineModel();
        for (Aircraft aircraft : flightSchedulingSession.getAllAircrafts()) {
            aircrafts.add(aircraft);
            selectedAircrafts.add(aircraft);
        }
    }

    public void setTimelineFlightSched() {
        model = new TimelineModel();
        for (Aircraft aircraft : selectedAircrafts) {
            flightSchedules = flightSchedulingSession.getFlightSchedulesByTailNoAndTime(aircraft.getTailNo(), startDate, endDate, FlightSchedMethod.DISPLAY);
            TimelineGroup timelineGroup = new TimelineGroup(aircraft.getTailNo(), aircraft);
            model.addGroup(timelineGroup);
            if (flightSchedules.isEmpty()) {
                model.add(new TimelineEvent(null, new Date(), new Date(), true, aircraft.getTailNo()));
            }
            for (FlightSchedule flightSchedule : flightSchedules) {
                if (flightSchedule.getPreFlightSched() == null) {
                    flightSchedulingSession.setRouteFlightSchedule(flightSchedule);
                    model.add(new TimelineEvent(flightSchedule, flightSchedule.getDepartDate(), flightSchedule.getArrivalDate(), true, aircraft.getTailNo()));
                }
            }
        }
    }

    public void resetFilters() {
        selectedAircrafts = new ArrayList<>();
    }
    
    public void onFilterChange(){
        setTimelineFlightSched();
    }

    public void onFlightSchedChange(TimelineModificationEvent e) {
        event = e.getTimelineEvent();
        oldFlightSched = (FlightSchedule) event.getData();
        deptDate = event.getStartDate();
        arrDate = event.getEndDate();
        Flight flight = oldFlightSched.getFlight();
        flightNo = flight.getFlightNo() + "/" + flight.getReturnedFlight().getFlightNo();
        try {
            selectedAircraft = flightSchedulingSession.getAircraftByTailNo(event.getGroup());
        } catch (NoSuchAircraftException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        updateFlightSchedule();
    }

    public void onFlightSchedSelect(TimelineModificationEvent e) {
        event = e.getTimelineEvent();
        oldFlightSched = (FlightSchedule) event.getData();
        Flight flight = oldFlightSched.getFlight();
        RouteHelper routeHelper = new RouteHelper();
        initializeRouteHelper(flight, routeHelper);
        setRouteDuration(routeHelper);
        deptAirport = routeHelper.getOrigin().getAirportName();
        arriveAirport = routeHelper.getDestination().getAirportName();
        flightNo = flight.getFlightNo() + "/" + flight.getReturnedFlight().getFlightNo();
        deptDate = oldFlightSched.getDepartDate();
        arrDate = flightSchedulingSession.addHourToDate(deptDate, routeDuration);
    }

    public void onFlightScheduleDelete(TimelineModificationEvent e) {
        event = e.getTimelineEvent();
        oldFlightSched = (FlightSchedule) event.getData();
    }

    public void updateFlightSchedule() {
        try {
            System.out.println("Update flight schedule time: " + deptDate + " - " + arrDate);
            FlightSchedule newFlightSched = flightSchedulingSession.updateFlightSchedule(flightNo.split("/")[0], selectedAircraft, deptDate, arrDate, calendarMinDate, calendarMaxDate, oldFlightSched);
            flightSchedulingSession.setRouteFlightSchedule(newFlightSched);
            TimelineUpdater timelineUpdater = TimelineUpdater.getCurrentInstance(":form:timeline");
            event = new TimelineEvent(newFlightSched, deptDate, arrDate, true, selectedAircraft.getTailNo());
            model.update(event, timelineUpdater);
            msgController.addMessage("Update flight schedule succesffuly");
            setTimelineFlightSched();
        } catch (NoSuchFlightException | NoMoreUnscheduledFlightException | NoSelectAircraftException | NoSuchFlightSchedulException | ExistSuchFlightScheduleException e) {
            msgController.addErrorMessage(e.getMessage());
        }
    }

    public void deleteFlightSchedule() {
        try {
            flightSchedulingSession.deleteFlightSchedule(oldFlightSched, FlightSchedMethod.DELETE);
            flightSchedulingSession.setRouteFlightSchedule(oldFlightSched);
            TimelineUpdater timelineUpdater = TimelineUpdater.getCurrentInstance(":form:timeline");
            event = new TimelineEvent(oldFlightSched, oldFlightSched.getDepartDate(), oldFlightSched.getArrivalDate(), true, oldFlightSched.getAircraft().getTailNo());
            model.delete(event, timelineUpdater);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('deleteFlightSchedDialog').hide()");
            msgController.addMessage("Delete fligth schedule succesffully");
            setTimelineFlightSched();
        } catch (NoSuchFlightSchedulException e) {
            msgController.addErrorMessage(e.getMessage());
        }
    }

    public void onStartTimeChange() {
        calcFixedEndTime();
    }

    private void calcFixedEndTime() {
        arrDate = flightSchedulingSession.addHourToDate(deptDate, routeDuration);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(arrDate);
        fixedEndDate = calendar.getTime();
        fixedEndHour = calendar.get(Calendar.HOUR_OF_DAY);
        fixedEndMinute = calendar.get(Calendar.MINUTE);
    }

    private void initializeRouteHelper(Flight flight, RouteHelper routeHelper) {
        routePlanningSession.getRouteDetail(flight.getRoute(), routeHelper);
        flightSchedulingSession.calcFlightDuration(getModelWithMinMachNo(flight), routeHelper, flight.getSpeedFraction());
    }

    private AircraftType getModelWithMinMachNo(Flight flight) {
        return flightSchedulingSession.getModelWithMinMachNo(flight.getAircraftTypes());
    }

    private void setRouteDuration(RouteHelper routeHelper) {
        routeDuration = routeHelper.getTotalDuration() * 2;
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

    public String getDeptAirport() {
        return deptAirport;
    }

    public void setDeptAirport(String deptAirport) {
        this.deptAirport = deptAirport;
    }

    public String getArriveAirport() {
        return arriveAirport;
    }

    public void setArriveAirport(String arriveAirport) {
        this.arriveAirport = arriveAirport;
    }

    public String getFlightNo() {
        return flightNo;
    }

    public void setFlightNo(String flightNo) {
        this.flightNo = flightNo;
    }

    public Date getDeptDate() {
        return deptDate;
    }

    public void setDeptDate(Date deptDate) {
        this.deptDate = deptDate;
    }

    public Date getArrDate() {
        return arrDate;
    }

    public void setArrDate(Date arrDate) {
        this.arrDate = arrDate;
    }

    public Date getCalendarMinDate() {
        return calendarMinDate;
    }

    public void setCalendarMinDate(Date calendarMinDate) {
        this.calendarMinDate = calendarMinDate;
    }

    public Date getCalendarMaxDate() {
        return calendarMaxDate;
    }

    public void setCalendarMaxDate(Date calendarMaxDate) {
        this.calendarMaxDate = calendarMaxDate;
    }

    public int getFixedEndHour() {
        return fixedEndHour;
    }

    public void setFixedEndHour(int fixedEndHour) {
        this.fixedEndHour = fixedEndHour;
    }

    public int getFixedEndMinute() {
        return fixedEndMinute;
    }

    public void setFixedEndMinute(int fixedEndMinute) {
        this.fixedEndMinute = fixedEndMinute;
    }

    public Date getFixedEndDate() {
        return fixedEndDate;
    }

    public void setFixedEndDate(Date fixedEndDate) {
        this.fixedEndDate = fixedEndDate;
    }

    public Aircraft getSelectedAircraft() {
        return selectedAircraft;
    }

    public void setSelectedAircraft(Aircraft selectedAircraft) {
        this.selectedAircraft = selectedAircraft;
    }

}
