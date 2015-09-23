/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import ams.aps.entity.Airport;
import ams.aps.entity.City;
import ams.aps.entity.Country;
import ams.aps.entity.Leg;
import ams.aps.entity.Route;
import ams.aps.session.RoutePlanningSessionLocal;
import ams.aps.util.helper.RouteDisplayHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.inject.Inject;
import managedbean.application.MsgController;

/**
 *
 * @author ChuningLiu
 */
@Named(value = "routeController")
@SessionScoped
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
    private List<List<String>> routesShowList;
    private List<List<String>> obsoleteRoutesShowList;
    private List<RouteDisplayHelper> routeDisplayList;

    /**
     * Creates a new instance of RouteController
     */
    public RouteController() {
    }

    @PostConstruct
    public void init() {
        countries = (List<Country>) routePlanningSession.getCountryList();
        hubs = (List<Airport>) routePlanningSession.getHubs();
        viewRoutes();
        viewObsoleteRoutes();
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
        if (routePlanningSession.addHub(airport.getIcaoCode())) {
            msgController.addMessage("Add a hub successfully!");
            cleanGlobalVariable();
        } else {
            msgController.addErrorMessage("Failed to add hub!");
            cleanGlobalVariable();
        }
    }

    public void cancelHub(ActionEvent event) {
        System.out.println("RouteController: cancelHub()");
        if (routePlanningSession.cancelHub(hub.getIcaoCode())) {
            msgController.addMessage("Cancel a hub successfully!");
            cleanGlobalVariable();
        } else {
            msgController.addErrorMessage("Failed to cancel hub!");
            cleanGlobalVariable();
        }
    }

    public void onCountryChange() {
        setCities((List<City>) routePlanningSession.getCityListByCountry(country.getIsoCode()));
        city = null;
        airport = null;
        airportsNotHub = new ArrayList<Airport>();
    }

    public void onCityChange() {
        setAirportsNotHub((List<Airport>) routePlanningSession.getNonHubAirportListByCity(city.getId()));
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
        List<Airport> allStops = new ArrayList<Airport>();
        allStops.add(hub);
        allStops.add(stopover);
        allStops.add(destination);
        if (routePlanningSession.checkRouteExistence(allStops) != null) {
            msgController.addErrorMessage("Route exists already!");
            cleanGlobalVariable();
        } else {
            if (routePlanningSession.addRoute(allStops)) {
                msgController.addMessage("Add route and return route successfully!");
                cleanGlobalVariable();
            } else {
                msgController.addErrorMessage("Fail to add route!");
                cleanGlobalVariable();
            }
        }

        viewRoutes();
    }

    public void deleteRoute(ActionEvent event) {
        if (routePlanningSession.softDeleteRoute(route.getId())) {
            msgController.addMessage("Route and return route deleted successfully!");
            cleanGlobalVariable();
        } else {
            msgController.addErrorMessage("Fail to delete route and return route!");
            cleanGlobalVariable();
        }

        viewRoutes();
        viewObsoleteRoutes();
    }

    public void viewRoutes() {
        routeList = routePlanningSession.getAllRoutes();
        setRoutesShowList(new ArrayList<List<String>>());
        for (Route r : routeList) {
            List<String> routeTemp = new ArrayList<String>();
            List<Leg> legs = r.getLegs();
            int size = legs.size();

            System.out.println("Retrived Route: " + legs.get(0).getDepartAirport().getAirportName() + "-" + legs.get(0).getArrivalAirport().getAirportName()+ "-" + legs.get(1).getDepartAirport().getAirportName() + "-" + legs.get(1).getArrivalAirport().getAirportName());
  
//            System.out.println("route input: " + legs.get(0).getDepartAirport().getAirportName() + " -> " + legs.get(1).getArrivalAirport().getAirportName());

            if (size > 1) {
                String stopovers = legs.get(0).getArrivalAirport().getAirportName();
                for (int i = 1; i < size - 1; i++) {
                    stopovers += " - ";
                    stopovers += legs.get(i).getArrivalAirport().getAirportName();
                }
                routeTemp.add(r.getId().toString());
                routeTemp.add(legs.get(0).getDepartAirport().getAirportName());
                routeTemp.add(legs.get(size - 1).getArrivalAirport().getAirportName());
                routeTemp.add(stopovers);
            } else {
                routeTemp.add(r.getId().toString());
                routeTemp.add(legs.get(0).getDepartAirport().getAirportName());
                routeTemp.add(legs.get(0).getArrivalAirport().getAirportName());
                routeTemp.add("N.A.");
            }
            getRoutesShowList().add(routeTemp);
        }
    }

    public void viewObsoleteRoutes() {
        List<Route> obRouteList = routePlanningSession.getAllObsoleteRoutes();
        setObsoleteRoutesShowList(new ArrayList<List<String>>());
        for (Route r : obRouteList) {
            List<String> routeTemp = new ArrayList<String>();
            List<Leg> legs = r.getLegs();
            int size = legs.size();

//            System.out.println("route input: " + legs.get(0).getDepartAirport().getAirportName() + " -> " + legs.get(1).getArrivalAirport().getAirportName());

            if (size > 1) {
                String stopovers = legs.get(0).getArrivalAirport().getAirportName();
                for (int i = 1; i < size - 1; i++) {
                    stopovers += " - ";
                    stopovers += legs.get(i).getArrivalAirport().getAirportName();
                }
                routeTemp.add(r.getId().toString());
                routeTemp.add(legs.get(0).getDepartAirport().getAirportName());
                routeTemp.add(legs.get(size - 1).getArrivalAirport().getAirportName());
                routeTemp.add(stopovers);
            } else {
                routeTemp.add(r.getId().toString());
                routeTemp.add(legs.get(0).getDepartAirport().getAirportName());
                routeTemp.add(legs.get(0).getArrivalAirport().getAirportName());
                routeTemp.add("N.A.");
            }
            System.out.println("route check: " + routeTemp.get(1) + " -> " + routeTemp.get(2) + " -> " + routeTemp.get(3));
            getObsoleteRoutesShowList().add(routeTemp);
        }
    }

    private void cleanGlobalVariable() {
        country = null;
        cities = null;
        city = null;
        airports = null;
        airport = null;
        hub = null;
        airportsNotHub = null;
        route = null;
        routeList = null;
        setStopover(null);
        setDestination(null);

        hubs = (List<Airport>) routePlanningSession.getHubs();
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
        return countries;
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
        return hubs;
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
     * @return the routesShowList
     */
    public List<List<String>> getRoutesShowList() {
        return routesShowList;
    }

    /**
     * @param routesShowList the routesShowList to set
     */
    public void setRoutesShowList(List<List<String>> routesShowList) {
        this.routesShowList = routesShowList;
    }

    /**
     * @return the obesoleteRoutesShowList
     */
    public List<List<String>> getObsoleteRoutesShowList() {
        return obsoleteRoutesShowList;
    }

    /**
     * @param obesoleteRoutesShowList the obesoleteRoutesShowList to set
     */
    public void setObsoleteRoutesShowList(List<List<String>> obsoleteRoutesShowList) {
        this.obsoleteRoutesShowList = obsoleteRoutesShowList;
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
