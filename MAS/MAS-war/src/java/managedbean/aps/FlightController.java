/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import ams.aps.entity.Flight;
import ams.aps.entity.Route;
import ams.aps.session.FlightSchedulingSessionLocal;
import ams.aps.util.helper.RouteDisplayHelper;
import ams.aps.util.helper.RouteHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import managedbean.application.MsgController;

/**
 *
 * @author ChuningLiu
 */
@Named(value = "flightController")
@ViewScoped
public class FlightController implements Serializable{
    @Inject
    RouteController routeController;
    
    @Inject
    MsgController msgController;

    @EJB
    private FlightSchedulingSessionLocal flightSchedulingSession;

    private Flight flight;
    private Route route;
    private List<RouteDisplayHelper> routeDisplayHelperList = new ArrayList();
    private String flightNo;
    private String newFlightNo;
    private List<Route> availibleRoutes;
            
    /**
     * Creates a new instance of FlightController
     */
    @PostConstruct
    public void init() {
        getRoutes();
    }

    public FlightController() {
    }

    public void addFlight(ActionEvent event) {
        if (!flightSchedulingSession.checkFlightNotExisted(flightNo)) {
            msgController.addErrorMessage("Flight no. existed already!");
        } else {
            if (flightSchedulingSession.createFlight(getFlightNo(), getRoute().getRouteId())) {
                msgController.addMessage("New flight assigned!");
            } else {
                msgController.addErrorMessage("Fail to assign flight to route!");
            }
        }
    }

    public void getRoutes() {
        setAvailibleRoutes(flightSchedulingSession.getAvailableRoutes());
        RouteHelper routeHelper;
        RouteDisplayHelper routeDisplayHelper;
        
        for(Route thisRoute: availibleRoutes){
            routeHelper = new RouteHelper();
            routeDisplayHelper = new RouteDisplayHelper();
            routeController.getRouteDetail(thisRoute, routeHelper);
            
            routeDisplayHelper.setId(routeHelper.getId());
            routeDisplayHelper.setOrigin(routeHelper.getOrigin().getIcaoCode());
            routeDisplayHelper.setLegs(routeController.getStopoverString(routeHelper.getStopovers(), "icao"));
            routeDisplayHelper.setDestination(routeHelper.getDestination().getIcaoCode());
            routeDisplayHelperList.add(routeDisplayHelper);
        }
    }

    public void resetFlightNo(String oldFlightNo, String newFlightNo) {
        if (!flightSchedulingSession.checkFlightNoExistence(flightNo)) {
            msgController.addErrorMessage("Flight no. not existed!");
        }else{
            if (flightSchedulingSession.changeFlightNo(oldFlightNo, newFlightNo)) {
                msgController.addMessage("Flight no. changed!");
            } else {
                msgController.addErrorMessage("Fail to change flight no.!");
            }
        }
    }
    
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
     * @return the flightNo
     */
    public String getFlightNo() {
        return flightNo;
    }

    /**
     * @param flightNo the flightNo to set
     */
    public void setFlightNo(String flightNo) {
        this.flightNo = flightNo;
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

}
