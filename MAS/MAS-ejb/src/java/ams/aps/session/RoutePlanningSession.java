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
    public List<Airport> getAllAirports() {
        Query query = em.createQuery("SELECT a FROM Airport a");
        List<Airport> airports = null;
        try {
            airports = (List<Airport>) query.getResultList();
        } catch (NoResultException e) {
            e.printStackTrace();
        }
        return airports;
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
    }

    @Override
    public List<Airport> getAirportListByCity(String countryCode, String cityName) {
        Query query = em.createQuery("SELECT a FROM Airport a WHERE a.city.cityName = :inCityName AND a.country.isoCode = :inCountryCode");
        query.setParameter("inCityName", cityName);
        query.setParameter("inCountryCode", countryCode);
        return (List<Airport>) query.getResultList();
    }

    @Override
    public List<Airport> getAirportListByCityID(Long cityID) {
        Query query = em.createQuery("SELECT a FROM Airport a WHERE a.city.id = :inCityID");
        query.setParameter("inCityID", cityID);
        return (List<Airport>) query.getResultList();
    }

    @Override
    public List<Airport> getNonHubAirportListByCity(Long cityID) {
        Query query = em.createQuery("SELECT a FROM Airport a WHERE a.city.id = :inCityID AND a.isHub = FALSE");
        query.setParameter("inCityID", cityID);
        return (List<Airport>) query.getResultList();
    }

    @Override
    public List<Route> getRoutesByOD(String oriICAO, String desICAO) {
        Query query = em.createQuery("SELECT r FROM Route r WHERE r.originAirport.icaoCode = :inOriICAO AND r.destAirport.icaoCode = :inDesICAO");
        query.setParameter("inOriICAO", oriICAO);
        query.setParameter("inDesICAO", desICAO);
        return (List<Route>) query.getResultList();
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

    @Override
    public Leg checkLegExistence(Airport origin, Airport destination) {
        System.out.println("RoutePlanningSession: checkLegExistence()");

        Query query = em.createQuery("SELECT l FROM Leg l WHERE l.departAirport = :ori AND l.arrivalAirport= :dest");

        query.setParameter("ori", origin);
        query.setParameter("dest", destination);

        try {
            return (Leg) query.getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Route checkRouteExistence(List<Airport> allStops) {
        System.out.println("RoutePlanningSession: checkRouteExistence()");
        Leg l1 = checkLegExistence(allStops.get(0), allStops.get(1));
        Leg l2 = checkLegExistence(allStops.get(1), allStops.get(2));

//        List<Leg> legs = new ArrayList();
//        for(int i=0; i<allStops.size() - 1; i++){
//            legs.add(checkLegExistence(allStops.get(i),allStops.get(i+1)));
//        }
//        if(legs.contains(null)){
        if (l1 != null && l2 != null) {
            em.merge(l1);
            em.merge(l2);
            System.out.println("RoutePlanningSession: checkRouteExistence(): Legs exist");
            Query query = em.createQuery("SELECT r FROM Route AS r WHERE :leg1 MEMBER OF r.legs AND :leg2 MEMBER OF r.legs");
            query.setParameter("leg1", l1);
            query.setParameter("leg2", l2);
            try {
                Route r = (Route) query.getSingleResult();
                return r;
            } catch (Exception e) {
                return null;
            }
        } else {
            System.out.println("RoutePlanningSession: checkRouteExistence(): Route does not exist.");
            return null;
        }
    }

    @Override
    public boolean addRoute(List<Airport> stopList) {
        try {
//            Leg leg1 = checkLegExistence(stopList.get(0), stopList.get(1));
//            Leg leg2 = checkLegExistence(stopList.get(1), stopList.get(2));
//            Leg leg3 = new Leg();
//            Leg leg4 = new Leg();
//
//            Airport a1 = em.find(Airport.class, stopList.get(0).getId());
//            Airport a2 = em.find(Airport.class, stopList.get(1).getId());
//            Airport a3 = em.find(Airport.class, stopList.get(2).getId());
//
//            if (leg1 == null) {
//
//                leg1 = new Leg();
//
//                leg1.setArrivalAirport(a2);
//                leg1.setDepartAirport(a1);
//                leg3.setArrivalAirport(a1);
//                leg3.setDepartAirport(a2);
//
//                em.persist(leg1);
//                em.persist(leg3);
//
//                System.out.println("RoutePlanningSession: addRoute(): Set leg1 & 3.");
//            } else {
//                leg1 = em.find(Leg.class, leg1.getId());
//                leg3 = em.find(Leg.class, leg3.getId());
//
//                System.out.println("RoutePlanningSession: addRoute(): Find leg1 & 3.");
//            }
//
//            if (leg2 == null) {
//
//                leg2 = new Leg();
//
//                leg2.setArrivalAirport(a3);
//                leg2.setDepartAirport(a2);
//                leg4.setArrivalAirport(a2);
//                leg4.setDepartAirport(a3);
//
//                em.persist(leg2);
//                em.persist(leg4);
//
//                System.out.println("RoutePlanningSession: addRoute(): Set leg2 & 4.");
//            } else {
//                leg2 = em.find(Leg.class, leg2.getId());
//                leg4 = em.find(Leg.class, leg4.getId());
//
//                System.out.println("RoutePlanningSession: addRoute(): Find leg2 & 4.");
//            }
//
//            System.out.println("RoutePlanningSession: addRoute(): Start set return route.");
//            Route route1 = new Route();
//            Route route2 = new Route();
//            List<Leg> legs = new ArrayList<Leg>();
//            legs.add(leg1);
//            legs.add(leg2);
//
////            route1.setLegs(legs);
////            route1.setDeleted(Boolean.FALSE);
////            leg1.getRoutes().add(route1);
////            leg2.getRoutes().add(route1);
////            leg1.setRoutes(leg1.getRoutes());
////            leg2.setRoutes(leg2.getRoutes());
////            
////
////            legs.clear();
////            legs.add(leg4);
////            legs.add(leg3);
////
////            route2.setLegs(legs);
////            route2.setDeleted(Boolean.FALSE);
////            route2.setReturnRoute(route1);
////            route1.setReturnRoute(route2);
////            leg3.getRoutes().add(route2);
////            leg4.getRoutes().add(route2);
////            leg3.setRoutes(leg3.getRoutes());
////            leg4.setRoutes(leg4.getRoutes());
//
//            em.persist(leg1);
//            em.persist(leg2);
//            em.persist(leg3);
//            em.persist(leg4);

            System.out.println("RoutePlanningSession: addRoute(): Set return route.");

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public List<Route> getAllroutes() {
        Query query = em.createQuery("SELECT r FROM Route r WHERE r.status = 'added'");

        try {
            return (List<Route>) query.getResultList();
        } catch (Exception ex) {
            return null;
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
