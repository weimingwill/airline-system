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
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;

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
    private List<AircraftType> aircraftTypes;
    private String selectedAircraftTypeFamily;
    private Airport selectedAirport;
    private List<Airport> deptAirports;
    private List<AircraftType> selectedAircraftTypes;
    private List<RouteHelper> routeHelpers;
    private List<Flight> unscheduledFlights;

    /**
     * Creates a new instance of FlightScheduleManager
     */
    public FlightScheduleManager() {
    }

    @PostConstruct
    public void init() {
//        initializeAircraftTypeFamilys();
//        initilaizeDepteAirports();
    }

    public void initializeAircraftTypeFamilys() {
        aircraftTypeFamilys = flightSchedulingSession.getUnscheduledFlightAircraftTypeFamilys();
    }

    public void onAircraftTypeFamilyChange() {
        aircraftTypes = flightSchedulingSession.getUnscheduledAircraftTypesByTypeFamily(selectedAircraftTypeFamily);
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

    public void initilizeDepteAirports() {
        try {
            for (Route route : flightSchedulingSession.getUnscheduledFlightRoutes()) {
                RouteHelper routeHelper = new RouteHelper();
                routePlanningSession.getRouteDetail(route, routeHelper);
                deptAirports.add(routeHelper.getOrigin());
            }
        } catch (NoSuchRouteException e) {
            deptAirports = new ArrayList<>();
        }
    }

    public void resetFilters() {
        init();
    }

    public void applyFilters() {
        try {
            unscheduledFlights = flightSchedulingSession.getUnscheduledFlights(selectedAirport, aircraftTypes);
        } catch (NoSuchFlightException | NoSuchRouteException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
    }
    
    public RouteHelper getRouteHelper(Flight flight){
        RouteHelper routeHelper = new RouteHelper();
        routePlanningSession.getRouteDetail(flight.getRoute(), routeHelper);
        AircraftType aircraftType = flightSchedulingSession.getModelWithMinMachNo(flight.getAircraftTypes());
        flightSchedulingSession.calcFlightDuration(aircraftType, routeHelper, flight.getSpeedFraction());
        return routeHelper;
    }

//Getter and Setter
    public List<String> getAircraftTypeFamilys() {
        return aircraftTypeFamilys;
    }

    public void setAircraftTypeFamilys(List<String> aircraftTypeFamilys) {
        this.aircraftTypeFamilys = aircraftTypeFamilys;
    }

    public List<AircraftType> getAircraftTypes() {
        return aircraftTypes;
    }

    public void setAircraftTypes(List<AircraftType> aircraftTypes) {
        this.aircraftTypes = aircraftTypes;
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

    public List<AircraftType> getSelectedAircraftTypes() {
        return selectedAircraftTypes;
    }

    public void setSelectedAircraftTypes(List<AircraftType> selectedAircraftTypes) {
        this.selectedAircraftTypes = selectedAircraftTypes;
    }

    public List<Flight> getUnscheduledFlights() {
        return unscheduledFlights;
    }

    public void setUnscheduledFlights(List<Flight> unscheduledFlights) {
        this.unscheduledFlights = unscheduledFlights;
    }

}
