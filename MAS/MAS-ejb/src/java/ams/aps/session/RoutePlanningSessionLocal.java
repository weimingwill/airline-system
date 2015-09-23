/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.session;

import ams.aps.entity.Airport;
import ams.aps.entity.City;
import ams.aps.entity.Country;
import ams.aps.entity.Leg;
import ams.aps.entity.Route;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ChuningLiu
 */
@Local
public interface RoutePlanningSessionLocal {

    public List<Airport> getHubs();
    
    public List<Airport> getAllAirports();

    public List<Country> getCountryList();
    
    public Country getCountryByCode(String code);
    
    public City getCityByID(Long id);
    
    public Route getRouteByID(Long id);
    
    public Airport getAirportByICAOCode(String code);

    public List<City> getCityListByCountry(String countryCode);

    public List<Airport> getAirportListByCity(String countryCode, String cityName);
    
    public List<Airport> getAirportListByCityID(Long cityID);

    public List<Airport> getNonHubAirportListByCity(Long cityID);

    public List<Route> getRoutesByOD(String oriICAO, String desICAO);

    public boolean cancelHub(String icaoCode);

    public boolean addHub(String icaoCode);
    
    public boolean softDeleteRoute(Long id);
    
    public Leg checkLegExistence(Airport origin, Airport destination);
    
    public Route checkRouteExistence(List<Airport> allStopsNames);
    
    public boolean addRoute(List<Airport> stopList);
    
    public List<Route> getAllRoutes();
    
    public List<Route> getAllObsoleteRoutes();
    
}
