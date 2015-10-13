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
import ams.aps.util.exception.NoMoreUnscheduledFlightException;
import ams.aps.util.exception.NoSuchFlightException;
import ams.aps.util.exception.NoSuchRouteException;
import ams.aps.util.exception.ObjectDoesNotExistException;
import ams.aps.util.helper.RouteHelper;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ChuningLiu
 */
@Local
public interface FlightSchedulingSessionLocal {

    public Flight checkFlightExistence(String flightNo) throws ObjectDoesNotExistException;

    public List<Flight> getFlight(Boolean complete) throws EmptyTableException;

    public List<Flight> getAllFlights();

    public void deleteFlight(String flightNo) throws DeleteFailedException;

    public boolean changeFlightNo(String flightNo, String newFlightNo);

    public List<AircraftType> getCapableAircraftTypesForRoute(Double maxDist);

    public List<Aircraft> getAvailableAircraftsByType(AircraftType type);

    public void assignFlightScheduleToAircraft(FlightSchedule flightSchedule);

    public void modifyFlightSchedule(FlightSchedule newFlightSchedule) throws ObjectDoesNotExistException;

    public List<Flight> getAllUnscheduledFlights() throws NoSuchFlightException;

    public List<String> getUnscheduledFlightAircraftTypeFamilys();

    public List<String> getUnscheduledAircraftTypeCodesByTypeFamily(String typeFamily);

    public List<Route> getUnscheduledFlightRoutes() throws NoSuchRouteException;

    public List<Flight> getUnscheduledFlights(Airport deptAirport, List<String> aircraftTypeCodes)
            throws NoSuchFlightException, NoSuchRouteException;

    public void calcFlightDuration(AircraftType selectedModel, RouteHelper routeHelper, double speedFraction);

    public AircraftType getModelWithMinMachNo(List<AircraftType> models);

    public boolean createReturnedFlight(Flight flight, Flight returnedFlight);

    public void updateFlight(Flight flight) throws ObjectDoesNotExistException;
    
    public void createFlightSchedule(String flightNo, Aircraft aircraft, Date deptDate, Date arrivalDate) 
            throws NoSuchFlightException, NoMoreUnscheduledFlightException;
    
    public void verifyUnscheduledFlightNumber(Flight flight) throws NoMoreUnscheduledFlightException;
}
