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
import ams.aps.util.helper.RouteCompareHelper;
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
    
    public Airport getAirportByName(String name);

    public List<City> getCityListByCountry(String countryCode);

    public List<Airport> getAirportListByCity(String countryCode, String cityName);
    
    public List<Airport> getAirportListByCityID(Long cityID);

    public List<Airport> getNonHubAirportListByCity(Long cityID);

    public List<Route> getRoutesByOD(Airport ori, Airport dest);

    public boolean cancelHub(String icaoCode);
    
    public boolean checkHub(String icaoCode);

    public boolean addHub(String icaoCode);
    
    public boolean softDeleteRoute(Long id);
    
    public Leg checkLegExistence(Airport origin, Airport destination);
    
    public Route checkRouteExistence(List<Airport> allStopsNames);
    
    public boolean addRoute(List<Airport> stopList);
    
    public List<Route> getAllRoutes();
    
    public List<Route> getAllObsoleteRoutes();
    
    public List<Airport> getShortestHSRoute(Airport ori, Airport dest);
    
    public RouteCompareHelper compareRoutePreparation(String type, List<Airport> stopList);
    
    public boolean addCountry(String isoCode, String countryName);
    
    public boolean addCity(String countryISO, String cityName, Float utc);
    
    public boolean addAirport(String countryISO, String cityName, Airport airport);
    
    public boolean checkIATA(String iata);
    
    public boolean checkICAO(String icao);
    
    public boolean checkCityName(String cityName, String iso);
    
    public boolean checkISO(String iso, String countryName);
}
