/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftType;
import ams.aps.entity.Airport;
import ams.aps.entity.Flight;
import ams.aps.entity.FlightSchedule;
import ams.aps.entity.Route;
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
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;
import mas.util.helper.DateHelper;
import org.primefaces.context.RequestContext;
import org.primefaces.event.DragDropEvent;
import org.primefaces.extensions.component.timeline.TimelineUpdater;
import org.primefaces.extensions.event.timeline.TimelineModificationEvent;
import org.primefaces.extensions.model.timeline.TimelineEvent;
import org.primefaces.extensions.model.timeline.TimelineGroup;
import org.primefaces.extensions.model.timeline.TimelineModel;

/**
 *
 * @author winga_000
 */
@Named(value = "flightScheduleManager")
@SessionScoped
public class FlightScheduleManager implements Serializable {

    @Inject
    private MsgController msgController;
    @Inject
    private NavigationController navigationController;

    @EJB
    private FlightSchedulingSessionLocal flightSchedulingSession;
    @EJB
    private RoutePlanningSessionLocal routePlanningSession;

    //select flight
    private List<String> aircraftTypeFamilys;
    private List<String> aircraftTypeCodes;
    private String selectedAircraftTypeFamily;
    private Airport selectedAirport;
    private List<Aircraft> availableAircrafts = new ArrayList<>();
    private List<String> selectedAircraftTypeCodes = new ArrayList<>();

    //schedule flight
    private TimelineModel model;
    private Aircraft selectedAircraft;
    
    private Date selectedDate;
    private Flight droppedFlight;
    private List<Airport> deptAirports = new ArrayList<>();
    private List<Flight> unscheduledFlights = new ArrayList<>();
    //dialog attribute
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
    private List<FlightSchedule> flightSchedules;
    private FlightSchedule oldFlightSched;

    //timeline
    private long zoomMin;
    private long zoomMax;
    private String schedPeriod;
    private Date schedStartDate;
    private Date schedEndDate;
    private List<FlightSchedule> collidedFlightScheds;

    /**
     * Creates a new instance of FlightScheduleManager
     */
    @PostConstruct
    public void init() {
        clearVariables();
        initializeDeptAirports();
        initializeAircraftTypeFamilys();
        initializeTimeline();
    }

    public FlightScheduleManager() {
    }

    //Initialize
    public String toSelectFlight() {
        init();
        return navigationController.redirectToSelectFlight();
    }

    public void clearVariables() {
        setSelectedAirport(null);
        setSelectedAircraftTypeFamily(null);
        setSelectedAircraftTypeCodes(new ArrayList<>());
        setDeptAirports(new ArrayList<>());
        setAircraftTypeFamilys(new ArrayList<>());
        setSelectedAircraft(null);
        setUnscheduledFlights(new ArrayList<>());
        model = new TimelineModel();
    }

    public void initializeDeptAirports() {
        try {
            for (Route route : flightSchedulingSession.getUnscheduledFlightRoutes()) {
                RouteHelper routeHelper = new RouteHelper();
                routePlanningSession.getRouteDetail(route, routeHelper);
                if (!deptAirports.contains(routeHelper.getOrigin()) && routeHelper.getOrigin().getIsHub()) {
                    deptAirports.add(routeHelper.getOrigin());
                }
            }
        } catch (NoSuchRouteException e) {
        }
    }

    public void initializeAircraftTypeFamilys() {
        aircraftTypeFamilys = flightSchedulingSession.getUnscheduledFlightAircraftTypeFamilys();
    }

