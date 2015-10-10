/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import ams.aps.entity.Airport;
import ams.aps.entity.City;
import ams.aps.entity.Country;
import ams.aps.entity.Route;
import ams.aps.entity.RouteLeg;
import ams.aps.session.RoutePlanningSessionLocal;
import ams.aps.util.helper.LegHelper;
import ams.aps.util.helper.RouteDisplayHelper;
import ams.aps.util.helper.RouteHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import managedbean.application.MsgController;

/**
 *
 * @author ChuningLiu
 */
@Named(value = "routeController")
@ViewScoped
public class RouteController implements Serializable {

    @Inject
    private MsgController msgController;

    @EJB
    private RoutePlanningSessionLocal routePlanningSession;

    private Country country;
    private List<Country> countries;

    private City city;
    private List<City> cities;

    private Airport airport;
    private Airport hub;
    private Airport stopover;
    private Airport destination;
    private List<Airport> airports;
    private List<Airport> stopList;
    private List<Airport> destList;
    private List<Airport> hubs;
    private List<Airport> airportsNotHub;

    private Route route;
    private List<Route> routeList;
    private List<Route> sameODRoutes;

    private List<RouteDisplayHelper> routeDisplayList = new ArrayList<>();
    private List<RouteDisplayHelper> obsRouteDisplayList = new ArrayList<>();
    private List<RouteDisplayHelper> routesToBeDeleted;

    /**
     * Creates a new instance of RouteController
     */
    public RouteController() {
    }

