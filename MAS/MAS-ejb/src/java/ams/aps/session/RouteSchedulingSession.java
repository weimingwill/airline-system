/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.session;

import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftType;
import ams.aps.entity.Airport;
import ams.aps.entity.Flight;
import ams.aps.entity.Route;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author ChuningLiu
 */
@Stateless
public class RouteSchedulingSession implements RouteSchedulingSessionLocal {

    @PersistenceContext
    EntityManager em;

    @Override
    public boolean createFlight(String flightNo, Long routeID) {
        try {
            Route route = em.find(Route.class, routeID);

            Flight flight = new Flight();
            flight.setFlightNo(flightNo);
            flight.setRoute(route);

            List<Flight> fList = route.getFlights();
            fList.add(flight);
            route.setFlights(fList);

            em.persist(flight);
            em.merge(route);

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public boolean checkFlightNotExisted(String flightNo, Long routeID) {
        try {
            Route route = em.find(Route.class, routeID);
            List<Flight> flights = route.getFlights();

            Query query = em.createQuery("SELECT f FROM Flight f WHERE f.flightNo =:fNo");
            query.setParameter("fNo", flightNo);

            if (query.getResultList().isEmpty()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean changeFlightNo(String flightNo, String newFlightNo) {
        Query query = em.createQuery("SELECT f FROM Flight f WHERE f.flightNo =:fNo");
        query.setParameter("fNo", flightNo);
        try {
            Flight flight = (Flight)query.getSingleResult();
            flight.setFlightNo(newFlightNo);
            em.merge(flight);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public List<AircraftType> getCapableAircraftTypesForRoute(List<Airport> allStops) {
        double totalDistance = 0;
        for (int i = 0; i < allStops.size()-1; i++) {
            totalDistance += distance(allStops.get(i), allStops.get(i+1));
        }
        
        Query query = em.createQuery("SELECT atype FROM AircraftType atype WHERE atype.rangeInKm > :totDis");
        try {
            List<AircraftType> capableAircraftTypes = query.getResultList();
            return capableAircraftTypes;
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public List<Aircraft> getAvailableAircraftsByType(AircraftType type) {
        Query query = em.createQuery("SELECT a FROM Aircraft a WHERE a.aircraftType.id = :type AND a.status =: sta");
        query.setParameter("sta","Idle");
        try {
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }
    
    /*
     * @returns Distance in Meters
     */
    private double distance(Airport a1, Airport a2) {

        float lat1 = a1.getLatitude();
        float lon1 = a1.getLongitude();
        double el1 = 0.3048 * a1.getAltitude();

        float lat2 = a2.getLatitude();
        float lon2 = a2.getLongitude();
        double el2 = 0.3048 * a2.getAltitude();

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

        return Math.sqrt(distance)/1000;
    }

}
