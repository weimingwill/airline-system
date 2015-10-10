/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import ams.aps.entity.AircraftType;
import ams.aps.entity.Flight;
import ams.aps.entity.Route;
import ams.aps.session.FlightSchedulingSessionLocal;
import ams.aps.util.helper.LegHelper;
import ams.aps.util.helper.RouteDisplayHelper;
import ams.aps.util.helper.RouteHelper;
import javax.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 *
 * @author Lewis
 */
@Named(value = "flightManager")
@SessionScoped
public class FlightManager implements Serializable {

    @EJB
    private FlightSchedulingSessionLocal flightSchedulingSession;

    @Inject
    RouteController routeController;

    private Flight flight;
    private RouteDisplayHelper route = new RouteDisplayHelper();
    private RouteDisplayHelper returnedRoute = new RouteDisplayHelper();
    private RouteHelper routeHelper = new RouteHelper();
    private RouteHelper returnRouteHelper = new RouteHelper();
    private List<AircraftType> selectedModels = new ArrayList();
    private List<AircraftType> modelsForFlight = new ArrayList();
    private float maxDist = 0;

    /**
     * Creates a new instance of FlightManager
     */
    public FlightManager() {
    }

    @PostConstruct
    public void init() {
        getAddedFlight();
        System.out.println("init(): flight = " + flight);
        getRouteDetails();
        getAircraftModelsForFlight();
    }

    private void getAddedFlight() {
        Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        setFlight((Flight) map.get("flight"));
    }

    public void getRouteDetails() {
        Route thisRoute = flight.getRoute();
        Route thisReturnedRoute = thisRoute.getReturnRoute();
        setValueForRouteHelper(thisRoute, getRouteHelper(), route);
        setValueForRouteHelper(thisReturnedRoute, getReturnRouteHelper(), returnedRoute);
    }

    private void setValueForRouteHelper(Route thisRoute, RouteHelper thisRouteHelper, RouteDisplayHelper thisRouteDisplayHelper) {
        routeController.getRouteDetail(thisRoute, thisRouteHelper);
        thisRouteDisplayHelper.setId(thisRouteHelper.getId());
        thisRouteDisplayHelper.setReturnRouteId(thisRouteHelper.getReturnRouteId());
        thisRouteDisplayHelper.setOrigin(thisRouteHelper.getOrigin().getAirportName());
        thisRouteDisplayHelper.setDestination(thisRouteHelper.getDestination().getAirportName());
        thisRouteDisplayHelper.setLegs(routeController.getStopoverString(thisRouteHelper.getStopovers(), "name"));
        thisRouteDisplayHelper.setDistance(thisRouteHelper.getTotalDistance());
    }

    public void getAircraftModelsForFlight() {
        getMaxLegDistInRoute(routeHelper.getLegs());
        setModelsForFlight(flightSchedulingSession.getCapableAircraftTypesForRoute(getMaxDist()));
    }

    private void getMaxLegDistInRoute(List<LegHelper> legs) {
        setMaxDist((float) legs.get(0).getDistance());
        for (int i = 1; i < legs.size(); i++) {
            setMaxDist(Math.max(getMaxDist(), legs.get(i).getDistance()));
        }
        System.out.println("FlightManager: getMaxLegDistInRoute(): maxDist = " + getMaxDist());
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
    public RouteDisplayHelper getRoute() {
        return route;
    }

    /**
     * @param route the route to set
     */
    public void setRoute(RouteDisplayHelper route) {
        this.route = route;
    }

    /**
     * @return the returnedRoute
     */
    public RouteDisplayHelper getReturnedRoute() {
        return returnedRoute;
    }

    /**
     * @param returnedRoute the returnedRoute to set
     */
    public void setReturnedRoute(RouteDisplayHelper returnedRoute) {
        this.returnedRoute = returnedRoute;
    }

    /**
     * @return the routeHelper
     */
    public RouteHelper getRouteHelper() {
        return routeHelper;
    }

    /**
     * @param routeHelper the routeHelper to set
     */
    public void setRouteHelper(RouteHelper routeHelper) {
        this.routeHelper = routeHelper;
    }

    /**
     * @return the returnRouteHelper
     */
    public RouteHelper getReturnRouteHelper() {
        return returnRouteHelper;
    }

    /**
     * @param returnRouteHelper the returnRouteHelper to set
     */
    public void setReturnRouteHelper(RouteHelper returnRouteHelper) {
        this.returnRouteHelper = returnRouteHelper;
    }

    /**
     * @return the selectedModels
     */
    public List<AircraftType> getSelectedModels() {
        return selectedModels;
    }

    /**
     * @param selectedModels the selectedModels to set
     */
    public void setSelectedModels(List<AircraftType> selectedModels) {
        this.selectedModels = selectedModels;
    }

    /**
     * @return the modelsForSelectedFlight
     */
    public List<AircraftType> getModelsForFlight() {
        return modelsForFlight;
    }

    /**
     * @param modelsForFlight the modelsForSelectedFlight to set
     */
    public void setModelsForFlight(List<AircraftType> modelsForFlight) {
        this.modelsForFlight = modelsForFlight;
    }

    /**
     * @return the maxDist
     */
    public float getMaxDist() {
        return maxDist;
    }

    /**
     * @param maxDist the maxDist to set
     */
    public void setMaxDist(float maxDist) {
        this.maxDist = maxDist;
    }

}
