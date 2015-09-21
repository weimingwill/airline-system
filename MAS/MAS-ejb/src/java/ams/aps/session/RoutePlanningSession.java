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
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author ChuningLiu
 */
@Stateless
public class RoutePlanningSession implements RoutePlanningSessionLocal {

    @PersistenceContext
    EntityManager em;

    @Override
    public List<Airport> getHubs() {
        Query query = em.createQuery("SELECT a FROM Airport a WHERE a.isHub = TRUE");
        List<Airport> hubs = null;
        try {
            hubs = (List<Airport>) query.getResultList();
        } catch (NoResultException e) {
            e.printStackTrace();
        }
        return hubs;
    }

    @Override
    public Country getCountryByCode(String code) {
        Query query = em.createQuery("SELECT c FROM Country c WHERE c.isoCode = :inCode");
        query.setParameter("inCode", code);
        Country country = null;
        try {
            country = (Country) query.getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
        }
        return country;
    }

    @Override
    public City getCityByID(Long id) {
        Query query = em.createQuery("SELECT c FROM City c WHERE c.id = :inID");
        query.setParameter("inID", id);
        City city = null;
        try {
            city = (City) query.getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
        }
        return city;
    }

    @Override
    public List<Country> getCountryList() {
        Query query = em.createQuery("SELECT c FROM Country c");

        List<Country> countries = new ArrayList<Country>();
        List<ArrayList> CountryList = new ArrayList<ArrayList>();
        try {
            countries = (List<Country>) query.getResultList();
            for (Object o : countries) {
                ArrayList<String> country = new ArrayList();
                Country c = (Country) o;
                country.add(c.getIsoCode());
                country.add(c.getCountryName());
                CountryList.add(country);
            }
        } catch (NoResultException e) {
            e.printStackTrace();
        }
        return countries;
    }

    @Override
    public List<City> getCityListByCountry(String countryCode) {
        Country country = getCountryByCode(countryCode);
        System.out.println("RoutePlanningSession: getCityListByCountry():" + countryCode);
        return (List<City>) country.getCities();
//        ArrayList<City> cities = (ArrayList<City>) country.getCities();
//        ArrayList<String> cityList = new ArrayList();
//        for (Object o : cities) {
//            City c = (City) o;
//            cityList.add(c.getCityName());
//        }
//        return cityList;
    }

    @Override
    public List<Airport> getAirportListByCity(String countryCode, String cityName) {
        Query query = em.createQuery("SELECT a FROM Airport a WHERE a.city.cityName = :inCityName AND a.country.isoCode = :inCountryCode");
        query.setParameter("inCityName", cityName);
        query.setParameter("inCountryCode", countryCode);
        return (List<Airport>) query.getResultList();
//            for (Object o : airports) {
//                ArrayList<String> airport = new ArrayList();
//                Airport a = (Airport) o;
//                airport.add(a.getIcaoCode());
//                airport.add(a.getAirportName());
//                AirportList.add(airport);
//            
//        return AirportList;
    }

    @Override
    public List<Airport> getAirportListByCityID(Long cityID) {
        Query query = em.createQuery("SELECT a FROM Airport a WHERE a.city.id = :inCityID");
        query.setParameter("inCityID", cityID);
        return (List<Airport>) query.getResultList();
//            for (Object o : airports) {
//                ArrayList<String> airport = new ArrayList();
//                Airport a = (Airport) o;
//                airport.add(a.getIcaoCode());
//                airport.add(a.getAirportName());
//                AirportList.add(airport);
//            
//        return AirportList;
    }

    @Override
    public List<Airport> getNonHubAirportListByCity(Long cityID) {
        Query query = em.createQuery("SELECT a FROM Airport a WHERE a.city.id = :inCityID AND a.isHub = FALSE");
        query.setParameter("inCityID", cityID);
//        ArrayList<Airport> airports = new ArrayList<Airport>();
//        ArrayList<ArrayList> AirportList = new ArrayList<ArrayList>();
        return (List<Airport>) query.getResultList();
//            for (Object o : airports) {
//                ArrayList<String> airport = new ArrayList();
//                Airport a = (Airport) o;
//                airport.add(a.getIcaoCode());
//                airport.add(a.getAirportName());
//                AirportList.add(airport);
//        return AirportList;
    }

    @Override
    public ArrayList<ArrayList> getRoutesByOD(String oriICAO, String desICAO) {
        Query query = em.createQuery("SELECT r FROM Route r WHERE r.originAirport.icaoCode = :inOriICAO AND r.destAirport.icaoCode = :inDesICAO");
        query.setParameter("inOriICAO", oriICAO);
        query.setParameter("inDesICAO", desICAO);

        ArrayList<Route> routes = null;
        ArrayList<ArrayList> routeList = null;
        Airport origin = getAirportByICAOCode(oriICAO);

        ArrayList<String> ori = new ArrayList();
        ori.add(oriICAO);
        ori.add(origin.getAirportName());

        try {
            routes = (ArrayList<Route>) query.getResultList();
            for (Object o : routes) {
                ArrayList<ArrayList> routeOne = new ArrayList();
                routeOne.add(ori);
                ArrayList<String> airport = new ArrayList();

                Route r = (Route) o;
                ArrayList<Leg> legs = (ArrayList<Leg>) r.getLegs();

                for (Object b : legs) {
                    Leg l = (Leg) o;
                    airport.add(l.getArrivalAirport().getIcaoCode());
                    airport.add(l.getArrivalAirport().getAirportName());
                    routeOne.add(airport);
                }
                routeList.add(routeOne);
            }
        } catch (NoResultException e) {
            e.printStackTrace();
        }
        return routeList;
    }

    @Override
    public boolean cancelHub(String icaoCode) {
        try {
            Airport hub = em.find(Airport.class, getAirportByICAOCode(icaoCode).getId());
            hub.setIsHub(FALSE);
            em.merge(hub);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public boolean addHub(String icaoCode) {
        try {
            Airport hub = em.find(Airport.class, getAirportByICAOCode(icaoCode).getId());
            hub.setIsHub(TRUE);
            em.merge(hub);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /*
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     * 
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public double distance(float lat1, float lat2, float lon1, float lon2, float el1, float el2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    @Override
    public Airport getAirportByICAOCode(String code) {
        System.out.println("RoutePlanningSession: getAirportByICAOCode(): " + code);
        Query query = em.createQuery("SELECT a FROM Airport a WHERE a.icaoCode = :inCode");
        query.setParameter("inCode", code);
        Airport airport = null;
        try {
            airport = (Airport) query.getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
        }
        return airport;
    }

}
