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
import ams.aps.entity.FlightSchedule;
import ams.aps.entity.Route;
import ams.aps.entity.RouteLeg;
import ams.aps.util.exception.EmptyTableException;
import ams.aps.util.exception.FlightDoesNotExistException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
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
public class FlightSchedulingSession implements FlightSchedulingSessionLocal {

    @PersistenceContext
    EntityManager em;

    public final double BUFFER_TIME = 1 / 6;
    public final double STOPOVER_TIME = 1 / 3;

    @Override
    public boolean createFlight(Flight flight) {
        Route route = flight.getRoute();
        System.out.println("FlightSchedulingSession: createFlight(): route =" +route);
        try {
            List<Flight> fList = route.getFlights();
            if (fList == null) {
                fList = new ArrayList();
            }
            fList.add(flight);
            route.setFlights(fList);
            em.merge(route);
            flight.setCompleted(Boolean.FALSE);
            em.merge(flight);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public Flight checkFlightExistence(String flightNo) throws FlightDoesNotExistException{
        try {
            Query query = em.createQuery("SELECT f FROM Flight f WHERE f.flightNo =:fNo");
            query.setParameter("fNo", flightNo);

            Flight flight = (Flight) query.getSingleResult();
            return flight;
        } catch (Exception e) {
            throw new FlightDoesNotExistException();
        }
    }

    @Override
    public boolean changeFlightNo(String flightNo, String newFlightNo) {
        Query query = em.createQuery("SELECT f FROM Flight f WHERE f.flightNo =:fNo");
        query.setParameter("fNo", flightNo);
        try {
            Flight flight = (Flight) query.getSingleResult();
            flight.setFlightNo(newFlightNo);
            em.merge(flight);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<AircraftType> getCapableAircraftTypesForRoute(Route route) {
        List<Airport> allStops = getAllStopsOfRoute(route);
        double totalDistance = 0;
        for (int i = 0; i < allStops.size() - 1; i++) {
            totalDistance += distance(allStops.get(i), allStops.get(i + 1));
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
        query.setParameter("sta", "Idle");
        try {
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    /*
     * @returns Distance in Kilometers
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

        return Math.sqrt(distance) / 1000;
    }

    // frequency per week
    @Override
    public double getMaxFlightFrequency(AircraftType type, Route r) {

        double distance = 0;
        double totalFlyingTime = 0;

        List<Airport> stopList = getAllStopsOfRoute(r);
        for (int i = 0; i < stopList.size() - 1; i++) {
            distance += distance(stopList.get(i), stopList.get(i + 1));
        }
        totalFlyingTime += 2 * distance / (type.getMaxMachNo() * 1225.04);

        double turnover = getTurnoverTime(type) + BUFFER_TIME;
        totalFlyingTime += turnover;

        if (checkRouteIsInternational(stopList)) {
            totalFlyingTime += 0.25;
        }

        return 7 * 24 / (totalFlyingTime + turnover + STOPOVER_TIME * (stopList.size() - 2));
    }

    private double getTurnoverTime(AircraftType type) {
        double acLength = type.getOverallLengthInM();
        if (acLength <= 35) {
            return 0.5;
        } else if (acLength > 35 && acLength <= 50) {
            return 5 / 6;
        } else if (acLength > 50 && acLength <= 60) {
            return 7 / 6;
        } else {
            return 1.5;
        }
    }

    private List<Airport> getAllStopsOfRoute(Route thisRoute) {
        Dictionary legAirports = new Hashtable();
        int numOfLegs = thisRoute.getRouteLegs().size();
        List<Airport> allStops = new ArrayList<>();
        Airport ori = new Airport();
        Airport dest = new Airport();

        for (RouteLeg thisRouteLeg : thisRoute.getRouteLegs()) {
            int legSeq = thisRouteLeg.getLegSeq();

            System.out.println("Route Controller: viewRoutes(): FROM - TO (" + legSeq + "): " + thisRouteLeg.getLeg().getDepartAirport().getAirportName() + " - " + thisRouteLeg.getLeg().getArrivalAirport().getAirportName());
            if (numOfLegs == 1) {
                ori = thisRouteLeg.getLeg().getDepartAirport();
                dest = thisRouteLeg.getLeg().getArrivalAirport();
            } else {
                if (legSeq == 0) {
                    ori = thisRouteLeg.getLeg().getDepartAirport();
                } else if (legSeq == (numOfLegs - 1)) {
                    legAirports.put(legSeq, thisRouteLeg.getLeg().getDepartAirport());
                    dest = thisRouteLeg.getLeg().getArrivalAirport();
                } else {
                    System.out.println(thisRouteLeg.getLegSeq() - 1);
                    legAirports.put(legSeq, thisRouteLeg.getLeg().getDepartAirport());
                }
            }
        }

        if (legAirports.isEmpty()) {

            allStops.add(ori);
            allStops.add(dest);

        } else {
            allStops.add(ori);
            for (int i = 1; i <= legAirports.size(); i++) {
                allStops.add((Airport) legAirports.get(i));
            }
            allStops.add(dest);
        }

        return allStops;
    }

    private boolean checkRouteIsInternational(List<Airport> stops) {
        for (int i = 0; i < stops.size() - 1; i++) {
            if (!stops.get(i).getCountry().getCountryName().equals(stops.get(i + 1).getCountry().getCountryName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void assignFlightScheduleToAircraft(FlightSchedule flightSchedule) {
        em.persist(flightSchedule);
    }

    @Override
    public void modifyFlightSchedule(FlightSchedule newFlightSchedule) {
        FlightSchedule fs = em.find(FlightSchedule.class, newFlightSchedule.getFlightScheduleId());
        fs = newFlightSchedule;
        em.merge(fs);
    }

    @Override
    public List<Route> getAvailableRoutes() {
        Query query = em.createQuery("SELECT r FROM Route r WHERE r.deleted = FALSE");
        try {
            return query.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public List<Flight> getFlight(Boolean complete) throws EmptyTableException {
        try {
            Query query = em.createQuery("SELECT f FROM Flight f WHERE f.completed = :inComplete");
            query.setParameter("inComplete", complete);
            List<Flight> outputFlights;
            outputFlights = (List<Flight>) query.getResultList();
            return outputFlights;
        } catch (NoResultException ex) {
            throw new EmptyTableException("No Flight Record Found!");
        }
    }

}