    @PostConstruct
    public void init() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String uri = request.getRequestURI();
        uri = uri.substring(uri.lastIndexOf("/") + 1, uri.indexOf('.', uri.lastIndexOf("/")));
        switch (uri) {
            case "viewRoutes":
            case "createFlight":
                viewRoutes();
                break;
            case "viewObsoleteRoute":
                viewObsoleteRoutes();
                break;
        }
    }

    public Country getCountryByCode(String isoCode) {
        return routePlanningSession.getCountryByCode(isoCode);
    }

    public City getCityByID(Long id) {
        return routePlanningSession.getCityByID(id);
    }

    public Airport getAirportByICAO(String icao) {
        return routePlanningSession.getAirportByICAOCode(icao);
    }

    public Route getRouteByID(Long id) {
        return routePlanningSession.getRouteByID(id);
    }

    public void addNewHub(ActionEvent event) {
        System.out.println("RouteController: addHub()");
        if (airport == null) {
            msgController.addErrorMessage("Airport must be selected!");
        } else {
            if (routePlanningSession.addHub(airport.getIcaoCode())) {
                msgController.addMessage("Add a hub successfully!");
            } else {
                msgController.addErrorMessage("Failed to add hub!");
            }
        }
    }

    public void cancelHub(ActionEvent event) {
        System.out.println("RouteController: cancelHub()");
        if (routePlanningSession.checkHub(hub.getIcaoCode())) {
            if (routePlanningSession.cancelHub(hub.getIcaoCode())) {
                msgController.addMessage("Cancel a hub successfully!");
            } else {
                msgController.addErrorMessage("Failed to cancel hub!");
            }
        } else {
            msgController.addErrorMessage("Hub is in operation, cannot be cancelled!");
        }
    }

    public void onCountryChange() {
        setCities((List<City>) routePlanningSession.getCityListByCountry(country.getIsoCode()));
        if (cities.isEmpty()) {
            msgController.addErrorMessage("No cities found in this country!");
        }
        city = null;
        airport = null;
        airportsNotHub = new ArrayList();
    }

    public void onCityChange() {
        setAirportsNotHub((List<Airport>) routePlanningSession.getNonHubAirportListByCity(city.getId()));
        if (airportsNotHub.isEmpty()) {
            msgController.addErrorMessage("No airports not hub found in this city!");
        }
    }

    public void onOriginChange() {
        stopover = null;
        destination = null;
        stopList = null;
        destList = null;
        airports = routePlanningSession.getAllAirports();
        setStopList(airports);
        stopList.remove(hub);
        setDestList(airports);
        destList.remove(hub);
    }

    public void onStopoverChange() {
        destination = null;
        destList = null;
        airports = routePlanningSession.getAllAirports();
        setDestList(airports);
        destList.remove(hub);
        destList.remove(stopover);
    }

    public void addRouteSimple(ActionEvent event) {
        List<Airport> allStops = new ArrayList();
        allStops.add(hub);
        if (stopover != null) {
            allStops.add(stopover);
        }
        allStops.add(destination);
        if (routePlanningSession.checkRouteExistence(allStops) != null) {
            msgController.addErrorMessage("Route exists already!");
        } else {
            if (routePlanningSession.addRoute(allStops)) {
                msgController.addMessage("Add route and return route successfully!");
            } else {
                msgController.addErrorMessage("Fail to add route!");
            }
        }
    }

    public void deleteRoute(ActionEvent event) {
        boolean deleted = true;
        for (RouteDisplayHelper r : routesToBeDeleted) {
            if (!routePlanningSession.softDeleteRoute(r.getId())) {
                deleted = false;
                msgController.addErrorMessage("Fail to delete routes and return routes!");
//                cleanGlobalVariable();
                break;
            }
        }
        if (deleted) {
            msgController.addMessage("Routes and return routes deleted successfully!");
        }
    }

    public List<RouteDisplayHelper> viewRoutes() {
        setRouteList(routePlanningSession.getAllRoutes());
        setRouteDisplayList(new ArrayList());
        createRouteDisplayHelpers(routeList, routeDisplayList);
        return routeDisplayList;
    }

    public List<RouteDisplayHelper> viewObsoleteRoutes() {
        List<Route> obRouteList = routePlanningSession.getAllObsoleteRoutes();
        setObsRouteDisplayList(new ArrayList());
        createRouteDisplayHelpers(obRouteList, obsRouteDisplayList);
        return obsRouteDisplayList;
    }

    public void createRouteDisplayHelpers(List<Route> routeList, List<RouteDisplayHelper> displayHelperList) {
        for (Route thisRoute : routeList) {
            RouteDisplayHelper routeDisplayHelper = new RouteDisplayHelper();
            RouteHelper routeHelper = new RouteHelper();
            String legString = "";
            getRouteDetail(thisRoute, routeHelper);

            TreeMap<Integer, Airport> legAirports = routeHelper.getStopovers();
            legString = getStopoverString(legAirports, "name");
            routeDisplayHelper.setId(routeHelper.getId());
            routeDisplayHelper.setOrigin(routeHelper.getOrigin().getAirportName());
            routeDisplayHelper.setDestination(routeHelper.getDestination().getAirportName());
            routeDisplayHelper.setLegs(legString);
            routeDisplayHelper.setReturnRouteId(routeHelper.getReturnRouteId());
            displayHelperList.add(routeDisplayHelper);
        }
    }

    public String getStopoverString(TreeMap<Integer, Airport> legAirports, String type) {
        String legString = "";
        if (legAirports.isEmpty()) {
            legString = "N.A.";
        } else {
            for (int i = 1; i <= legAirports.size(); i++) {
                if (i < legAirports.size()) {
                    switch (type) {
                        case "name":
                            legString += legAirports.get(i).getAirportName() + "-";
                            break;
                        case "icao":
                            legString += legAirports.get(i).getIcaoCode() + "-";
                            break;
                        case "iata":
                            legString += legAirports.get(i).getIataCode() + "-";
                    }
                } else {
                    switch (type) {
                        case "name":
                            legString += legAirports.get(i).getAirportName();
                            break;
                        case "icao":
                            legString += legAirports.get(i).getIcaoCode();
                            break;
                        case "iata":
                            legString += legAirports.get(i).getIataCode();
                    }
                }
            }
        }
        return legString;
    }

    public void getRouteDetail(Route thisRoute, RouteHelper routeHelper) {
        TreeMap<Integer, Airport> legAirports = new TreeMap();
        int numOfLegs = thisRoute.getRouteLegs().size();

        routeHelper.setId(thisRoute.getRouteId());
        routeHelper.setReturnRouteId(thisRoute.getReturnRoute().getRouteId());

        System.out.println("Route Controller: viewRoutes(): thisRoute = " + routeHelper.getId());
        for (RouteLeg thisRouteLeg : thisRoute.getRouteLegs()) {
            int legSeq = thisRouteLeg.getLegSeq();

            System.out.println("Route Controller: viewRoutes(): FROM - TO (" + legSeq + "): " + thisRouteLeg.getLeg().getDepartAirport().getAirportName() + " - " + thisRouteLeg.getLeg().getArrivalAirport().getAirportName());
            if (numOfLegs == 1) {
                routeHelper.setOrigin(thisRouteLeg.getLeg().getDepartAirport());
                routeHelper.setDestination(thisRouteLeg.getLeg().getArrivalAirport());
            } else {
                if (legSeq == 0) {
                    routeHelper.setOrigin(thisRouteLeg.getLeg().getDepartAirport());
                } else if (legSeq == (numOfLegs - 1)) {
                    legAirports.put(legSeq, thisRouteLeg.getLeg().getDepartAirport());
                    routeHelper.setDestination(thisRouteLeg.getLeg().getArrivalAirport());
                } else {
                    legAirports.put(legSeq, thisRouteLeg.getLeg().getDepartAirport());
                }
            }
        }
        routeHelper.setStopovers(legAirports);
        // Calculate route distance
        List<LegHelper> legHelpers = routePlanningSession.calcRouteLegDist(thisRoute);
        float totalDist = 0;
        for(LegHelper thisLegHelper: legHelpers){
            totalDist += thisLegHelper.getDistance();
        }
        routeHelper.setTotalDistance(totalDist);
        routeHelper.setLegs(legHelpers);
    }

    /**
     * @return the country
     */
    public Country getCountry() {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(Country country) {
        System.out.println("RouteController: setCountry()");
        this.country = country;
    }

    /**
     * @return the countries
     */
    public List<Country> getCountries() {
        return (List<Country>) routePlanningSession.getCountryList();
    }

    /**
     * @param countries the countries to set
     */
    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

    /**
     * @return the city
     */
    public City getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(City city) {
        this.city = city;
    }

    /**
     * @return the cities
     */
    public List<City> getCities() {
        return cities;
    }

    /**
     * @param cities the cities to set
     */
    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    /**
     * @return the airport
     */
    public Airport getAirport() {
        return airport;
    }

    /**
     * @param airport the airport to set
     */
    public void setAirport(Airport airport) {
        this.airport = airport;
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
     * @return the hubs
     */
    public List<Airport> getHubs() {
        return routePlanningSession.getHubs();
    }

    /**
     * @param hubs the hubs to set
     */
    public void setHubs(List<Airport> hubs) {
        this.hubs = hubs;
    }

    /**
     * @return the airportsNotHub
     */
    public List<Airport> getAirportsNotHub() {
        return airportsNotHub;
    }

    /**
     * @param airportsNotHub the airportsNotHub to set
     */
    public void setAirportsNotHub(List<Airport> airportsNotHub) {
        this.airportsNotHub = airportsNotHub;
    }

    /**
     * @return the hub
     */
    public Airport getHub() {
        return hub;
    }

    /**
     * @param hub the hub to set
     */
    public void setHub(Airport hub) {
        this.hub = hub;
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
     * @return the routeList
     */
    public List<Route> getRouteList() {
        return routeList;
    }

    /**
     * @param routeList the routeList to set
     */
    public void setRouteList(List<Route> routeList) {
        this.routeList = routeList;
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
     * @return the stopList
     */
    public List<Airport> getStopList() {
        return stopList;
    }

    /**
     * @param stopList the stopList to set
     */
    public void setStopList(List<Airport> stopList) {
        this.stopList = stopList;
    }

    /**
     * @return the destList
     */
    public List<Airport> getDestList() {
        return destList;
    }

    /**
     * @param destList the destList to set
     */
    public void setDestList(List<Airport> destList) {
        this.destList = destList;
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

    /**
     * @return the obsRouteDisplayList
     */
    public List<RouteDisplayHelper> getObsRouteDisplayList() {
        return obsRouteDisplayList;
    }

    /**
     * @param obsRouteDisplayList the obsRouteDisplayList to set
     */
    public void setObsRouteDisplayList(List<RouteDisplayHelper> obsRouteDisplayList) {
        this.obsRouteDisplayList = obsRouteDisplayList;
    }

    /**
     * @return the routesToBeDeleted
     */
    public List<RouteDisplayHelper> getRoutesToBeDeleted() {
        return routesToBeDeleted;
    }

    /**
     * @param routesToBeDeleted the routesToBeDeleted to set
     */
    public void setRoutesToBeDeleted(List<RouteDisplayHelper> routesToBeDeleted) {
        this.routesToBeDeleted = routesToBeDeleted;
    }
}
