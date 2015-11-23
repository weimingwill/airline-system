/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.session;

import ams.ais.entity.BookingClass;
import ams.ais.entity.CabinClassTicketFamily;
import ams.ais.entity.FlightScheduleBookingClass;
import ams.ais.entity.TicketFamily;
import ams.ais.entity.helper.FlightScheduleBookingClassId;
import ams.ais.session.ProductDesignSessionLocal;
import ams.ais.session.RevMgmtSessionLocal;
import ams.ais.util.exception.ExistSuchBookingClassNameException;
import ams.ais.util.exception.NoSuchCabinClassTicketFamilyException;
import ams.ais.util.helper.BookingClassHelper;
import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftCabinClass;
import ams.aps.entity.AircraftType;
import ams.aps.entity.Airport;
import ams.aps.entity.Flight;
import ams.aps.entity.FlightSchedule;
import ams.aps.entity.FlightScheduleSeat;
import ams.aps.entity.Leg;
import ams.aps.entity.Route;
import ams.aps.util.exception.DeleteFailedException;
import ams.aps.util.exception.EmptyTableException;
import ams.aps.util.exception.ExistSuchFlightScheduleException;
import ams.aps.util.exception.NoMoreUnscheduledFlightException;
import ams.aps.util.exception.NoSelectAircraftException;
import ams.aps.util.exception.NoSuchAircraftException;
import ams.aps.util.exception.NoSuchFlightException;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import ams.aps.util.exception.NoSuchRouteException;
import ams.aps.util.helper.ApsMsg;
import ams.aps.util.exception.ObjectDoesNotExistException;
import ams.aps.util.helper.AircraftStatus;
import ams.aps.util.helper.FlightSchedStatus;
import ams.aps.util.helper.FlightSchedMethod;
import ams.aps.util.helper.LegHelper;
import ams.aps.util.helper.RouteHelper;
import ams.crm.util.helper.ChannelHelper;
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
import mas.util.helper.DateHelper;

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
    @EJB
    private RevMgmtSessionLocal revMgmtSession;
    @EJB
    private ProductDesignSessionLocal productDesignSession;

    private final double BUFFER_TIME = 1 / 6.0; // buffer time for passengers to alight
    private final double STOPOVER_TIME = 1 / 3.0; //time for plane to rest at the stop-over airport
    private final double DEFAULT_SPEED_FRACTION = 0.8;
    private final long ONE_MINUTE_IN_MILLIS = 60 * 1000;//one minute in millisecs

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
            if ((!flight.getCompleted() && !returnedFlight.getCompleted())
                    || (flight.getNumOfUnscheduled().equals(flight.getWeeklyFrequency())
                    || returnedFlight.getNumOfUnscheduled().equals(returnedFlight.getWeeklyFrequency()))) {
                returnedFlight.setDeleted(Boolean.TRUE);
                flight.setDeleted(Boolean.TRUE);
                em.merge(flight);
                em.merge(returnedFlight);
            } else {
                throw new DeleteFailedException("Flight is scheduled, cannot be deleted!");
            }
        } catch (ObjectDoesNotExistException ex) {
            throw new DeleteFailedException("Flight does not exist!");
        }
    }

    @Override
    public List<Flight> getAllUnscheduledFlights() throws NoSuchFlightException {
        Query query = em.createQuery("SELECT f FROM Flight f WHERE f.numOfUnscheduled > 0 AND f.deleted = FALSE AND f.route.deleted = FALSE");
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
        Query query = em.createQuery("SELECT r FROM Flight f, Route r WHERE f.route.routeId = r.routeId AND f.numOfUnscheduled > 0 AND f.deleted = FALSE AND r.deleted = FALSE");
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
                for (Flight flight : getUnscheduledFlightsByRouteAndAircraftType(route, aircraftType)) {
                    hs.add(flight);
                    hs.add(flight.getReturnedFlight());
                }
//                hs.addAll(getUnscheduledFlightsByRouteAndAircraftType(route, aircraftType));
            }
        }
        flights.addAll(hs);
        return flights;
    }

    private List<Flight> getUnscheduledFlightsByRouteAndAircraftType(Route route, AircraftType aircraftType) throws NoSuchFlightException {
        Query query = em.createQuery("SELECT f FROM Flight f, AircraftType a WHERE f.route.routeId = :inRouteId AND :inAircraftType MEMBER OF f.aircraftTypes AND f.route.deleted = FALSE AND f.numOfUnscheduled > 0 AND f.deleted = FALSE");
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

    private List<Route> getRoutesByDeptAirport(Airport deptAirport) throws NoSuchRouteException {
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

    @Override
    public void updateFlight(Flight flight) throws ObjectDoesNotExistException {
        try {
            System.out.println("UpdateFlight(): flight = " + flight);
            em.merge(flight);
            em.merge(flight.getReturnedFlight());
        } catch (Exception e) {
            System.out.println("Error!!!!");
            throw new ObjectDoesNotExistException();
        }
    }

    @Override
    public void verifyUnscheduledFlightNumber(Flight flight) throws NoMoreUnscheduledFlightException {
        if (flight.getNumOfUnscheduled() <= 0) {
            throw new NoMoreUnscheduledFlightException(ApsMsg.NO_MORE_UNSCHEDULED_FLIGHT_ERROR);
        }
    }

    private void verifyScheduleCollision(Aircraft aircraft, Date deptDate, Date arrDate, Date startDate, Date endDate, FlightSchedule currentFlightSched) throws ExistSuchFlightScheduleException {
        List<FlightSchedule> flightSchedules = getFlightSchedulesByTailNoAndTime(aircraft.getTailNo(), startDate, endDate, FlightSchedMethod.DISPLAY);
        //remove all Flightschedules starting from current flight schedule
        flightSchedules.remove(currentFlightSched);

        while ((currentFlightSched = currentFlightSched.getNextFlightSched()) != null) {
            flightSchedules.remove(currentFlightSched);
        }
        for (FlightSchedule flightSchedule : flightSchedules) {
            Date arrDateWithTurnOver = addHourToDate(flightSchedule.getArrivalDate(), flightSchedule.getTurnoverTime());
            if (arrDate.after(flightSchedule.getDepartDate()) && arrDateWithTurnOver.after(deptDate)) {
                throw new ExistSuchFlightScheduleException(ApsMsg.EXIST_SUCH_FLIGHTSCHEDULE_ERROR);
            }
        }
    }

    @Override
    public FlightSchedule createFlightSchedule(Flight flight, Aircraft aircraft, Date deptDate, Date arrDate, Date startDate, Date endDate, String method)
            throws NoSuchFlightException, NoMoreUnscheduledFlightException, NoSelectAircraftException, ExistSuchFlightScheduleException {
        System.out.println("create flight in process");

        verifyScheduleCollision(aircraft, deptDate, arrDate, startDate, endDate, new FlightSchedule());
        List<FlightSchedule> flightSchedules = new ArrayList<>();
        deptDate = setFlightSchedule(flight, aircraft, deptDate, flightSchedules, method);

        verifyUnscheduledFlightNumber(flight.getReturnedFlight());
        List<FlightSchedule> reFlightSchedules = new ArrayList<>();
        setFlightSchedule(flight.getReturnedFlight(), aircraft, deptDate, reFlightSchedules, method);

        FlightSchedule firstFlightSched = em.find(FlightSchedule.class, flightSchedules.get(0).getFlightScheduleId());
        FlightSchedule lastFlightSched = em.find(FlightSchedule.class, flightSchedules.get(1).getFlightScheduleId());
        FlightSchedule reFirstFlightSched = em.find(FlightSchedule.class, reFlightSchedules.get(0).getFlightScheduleId());
//        FlightSchedule reLastFlightSched = em.find(FlightSchedule.class, reFlightSchedules.get(1).getFlightScheduleId());

        lastFlightSched.setNextFlightSched(reFirstFlightSched);
        reFirstFlightSched.setPreFlightSched(lastFlightSched);
        em.merge(lastFlightSched);
        return firstFlightSched;
    }

    private Date setFlightSchedule(Flight flight, Aircraft selectedAircraft, Date deptDate, List<FlightSchedule> flightSchedules, String method)
            throws NoMoreUnscheduledFlightException, NoSelectAircraftException {
        RouteHelper routeHelper = new RouteHelper();
        routePlanningSession.getRouteDetail(flight.getRoute(), routeHelper);
        calcFlightDuration(getModelWithMinMachNo(flight.getAircraftTypes()), routeHelper, flight.getSpeedFraction());
        List<LegHelper> legHelpers = routeHelper.getLegs();
        Date dept = deptDate;
        Date arrival;
        Aircraft aircraft = em.find(Aircraft.class, selectedAircraft.getAircraftId());
        Flight f = em.find(Flight.class, flight.getId());
        if (aircraft == null) {
            throw new NoSelectAircraftException(ApsMsg.NO_SELECT_AIRCRAFT_ERROR);
        }
        FlightSchedule flightSched = new FlightSchedule();
        int i = 0;
        for (LegHelper legHelper : legHelpers) {
            arrival = addHourToDate(dept, legHelper.getFlyingTime());
            flightSched.setCreatedTime(new Date());
            flightSched.setDepartDate(dept);
            flightSched.setArrivalDate(arrival);
            flightSched.setFlight(f);
            flightSched.setAircraft(aircraft);
            flightSched.setDepartTerminal("");
            flightSched.setDepartGate("");
            flightSched.setArrivalTerminal("");
            flightSched.setArrivalGate("");
            flightSched.setPriced(false);
            flightSched.setSeatAllocated(false);
            flightSched.setDeleted(false);
            flightSched.setCompleted(false);
            flightSched.setTurnoverTime(legHelper.getTurnaroundTime());
            flightSched.setStatus(FlightSchedStatus.RELEASE);
            em.persist(flightSched);
            System.out.println("Set Flight Schedule Leg Id: " + legHelper.getLegId());
            flightSched.setLeg(em.find(Leg.class, legHelper.getLegId()));
            flightSched.setFlightSchedSeats(getFlightSchedSeats(aircraft));
            em.merge(flightSched);
            em.flush();

            if (i == 0) {
                flightSchedules.add(flightSched);
            }
            FlightSchedule nextFlightSched = new FlightSchedule();

            if (i == legHelpers.size() - 1) {
                flightSched.setNextFlightSched(null);
                em.merge(flightSched);
                em.flush();
                flightSchedules.add(flightSched);
            } else {
                flightSched.setNextFlightSched(nextFlightSched);
                nextFlightSched.setPreFlightSched(flightSched);
                nextFlightSched.setNextFlightSched(null);
                em.persist(nextFlightSched);
                em.merge(flightSched);
                em.flush();
            }
            createFlightSchedBookingClass(flightSched);
            flightSched = nextFlightSched;

            i++;
            dept = addHourToDate(arrival, legHelper.getTurnaroundTime());
        }
        if (method.equals(FlightSchedMethod.CREATE)) {
            completeOneFlightSchedule(flight);
        }
        return dept;
    }

    private List<FlightScheduleSeat> getFlightSchedSeats(Aircraft aircraft) {
        List<FlightScheduleSeat> seatList = new ArrayList<>();
        for (AircraftCabinClass aircraftCabinClass : aircraft.getAircraftCabinClasses()) {
            FlightScheduleSeat flightSchedSeat = new FlightScheduleSeat();
            flightSchedSeat.setRank(aircraftCabinClass.getCabinClass().getRank());
            flightSchedSeat.setSeats(aircraftCabinClass.getSeats());
            seatList.add(flightSchedSeat);
            em.persist(flightSchedSeat);
            em.flush();
        }
        return seatList;
    }

    //Set default one booking class to each ticket family
    private void createFlightSchedBookingClass(FlightSchedule flightSchedule) {
        int i = 0;
        boolean noError = true;
        double basicPrice = 100000;
        List<FlightScheduleBookingClass> flightScheduleBookingClasses = new ArrayList<>();
        for (TicketFamily ticketFamily : revMgmtSession.getFlightScheduleTixFams(flightSchedule.getFlightScheduleId())) {
            //get min price as the basic price
            System.out.println("ticket family"+ticketFamily.getName()+ticketFamily.getCabinClass().getName());
            double price = revMgmtSession.calTicketFamilyPrice(flightSchedule.getFlightScheduleId(), ticketFamily.getTicketFamilyId());
            if (basicPrice > price) {
                basicPrice = price;
            }
            while (noError && i < 26) {
                BookingClass bookingClass;
                try {
                    bookingClass = revMgmtSession.createBookingClass(BookingClassHelper.getBookingClsNames().get(i), ticketFamily, ChannelHelper.ARS);
                } catch (ExistSuchBookingClassNameException e) {
                    bookingClass = revMgmtSession.getBookingClassByName(BookingClassHelper.getBookingClsNames().get(i));
                }
                //Create flightSchedule booking class
                FlightScheduleBookingClassId flightScheduleBookingClassId = new FlightScheduleBookingClassId();
                flightScheduleBookingClassId.setFlightScheduleId(flightSchedule.getFlightScheduleId());
                flightScheduleBookingClassId.setBookingClassId(bookingClass.getBookingClassId());
                FlightScheduleBookingClass flightSchedBookingCls = revMgmtSession.createFlightSchedBookingCls(flightSchedule, bookingClass, flightScheduleBookingClassId);

                CabinClassTicketFamily cabinClsTixFam = new CabinClassTicketFamily();
                try {
                    cabinClsTixFam = productDesignSession.getCabinClassTicketFamilyJoinTable(flightSchedule.getAircraft().getAircraftId(), ticketFamily.getCabinClass().getCabinClassId(), ticketFamily.getTicketFamilyId());
                } catch (NoSuchCabinClassTicketFamilyException ex) {
                    System.out.println(ex.getMessage());
                }
                flightSchedBookingCls.setSeatQty(cabinClsTixFam.getSeatQty());
                flightSchedBookingCls.setPrice((float) (price + flightSchedBookingCls.getBookingClass().getTicketFamily().getCabinClass().getBasePrice()));
                em.merge(flightSchedBookingCls);
                em.flush();
                flightScheduleBookingClasses.add(flightSchedBookingCls);
                noError = false;
            }
            i++;
            noError = true;
        }
        setBasicPriceAndPriceCoefficient(flightScheduleBookingClasses, basicPrice);
    }

    private void setBasicPriceAndPriceCoefficient(List<FlightScheduleBookingClass> flightScheduleBookingClasses, double basicPrice) {
        for (FlightScheduleBookingClass flightSchedBookingCls : flightScheduleBookingClasses) {
            flightSchedBookingCls = em.find(FlightScheduleBookingClass.class, flightSchedBookingCls.getFlightScheduleBookingClassId());
            flightSchedBookingCls.setBasicPrice((float) basicPrice);
            flightSchedBookingCls.setPriceCoefficient((float) (flightSchedBookingCls.getPrice() / basicPrice));
            em.merge(flightSchedBookingCls);
            em.flush();
        }
    }

    private void completeOneFlightSchedule(Flight flight) throws NoMoreUnscheduledFlightException {
        if (flight.getNumOfUnscheduled() <= 0) {
            throw new NoMoreUnscheduledFlightException(ApsMsg.NO_MORE_UNSCHEDULED_FLIGHT_ERROR);
        } else {
            flight = em.find(Flight.class, flight.getId());
            flight.setNumOfUnscheduled(flight.getNumOfUnscheduled() - 1);
            em.merge(flight);
        }
    }

    @Override
    public FlightSchedule updateFlightSchedule(String flightNo, Aircraft aircraft, Date deptDate, Date arrDate, Date startDate, Date endDate, FlightSchedule oldFlightSched)
            throws NoSelectAircraftException, NoSuchFlightException, NoSuchFlightSchedulException, ExistSuchFlightScheduleException {
        System.out.println("Update flight schedule in process");
        verifyScheduleCollision(aircraft, deptDate, arrDate, startDate, endDate, oldFlightSched);
        Flight flight = getFlightByFlightNo(flightNo);
//        FlightSchedule firstFlightSched = em.find(FlightSchedule.class, getFlightScheduleByFlightNoAndDeptDate(flight.getFlightNo(), oldDeptDate).getFlightScheduleId());

        FlightSchedule firstFlightSched = em.find(FlightSchedule.class, oldFlightSched.getFlightScheduleId());
        FlightSchedule lastFlightSched = new FlightSchedule();
        List<FlightSchedule> flightSchedules = new ArrayList<>();
        flightSchedules.add(firstFlightSched);
        flightSchedules.add(lastFlightSched);

        deptDate = reSetFlightSchedule(flight, aircraft, deptDate, flightSchedules);

        FlightSchedule reFirstFlightSched = flightSchedules.get(1).getNextFlightSched();
        FlightSchedule reLastFlightSched = new FlightSchedule();

        List<FlightSchedule> reFlightSchedules = new ArrayList<>();
        reFlightSchedules.add(reFirstFlightSched);
        reFlightSchedules.add(reLastFlightSched);

        reSetFlightSchedule(flight.getReturnedFlight(), aircraft, deptDate, reFlightSchedules);

        firstFlightSched = em.find(FlightSchedule.class, flightSchedules.get(0).getFlightScheduleId());
        lastFlightSched = em.find(FlightSchedule.class, flightSchedules.get(1).getFlightScheduleId());
        reFirstFlightSched = em.find(FlightSchedule.class, reFlightSchedules.get(0).getFlightScheduleId());
//        reLastFlightSched = em.find(FlightSchedule.class, reFlightSchedules.get(1).getFlightScheduleId());

        lastFlightSched.setNextFlightSched(reFirstFlightSched);
        reFirstFlightSched.setPreFlightSched(lastFlightSched);
        em.merge(lastFlightSched);
        return firstFlightSched;
    }

    private Date reSetFlightSchedule(Flight flight, Aircraft aircraft, Date deptDate, List<FlightSchedule> flightSchedules)
            throws NoSelectAircraftException, NoSuchFlightSchedulException {
        RouteHelper routeHelper = new RouteHelper();
        routePlanningSession.getRouteDetail(flight.getRoute(), routeHelper);
        calcFlightDuration(getModelWithMinMachNo(flight.getAircraftTypes()), routeHelper, flight.getSpeedFraction());
        List<LegHelper> legHelpers = routeHelper.getLegs();
        Date dept = deptDate;
        Date arrival;
        int i = 0;

        FlightSchedule flightSched = em.find(FlightSchedule.class, flightSchedules.get(0).getFlightScheduleId());

        for (LegHelper legHelper : legHelpers) {
            arrival = addHourToDate(dept, legHelper.getFlyingTime());
            flightSched.setDepartDate(dept);
            flightSched.setArrivalDate(arrival);
            flightSched.setAircraft(aircraft);

            if (i == 0) {
                flightSchedules.set(0, flightSched);
            }

            if (i == legHelpers.size() - 1) {
                flightSchedules.set(1, flightSched);
            }

            em.merge(flightSched);
            em.flush();
            i++;
            if (flightSched.getNextFlightSched() != null) {
                flightSched = em.find(FlightSchedule.class, flightSched.getNextFlightSched().getFlightScheduleId());
            }
            dept = addHourToDate(arrival, legHelper.getTurnaroundTime());
        }
        System.out.println("Update flight schedule successfully");
        return dept;
    }

    @Override
    public Date addHourToDate(Date start, double hours) {
        long time = start.getTime();
        long minutes = (long) (hours * 60);
        return new Date(time + (minutes * ONE_MINUTE_IN_MILLIS));
    }

    @Override
    public Flight getFlightByFlightNo(String fligthNo) throws NoSuchFlightException {
        Query query = em.createQuery("SELECT f FROM Flight f WHERE f.flightNo = :inFlightNo AND f.deleted = FALSE");
        query.setParameter("inFlightNo", fligthNo);
        Flight flight = new Flight();
        try {
            flight = (Flight) query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoSuchFlightException(ApsMsg.NO_SUCH_FLIGHT_ERROR);
        }
        return flight;
    }

    @Override
    public List<Flight> getAllFlights() {
        Query query = em.createQuery("SELECT f FROM Flight f WHERE f.deleted = FALSE");
        List<Flight> outputFlights = new ArrayList();
        try {
            outputFlights = (List<Flight>) query.getResultList();

        } catch (NoResultException ex) {

        }
        return outputFlights;
    }

    @Override
    public Aircraft getAircraftByTailNo(String tailNo) throws NoSuchAircraftException {
        Query query = em.createQuery("SELECT a FROM Aircraft a WHERE a.tailNo = :inTailNo AND a.status <> :inRetired AND a.status <> :inCrashed");
        query.setParameter("inTailNo", tailNo);
        query.setParameter("inRetired", AircraftStatus.RETIRED);
        query.setParameter("inCrashed", AircraftStatus.CRASHED);
        Aircraft aircraft = new Aircraft();
        try {
            aircraft = (Aircraft) query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoSuchAircraftException(ApsMsg.NO_SUCH_AIRCRAFT_ERROR);
        }
        return aircraft;
    }

    @Override
    public List<FlightSchedule> getFlightSchedulesByTailNoAndTime(String tailNo, Date startDate, Date endDate, String method) {
        Query query = em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.aircraft.tailNo = :inTailNo AND fs.deleted = FALSE AND fs.departDate BETWEEN :inStartDate AND :inEndDate");
        if (method.equals(FlightSchedMethod.DISPLAY)) {
            query = em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.aircraft.tailNo = :inTailNo AND fs.deleted = FALSE AND ((fs.departDate BETWEEN :inStartDate AND :inEndDate) OR (fs.arrivalDate BETWEEN :inStartDate AND :inEndDate))");
        }
        query.setParameter("inTailNo", tailNo);
        query.setParameter("inStartDate", startDate);
        query.setParameter("inEndDate", endDate);

        List<FlightSchedule> flightSchedules;
        try {
            flightSchedules = (List<FlightSchedule>) query.getResultList();
        } catch (NoResultException e) {
            flightSchedules = new ArrayList<>();
        }
        return flightSchedules;
    }

    @Override
    public List<FlightSchedule> getFlightSchedulesByTailNo(String tailNo) {
        Query query = em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.aircraft.tailNo = :inTailNo AND fs.deleted = FALSE");
        query.setParameter("inTailNo", tailNo);

        List<FlightSchedule> flightSchedules;
        try {
            flightSchedules = (List<FlightSchedule>) query.getResultList();
        } catch (NoResultException e) {
            flightSchedules = new ArrayList<>();
        }
        return flightSchedules;
    }

    @Override
    public void deleteFlightSchedule(FlightSchedule flightSchedule, String method) throws NoSuchFlightSchedulException {
        if (flightSchedule.getPreFlightSched() != null) {
            throw new NoSuchFlightSchedulException(ApsMsg.NO_SUCH_FLIGHT_SHCEDULE_ERROR);
        } else {
            FlightSchedule deletedFlightSched = flightSchedule;
            while (flightSchedule != null) {
                flightSchedule = em.find(FlightSchedule.class, flightSchedule.getFlightScheduleId());
                flightSchedule.setDeleted(true);
                em.merge(flightSchedule);
                em.flush();
                flightSchedule = flightSchedule.getNextFlightSched();
            }
            if (method.equals(FlightSchedMethod.DELETE)) {
                deleteOneFlightSchedule(deletedFlightSched.getFlight());
                deleteOneFlightSchedule(deletedFlightSched.getFlight().getReturnedFlight());
            }
        }
    }

    private void deleteOneFlightSchedule(Flight flight) {
        if (flight.getNumOfUnscheduled() < flight.getWeeklyFrequency()) {
            flight = em.find(Flight.class, flight.getId());
            flight.setNumOfUnscheduled(flight.getNumOfUnscheduled() + 1);
            em.merge(flight);
        }
    }

    private List<FlightSchedule> getCollisionFlightSched(Aircraft aircraft, Date startDate, Date endDate, Date weekStartDate, Date weekEndDate, List<FlightSchedule> weekFlightScheds) {
        List<FlightSchedule> collidedFlightScheds = new ArrayList<>();
        List<FlightSchedule> flightSchedules = setRouteFlightSchedules(getFlightSchedulesByTailNoAndTime(aircraft.getTailNo(), startDate, endDate, FlightSchedMethod.DISPLAY));

        for (FlightSchedule flightSchedule : flightSchedules) {
            for (FlightSchedule weekFlightSched : weekFlightScheds) {
                //Get departure date and arrive date of the specific flightSchedule on specifc week.
                List<Date> dates = setDeptDateArrDate(startDate, weekStartDate, weekFlightSched);
                Date deptDate = dates.get(0);
                Date arrDate = dates.get(1);
                if (deptDate.equals(flightSchedule.getDepartDate()) && arrDate.equals(flightSchedule.getArrivalDate())) {

                } else if (arrDate.after(flightSchedule.getDepartDate()) && flightSchedule.getArrivalDate().after(deptDate)) {
                    collidedFlightScheds.addAll(setRouteFlightSchedules(getFlightSchedulesByTailNoAndTime(aircraft.getTailNo(), deptDate, arrDate, FlightSchedMethod.DISPLAY)));
                }

            }
        }
        return collidedFlightScheds;
    }

    private List<Date> setDeptDateArrDate(Date startDate, Date weekStartDate, FlightSchedule weekFlightSched) {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.MILLISECOND, calcDateDifference(weekStartDate, weekFlightSched.getDepartDate()));
        Date deptDate = calendar.getTime();
        calendar.setTime(startDate);
        calendar.add(Calendar.MILLISECOND, calcDateDifference(weekStartDate, weekFlightSched.getArrivalDate()));
        Date arrDate = calendar.getTime();
        dates.add(deptDate);
        dates.add(arrDate);
        return dates;
    }

    private int calcDateDifference(Date startDate, Date endDate) {
        return (int) (endDate.getTime() - startDate.getTime());
    }

    @Override
    public void setRouteFlightSchedule(FlightSchedule flightSchedule) {
        Flight flight = flightSchedule.getFlight();
        RouteHelper routeHelper = new RouteHelper();
        routePlanningSession.getRouteDetail(flight.getRoute(), routeHelper);
        calcFlightDuration(getModelWithMinMachNo(flight.getAircraftTypes()), routeHelper, flight.getSpeedFraction());
        Leg leg = new Leg();
        System.out.println("setRouteFlightSchedule() new leg: " + routeHelper.getOrigin().getCity().getCityName() + " - " + routeHelper.getDestination().getCity().getCityName());
        leg.setDepartAirport(routeHelper.getOrigin());
        leg.setArrivalAirport(routeHelper.getDestination());
        flightSchedule.setDepartDate(flightSchedule.getDepartDate());
        flightSchedule.setArrivalDate(addHourToDate(flightSchedule.getDepartDate(), routeHelper.getTotalDuration() * 2));
        flightSchedule.setLeg(leg);
    }

    private List<FlightSchedule> setRouteFlightSchedules(List<FlightSchedule> inFlightScheds) {
        List<FlightSchedule> outFlightScheds = new ArrayList<>();
        for (FlightSchedule flightSchedule : inFlightScheds) {   
            if (flightSchedule.getPreFlightSched() == null) {
                setRouteFlightSchedule(flightSchedule);
                outFlightScheds.add(flightSchedule);
            }
        }
//        System.out.println("Set Route Flight Schedule size : " + outFlightScheds.size());
        return outFlightScheds;
    }

    @Override
    public List<FlightSchedule> verifyApplyFlightSchedCollision(List<Aircraft> aircrafts, Date startDate, Date endDate, Date weekStartDate, Date weekEndDate) {
        List<Date> dates = new ArrayList<>();
        dates.add(startDate);
        dates.add(endDate);

        initializeApplyDate(dates);

        startDate = dates.get(0);
        endDate = dates.get(1);
        Date deptDate = dates.get(2);
        Date arrDate = dates.get(3);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(arrDate);

        List<FlightSchedule> collidedFlightScheds = new ArrayList<>();
        Set<FlightSchedule> flightSchedHs = new HashSet<>();
        for (Aircraft aircraft : aircrafts) {

            List<FlightSchedule> weekFlightScheds = setRouteFlightSchedules(getFlightSchedulesByTailNoAndTime(aircraft.getTailNo(), weekStartDate, weekEndDate, FlightSchedMethod.DISPLAY));

            while (arrDate.before(endDate) || arrDate.equals(endDate)) {
                flightSchedHs.addAll(getCollisionFlightSched(aircraft, deptDate, arrDate, weekStartDate, weekEndDate, weekFlightScheds));
                calendar.add(Calendar.DATE, 1);
                deptDate = calendar.getTime();
                calendar.add(Calendar.DATE, 6);
                arrDate = calendar.getTime();
            }
            deptDate = dates.get(2);
            arrDate = dates.get(3);
        }
        collidedFlightScheds.addAll(flightSchedHs);
        return collidedFlightScheds;
    }

    @Override
    public void applyFlightSchedulesToPeriod(List<Aircraft> aircrafts, Date startDate, Date endDate, Date weekStartDate, Date weekEndDate) {
        List<Date> dates = new ArrayList<>();
        dates.add(startDate);
        dates.add(endDate);

        initializeApplyDate(dates);
        startDate = dates.get(0);
        endDate = dates.get(1);
        Date deptDate = dates.get(2);
        Date arrDate = dates.get(3);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(arrDate);
        for (Aircraft aircraft : aircrafts) {
            System.out.println("Aircraft: " + aircraft.getTailNo());
            List<FlightSchedule> fs = getFlightSchedulesByTailNoAndTime(aircraft.getTailNo(), weekStartDate, weekEndDate, FlightSchedMethod.DISPLAY);
            List<FlightSchedule> weekFlightScheds = setRouteFlightSchedules(fs);
            while (arrDate.before(endDate) || arrDate.equals(endDate)) {
                List<FlightSchedule> changedWeekFlightScheds = new ArrayList<>();
                changedWeekFlightScheds.addAll(weekFlightScheds);
                List<FlightSchedule> fs2 = getFlightSchedulesByTailNoAndTime(aircraft.getTailNo(), deptDate, arrDate, FlightSchedMethod.DISPLAY);
                List<FlightSchedule> fligthSchedules = setRouteFlightSchedules(fs2);
                if (!fligthSchedules.isEmpty()) {
                    for (FlightSchedule flightSchedule : fligthSchedules) {
                        //Identify the flight schedules that is not changed, remove them from the week flight schedule List
                        for (FlightSchedule weekFlightSched : weekFlightScheds) {
                            //Get departure date and arrive date of the specific flightSchedule on specifc week.
                            List<Date> newDates = setDeptDateArrDate(deptDate, weekStartDate, weekFlightSched);
                            Date newDeptDate = newDates.get(0);
                            Date newArrDate = newDates.get(1);

                            System.out.println("newDeptDate: " + newDeptDate + ". newArrDate: " + newArrDate);
                            System.out.println("DeptDate: " + flightSchedule.getDepartDate() + ". ArrDate: " + flightSchedule.getArrivalDate());
                            if (newDeptDate.equals(flightSchedule.getDepartDate()) && newArrDate.equals(flightSchedule.getArrivalDate())) {
                                changedWeekFlightScheds.remove(weekFlightSched);
                                System.out.println("Apply: same database result, continue");
                            }
                        }
                    }

                    for (FlightSchedule flightSchedule : fligthSchedules) {
                        for (FlightSchedule weekFlightSched : changedWeekFlightScheds) {
                            List<Date> newDates = setDeptDateArrDate(deptDate, weekStartDate, weekFlightSched);
                            Date newDeptDate = newDates.get(0);
                            Date newArrDate = newDates.get(1);
                            if (newArrDate.after(flightSchedule.getDepartDate()) && flightSchedule.getArrivalDate().after(newDeptDate)) {
                                List<FlightSchedule> fs3 = getFlightSchedulesByTailNoAndTime(aircraft.getTailNo(), newDeptDate, newArrDate, FlightSchedMethod.DISPLAY);
                                List<FlightSchedule> collidedFlightScheds = setRouteFlightSchedules(fs3);
                                FlightSchedule updatedFlightSched = collidedFlightScheds.get(0);
                                FlightSchedule oldFlightSched = updatedFlightSched;
                                try {
                                    System.out.println("Apply: Collide Update to " + aircraft.getTailNo() + " " + newDeptDate + " - " + newArrDate);
                                    updateFlightSchedule(updatedFlightSched.getFlight().getFlightNo(), aircraft, newDeptDate, newArrDate, deptDate, arrDate, oldFlightSched);
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                    System.out.println("Update flight error");
                                }
                                for (int i = 1; i < collidedFlightScheds.size(); i++) {
                                    try {
                                        System.out.println("Apply: Delete to " + aircraft.getTailNo() + " " + newDeptDate + " - " + newArrDate);
                                        deleteFlightSchedule(collidedFlightScheds.get(i), FlightSchedMethod.APPLY);
                                    } catch (Exception e) {
                                        System.out.println("Apply delete flight schedule");
                                    }
                                }
                            } else {
                                try {
                                    System.out.println("Apply internal: Create to " + aircraft.getTailNo() + " " + newDeptDate + " - " + newArrDate);
                                    createFlightSchedule(weekFlightSched.getFlight(), aircraft, newDeptDate, newArrDate, deptDate, arrDate, FlightSchedMethod.APPLY);
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                        }
                    }
                } else {
                    for (FlightSchedule weekFlightSched : weekFlightScheds) {
                        List<Date> newDates = setDeptDateArrDate(deptDate, weekStartDate, weekFlightSched);
                        Date newDeptDate = newDates.get(0);
                        Date newArrDate = newDates.get(1);
                        System.out.println("Apply external: Create to " + aircraft.getTailNo() + " " + newDeptDate + " - " + newArrDate);
                        try {
                            createFlightSchedule(weekFlightSched.getFlight(), aircraft, newDeptDate, newArrDate, deptDate, arrDate, FlightSchedMethod.APPLY);
                        } catch (Exception e) {
                            System.out.println("Apply create flight schedule error");
                            System.out.println(e.getMessage());
                        }
                    }
                }
                calendar.add(Calendar.DATE, 1);
                deptDate = calendar.getTime();
                calendar.add(Calendar.DATE, 6);
                arrDate = calendar.getTime();
            }
            for (FlightSchedule weekFlightSched : weekFlightScheds) {
                try {
                    updateFlightSchedule(weekFlightSched.getFlight().getFlightNo(), aircraft, weekFlightSched.getDepartDate(), weekFlightSched.getArrivalDate(), weekStartDate, weekEndDate, weekFlightSched);
                } catch (Exception e) {
                }
            }

            deptDate = dates.get(2);
            arrDate = dates.get(3);
            calendar.setTime(arrDate);
        }
    }

    private void initializeApplyDate(List<Date> dates) {
        Date startDate = dates.get(0);
        Date endDate = dates.get(1);

        //set start date to the next week sunday of the selected date
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);
        startCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        DateHelper.setToStartOfDay(startCalendar);
//        startCalendar.add(Calendar.DATE, 7);
        startDate = startCalendar.getTime();

        //initialze departure date and arrival date
        startCalendar.add(Calendar.DATE, 7);
        Date deptDate = startCalendar.getTime();
        startCalendar.add(Calendar.DATE, 6);
        Date arrDate = startCalendar.getTime();

        //set end date to the next week saturday of the selected date
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        DateHelper.setToEndOfDay(endCalendar);
        if (endCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
            endCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            endCalendar.add(Calendar.DATE, -1);
        }
        endDate = endCalendar.getTime();
        dates.set(0, startDate);
        dates.set(1, endDate);
        dates.add(deptDate);
        dates.add(arrDate);
    }

    @Override
    public List<Aircraft> getAllAircrafts() {
        Query query = em.createQuery("SELECT a FROM Aircraft a WHERE a.status <> :retired AND a.status <> :crashed");
        query.setParameter("retired", AircraftStatus.RETIRED);
        query.setParameter("crashed", AircraftStatus.CRASHED);
        List<Aircraft> aircrafts = new ArrayList<>();
        try {
            aircrafts = (List<Aircraft>) query.getResultList();
        } catch (NoResultException e) {
        }
        return aircrafts;
    }

    @Override
    public List<Aircraft> getAircraftsByAircraftType(String aircraftType) {
        Query query = em.createQuery("SELECT a FROM Aircraft a WHERE a.status <> :retired AND a.status <> :crashed AND a.aircraftType.typeCode = :aircraftType");
        query.setParameter("retired", AircraftStatus.RETIRED);
        query.setParameter("crashed", AircraftStatus.CRASHED);
        query.setParameter("aircraftType", aircraftType);
        List<Aircraft> aircrafts = new ArrayList<>();
        try {
            aircrafts = (List<Aircraft>) query.getResultList();
        } catch (NoResultException e) {
        }
        return aircrafts;
    }
}
