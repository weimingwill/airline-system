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
import ams.aps.session.FleetPlanningSessionLocal;
import ams.aps.session.FlightSchedulingSessionLocal;
import ams.aps.session.RoutePlanningSessionLocal;
import ams.aps.util.exception.NoMoreUnscheduledFlightException;
import ams.aps.util.exception.NoSelectAircraftException;
import ams.aps.util.exception.NoSuchAircraftException;
import ams.aps.util.exception.NoSuchFlightException;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import ams.aps.util.exception.NoSuchRouteException;
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
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;
import org.primefaces.event.DragDropEvent;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.extensions.model.timeline.TimelineEvent;
import org.primefaces.extensions.model.timeline.TimelineGroup;
import org.primefaces.extensions.model.timeline.TimelineModel;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

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
    @EJB
    private FleetPlanningSessionLocal fleetPlanningSession;

    //select flight
    private List<String> aircraftTypeFamilys;
    private List<String> aircraftTypeCodes;
    private String selectedAircraftTypeFamily;
    private Airport selectedAirport;
    private String selectedAircraftTailNo;
    private List<SelectItem> availableAircraftGroup = new ArrayList<>();
    private List<Aircraft> availableAircrafts = new ArrayList<>();
    private List<String> selectedAircraftTypeCodes = new ArrayList<>();

    //schedule flight
    private TimelineModel model;
    private Aircraft selectedAircraft;
    private Date selectedDate;
    private Flight droppedFlight;
    private List<Airport> deptAirports = new ArrayList<>();
    private List<Flight> unscheduledFlights = new ArrayList<>();
    private String deptAirport = new String();
    private String arriveAirport = new String();
    private ScheduleModel eventModel;
    private ScheduleEvent event = new DefaultScheduleEvent();
    private Date calendarMinDate;
    private Date calendarMaxDate;
    private int fixedEndHour;
    private int fixedEndMinute;
    private Date fixedEndDate;
    private double routeDuration;
    private List<FlightSchedule> flightSchedules;
    private Date oldDeptDte;
    
    //timeline
    private long zoomMin;
    private long zoomMax;

    /**
     * Creates a new instance of FlightScheduleManager
     */
    @PostConstruct
    public void init() {
        clearVariables();
        initializeDeptAirports();
        initializeAircraftTypeFamilys();
        initializeTimeline();
        eventModel = new DefaultScheduleModel();
//        eventModel.addEvent(new DefaultScheduleEvent("Champions League Match", previousDay8Pm(), previousDay11Pm()));
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
        setSelectedAircraftTailNo(null);
        setUnscheduledFlights(new ArrayList<>());
        setEventModel(null);
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

    public void onAircraftTypeFamilyChange() {
        aircraftTypeCodes = flightSchedulingSession.getUnscheduledAircraftTypeCodesByTypeFamily(selectedAircraftTypeFamily);
    }

    //Select Flight
    public void applyFilters() {
        try {
            setUnscheduledFlights();
            model = new TimelineModel();

            availableAircrafts = new ArrayList<>();
            Set<Aircraft> hs = new HashSet<>();
            for (String aircraftTypeCode : selectedAircraftTypeCodes) {
                AircraftType aircraftType = fleetPlanningSession.getAircraftTypeByCode(aircraftTypeCode);
                SelectItemGroup group = new SelectItemGroup(aircraftType.getTypeFamily());
                SelectItem[] selectItems = new SelectItem[aircraftType.getAircrafts().size()];
                int i = 0;

                for (Aircraft aircraft : aircraftType.getAircrafts()) {
                    hs.add(aircraft);
                    
                    selectItems[i] = new SelectItem(aircraft.getTailNo(), aircraft.getTailNo());
                    i++;
                }
                group.setSelectItems(selectItems);
                availableAircraftGroup.add(group);
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

    public void setTimelineGroup() {
        for (Aircraft aircraft : availableAircrafts) {
            System.out.println("Aircraft Tail No: " + aircraft.getTailNo());
            TimelineGroup timelineGroup = new TimelineGroup(aircraft.getTailNo(), aircraft);
            model.addGroup(timelineGroup);

            flightSchedules = flightSchedulingSession.getFlightSchedulesByTailNoAndTime(aircraft.getTailNo(), calendarMinDate, calendarMaxDate);
            if (flightSchedules.isEmpty()) {
                model.add(new TimelineEvent(null, new Date(), new Date(), true, aircraft.getTailNo()));
            }
            System.out.println("Flight Schedules: " + flightSchedules);
            for (FlightSchedule flightSchedule : flightSchedules) {
                Flight flight = flightSchedule.getFlight();
                RouteHelper routeHelper = new RouteHelper();
                initializeRouteHelper(flight, routeHelper);
                System.out.println("RouteHelper: " + routeHelper + " DeptDate: " + flightSchedule.getDepartDate());
                System.out.println("RouteHelper: is hub" + routeHelper.getOrigin().getIsHub());
                if (routeHelper.getOrigin().getIsHub()) {
                    Date deptDate = flightSchedule.getDepartDate();
                    Date arriveDate = flightSchedulingSession.addHourToDate(deptDate, routeHelper.getTotalDuration());
                    model.add(new TimelineEvent(flightSchedule, deptDate, arriveDate, true, aircraft.getTailNo()));
//                    eventModel.clear();
//                    eventModel.addEvent(new DefaultScheduleEvent(flight.getFlightNo() + "/" + flight.getReturnedFlight().getFlightNo(), deptDate, arriveDate));
                }
            }
        }
    }

    public void setUnscheduledFlights() throws NoSuchFlightException, NoSuchRouteException {
        unscheduledFlights = flightSchedulingSession.getUnscheduledFlights(selectedAirport, aircraftTypeCodes);
    }

    public void resetFilters() {
        unscheduledFlights = new ArrayList<>();
    }

    public RouteHelper getRouteHelper(Flight flight) {
        RouteHelper routeHelper = new RouteHelper();
        routePlanningSession.getRouteDetail(flight.getRoute(), routeHelper);
        AircraftType aircraftType = flightSchedulingSession.getModelWithMinMachNo(flight.getAircraftTypes());
        flightSchedulingSession.calcFlightDuration(aircraftType, routeHelper, flight.getSpeedFraction());
        return routeHelper;
    }

    //Schedule flight
    public void initializeTimeline() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, 1);
        selectedDate = calendar.getTime();
        calcCalendarMinDate();
        calcCalendarMaxDate();
        setZoomMin(1000L * 60 * 60 * 12); //Visible interval is limited to a minimum of 8 hours
    }

    public void setToStartOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getMinimum(Calendar.MILLISECOND));
    }

    public void calcCalendarMinDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        setToStartOfDay(calendar);
        calendarMinDate = calendar.getTime();
        System.out.println("Min Date: " + calendarMinDate);
    }

    public void calcCalendarMaxDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.add(Calendar.DATE, 7);
        setToStartOfDay(calendar);
        calendarMaxDate = calendar.getTime();
        System.out.println("Max Date: " + calendarMaxDate);
    }

    public void calcFixedEndTime() {
        Date newDate = flightSchedulingSession.addHourToDate(event.getStartDate(), routeDuration);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(newDate);
        fixedEndDate = calendar.getTime();
        fixedEndHour = calendar.get(Calendar.HOUR_OF_DAY);
        fixedEndMinute = calendar.get(Calendar.MINUTE);
    }

    public Date getInitialDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return calendar.getTime();
    }

    public void onCalendarDateChange() {
        calcCalendarMinDate();
        calcCalendarMaxDate();
    }

    public void onStartTimeChange() {
        System.out.println("Date: " + event.getStartDate() + " - " + event.getEndDate());
        calcFixedEndTime();
    }

    public void onAircraftChange() {
        System.out.println("calendarMinDate: " + calendarMinDate);
        System.out.println("calendarMaxDate: " + calendarMaxDate);
        flightSchedules = flightSchedulingSession.getFlightSchedulesByTailNoAndTime(selectedAircraftTailNo, calendarMinDate, calendarMaxDate);
        System.out.println("Flight Schedules: " + flightSchedules);
        for (FlightSchedule flightSchedule : flightSchedules) {
            Flight flight = flightSchedule.getFlight();
            RouteHelper routeHelper = new RouteHelper();
            initializeRouteHelper(flight, routeHelper);
            System.out.println("RouteHelper: " + routeHelper + " DeptDate: " + flightSchedule.getDepartDate());
            System.out.println("RouteHelper: is hub" + routeHelper.getOrigin().getIsHub());
            if (routeHelper.getOrigin().getIsHub()) {
                Date deptDate = flightSchedule.getDepartDate();
                Date arriveDate = flightSchedulingSession.addHourToDate(deptDate, routeHelper.getTotalDuration());
                eventModel.clear();
                eventModel.addEvent(new DefaultScheduleEvent(flight.getFlightNo() + "/" + flight.getReturnedFlight().getFlightNo(), deptDate, arriveDate));
            }
        }
    }

    public void onFlightDrop(DragDropEvent ddEvent) {
        System.out.println("Dropped Flight: ");
        droppedFlight = ((Flight) ddEvent.getData());
        RouteHelper routeHelper = new RouteHelper();
        initializeRouteHelper(droppedFlight, routeHelper);
        if (!routeHelper.getOrigin().getIsHub()) {
            droppedFlight = droppedFlight.getReturnedFlight();
        }
        System.out.println("Dropped Flight: " + droppedFlight.getFlightNo());
        setRouteDuration(routeHelper);
        deptAirport = routeHelper.getOrigin().getAirportName();
        arriveAirport = routeHelper.getDestination().getAirportName();
        //Need to fix time duration
        event = new DefaultScheduleEvent(droppedFlight.getFlightNo() + "/" + droppedFlight.getReturnedFlight().getFlightNo(), selectedDate, flightSchedulingSession.addHourToDate(selectedDate, routeDuration));
    }

    public void addEvent(ActionEvent actionEvent) {
        System.out.println("Dropped Flight:" + droppedFlight);
        if (event.getId() == null) {
            eventModel.addEvent(event);
            createFligthSchedule();
        } else {
            eventModel.updateEvent(event);
            updateFlightSchedule();
        }
        event = new DefaultScheduleEvent();
    }

    public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
        String flightNo = event.getTitle().split("/")[0];
        oldDeptDte = event.getStartDate();
        System.out.println("FlightNo: " + flightNo);

        Flight flight = new Flight();
        try {
            flight = flightSchedulingSession.getFlightByFlightNo(flightNo);
        } catch (NoSuchFlightException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }

        RouteHelper routeHelper = new RouteHelper();
        initializeRouteHelper(flight, routeHelper);
        setRouteDuration(routeHelper);
    }

    public void onEventMove(ScheduleEntryMoveEvent event) {
        msgController.addMessage("Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
    }

    public void createFligthSchedule() {
        try {
            selectedAircraft = flightSchedulingSession.getAircraftByTailNo(selectedAircraftTailNo);
            String flightNo = event.getTitle().split("/")[0];
            flightSchedulingSession.createFlightSchedule(flightNo, selectedAircraft, event.getStartDate(), event.getEndDate());
            msgController.addMessage("Add flight schedule succesffuly");
            setUnscheduledFlights();
        } catch (NoSuchFlightException | NoMoreUnscheduledFlightException | NoSelectAircraftException | NoSuchAircraftException | NoSuchRouteException e) {
            msgController.addErrorMessage(e.getMessage());
        }
    }

    public void updateFlightSchedule() {
        try {
            String flightNo = event.getTitle().split("/")[0];
            flightSchedulingSession.updateFlightSchedule(flightNo, event.getStartDate(), oldDeptDte);
            msgController.addMessage("Update flight schedule succesffuly");
            setUnscheduledFlights();
        } catch (NoSuchFlightException | NoMoreUnscheduledFlightException | NoSelectAircraftException | NoSuchRouteException | NoSuchFlightSchedulException e) {
            msgController.addErrorMessage(e.getMessage());
        }
    }

    public AircraftType getModelWithMinMachNo(Flight flight) {
        return flightSchedulingSession.getModelWithMinMachNo(flight.getAircraftTypes());
    }

    //Helper classes
    public void initializeRouteHelper(Flight flight, RouteHelper routeHelper) {
        routePlanningSession.getRouteDetail(flight.getRoute(), routeHelper);
        flightSchedulingSession.calcFlightDuration(getModelWithMinMachNo(flight), routeHelper, flight.getSpeedFraction());
    }

    public void setRouteDuration(RouteHelper routeHelper) {
        routeDuration = routeHelper.getTotalDuration() * 2;
    }

    /**
     * Getter and Setter
     *
     */
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
    
    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
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

    public String getSelectedAircraftTailNo() {
        return selectedAircraftTailNo;
    }

    public void setSelectedAircraftTailNo(String selectedAircraftTailNo) {
        this.selectedAircraftTailNo = selectedAircraftTailNo;
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

    public Date getOldDeptDte() {
        return oldDeptDte;
    }

    public void setOldDeptDte(Date oldDeptDte) {
        this.oldDeptDte = oldDeptDte;
    }

    public TimelineModel getModel() {
        return model;
    }

    public void setModel(TimelineModel model) {
        this.model = model;
    }

    public List<SelectItem> getAvailableAircraftGroup() {
        return availableAircraftGroup;
    }

    public void setAvailableAircraftGroup(List<SelectItem> availableAircraftGroup) {
        this.availableAircraftGroup = availableAircraftGroup;
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
    
}
