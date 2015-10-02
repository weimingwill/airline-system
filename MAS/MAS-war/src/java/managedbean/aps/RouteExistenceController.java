/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import ams.aps.entity.Airport;
import ams.aps.entity.Route;
import ams.aps.entity.RouteLeg;
import ams.aps.session.RoutePlanningSessionLocal;
import ams.aps.util.helper.RouteDisplayHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import managedbean.application.MsgController;

/**
 *
 * @author ChuningLiu
 */
@Named(value = "routeExistenceController")
@RequestScoped
public class RouteExistenceController implements Serializable {

    @Inject
    RouteController routeController;
    
    @Inject
    RoutePlanningController routePlanningController;

    @EJB
    private RoutePlanningSessionLocal routePlanningSession;

    private List<Airport> hubs;
    private Airport origin;
    private Airport destination;
    private List<Airport> airports;
    private List<Route> sameODRoutes;
    private List<RouteDisplayHelper> routeDisplayList = new ArrayList<>();

    private String headMsg;
    private String confirmMsg;

    @PostConstruct
    public void init() {
        setHubs(routePlanningSession.getHubs());
        setAirports(routePlanningSession.getAllAirports());
    }

    public void onOriginChange() {
        setAirports(routePlanningSession.getAllAirports());
        airports.remove(origin);
    }

    public void checkRouteExistence(ActionEvent event) {
        setSameODRoutes(routePlanningSession.getRoutesByOD(getOrigin(), getDestination()));
        if (sameODRoutes.isEmpty()) {
            setHeadMsg("Route not existed");
            setConfirmMsg("Do you want to plan the route?");
        } else {
            setHeadMsg("Following routes are existed");
            setConfirmMsg("Do you still want to plan the route?");
            setRouteDisplayList(new ArrayList());
            routeController.createRouteDisplayHelpers(sameODRoutes, routeDisplayList);
        }

    }

    public void proceedToPlanning() {
        Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        map.put("origin", origin);
        map.put("destination", destination);
        routePlanningController.odPass();
    }

    /**
     * @return the hubs
     */
    public List<Airport> getHubs() {
        return hubs;
    }

    /**
     * @param hubs the hubs to set
     */
    public void setHubs(List<Airport> hubs) {
        this.hubs = hubs;
    }

    /**
     * @return the origin
     */
    public Airport getOrigin() {
        return origin;
    }

    /**
     * @param origin the origin to set
     */
    public void setOrigin(Airport origin) {
        this.origin = origin;
    }

    /**
     * @return the destination
     */
    public Airport getDestination() {
        return destination;
    }

    /**
     * @param destination the destination to set
     */
    public void setDestination(Airport destination) {
        this.destination = destination;
    }

    /**
     * @return the airports
     */
    public List<Airport> getAirports() {
        return airports;
    }

    /**
     * @param airports the airports to set
     */
    public void setAirports(List<Airport> airports) {
        this.airports = airports;
    }

    /**
     * @return the sameODRoutes
     */
    public List<Route> getSameODRoutes() {
        return sameODRoutes;
    }

    /**
     * @param sameODRoutes the sameODRoutes to set
     */
    public void setSameODRoutes(List<Route> sameODRoutes) {
        this.sameODRoutes = sameODRoutes;
    }

    /**
     * @return the headMsg
     */
    public String getHeadMsg() {
        return headMsg;
    }

    /**
     * @param headMsg the headMsg to set
     */
    public void setHeadMsg(String headMsg) {
        this.headMsg = headMsg;
    }

    /**
     * @return the confirmMsg
     */
    public String getConfirmMsg() {
        return confirmMsg;
    }

    /**
     * @param confirmMsg the confirmMsg to set
     */
    public void setConfirmMsg(String confirmMsg) {
        this.confirmMsg = confirmMsg;
    }

    /**
     * @return the routeDisplayList
     */
    public List<RouteDisplayHelper> getRouteDisplayList() {
        return routeDisplayList;
    }

    /**
     * @param routeDisplayList the routeDisplayList to set
     */
    public void setRouteDisplayList(List<RouteDisplayHelper> routeDisplayList) {
        this.routeDisplayList = routeDisplayList;
    }

}
