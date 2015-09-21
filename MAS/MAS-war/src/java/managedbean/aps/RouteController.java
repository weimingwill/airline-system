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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.event.ValueChangeEvent;

/**
 *
 * @author ChuningLiu
 */
@Named(value = "routeController")
@SessionScoped
public class RouteController implements Serializable {

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

    /**
     * Creates a new instance of RouteController
     */
    public RouteController() {
    }

    @PostConstruct
    public void init() {
        countries = (List<Country>) routePlanningSession.getCountryList();
        hubs = (List<Airport>) routePlanningSession.getHubs();
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

    public void addNewHub(ActionEvent event) {
        System.out.println("RouteController: addHub()");
        FacesContext context = FacesContext.getCurrentInstance();
        if (routePlanningSession.addHub(airport.getIcaoCode())) {
            context.addMessage(null, new FacesMessage("Successful", "Add a hub successfully!"));
            cleanGlobalVariable();
        } else {
            context.addMessage(null, new FacesMessage("Failed", "Failed to add hub!"));
            cleanGlobalVariable();
        }
    }

    public void cancelHub(ActionEvent event) {
        System.out.println("RouteController: cancelHub()");
        FacesContext context = FacesContext.getCurrentInstance();
        if (routePlanningSession.cancelHub(hub.getIcaoCode())) {
            context.addMessage(null, new FacesMessage("Successful", "Cancel a hub successfully!"));
            cleanGlobalVariable();
        } else {
            context.addMessage(null, new FacesMessage("Failed", "Failed to cancel hub!"));
            cleanGlobalVariable();
        }
    }

    public void getCitiesByCountry(ValueChangeEvent event) {
        String isoCode = ((Country) event.getNewValue()).getIsoCode();
        setCities((List<City>) routePlanningSession.getCityListByCountry(isoCode));
    }

    public void getNonHubAitportsByCityID(ValueChangeEvent event) {
        Long cityID = ((City) event.getNewValue()).getId();
        setAirportsNotHub((List<Airport>) routePlanningSession.getNonHubAirportListByCity(cityID));
    }

//    public List<Route> checkRouteExistnce(String oriAirportIcao, String DesAirportIcao) {
//        sameODRoutes = (List<Route>) routePlanningSession.getRoutesByOD(oriAirportIcao, DesAirportIcao);
//        return sameODRoutes;
//    }

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
        FacesContext context = FacesContext.getCurrentInstance();
        List<Airport> allStops = new ArrayList<Airport>();
        allStops.add(hub);
        allStops.add(stopover);
        allStops.add(destination);
        if (routePlanningSession.checkRouteExistence(allStops)!=null) {
            context.addMessage(null, new FacesMessage("Error", "Route exists already!"));
        } else {
            if (routePlanningSession.addRoute(allStops)) {
                context.addMessage(null, new FacesMessage("Successful", "Add route and return route successfully!"));
                cleanGlobalVariable();
            } else {
                context.addMessage(null, new FacesMessage("Failed", "Failed to add route!"));
                cleanGlobalVariable();
            }
        }
    }
    
    public void viewRoutes() {
        routeList = routePlanningSession.getAllroutes();
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

}
