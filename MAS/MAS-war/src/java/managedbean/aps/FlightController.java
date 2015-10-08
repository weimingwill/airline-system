/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import ams.aps.entity.Flight;
import ams.aps.entity.Route;
import ams.aps.session.FlightSchedulingSessionLocal;
import ams.aps.util.exception.EmptyTableException;
import ams.aps.util.exception.FlightDoesNotExistException;
import ams.aps.util.helper.RouteDisplayHelper;
import ams.aps.util.helper.RouteHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;

/**
 *
 * @author ChuningLiu
 */
@Named(value = "flightController")
@ViewScoped
public class FlightController implements Serializable {

    @Inject
    RouteController routeController;

    @Inject
    MsgController msgController;

    @Inject
    NavigationController navigationController;

    @Inject
    FlightManager flightManager;

    @EJB
    private FlightSchedulingSessionLocal flightSchedulingSession;

    private String flightNo;
    private Flight flight;
    private List<Flight> incompleteFlights;
    private Route route;
    private List<RouteDisplayHelper> routeDisplayHelperList = new ArrayList();
    private RouteDisplayHelper selectedRouteDisplayHelper;
    private String newFlightNo;
    private List<Route> availibleRoutes;

    /**
     * Creates a new instance of FlightController
     */
    @PostConstruct
    public void init() {
        getIncompleteFlight();
        getRoutes();
    }

    public FlightController() {
    }

    public void getIncompleteFlight() {
        try {
            setIncompleteFlights(flightSchedulingSession.getFlight(Boolean.FALSE));
        } catch (EmptyTableException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public String addFlight() {
        System.out.println("FlightController: addFlight()");
        try {
            flightSchedulingSession.checkFlightExistence(flightNo);
            msgController.addErrorMessage("Flight no. existed already!");
            return navigationController.redirectToCurrentPage();
        } catch (FlightDoesNotExistException ex) {
            flight = new Flight();
            flight.setFlightNo(flightNo);
            flight.setRoute(route);
            if (flightSchedulingSession.createFlight(flight)) {
                msgController.addMessage("New flight assigned!");
                return toCreateFlightStep();
            } else {
                msgController.addErrorMessage("Fail to assign flight to route!");
                return navigationController.redirectToCurrentPage();
            }
        }

    }

    public String findFlightByFlightNo(String flightNo) {
        try {
            flight = flightSchedulingSession.checkFlightExistence(flightNo);
        } catch (FlightDoesNotExistException ex) {
            return navigationController.redirectToCurrentPage();
        }
        return toCreateFlightStep();
    }

    public String toCreateFlightStep() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        map.put("flight", flight);
        flightManager.init();
        String returnPage;
        if (flight.getAircraftTypes() == null || flight.getAircraftTypes().isEmpty()) {
            returnPage = navigationController.toFleetAssignment();
        } else if (flight.getWeeklyFrequency() == null || flight.getWeeklyFrequency() == 0) {
            returnPage = navigationController.toFreqPlanning();
        } else {
            returnPage = navigationController.toConfirmFlight();
        }
        return returnPage;
    }

    public void getRoutes() {
        System.out.println("FlightController: getRoutes()");
        setAvailibleRoutes(flightSchedulingSession.getAvailableRoutes());
//        RouteHelper routeHelper;
//        RouteDisplayHelper routeDisplayHelper;
//        
//        for(Route thisRoute: availibleRoutes){
//            routeHelper = new RouteHelper();
//            routeDisplayHelper = new RouteDisplayHelper();
//            routeController.getRouteDetail(thisRoute, routeHelper);
//            
//            routeDisplayHelper.setId(routeHelper.getId());
//            routeDisplayHelper.setOrigin(routeHelper.getOrigin().getIcaoCode());
//            routeDisplayHelper.setLegs(routeController.getStopoverString(routeHelper.getStopovers(), "icao"));
//            routeDisplayHelper.setDestination(routeHelper.getDestination().getIcaoCode());
//            routeDisplayHelperList.add(routeDisplayHelper);
//        }
    }

//    public void resetFlightNo(String oldFlightNo, String newFlightNo) {
//        if (!flightSchedulingSession.checkFlightNoExistence(newFlightNo)) {
//            msgController.addErrorMessage("Flight no. not existed!");
//        }else{
//            if (flightSchedulingSession.changeFlightNo(oldFlightNo, newFlightNo)) {
//                msgController.addMessage("Flight no. changed!");
//            } else {
//                msgController.addErrorMessage("Fail to change flight no.!");
//            }
//        }
//    }
    /**
     * @return the flight
     */
    public Flight getFlight() {
        return flight;
    }

    /**
     * @param flight the flight to set
     */
    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    /**
     * @return the route
     */
    public Route getRoute() {
        return route;
    }

    /**
     * @param route the route to set
     */
    public void setRoute(Route route) {
        this.route = route;
    }

    /**
     * @return the availibleRoutes
     */
    public List<Route> getAvailibleRoutes() {
        return availibleRoutes;
    }

    /**
     * @param availibleRoutes the availibleRoutes to set
     */
    public void setAvailibleRoutes(List<Route> availibleRoutes) {
        this.availibleRoutes = availibleRoutes;
    }

    /**
     * @return the newFlightNo
     */
    public String getNewFlightNo() {
        return newFlightNo;
    }

    /**
     * @param newFlightNo the newFlightNo to set
     */
    public void setNewFlightNo(String newFlightNo) {
        this.newFlightNo = newFlightNo;
    }

    /**
     * @return the routeDisplayHelperList
     */
    public List<RouteDisplayHelper> getRouteDisplayHelperList() {
        return routeDisplayHelperList;
    }

    /**
     * @param routeDisplayHelperList the routeDisplayHelperList to set
     */
    public void setRouteDisplayHelperList(List<RouteDisplayHelper> routeDisplayHelperList) {
        this.routeDisplayHelperList = routeDisplayHelperList;
    }

    public String getFlightNo() {
        return flightNo;
    }

    public void setFlightNo(String flightNo) {
        this.flightNo = flightNo;
    }

    public RouteDisplayHelper getSelectedRouteDisplayHelper() {
        return selectedRouteDisplayHelper;
    }

    public void setSelectedRouteDisplayHelper(RouteDisplayHelper selectedRouteDisplayHelper) {
        this.selectedRouteDisplayHelper = selectedRouteDisplayHelper;
    }

    /**
     * @return the incompleteFlights
     */
    public List<Flight> getIncompleteFlights() {
        return incompleteFlights;
    }

    /**
     * @param incompleteFlights the incompleteFlights to set
     */
    public void setIncompleteFlights(List<Flight> incompleteFlights) {
        this.incompleteFlights = incompleteFlights;
    }

}
