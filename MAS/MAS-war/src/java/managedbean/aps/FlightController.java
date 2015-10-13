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
import ams.aps.util.exception.DeleteFailedException;
import ams.aps.util.exception.EmptyTableException;
import ams.aps.util.exception.ObjectDoesNotExistException;
import ams.aps.util.helper.RouteDisplayHelper;
import ams.aps.util.helper.RouteHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Named;
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

    @EJB
    private RoutePlanningSessionLocal routePlanningSession;

    private String flightNo;
    private Flight flight;
    private Flight returnedFlight;
    private Flight selectedFlight;
    private List<Flight> flights;
    private Route route;
    private List<RouteDisplayHelper> routeDisplayHelperList = new ArrayList();
    private RouteDisplayHelper selectedRouteDisplayHelper;
    private String oldFlightNo;
    private List<Route> availibleRoutes;
    private Boolean isComplete = null;
    private List<String> selectedType = new ArrayList(Arrays.asList("0", "1"));

    /**
     * Creates a new instance of FlightController
     */
    @PostConstruct
    public void init() {
        getFlightList(isComplete);
        getRoutes();
    }

    public FlightController() {
    }

    public void getFlightList(Boolean isComplete) {
        try {
            if (isComplete == null) {
                setFlights(flightSchedulingSession.getAllFlights());
            } else {
                setFlights(flightSchedulingSession.getFlight(isComplete));
            }
            removeReturned(flights);
        } catch (EmptyTableException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public String addFlight() {
        System.out.println("FlightController: addFlight()");
        if (Integer.parseInt(flightNo.split("MA")[1]) % 2 == 0) {
            msgController.addErrorMessage("Flight number must be an odd number");
            return "";
        } else {
            String returnedFlightNo = getReturnedFlightNo(flightNo);
            try {
                flightSchedulingSession.checkFlightExistence(flightNo);
                msgController.addErrorMessage("Flight Number: " + flightNo + "/" + returnedFlightNo + " exist!");
                return navigationController.redirectToCurrentPage();
            } catch (ObjectDoesNotExistException ex) {
                flight = new Flight();
                returnedFlight = new Flight();
                flight.setFlightNo(flightNo);
                returnedFlight.setFlightNo(returnedFlightNo);
                flight.setRoute(route);
                returnedFlight.setRoute(route.getReturnRoute());

                if (flightSchedulingSession.createReturnedFlight(flight, returnedFlight)) {
                    msgController.addMessage("New flight " + flightNo + " and returned flight " + returnedFlightNo + " created!");
                    return toCreateFlightStep();
                } else {
                    msgController.addErrorMessage("Fail to create flight " + flightNo + " and returned flight " + returnedFlightNo + "!");
                    return navigationController.redirectToCurrentPage();
                }
            }
        }
    }

    public String deleteFlight(String thisFlightNo) {
        thisFlightNo = thisFlightNo.split("/")[0];
        System.out.println("FlightController: deleteFlight(): flightNo = " + thisFlightNo);
        try {
            flightSchedulingSession.deleteFlight(thisFlightNo);
            msgController.addMessage("Delete Flight " + thisFlightNo + " and its Returned Flight");
        } catch (DeleteFailedException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToCurrentPage();
    }

    public String findFlightByFlightNo(String thisFlightNo) {
        System.out.println("findFlightByFlightNo: " + thisFlightNo.split("/")[0] + " AND " + thisFlightNo.split("/")[1]);
        try {
            flight = flightSchedulingSession.checkFlightExistence(thisFlightNo.split("/")[0]);
            returnedFlight = flightSchedulingSession.checkFlightExistence(thisFlightNo.split("/")[1]);
        } catch (ObjectDoesNotExistException ex) {
            System.out.println("Does Not Exists");
            return navigationController.redirectToCurrentPage();
        }
        return toCreateFlightStep();
    }

    public String toCreateFlightStep() {
        try {
            setFlight(flightSchedulingSession.checkFlightExistence(flight.getFlightNo()));
            setReturnedFlight(flightSchedulingSession.checkFlightExistence(returnedFlight.getFlightNo()));

            FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
            Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            map.put("flight", flight);
            map.put("returnedFlight", returnedFlight);
            flightManager.init();
            String returnPage;
            if (flight.getAircraftTypes() == null || flight.getAircraftTypes().isEmpty()) {
                returnPage = navigationController.toFleetAssignment();
            } else {
                flightManager.initAircraftModel(flight);
                returnPage = navigationController.toFreqPlanning();
            }
            return returnPage;
        } catch (ObjectDoesNotExistException ex) {
            return "";
        }
    }

    private String getReturnedFlightNo(String flightNo) {
        return "MA" + (Integer.parseInt(flightNo.split("MA")[1]) + 1);
    }

    public void getRoutes() {
        System.out.println("FlightController: getRoutes()");
        setAvailibleRoutes(routePlanningSession.getAllRoutes());
        removeReturned(availibleRoutes);

    }

    public String getOriDestString(Flight thisFlight) {
        if (thisFlight != null) {
            RouteHelper thisRouteHelper = new RouteHelper();
            routePlanningSession.getRouteAirports(thisFlight.getRoute(), thisRouteHelper);
            return thisRouteHelper.getOrigin().getCity().getCityName() + " - " + thisRouteHelper.getDestination().getCity().getCityName();
        } else {
            return "";
        }
    }

    public String getAircraftTypeString(Flight thisFlight) {
        String output = "";
        if (thisFlight != null) {
            for (AircraftType model : thisFlight.getAircraftTypes()) {
                output += model.getTypeCode() + " ";
            }
        }
        return output;
    }

    public RouteHelper getFlightRouteInfo(Flight thisFlight) {
        if (thisFlight != null) {
            RouteHelper thisFlightRoute = new RouteHelper();
            routePlanningSession.getRouteDistance(thisFlight.getRoute(), thisFlightRoute);
            flightSchedulingSession.calcFlightDuration(flightSchedulingSession.getModelWithMinMachNo(thisFlight.getAircraftTypes()), thisFlightRoute, thisFlight.getSpeedFraction());
            return thisFlightRoute;
        }
        return null;
    }

    private boolean failValidateFlightNo(String flightNo) {
        return Integer.parseInt(flightNo.split("MA")[1]) % 2 == 0;
    }

    public String updateFlightNo() {
        String newFlightNo = selectedFlight.getFlightNo();
        if (failValidateFlightNo(newFlightNo)) {
            msgController.addErrorMessage("Flight number must be an odd number");
            return "";
        } else {
            try {
                flightSchedulingSession.checkFlightExistence(newFlightNo);
                msgController.addErrorMessage("Flight " + newFlightNo + " Exists");
                return "";
            } catch (ObjectDoesNotExistException ex) {
                try {
                    Flight thisFlight = flightSchedulingSession.checkFlightExistence(oldFlightNo);
                    thisFlight.setFlightNo(newFlightNo);
                    thisFlight.getReturnedFlight().setFlightNo(selectedFlight.getReturnedFlight().getFlightNo());
                    flightSchedulingSession.updateFlight(thisFlight);
                    msgController.addMessage("Change flight number");
                    return navigationController.toCreateFlight();
                } catch (ObjectDoesNotExistException ex1) {
                    msgController.addErrorMessage("Change flight number.");
                    return "";
                }
            }
        }

    }

    public void onFlightNumChange(AjaxBehaviorEvent event) {
        String thisFlightNo = selectedFlight.getFlightNo();
        if (failValidateFlightNo(thisFlightNo)) {
            msgController.addErrorMessage("Flight number must be an odd number");
        } else {
            String thisReturnFlightNo = getReturnedFlightNo(thisFlightNo);
            selectedFlight.getReturnedFlight().setFlightNo(thisReturnFlightNo);
        }

    }

    public void onFlightFilterChange(AjaxBehaviorEvent event) {
        System.out.println("Selected type = " + selectedType);

        if (selectedType.isEmpty() || selectedType.size() == 2) {
            getFlightList(null);
        } else if (selectedType.contains("1")) {
            getFlightList(true);
        } else {
            getFlightList(false);
        }

    }

    public void setThisSelectedFlight(String flightNo) {
        flightNo = flightNo.split("/")[0];
        System.out.println("setSelectedFlight(): " + flightNo);
        try {
            setSelectedFlight(flightSchedulingSession.checkFlightExistence(flightNo));
            setOldFlightNo(flightNo);
            System.out.println("setSelectedFlight() selectedFlight = " + selectedFlight);
        } catch (ObjectDoesNotExistException ex) {

        }
    }

    private void removeReturned(Object list) {
        for (int i = 1; i < ((List<Object>) list).size(); i++) {
            ((List<Object>) list).remove(i);
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
    public String getOldFlightNo() {
        return oldFlightNo;
    }

    /**
     * @param oldFlightNo the newFlightNo to set
     */
    public void setOldFlightNo(String oldFlightNo) {
        this.oldFlightNo = oldFlightNo;
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

    /**
     * @return the selectedFlight
     */
    public Flight getSelectedFlight() {
        return selectedFlight;
    }

    /**
     * @param selectedFlight the selectedFlight to set
     */
    public void setSelectedFlight(Flight selectedFlight) {
        this.selectedFlight = selectedFlight;
    }

    /**
     * @return the flights
     */
    public List<Flight> getFlights() {
        return flights;
    }

    /**
     * @param flights the flights to set
     */
    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    /**
     * @return the isComplete
     */
    public Boolean getIsComplete() {
        return isComplete;
    }

    /**
     * @param isComplete the isComplete to set
     */
    public void setIsComplete(Boolean isComplete) {
        this.isComplete = isComplete;
    }

    /**
     * @return the selectedType
     */
    public List<String> getSelectedType() {
        return selectedType;
    }

    /**
     * @param selectedType the selectedType to set
     */
    public void setSelectedType(List<String> selectedType) {
        this.selectedType = selectedType;
    }

}