    public void initializeTimeline() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, 1);
        selectedDate = calendar.getTime();
        schedStartDate = calendar.getTime();
        schedPeriod = "1";
        calcSchedEndTime();
        calcCalendarMinDate();
        calcCalendarMaxDate();
        setZoomMin(1000L * 60 * 60 * 12); //Visible interval is limited to a minimum of 8 hours
    }

    public void onAircraftTypeFamilyChange() {
        aircraftTypeCodes = flightSchedulingSession.getUnscheduledAircraftTypeCodesByTypeFamily(selectedAircraftTypeFamily);
    }

    //Select Flight
    public void applyFilters() {
        try {
            setUnscheduledFlights();
            availableAircrafts = new ArrayList<>();
            Set<Aircraft> hs = new HashSet<>();
            for (String aircraftTypeCode : selectedAircraftTypeCodes) {
                hs.addAll(flightSchedulingSession.getAircraftsByAircraftType(aircraftTypeCode));
            }
            availableAircrafts.addAll(hs);
            setTimelineGroup();
            if (unscheduledFlights.isEmpty()) {
                msgController.warn("There is no such unscheduled flight");
            }
        } catch (NoSuchFlightException | NoSuchRouteException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
    }

    //Initiate the timeline with all the flight schedules under each aircraft
    public void setTimelineGroup() {
        model = new TimelineModel();
        for (Aircraft aircraft : availableAircrafts) {
            TimelineGroup timelineGroup = new TimelineGroup(aircraft.getTailNo(), aircraft);
            model.addGroup(timelineGroup);

            flightSchedules = flightSchedulingSession.getFlightSchedulesByTailNoAndTime(aircraft.getTailNo(), calendarMinDate, calendarMaxDate, FlightSchedMethod.DISPLAY);
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
        unscheduledFlights = new ArrayList<>();
    }

    //Helper in displaying the drag Flight info panel grid
    public RouteHelper getRouteHelper(Flight flight) {
        RouteHelper routeHelper = new RouteHelper();
        routePlanningSession.getRouteDetail(flight.getRoute(), routeHelper);
        AircraftType aircraftType = flightSchedulingSession.getModelWithMinMachNo(flight.getAircraftTypes());
        flightSchedulingSession.calcFlightDuration(aircraftType, routeHelper, flight.getSpeedFraction());
        return routeHelper;
    }

    //Schedule flight
    public void onCalendarMonthChange() {
        calcCalendarMinDate();
        calcCalendarMaxDate();
        calcSchedEndTime();
    }

    public void onCalendarDateChange() {
        calcCalendarMinDate();
        calcCalendarMaxDate();
        calcSchedEndTime();
        setTimelineGroup();
    }

    public void onStartTimeChange() {
        calcFixedEndTime();
    }

    public void onSchedPeriodChange() {

    }

    public void onFlightDrop(DragDropEvent ddEvent) {
        System.out.println("Dropped Flight: ");
        droppedFlight = ((Flight) ddEvent.getData());
        RouteHelper routeHelper = new RouteHelper();
        initializeRouteHelper(droppedFlight, routeHelper);
        if (!routeHelper.getOrigin().getIsHub()) {
            droppedFlight = droppedFlight.getReturnedFlight();
            deptAirport = routeHelper.getDestination().getAirportName();
            arriveAirport = routeHelper.getOrigin().getAirportName();
        } else {
            deptAirport = routeHelper.getOrigin().getAirportName();
            arriveAirport = routeHelper.getDestination().getAirportName();
        }
        System.out.println("Dropped Flight: " + droppedFlight.getFlightNo());
        setRouteDuration(routeHelper);
        flightNo = droppedFlight.getFlightNo() + "/" + droppedFlight.getReturnedFlight().getFlightNo();
        deptDate = calendarMinDate;
        arrDate = flightSchedulingSession.addHourToDate(deptDate, routeDuration);
    }

    public void save(ActionEvent actionEvent) {
        if (event.getData() == null) {
            System.out.println("Dropped Flight:" + droppedFlight);
            createFligthSchedule();
        } else {
            System.out.println("old Flight:" + oldFlightSched);
            updateFlightSchedule();
        }
    }

    public void createFligthSchedule() {
        try {
            FlightSchedule newFlightSched = flightSchedulingSession.createFlightSchedule(droppedFlight, selectedAircraft, deptDate, arrDate, calendarMinDate, calendarMaxDate, FlightSchedMethod.CREATE);
            //Create flightSchedule to be displayed on new timelineEvent object.
            flightSchedulingSession.setRouteFlightSchedule(newFlightSched);
            model.add(new TimelineEvent(newFlightSched, deptDate, arrDate, true, selectedAircraft.getTailNo()));
            msgController.addMessage("Add flight schedule succesffuly");
            setUnscheduledFlights();
        } catch (NoSuchFlightException | NoMoreUnscheduledFlightException | NoSelectAircraftException | NoSuchRouteException | ExistSuchFlightScheduleException e) {
            msgController.addErrorMessage(e.getMessage());
        }
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
        deptAirport = routeHelper.getOrigin().getAirportName();
        arriveAirport = routeHelper.getDestination().getAirportName();
        setRouteDuration(routeHelper);
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
            FlightSchedule newFlightSched = flightSchedulingSession.updateFlightSchedule(flightNo.split("/")[0], selectedAircraft, deptDate, arrDate, calendarMinDate, calendarMaxDate, oldFlightSched);
            flightSchedulingSession.setRouteFlightSchedule(newFlightSched);
            TimelineUpdater timelineUpdater = TimelineUpdater.getCurrentInstance(":form:timeline");
            event = new TimelineEvent(newFlightSched, deptDate, arrDate, true, selectedAircraft.getTailNo());
            model.update(event, timelineUpdater);
            clearDialogVariables();

            msgController.addMessage("Update flight schedule succesffuly");
            setUnscheduledFlights();
        } catch (NoSuchFlightException | NoMoreUnscheduledFlightException | NoSelectAircraftException 
                | NoSuchRouteException | NoSuchFlightSchedulException | ExistSuchFlightScheduleException e) {
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
            clearDialogVariables();
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('deleteFlightSchedDialog').hide()");
            msgController.addMessage("Delete fligth schedule succesffully");
            setUnscheduledFlights();
        } catch (NoSuchFlightSchedulException | NoSuchFlightException | NoSuchRouteException e) {
            msgController.addErrorMessage(e.getMessage());
        }
    }

    public void verifyApplyFlightSchedCollision() {
        collidedFlightScheds = flightSchedulingSession.verifyApplyFlightSchedCollision(availableAircrafts, schedStartDate, schedEndDate, calendarMinDate, calendarMaxDate);
        if (!collidedFlightScheds.isEmpty()) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.update(":collidedFlightSchedForm:collidedFlightSchedTable");
            context.execute("PF('collidedFlightSchedDialog').show()");
        } else {
            applyFlightSchedulesToPeriod();
        }
    }

    public void applyFlightSchedulesToPeriod() {
        flightSchedulingSession.applyFlightSchedulesToPeriod(availableAircrafts, schedStartDate, schedEndDate, calendarMinDate, calendarMaxDate);
        msgController.addMessage("Apply flight schedule successfuly!");
    }
    
    // 
    //Helper classes
    //
    public AircraftType getModelWithMinMachNo(Flight flight) {
        return flightSchedulingSession.getModelWithMinMachNo(flight.getAircraftTypes());
    }

    public void setUnscheduledFlights() throws NoSuchFlightException, NoSuchRouteException {
        unscheduledFlights = flightSchedulingSession.getUnscheduledFlights(selectedAirport, aircraftTypeCodes);
    }

    public void initializeRouteHelper(Flight flight, RouteHelper routeHelper) {
        routePlanningSession.getRouteDetail(flight.getRoute(), routeHelper);
        flightSchedulingSession.calcFlightDuration(getModelWithMinMachNo(flight), routeHelper, flight.getSpeedFraction());
    }

    public void setRouteDuration(RouteHelper routeHelper) {
        routeDuration = routeHelper.getTotalDuration() * 2;
    }

    public void calcCalendarMinDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(schedStartDate);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        DateHelper.setToStartOfDay(calendar);
        calendarMinDate = calendar.getTime();
        schedStartDate = calendar.getTime();
    }

    public void calcCalendarMaxDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(schedStartDate);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.add(Calendar.DATE, 6);
        DateHelper.setToEndOfDay(calendar);
        calendarMaxDate = calendar.getTime();
    }

    public void calcSchedEndTime() {
        int months = Integer.parseInt(schedPeriod);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(schedStartDate);
        calendar.add(Calendar.MONTH, months);
        if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            calendar.add(Calendar.DATE, -1);
        }
        schedEndDate = calendar.getTime();
    }

    public void calcFixedEndTime() {
        arrDate = flightSchedulingSession.addHourToDate(deptDate, routeDuration);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(arrDate);
        fixedEndDate = calendar.getTime();
        fixedEndHour = calendar.get(Calendar.HOUR_OF_DAY);
        fixedEndMinute = calendar.get(Calendar.MINUTE);
    }

    public void clearDialogVariables() {
        event = new TimelineEvent();
        oldFlightSched = null;
        selectedAircraft = null;
    }

    //
    //Getter and Setter
    //
    public List<String> getAircraftTypeFamilys() {
        return aircraftTypeFamilys;
    }

    public void setAircraftTypeFamilys(List<String> aircraftTypeFamilys) {
        this.aircraftTypeFamilys = aircraftTypeFamilys;
    }

    public String getSelectedAircraftTypeFamily() {
        return selectedAircraftTypeFamily;
    }

    public void setSelectedAircraftTypeFamily(String selectedAircraftTypeFamily) {
        this.selectedAircraftTypeFamily = selectedAircraftTypeFamily;
    }

    public Airport getSelectedAirport() {
        return selectedAirport;
    }

    public void setSelectedAirport(Airport selectedAirport) {
        this.selectedAirport = selectedAirport;
    }

    public List<Airport> getDeptAirports() {
        return deptAirports;
    }

    public void setDeptAirports(List<Airport> deptAirports) {
        this.deptAirports = deptAirports;
    }

    public List<String> getSelectedAircraftTypeCodes() {
        return selectedAircraftTypeCodes;
    }

    public void setSelectedAircraftTypeCodes(List<String> selectedAircraftTypeCodes) {
        this.selectedAircraftTypeCodes = selectedAircraftTypeCodes;
    }

    public List<String> getAircraftTypeCodes() {
        return aircraftTypeCodes;
    }

    public void setAircraftTypeCodes(List<String> aircraftTypeCodes) {
        this.aircraftTypeCodes = aircraftTypeCodes;
    }

    public List<Flight> getUnscheduledFlights() {
        return unscheduledFlights;
    }

    public void setUnscheduledFlights(List<Flight> unscheduledFlights) {
        this.unscheduledFlights = unscheduledFlights;
    }

    public Flight getDroppedFlight() {
        return droppedFlight;
    }

    public void setDroppedFlight(Flight droppedFlight) {
        this.droppedFlight = droppedFlight;
    }

    public Aircraft getSelectedAircraft() {
        return selectedAircraft;
    }

    public void setSelectedAircraft(Aircraft selectedAircraft) {
        this.selectedAircraft = selectedAircraft;
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }

    public List<Aircraft> getAvailableAircrafts() {
        return availableAircrafts;
    }

    public void setAvailableAircrafts(List<Aircraft> availableAircrafts) {
        this.availableAircrafts = availableAircrafts;
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

    public List<FlightSchedule> getFlightSchedules() {
        return flightSchedules;
    }

    public void setFlightSchedules(List<FlightSchedule> flightSchedules) {
        this.flightSchedules = flightSchedules;
    }

    public TimelineModel getModel() {
        return model;
    }

    public void setModel(TimelineModel model) {
        this.model = model;
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

    public TimelineEvent getEvent() {
        return event;
    }

    public void setEvent(TimelineEvent event) {
        this.event = event;
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

    public String getSchedPeriod() {
        return schedPeriod;
    }

    public void setSchedPeriod(String schedPeriod) {
        this.schedPeriod = schedPeriod;
    }

    public Date getSchedStartDate() {
        return schedStartDate;
    }

    public void setSchedStartDate(Date schedStartDate) {
        this.schedStartDate = schedStartDate;
    }

    public Date getSchedEndDate() {
        return schedEndDate;
    }

    public void setSchedEndDate(Date schedEndDate) {
        this.schedEndDate = schedEndDate;
    }

    public List<FlightSchedule> getCollidedFlightScheds() {
        return collidedFlightScheds;
    }

    public void setCollidedFlightScheds(List<FlightSchedule> collidedFlightScheds) {
        this.collidedFlightScheds = collidedFlightScheds;
    }
    
}
