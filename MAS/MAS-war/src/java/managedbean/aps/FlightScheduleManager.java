/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import ams.aps.entity.AircraftType;
import ams.aps.entity.Airport;
import ams.aps.entity.Flight;
import ams.aps.entity.Route;
import ams.aps.session.FlightSchedulingSessionLocal;
import ams.aps.session.RoutePlanningSessionLocal;
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
    private List<String> aircraftTypeFamilys;
    private List<String> aircraftTypeCodes;
    private String selectedAircraftTypeFamily;
    private Airport selectedAirport;
    private List<Airport> deptAirports = new ArrayList<>();
    private List<String> selectedAircraftTypeCodes = new ArrayList<>();
    private List<RouteHelper> routeHelpers = new ArrayList<>();
    private List<Flight> unscheduledFlights = new ArrayList<>();
    private String deptAirport = new String();
    private String arriveAirport = new String();
    private ScheduleModel eventModel;
    private ScheduleEvent event = new DefaultScheduleEvent();

    /**
     * Creates a new instance of FlightScheduleManager
     */
    @PostConstruct
    public void init() {
        initializeAircraftTypeFamilys();
        initilizeDeptAirports();
        eventModel = new DefaultScheduleModel();
//        eventModel.addEvent(new DefaultScheduleEvent("Champions League Match", previousDay8Pm(), previousDay11Pm()));
    }

    public FlightScheduleManager() {
    }

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

    public void resetFilters() {
        unscheduledFlights = new ArrayList<>();
    }

    public void applyFilters() {
        try {
            unscheduledFlights = flightSchedulingSession.getUnscheduledFlights(selectedAirport, aircraftTypeCodes);
//            if (unscheduledFlights.isEmpty()) {
//                msgController.addErrorMessage("There is no unscheduled flight starting from " + selectedAirport.getAirportName() + " with aircraft type " + aircraftTypeCodes.toString());
//            }
        } catch (NoSuchFlightException | NoSuchRouteException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
    }

    public RouteHelper getRouteHelper(Flight flight) {
        RouteHelper routeHelper = new RouteHelper();
        routePlanningSession.getRouteDetail(flight.getRoute(), routeHelper);
        AircraftType aircraftType = flightSchedulingSession.getModelWithMinMachNo(flight.getAircraftTypes());
        flightSchedulingSession.calcFlightDuration(aircraftType, routeHelper, flight.getSpeedFraction());
        return routeHelper;
    }

    public void onFlightDrop(DragDropEvent ddEvent) {
        Flight flight = (Flight) ddEvent.getData();
        Date date = new Date();
        //Need to fix time duration
        event = new DefaultScheduleEvent(flight.getFlightNo(), date, date);
    }

    public Date getInitialDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), Calendar.FEBRUARY, calendar.get(Calendar.DATE), 0, 0, 0);

        return calendar.getTime();
    }

    private Calendar today() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);

        return calendar;
    }

    public void addEvent(ActionEvent actionEvent) {
        if (event.getId() == null) {
            eventModel.addEvent(event);
            
            //minus one unscheduled Flight
            
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

//Getter and Setter
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
}
