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
import ams.aps.util.exception.DeleteFailedException;
import ams.aps.util.exception.EmptyTableException;
import ams.aps.util.exception.FlightDoesNotExistException;
import ams.aps.util.exception.NoSuchFlightException;
import ams.aps.util.helper.ApsMsg;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import javax.ejb.EJB;
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

    @EJB
    private RoutePlanningSessionLocal routePlanningSession;

    @PersistenceContext
    EntityManager em;

    public final double BUFFER_TIME = 1 / 6;
    public final double STOPOVER_TIME = 1 / 3;

    @Override
    public boolean createFlight(Flight flight) {
        Route route = flight.getRoute();
        System.out.println("FlightSchedulingSession: createFlight(): route =" + route);
        try {
            List<Flight> fList = route.getFlights();
            if (fList == null) {
                fList = new ArrayList();
            }
            fList.add(flight);
            route.setFlights(fList);
            em.merge(route);
            // TODO add Flight isScheduled = FALSE;
            flight.setCompleted(Boolean.FALSE);
            em.merge(flight);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public Flight checkFlightExistence(String flightNo) throws FlightDoesNotExistException {
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
    public List<AircraftType> getCapableAircraftTypesForRoute(Float maxDist) {
        Query query = em.createQuery("SELECT DISTINCT(atype) FROM AircraftType atype, IN (atype.aircrafts) craft WHERE atype.rangeInKm >= :totDis ORDER BY atype.rangeInKm ASC");
        query.setParameter("totDis", maxDist);
        query.setMaxResults(3);
        try {
            List<AircraftType> capableAircraftTypes = query.getResultList();
            return capableAircraftTypes;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Aircraft> getAvailableAircraftsByType(AircraftType type) {
        Query query = em.createQuery("SELECT a FROM Aircraft a WHERE a.aircraftType.id = :type AND a.status =:sta");
        query.setParameter("sta", "Idle");
        try {
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    // frequency per week
    @Override
    public double getMaxFlightFrequency(AircraftType type, Route r) {

        double distance = 0;
        double totalFlyingTime = 0;

        List<Airport> stopList = getAllStopsOfRoute(r);
        for (int i = 0; i < stopList.size() - 1; i++) {
            distance += routePlanningSession.distance(stopList.get(i), stopList.get(i + 1)) / 1000;
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

    @Override
    public void deleteFlight(String flightNo) throws DeleteFailedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Flight> getUnscheduledFlights() throws NoSuchFlightException {
        Query query = em.createQuery("SELECT f.route FROM Flight f WHERE f.scheduled = FALSE AND f.deleted = FALSE");
        List<Flight> flights = new ArrayList<>();
        try {
            flights = (List<Flight>)query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchFlightException(ApsMsg.NO_SUCH_FLIGHT_ERROR);
        }
        return flights;
    }

    @Override
    public List<String> getUnscheduledFlightAircraftTypeFamilys() {
        List<String> aircraftTypeFamilys = new ArrayList<>();
        try {
            for (Flight flight : getUnscheduledFlights()) {
                for (AircraftType aircraftType : flight.getAircraftTypes()) {
                    aircraftTypeFamilys.add(aircraftType.getTypeFamily());
                }
            }
        } catch (NoSuchFlightException e) {
        }
        return aircraftTypeFamilys;
    }

    @Override
    public List<AircraftType> getUnscheduledAircraftTypesByTypeFamily(String typeFamily) {
       List<AircraftType> aircraftTypes = new ArrayList<>();
        try {
            for (Flight flight : getUnscheduledFlights()) {
                for (AircraftType aircraftType : flight.getAircraftTypes()) {
                    if (aircraftType.getTypeFamily().equals(typeFamily)) {
                        aircraftTypes.add(aircraftType);
                    }
                }
            }
        } catch (NoSuchFlightException e) {
        }
        return aircraftTypes;        
    }
}
