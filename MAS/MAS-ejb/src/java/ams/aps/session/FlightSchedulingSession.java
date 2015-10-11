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
import ams.aps.util.exception.DeleteFailedException;
import ams.aps.util.exception.EmptyTableException;
import ams.aps.util.exception.FlightDoesNotExistException;
import ams.aps.util.exception.NoSuchFlightException;
import ams.aps.util.exception.NoSuchRouteException;
import ams.aps.util.helper.ApsMsg;
import ams.aps.util.exception.ObjectDoesNotExistException;
import ams.aps.util.helper.LegHelper;
import ams.aps.util.helper.RouteHelper;
import java.util.ArrayList;
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
            flight.setDeleted(Boolean.FALSE);
            flight.setScheduled(Boolean.FALSE);
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
    public List<AircraftType> getCapableAircraftTypesForRoute(Double maxDist) {
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

    @Override
    public void calcFlightDuration(AircraftType selectedModel, RouteHelper routeHelper) {
        double speed = machToKmh(selectedModel.getMaxMachNo());
        double totalDuration = 0;
        double legFlyTime;
        double legTurnaroundTime = getTurnaroundTime(selectedModel);
        for (LegHelper leg : routeHelper.getLegs()) {
            legFlyTime = leg.getDistance() / speed;
            leg.setFlyingTime(legFlyTime);
            totalDuration += legFlyTime;
            if (checkRouteIsInternational(leg.getDeparture(), leg.getArrival())) {
                leg.setTurnaroundTime(legTurnaroundTime + 0.25);
                totalDuration += legTurnaroundTime + 0.25;
            } else {
                leg.setTurnaroundTime(legTurnaroundTime);
                totalDuration += legTurnaroundTime;
            }
        }
        routeHelper.setTotalDuration(totalDuration);
    }

    private double machToKmh(float machNo) {
        return machNo * 1225.044;
    }

    private double getTurnaroundTime(AircraftType type) {
        double acLength = type.getOverallLengthInM();
        double turnaroundTime = BUFFER_TIME + STOPOVER_TIME;
        double cleanUpTime = 0;
        // Based on aircraft overall length, get estimated time for alighting and clean-up
        if (acLength <= 35) {
            cleanUpTime += 0.5;
        } else if (acLength > 35 && acLength <= 50) {
            cleanUpTime += 5 / 6;
        } else if (acLength > 50 && acLength <= 60) {
            cleanUpTime += 7 / 6;
        } else {
            cleanUpTime += 1.5;
        }
        turnaroundTime += cleanUpTime;
        return turnaroundTime;
    }

    private boolean checkRouteIsInternational(Airport departure, Airport arrival) {
        return !departure.getCountry().getCountryName().equals(arrival.getCountry().getCountryName());
    }

    @Override
    public void assignFlightScheduleToAircraft(FlightSchedule flightSchedule) {
        em.persist(flightSchedule);
    }

    @Override
    public void modifyFlightSchedule(FlightSchedule newFlightSchedule) throws ObjectDoesNotExistException {
        try {
            em.find(FlightSchedule.class, newFlightSchedule.getFlightScheduleId());
            em.merge(newFlightSchedule);
        } catch (Exception e) {
            throw new ObjectDoesNotExistException("Flight Schedule " + newFlightSchedule.getFlightScheduleId() + " Does Not Exist");
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

    @Override
    public void deleteFlight(String flightNo) throws DeleteFailedException {
        try {
            Flight flight = checkFlightExistence(flightNo);
            flight.setDeleted(Boolean.TRUE);
            em.merge(flight);
        } catch (FlightDoesNotExistException ex) {
            throw new DeleteFailedException();
        }
    }

    @Override
    public List<Flight> getAllUnscheduledFlights() throws NoSuchFlightException {
        Query query = em.createQuery("SELECT f FROM Flight f WHERE f.scheduled = FALSE AND f.deleted = FALSE AND f.route.deleted = FALSE");
        List<Flight> flights = new ArrayList<>();
        try {
            flights = (List<Flight>) query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchFlightException(ApsMsg.NO_SUCH_FLIGHT_ERROR);
        }
        return flights;
    }

    @Override
    public List<String> getUnscheduledFlightAircraftTypeFamilys() {
        List<String> aircraftTypeFamilys = new ArrayList<>();
        try {
            for (Flight flight : getAllUnscheduledFlights()) {
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
            for (Flight flight : getAllUnscheduledFlights()) {
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

    @Override
    public List<Route> getUnscheduledFlightRoutes() throws NoSuchRouteException {
        Query query = em.createQuery("SELECT r FROM Flight f, Route r WHERE f.route.routeId = r.routeId AND f.scheduled = FALSE AND f.deleted = FALSE AND r.deleted = FALSE");
        List<Route> routes = new ArrayList<>();
        try {
            routes = (List<Route>) query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchRouteException(ApsMsg.NO_SUCH_ROUTE_ERROR);
        }
        return routes;
    }

    @Override
    public List<Flight> getUnscheduledFlights(Airport deptAirport, List<AircraftType> aircraftTypes) 
            throws NoSuchFlightException, NoSuchRouteException {
        List<Flight> flights = new ArrayList<>();
        for (Route route : getRoutesByDeptAirport(deptAirport)) {
            for (AircraftType aircraftType  : aircraftTypes) {
                flights.addAll(getUnscheduledFlightsByRouteAndAircraftType(route, aircraftType));
            }
        }
        return flights;
    }

    public List<Flight> getUnscheduledFlightsByRouteAndAircraftType(Route route, AircraftType aircraftType) throws NoSuchFlightException{
        Query query = em.createQuery("SELECT f FROM Flight f, AircraftType a WHERE f.route.routeId = :inRouteId AND :inAircraftType MEMBER OF f.aircraftTypes AND f.route.deleted = FALSE AND f.scheduled = FALSE AND f.deleted = FALSE");
        List<Flight> flights = new ArrayList<>();
        try {
            flights = (List<Flight>) query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchFlightException(ApsMsg.NO_SUCH_FLIGHT_ERROR);
        }
        return flights;    
    }

    public List<Route> getRoutesByDeptAirport(Airport deptAirport) throws NoSuchRouteException{
        Query query = em.createQuery("SELECT r FROM Route r, RouteLeg rl, Leg l WHERE r.routeId = rl.routeId AND rl.legSeq = 0 AND rl.leg.legId = l.legId AND l.departAirport.id = :inId AND r.deleted = FALSE");
        query.setParameter("inId", deptAirport.getId());
        List<Route> routes= new ArrayList<>();
        try {
            routes = (List<Route>) query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchRouteException(ApsMsg.NO_SUCH_ROUTE_ERROR);
        }
        return routes;
    }
}
