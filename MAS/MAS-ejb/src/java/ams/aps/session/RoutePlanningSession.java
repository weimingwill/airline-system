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
import ams.aps.entity.RouteLeg;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
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
    public Route getRouteByID(Long id) {
        Query query = em.createQuery("SELECT r FROM Route r WHERE r.routeId = :inID");
        query.setParameter("inID", id);
        Route route = null;
        try {
            route = (Route) query.getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
        }
        return route;
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
        Query query = em.createQuery("SELECT r FROM Route r WHERE r.routeId = (SELECT rl.routeId FROM RouteLeg rl WHERE rl.leg.departAirport.icaoCode=:inOriICAO AND rl.leg.arrivalAirport.icaoCode = :inDesICAO)");
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
    public boolean softDeleteRoute(Long id) {
        try {

            System.out.println("RoutePlanningSession: softDeleteRoute: " + id);

            Route route = em.find(Route.class, id);
            Route returnRoute = em.find(Route.class, route.getReturnRoute().getRouteId());

            System.out.println("RoutePlanningSession: softDeleteRoute: Route and return route found.");
            route.setDeleted(true);
            returnRoute.setDeleted(true);
            em.merge(route);
            em.merge(returnRoute);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public Leg checkLegExistence(Airport origin, Airport destination) {
        System.out.println("RoutePlanningSession: checkLegExistence()");

        Query query = em.createQuery("SELECT l FROM Leg l, Airport A1, Airport A2 WHERE (A1=:ori AND A1 MEMBER OF (l.departAirport)) AND (A2=:dest AND A2 MEMBER OF (l.arrivalAirport))");

        query.setParameter("ori", origin);
        query.setParameter("dest", destination);

        try {
            Leg l = (Leg) query.getSingleResult();
            System.out.println("Leg = " + l);;
            return l;

        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Route checkRouteExistence(List<Airport> allStops) {
        System.out.println("RoutePlanningSession: checkRouteExistence()");
        Airport origin, destination;

        List<Leg> legs = new ArrayList();
        for (int i = 0; i < allStops.size() - 1; i++) {
            origin = em.find(Airport.class, allStops.get(i).getId());
            destination = em.find(Airport.class, allStops.get(i + 1).getId());
//            System.out.println("Orgin: " + origin.getAirportName() + " -> " +destination.getAirportName());
            legs.add(checkLegExistence(origin, destination));
        }
        // There are legs that does not exist => the route does not exist
        if (legs.contains(null)) {
            return null;
        } // All legs exits -> Check if the legs has the same route id
        else {
            String CHECK_EXIST_QUERY = "SELECT r FROM Route r WHERE ";
            Long legId;
            for (int i = 0; i < legs.size(); i++) {
                legId = legs.get(i).getLegId();
                CHECK_EXIST_QUERY += "r.routeId IN (SELECT rl.routeId FROM RouteLeg rl WHERE rl.legId = :inId" + legId + ")";
                if ((i + 1) < legs.size()) {
                    CHECK_EXIST_QUERY += " AND ";
                }
            }
            Query query = em.createQuery(CHECK_EXIST_QUERY);
            for (Leg leg : legs) {
                legId = leg.getLegId();
                query.setParameter("inId" + legId, legId);
            }
            System.out.println(CHECK_EXIST_QUERY);
            try {
                Route r = (Route) query.getSingleResult();
                System.out.println("Route = " + r);
                return new Route();
            } catch (NoResultException e) {
                return null;
            } catch (NonUniqueResultException e) {
                return new Route();
            }
        }
    }

    @Override
    public boolean addRoute(List<Airport> stopList) {
        System.out.println("RoutePlanningSession: addRoute():");
        List<Leg> legList = new ArrayList<>(), returnLegList = new ArrayList<>();
        Route newRoute = new Route(), newReturnRoute = new Route();
        Airport origin, destination;

        newRoute.setReturnRoute(newReturnRoute);
        newReturnRoute.setReturnRoute(newRoute);
        em.persist(newRoute);
        em.persist(newReturnRoute);

        for (int i = 0; i < stopList.size() - 1; i++) {
            origin = em.find(Airport.class, stopList.get(i).getId());
            destination = em.find(Airport.class, stopList.get(i + 1).getId());

            Leg newLeg = checkLegExistence(origin, destination);
            Leg newReturnLeg = checkLegExistence(destination, origin);

            if (newLeg == null) {
                newLeg = new Leg();
                newReturnLeg = new Leg();
                newLeg.setDepartAirport(origin);
                newLeg.setArrivalAirport(destination);
                newReturnLeg.setDepartAirport(destination);
                newReturnLeg.setArrivalAirport(origin);
                em.persist(newLeg);
                em.persist(newReturnLeg);
                em.flush();
            } else {
                em.merge(newLeg);
                em.merge(newReturnLeg);
            }
            legList.add(newLeg);
            returnLegList.add(0, newReturnLeg);
        }
        System.out.println("Add route:");
        addRouteLeg(legList, newRoute);
        System.out.println("Add return route");
        addRouteLeg(returnLegList, newReturnRoute);
        return true;
    }

    private void addRouteLeg(List<Leg> legs, Route route) {
        System.out.println("RoutePlanningSession: addRouteLeg():");
        Leg thisLeg;
        List<RouteLeg> routeLegList = new ArrayList<>();
        int i = 0;
        for (Leg leg : legs) {
            System.out.println("Sequence" + i + "From: " + leg.getDepartAirport().getAirportName() + " - To: " + leg.getArrivalAirport().getAirportName());
            RouteLeg thisRouteLeg = new RouteLeg();
            thisLeg = em.find(Leg.class, leg.getLegId());
            thisRouteLeg.setLeg(thisLeg);
            thisRouteLeg.setLegId(thisLeg.getLegId());
            thisRouteLeg.setRoute(route);
            thisRouteLeg.setRouteId(route.getRouteId());
            thisRouteLeg.setLegSeq(i);
            em.persist(thisRouteLeg);
            routeLegList.add(thisRouteLeg);
            i++;
        }
        route.setRouteLegs(routeLegList);
        em.merge(route);
        em.flush();
    }

    @Override
    public List<Route> getAllRoutes() {
        Query query = em.createQuery("SELECT r FROM Route r WHERE r.deleted = 0");

        try {
            return (List<Route>) query.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public List<Route> getAllObsoleteRoutes() {
        Query query = em.createQuery("SELECT r FROM Route r WHERE r.deleted = 1");

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
