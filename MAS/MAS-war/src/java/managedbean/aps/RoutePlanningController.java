/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import ams.aps.entity.Airport;
import ams.aps.entity.Route;
import ams.aps.session.RoutePlanningSessionLocal;
import ams.aps.util.helper.RouteCompareHelper;
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
import managedbean.application.MsgController;

/**
 *
 * @author ChuningLiu
 */
@Named(value = "routePlanningController")
@SessionScoped
public class RoutePlanningController implements Serializable {

    @Inject
    private MsgController msgController;

    @EJB
    private RoutePlanningSessionLocal routePlanningSession;

    private Airport origin;
    private Airport destination;
    private Airport stopover;
    private List<Airport> hubs;
    private List<Airport> stopOvers;

    private double dirDistance;
    private double hsDistance;

    private RouteCompareHelper od;
    private RouteCompareHelper hs;
    private RouteCompareHelper cust;
    private List<RouteCompareHelper> routeList = new ArrayList<>();
    private List<RouteCompareHelper> selectedRoutes = new ArrayList<>();

    @PostConstruct
    public void init() {

        stopOvers = routePlanningSession.getAllAirports();
        stopOvers.remove(origin);
        stopOvers.remove(destination);

        odPass();
        autoGenerateRoute();
    }

    public RoutePlanningController() {
    }

    public void odPass() {
        Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        Airport temp = (Airport) map.get("origin");
        if (temp != null) {
            setOrigin(temp);
            setDestination((Airport) map.get("destination"));
        }

        System.out.println("RoutePlanningController: odPass: " + origin.getAirportName() + " " + destination.getAirportName());
    }

    public void planODRoute() {
        List<Airport> stops = new ArrayList<>();
        stops.add(origin);
        stops.add(destination);

        System.out.println("RoutePlanningController: planODRoute(): ");
        setOd(routePlanningSession.compareRoutePreparation("O-D", stops));
        System.out.println("RoutePlanningController: planODRoute(): planning successful");
        routeList.add(od);
    }

    public void planHSRoute() {
        System.out.println("RoutePlanningController: planHSRoute(): ");

        List<Airport> stops = routePlanningSession.getShortestHSRoute(origin, destination);
        if (stops.isEmpty()) {
            setHs(od);
            System.out.println("RoutePlanningController: planHSRoute(): planning as OD");
        } else {
            setHs(routePlanningSession.compareRoutePreparation("H&S", stops));
            System.out.println("RoutePlanningController: planHSRoute(): planning successful");
        }

        routeList.add(hs);
    }

    public void custRoute() {
//        stopOvers acquired, not limited stopovers
        List<Airport> stops = new ArrayList<>();
        stops.add(origin);
        stops.add(stopover);
        stops.add(destination);
        setCust(routePlanningSession.compareRoutePreparation("Customized", stops));

        if (routeList.size() == 3) {
            routeList.remove(2);
        }
        routeList.add(cust);
    }

    public void autoGenerateRoute() {
        planODRoute();
        planHSRoute();
    }

    public void compareRoute() {
        planODRoute();
        planHSRoute();
        custRoute();
    }

    public void addPlannedRoutes() {
        System.out.println("RoutePlanningController: addPlannedRoutes()");
        List<Airport> stopList;
        for (RouteCompareHelper r : selectedRoutes) {
            stopList = new ArrayList<>();
            Airport tempA = routePlanningSession.getAirportByName(r.getOrigin());
            stopList.add(tempA);
            if (!r.getStops().equals("N.A.")) {
                String stops = r.getStops();
                String[] stopOverNames = stops.split("-");

                for (String so : stopOverNames) {
                    stopList.add(routePlanningSession.getAirportByName(so));
                }
            }
            stopList.add(routePlanningSession.getAirportByName(r.getDestination()));
            Route temp = routePlanningSession.checkRouteExistence(stopList);

            if (temp == null) {
                if (routePlanningSession.addRoute(stopList)) {
                    msgController.addMessage("Add route and return route successfully!");
                } else {
                    msgController.addErrorMessage("Fail to add route!");
                }
            } else {
                msgController.addErrorMessage("Route existed! with type " + r.getType());
            }
        }
    }

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
     * @return the dirDistance
     */
    public double getDirDistance() {
        return dirDistance;
    }

    /**
     * @param dirDistance the dirDistance to set
     */
    public void setDirDistance(double dirDistance) {
        this.dirDistance = dirDistance;
    }

    /**
     * @return the hsDistance
     */
    public double getHsDistance() {
        return hsDistance;
    }

    /**
     * @param hsDistance the hsDistance to set
     */
    public void setHsDistance(double hsDistance) {
        this.hsDistance = hsDistance;
    }

    /**
     * @return the od
     */
    public RouteCompareHelper getOd() {
        return od;
    }

    /**
     * @param od the od to set
     */
    public void setOd(RouteCompareHelper od) {
        this.od = od;
    }

    /**
     * @return the hs
     */
    public RouteCompareHelper getHs() {
        return hs;
    }

    /**
     * @param hs the hs to set
     */
    public void setHs(RouteCompareHelper hs) {
        this.hs = hs;
    }

    /**
     * @return the cust
     */
    public RouteCompareHelper getCust() {
        return cust;
    }

    /**
     * @param cust the cust to set
     */
    public void setCust(RouteCompareHelper cust) {
        this.cust = cust;
    }

    /**
     * @return the routeList
     */
    public List<RouteCompareHelper> getRouteList() {
        return routeList;
    }

    /**
     * @param routeList the routeList to set
     */
    public void setRouteList(List<RouteCompareHelper> routeList) {
        this.routeList = routeList;
    }

    /**
     * @return the stopOvers
     */
    public List<Airport> getStopOvers() {
        return stopOvers;
    }

    /**
     * @param stopOvers the stopOvers to set
     */
    public void setStopOvers(List<Airport> stopOvers) {
        this.stopOvers = stopOvers;
    }

    /**
     * @return the selectedRoutes
     */
    public List<RouteCompareHelper> getSelectedRoutes() {
        return selectedRoutes;
    }

    /**
     * @param selectedRoutes the selectedRoutes to set
     */
    public void setSelectedRoutes(List<RouteCompareHelper> selectedRoutes) {
        this.selectedRoutes = selectedRoutes;
    }

    /**
     * @return the stopover
     */
    public Airport getStopover() {
        return stopover;
    }

    /**
     * @param stopover the stopover to set
     */
    public void setStopover(Airport stopover) {
        this.stopover = stopover;
    }

}
