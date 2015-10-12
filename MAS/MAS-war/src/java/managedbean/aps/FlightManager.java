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
import ams.aps.session.RoutePlanningSessionLocal;
import ams.aps.util.exception.ObjectDoesNotExistException;
import ams.aps.util.helper.LegHelper;
import ams.aps.util.helper.RouteDisplayHelper;
import ams.aps.util.helper.RouteHelper;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;

/**
 *
 * @author Lewis
 */
@Named(value = "flightManager")
@SessionScoped
public class FlightManager implements Serializable {
    
    @EJB
    private RoutePlanningSessionLocal routePlanningSession;
    
    @EJB
    private FlightSchedulingSessionLocal flightSchedulingSession;
    
    @Inject
    MsgController msgController;
    
    @Inject
    NavigationController navigationController;
    
    @Inject
    RouteController routeController;
    
    private Flight flight;
    private Flight returnedFlight;
    private RouteDisplayHelper route = new RouteDisplayHelper();
    private RouteDisplayHelper returnedRoute = new RouteDisplayHelper();
    private RouteHelper routeHelper = new RouteHelper();
    private RouteHelper returnRouteHelper = new RouteHelper();
    private List<AircraftType> selectedModels = new ArrayList();
    private List<AircraftType> modelsForFlight = new ArrayList();
    private AircraftType modelWithMinMach = new AircraftType();
    private double maxDist = 0;
    private double speedFraction;
    private final int HOURS_PER_WEEK = 24 * 7;
    private final int DAYS_PER_WEEK = 7;
    private int weeklyFreq = 1;

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
        setSpeedFraction(flight.getSpeedFraction()); // get defualt speedFraction
    }
    
    public void initAircraftModel(Flight flight){
        setSelectedModels(flight.getAircraftTypes());
        getModelWithMinMachNo();
        routePlanningSession.getRouteDetail(flight.getRoute(), routeHelper);
        routePlanningSession.getRouteDetail(flight.getRoute(), returnRouteHelper);
        setFlightDuration();
        System.out.println("routeHelper = " + routeHelper.getTotalDistance());
        System.out.println("returnRouteHelper = " + returnRouteHelper.getTotalDistance());
    }
    
    private void getAddedFlight() {
        Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        setFlight((Flight) map.get("flight"));
        setReturnedFlight((Flight) map.get("returnedFlight"));
    }
    
    public String updateFlight(Flight flight) {
        try {
            flightSchedulingSession.updateFlight(flight);
            msgController.addMessage("Flight " + flight.getFlightNo() + " and Return Flight "+ flight.getReturnedFlight().getFlightNo() + " Saved!");
            return navigationController.toCreateFlight();
        } catch (ObjectDoesNotExistException ex) {
            msgController.addErrorMessage("Update Flight " + flight.getFlightNo() + " and Return Flight "+ flight.getReturnedFlight().getFlightNo() +" Information!");
            return "";
        }
    }
    
    public String updateFlightAircraftModel() {
        flight.setAircraftTypes(selectedModels);
        flight.getReturnedFlight().setAircraftTypes(selectedModels);
        return updateFlight(flight);
    }
    
    public String saveFlight() {
        flight.setAircraftTypes(selectedModels);
        flight.setSpeedFraction(speedFraction);
        flight.setWeeklyFrequency(weeklyFreq);
        flight.setCompleted(Boolean.TRUE);
        flight.getReturnedFlight().setAircraftTypes(selectedModels);
        flight.getReturnedFlight().setSpeedFraction(speedFraction);
        flight.getReturnedFlight().setWeeklyFrequency(weeklyFreq);
        flight.getReturnedFlight().setCompleted(Boolean.TRUE);
        return updateFlight(flight);
    }
    
    public void getRouteDetails() {
        Route thisRoute = flight.getRoute();
        Route thisReturnedRoute = thisRoute.getReturnRoute();
        setValueForRouteHelper(thisRoute, getRouteHelper(), route);
        setValueForRouteHelper(thisReturnedRoute, getReturnRouteHelper(), returnedRoute);
    }
    
    private void setValueForRouteHelper(Route thisRoute, RouteHelper thisRouteHelper, RouteDisplayHelper thisRouteDisplayHelper) {
        routePlanningSession.getRouteDetail(thisRoute, thisRouteHelper);
        thisRouteDisplayHelper.setId(thisRouteHelper.getId());
        thisRouteDisplayHelper.setReturnRouteId(thisRouteHelper.getReturnRouteId());
        thisRouteDisplayHelper.setOrigin(thisRouteHelper.getOrigin().getAirportName());
        thisRouteDisplayHelper.setDestination(thisRouteHelper.getDestination().getAirportName());
        thisRouteDisplayHelper.setLegs(routeController.getStopoverString(thisRouteHelper.getStopovers(), "name"));
        thisRouteDisplayHelper.setTotalDistance(thisRouteHelper.getTotalDistance());
    }
    
    public void getAircraftModelsForFlight() {
        getMaxLegDistInRoute(routeHelper.getLegs());
        setModelsForFlight(flightSchedulingSession.getCapableAircraftTypesForRoute(getMaxDist()));
    }
    
    public void setFlightDuration() {
        getModelWithMinMachNo();
        flightSchedulingSession.calcFlightDuration(modelWithMinMach, routeHelper, speedFraction);
        flightSchedulingSession.calcFlightDuration(modelWithMinMach, returnRouteHelper, speedFraction);
    }
    
    public String checkSelectedAircraftModels() {
        boolean sameTypeFamily = true;
        // no aircraft model selected
        if (selectedModels.isEmpty()) {
            msgController.error("Please select aircraft model");
            return "";
        } else {
            for (int i = 0; i < selectedModels.size() - 1; i++) {
                if (!selectedModels.get(i).getTypeFamily().equals(selectedModels.get(i + 1).getTypeFamily())) {
                    sameTypeFamily = false;
                    break;
                }
            }
            // if models selected are not the same type family
            if (!sameTypeFamily) {
                msgController.warn("Selected aicraft model must be of the same type family");
                return "";
            } else {
                setFlightDuration();
                return navigationController.toFreqPlanning();
            }
        }
    }
    
    public void onSpeedFractionChange(AjaxBehaviorEvent event) {
        setFlightDuration();
    }
    
    public int getMaxFreq() {
        double totalDuration = routeHelper.getTotalDuration() + returnRouteHelper.getTotalDuration();
        int maxFreq = (int) Math.floor(HOURS_PER_WEEK / totalDuration);
        return maxFreq > DAYS_PER_WEEK ? DAYS_PER_WEEK : maxFreq;
    }
    
    public double getCruiseSpeed() {
        return speedFraction * modelWithMinMach.getMaxMachNo() * 1225.044;
    }
    
    private void getMaxLegDistInRoute(List<LegHelper> legs) {
        setMaxDist(legs.get(0).getDistance());
        for (int i = 1; i < legs.size(); i++) {
            setMaxDist(Math.max(getMaxDist(), legs.get(i).getDistance()));
        }
        System.out.println("FlightManager: getMaxLegDistInRoute(): maxDist = " + getMaxDist());
    }
    
    private void getModelWithMinMachNo() {
        setModelWithMinMach(flightSchedulingSession.getModelWithMinMachNo(selectedModels));
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
    public double getMaxDist() {
        return maxDist;
    }

    /**
     * @param maxDist the maxDist to set
     */
    public void setMaxDist(double maxDist) {
        this.maxDist = maxDist;
    }

    /**
     * @return the modelWithMinMach
     */
    public AircraftType getModelWithMinMach() {
        return modelWithMinMach;
    }

    /**
     * @param modelWithMinMach the modelWithMinMach to set
     */
    public void setModelWithMinMach(AircraftType modelWithMinMach) {
        this.modelWithMinMach = modelWithMinMach;
    }

    /**
     * @return the speedFraction
     */
    public double getSpeedFraction() {
        return speedFraction;
    }

    /**
     * @param speedFraction the speedFraction to set
     */
    public void setSpeedFraction(double speedFraction) {
        this.speedFraction = speedFraction;
    }

    /**
     * @return the weeklyFreq
     */
    public int getWeeklyFreq() {
        return weeklyFreq;
    }

    /**
     * @param weeklyFreq the weeklyFreq to set
     */
    public void setWeeklyFreq(int weeklyFreq) {
        this.weeklyFreq = weeklyFreq;
    }

    /**
     * @return the returnedFlight
     */
    public Flight getReturnedFlight() {
        return returnedFlight;
    }

    /**
     * @param returnedFlight the returnedFlight to set
     */
    public void setReturnedFlight(Flight returnedFlight) {
        this.returnedFlight = returnedFlight;
    }
    
}
