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
import ams.aps.util.exception.NoSuchRouteException;
import ams.aps.util.helper.RouteHelper;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
    private List<String> aircraftTypeFamilys;
    private List<String> aircraftTypeCodes;
    private String selectedAircraftTypeFamily;
    private Airport selectedAirport;
    private Aircraft selectedAircraft;
    private String selectedAircraftTailNo;
    private List<SelectItem> availableAircrafts = new ArrayList<>();
    private List<String> selectedAircraftTypeCodes = new ArrayList<>();
    private Date selectedDate;
    private Flight droppedFlight;
    private List<Airport> deptAirports = new ArrayList<>();
    private List<RouteHelper> routeHelpers = new ArrayList<>();
    private List<Flight> unscheduledFlights = new ArrayList<>();
//    private List<Aircraft> availableAircrafts = new ArrayList<>();
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

    /**
     * Creates a new instance of FlightScheduleManager
     */
    @PostConstruct
    public void init() {
        initializeAircraftTypeFamilys();
        initilizeDeptAirports();
        initiliazeCalendarDate();
        eventModel = new DefaultScheduleModel();
//        eventModel.addEvent(new DefaultScheduleEvent("Champions League Match", previousDay8Pm(), previousDay11Pm()));
    }

    public FlightScheduleManager() {
    }

    //Initialize
    public void initializeAircraftTypeFamilys() {
        aircraftTypeFamilys = flightSchedulingSession.getUnscheduledFlightAircraftTypeFamilys();
    }

    public void onAircraftTypeFamilyChange() {
        aircraftTypeCodes = flightSchedulingSession.getUnscheduledAircraftTypeCodesByTypeFamily(selectedAircraftTypeFamily);
    }

    public void initiliazeRouteHelpers() {
        try {
            for (Route route : flightSchedulingSession.getUnscheduledFlightRoutes()) {
                RouteHelper routeHelper = new RouteHelper();
                routePlanningSession.getRouteDetail(route, routeHelper);
                routeHelpers.add(routeHelper);
            }
        } catch (NoSuchRouteException e) {
            routeHelpers = new ArrayList<>();
        }
    }

    public void initilizeDeptAirports() {
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

    //Select Flight
    public void applyFilters() {
        try {
            setUnscheduledFlights();
            for (String aircraftTypeCode : aircraftTypeCodes) {
                AircraftType aircraftType = fleetPlanningSession.getAircraftTypeByCode(aircraftTypeCode);
                SelectItemGroup group = new SelectItemGroup(aircraftType.getTypeFamily());
                SelectItem[] selectItems = new SelectItem[aircraftType.getAircrafts().size()];
                int i = 0;
                for (Aircraft aircraft : aircraftType.getAircrafts()) {
                    selectItems[i] = new SelectItem(aircraft.getTailNo(), aircraft.getTailNo());
                    i++;
                }
                group.setSelectItems(selectItems);
                availableAircrafts.add(group);
            }
//            if (unscheduledFlights.isEmpty()) {
//                msgController.addErrorMessage("There is no unscheduled flight starting from " + selectedAirport.getAirportName() + " with aircraft type " + aircraftTypeCodes.toString());
//            }
        } catch (NoSuchFlightException | NoSuchRouteException ex) {
            msgController.addErrorMessage(ex.getMessage());
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
    public void initiliazeCalendarDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, 1);
        selectedDate = calendar.getTime();
        calcCalendarMinDate();
        calcCalendarMaxDate();
    }

    public void calcCalendarMinDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendarMinDate = calendar.getTime();
        System.out.println("Min Date: " + calendarMinDate);
    }

    public void calcCalendarMaxDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.add(Calendar.DATE, 7);
        calendarMaxDate = calendar.getTime();
        System.out.println("Max Date: " + calendarMaxDate);
    }

    public void calcFixedEndTime() {
        Date newDate = flightSchedulingSession.addHourToDate(event.getStartDate(), routeDuration * 2);
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
        flightSchedules = flightSchedulingSession.getFlightSchedulesByTailNoAndTime(selectedAircraftTailNo, calendarMinDate, calendarMaxDate);
        for (FlightSchedule flightSchedule : flightSchedules) {
            Flight flight = flightSchedule.getFlight();
            RouteHelper routeHelper = new RouteHelper();
            if (routeHelper.getOrigin().getIsHub()) {
                routePlanningSession.getRouteDetail(flight.getRoute(), routeHelper);
                flightSchedulingSession.calcFlightDuration(flightSchedulingSession.getModelWithMinMachNo(flight.getAircraftTypes()), routeHelper, flight.getSpeedFraction());
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
        routePlanningSession.getRouteDetail(droppedFlight.getRoute(), routeHelper);
        if (!routeHelper.getOrigin().getIsHub()) {
            droppedFlight = droppedFlight.getReturnedFlight();
        }
        flightSchedulingSession.calcFlightDuration(flightSchedulingSession.getModelWithMinMachNo(droppedFlight.getAircraftTypes()), routeHelper, droppedFlight.getSpeedFraction());
        System.out.println("Dropped Flight: " + droppedFlight.getFlightNo());
        deptAirport = routeHelper.getOrigin().getAirportName();
        arriveAirport = routeHelper.getDestination().getAirportName();
        System.out.println("Airports: " + deptAirport + " - " + arriveAirport);
        routeDuration = routeHelper.getTotalDuration();

        //Need to fix time duration
        event = new DefaultScheduleEvent(droppedFlight.getFlightNo() + "/" + droppedFlight.getReturnedFlight().getFlightNo(), selectedDate, flightSchedulingSession.addHourToDate(selectedDate, routeDuration * 2));
    }

    public void addEvent(ActionEvent actionEvent) {
        System.out.println("Dropped Flight:" + droppedFlight);
        if (event.getId() == null) {
            eventModel.addEvent(event);
            try {
                selectedAircraft = flightSchedulingSession.getAircraftByTailNo(selectedAircraftTailNo);
                String flightNo = event.getTitle().split("/")[0];
                flightSchedulingSession.createFlightSchedule(flightNo, selectedAircraft, event.getStartDate(), event.getEndDate());
                msgController.addMessage("Add flight schedule succesffuly");
                setUnscheduledFlights();
            } catch (NoSuchFlightException | NoMoreUnscheduledFlightException | NoSelectAircraftException | NoSuchAircraftException | NoSuchRouteException e) {
                msgController.addErrorMessage(e.getMessage());
            }
        } else {
            eventModel.updateEvent(event);
        }
        event = new DefaultScheduleEvent();
    }

    public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
    }

    public void onDateSelect(SelectEvent selectEvent) {
        event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
    }

    public void onEventMove(ScheduleEntryMoveEvent event) {
        msgController.addMessage("Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
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

    public List<RouteHelper> getRouteHelpers() {
        return routeHelpers;
    }

    public void setRouteHelpers(List<RouteHelper> routeHelpers) {
        this.routeHelpers = routeHelpers;
    }

    public List<Flight> getUnscheduledFlights() {
        return unscheduledFlights;
    }

    public void setUnscheduledFlights(List<Flight> unscheduledFlights) {
        this.unscheduledFlights = unscheduledFlights;
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

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

    public ScheduleModel getEventModel() {
        return eventModel;
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

    public List<SelectItem> getAvailableAircrafts() {
        return availableAircrafts;
    }

    public void setAvailableAircrafts(List<SelectItem> availableAircrafts) {
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
}
