/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.session;

import ams.aps.entity.Airport;
import ams.aps.entity.City;
import ams.aps.entity.Country;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ChuningLiu
 */
@Local
public interface RoutePlanningSessionLocal {

    public List<Airport> getHubs();

    public List<Country> getCountryList();
    
    public Country getCountryByCode(String code);
    
    public City getCityByID(Long id);
    
    public Airport getAirportByICAOCode(String code);

    public List<City> getCityListByCountry(String countryCode);

    public List<Airport> getAirportListByCity(String countryCode, String cityName);
    
    public List<Airport> getAirportListByCityID(Long cityID);

    public List<Airport> getNonHubAirportListByCity(Long cityID);

    public ArrayList<ArrayList> getRoutesByOD(String oriICAO, String desICAO);

    public boolean cancelHub(String icaoCode);

    public boolean addHub(String icaoCode);
    
}
