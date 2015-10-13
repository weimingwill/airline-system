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
import ams.aps.entity.Leg;
import ams.aps.entity.Route;
import ams.aps.util.exception.DeleteFailedException;
import ams.aps.util.exception.EmptyTableException;
import ams.aps.util.exception.NoSuchFlightException;
import ams.aps.util.exception.NoSuchRouteException;
import ams.aps.util.helper.ApsMsg;
import ams.aps.util.exception.ObjectDoesNotExistException;
import ams.aps.util.helper.LegHelper;
import ams.aps.util.helper.RouteHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    @PersistenceContext
    EntityManager em;

    @EJB
    private FleetPlanningSessionLocal fleetPlanningSession;
    @EJB
    private RoutePlanningSessionLocal routePlanningSession;
    public final double BUFFER_TIME = 1 / 6.0; // buffer time for passengers to alight
    public final double STOPOVER_TIME = 1 / 3.0; //time for plane to rest at the stop-over airport
    public final double DEFAULT_SPEED_FRACTION = 0.8;

    private boolean createFlight(Flight flight) {
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
            flight.setCompleted(Boolean.FALSE);
            flight.setSpeedFraction(DEFAULT_SPEED_FRACTION);
            em.merge(flight);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public boolean createReturnedFlight(Flight flight, Flight returnedFlight) {
        try {
            createFlight(flight);
            createFlight(returnedFlight);
            flight.setReturnedFlight(returnedFlight);
            returnedFlight.setReturnedFlight(flight);
            em.merge(flight);
            em.merge(returnedFlight);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Flight checkFlightExistence(String flightNo) throws ObjectDoesNotExistException {
        try {
            Query query = em.createQuery("SELECT f FROM Flight f WHERE f.flightNo =:fNo AND f.deleted =FALSE");
            query.setParameter("fNo", flightNo);

            Flight flight = (Flight) query.getSingleResult();
            System.out.println("checkFlightExistence(): " + flight);
            return flight;
        } catch (Exception e) {
            throw new ObjectDoesNotExistException("Flight " + flightNo + " Does Not Exist");
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
    public AircraftType getModelWithMinMachNo(List<AircraftType> models) {
        float minMach = models.get(0).getMaxMachNo();
        int minIndex = 0;
        for (int i = 1; i < models.size(); i++) {
            if (models.get(i).getMaxMachNo() < minMach) {
                minIndex = i;
                minMach = models.get(i).getMaxMachNo();
            }
        }
        return models.get(minIndex);
    }

    @Override
    public void calcFlightDuration(AircraftType selectedModel, RouteHelper routeHelper, double speedFraction) {
        double speed = machToKmh(selectedModel.getMaxMachNo());
        double totalDuration = 0;
        double legFlyTime;
        double legTurnaroundTime = getTurnaroundTime(selectedModel);
        for (LegHelper leg : routeHelper.getLegs()) {
            legFlyTime = leg.getDistance() / (speed * speedFraction);
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
            cleanUpTime += 5 / 6.0;
        } else if (acLength > 50 && acLength <= 60) {
            cleanUpTime += 7 / 6.0;
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
            Query query = em.createQuery("SELECT f FROM Flight f WHERE f.completed = :inComplete AND f.deleted = FALSE");
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
            Flight returnedFlight = flight.getReturnedFlight();
            if(flight.getNumOfUnscheduled() < flight.getWeeklyFrequency() || returnedFlight.getNumOfUnscheduled() < returnedFlight.getWeeklyFrequency()){
                throw new DeleteFailedException("Flight is scheduled, cannot be deleted!");
            }
            returnedFlight.setDeleted(Boolean.TRUE);
            flight.setDeleted(Boolean.TRUE);
            em.merge(flight);
            em.merge(returnedFlight);
        } catch (ObjectDoesNotExistException ex) {
            throw new DeleteFailedException("Flight does not exist!");
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
                    if (!aircraftTypeFamilys.contains(aircraftType.getTypeFamily())) {
                        aircraftTypeFamilys.add(aircraftType.getTypeFamily());
                    }
                }
            }
        } catch (NoSuchFlightException e) {
        }
        return aircraftTypeFamilys;
    }

    @Override
    public List<String> getUnscheduledAircraftTypeCodesByTypeFamily(String typeFamily) {
        List<String> aircraftTypes = new ArrayList<>();
        try {
            for (Flight flight : getAllUnscheduledFlights()) {
                for (AircraftType aircraftType : flight.getAircraftTypes()) {
                    if (aircraftType.getTypeFamily().equals(typeFamily)) {
                        if (!aircraftTypes.contains(aircraftType.getTypeCode())) {
                            aircraftTypes.add(aircraftType.getTypeCode());
                        }
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
    public List<Flight> getUnscheduledFlights(Airport deptAirport, List<String> aircraftTypeCodes)
            throws NoSuchFlightException, NoSuchRouteException {
        List<Flight> flights = new ArrayList<>();
        Set<Flight> hs = new HashSet<>();
        for (Route route : getRoutesByDeptAirport(deptAirport)) {
            for (String aircraftTypeCode : aircraftTypeCodes) {
                AircraftType aircraftType = fleetPlanningSession.getAircraftTypeByCode(aircraftTypeCode);
                hs.addAll(getUnscheduledFlightsByRouteAndAircraftType(route, aircraftType));
            }
        }
        flights.addAll(hs);
        return flights;
    }

    public List<Flight> getUnscheduledFlightsByRouteAndAircraftType(Route route, AircraftType aircraftType) throws NoSuchFlightException {
        Query query = em.createQuery("SELECT f FROM Flight f, AircraftType a WHERE f.route.routeId = :inRouteId AND :inAircraftType MEMBER OF f.aircraftTypes AND f.route.deleted = FALSE AND f.scheduled = FALSE AND f.deleted = FALSE");
        query.setParameter("inRouteId", route.getRouteId());
        query.setParameter("inAircraftType", aircraftType);
        List<Flight> flights = new ArrayList<>();
        try {
            flights = (List<Flight>) query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchFlightException(ApsMsg.NO_SUCH_FLIGHT_ERROR);
        }
        return flights;
    }

    public List<Route> getRoutesByDeptAirport(Airport deptAirport) throws NoSuchRouteException {
        Query query = em.createQuery("SELECT r FROM Route r, RouteLeg rl, Leg l WHERE r.routeId = rl.routeId AND rl.legSeq = 0 AND rl.leg.legId = l.legId AND l.departAirport.id = :inId AND r.deleted = FALSE");
        query.setParameter("inId", deptAirport.getId());
        List<Route> routes = new ArrayList<>();
        try {
            routes = (List<Route>) query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchRouteException(ApsMsg.NO_SUCH_ROUTE_ERROR);
        }
        return routes;
    }

    public void updateFlight(Flight flight) throws ObjectDoesNotExistException {
        try {
            em.merge(flight);
            em.merge(flight.getReturnedFlight());
        } catch (Exception e) {
            System.out.println("Error!!!!");
            throw new ObjectDoesNotExistException();
        }
    }

    @Override
    public void createFlightSchedule(String flightNo, Aircraft aircraft, Date deptDate, Date arrivalDate) throws NoSuchFlightException {
        Flight flight = getFlightByFlightNo(flightNo);
        RouteHelper routeHelper = new RouteHelper();
        routePlanningSession.getRouteDetail(flight.getRoute(), routeHelper);
        List<LegHelper> legHelpers = routeHelper.getLegs();
        Calendar dept = dateToCalendar(deptDate);
        Calendar arrival = dateToCalendar(arrivalDate);
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        for (LegHelper legHelper : legHelpers) {
            start = (Calendar)dept.clone();
            end = (Calendar)dept.clone();
            end.set(Calendar.MINUTE, start.get(Calendar.MINUTE) + (int) (60 * (double)legHelper.getFlyingTime()));
            Leg leg = em.find(Leg.class, legHelper.getLegId());
        }
        
    }
    
    public Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
    
    public Flight getFlightByFlightNo(String fligthNo) throws NoSuchFlightException {
        Query query = em.createQuery("SELECT f FROM Flight f WHERE f.flightNo = :inFlightNo AND f.deleted = FALSE AND f.scheduled = FALSE");
        query.setParameter("inFlightNo", fligthNo);
        Flight flight = new Flight();
        try {
            flight = (Flight) query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoSuchFlightException(ApsMsg.NO_SUCH_FLIGHT_ERROR);
        }
        return flight;
    }
    public List<Flight> getAllFlights() {
        Query query = em.createQuery("SELECT f FROM Flight f WHERE f.deleted = FALSE");
        List<Flight> outputFlights = new ArrayList();
        try {
            outputFlights = (List<Flight>) query.getResultList();

        } catch (NoResultException ex) {

        }
        return outputFlights;
    }

}
