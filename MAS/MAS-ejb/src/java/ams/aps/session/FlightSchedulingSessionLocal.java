/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.session;

import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftType;
import ams.aps.entity.Flight;
import ams.aps.entity.FlightSchedule;
import ams.aps.entity.Route;
import ams.aps.util.exception.DeleteFailedException;
import ams.aps.util.exception.EmptyTableException;
import ams.aps.util.exception.FlightDoesNotExistException;
import ams.aps.util.exception.ObjectDoesNotExistException;
import ams.aps.util.helper.RouteHelper;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ChuningLiu
 */
@Local
public interface FlightSchedulingSessionLocal {
    public boolean createFlight(Flight flight);
    public Flight checkFlightExistence(String flightNo) throws FlightDoesNotExistException;
    public List<Flight> getFlight(Boolean complete) throws EmptyTableException;
    public void deleteFlight(String flightNo) throws DeleteFailedException;
    public boolean changeFlightNo(String flightNo, String newFlightNo);
    public List<AircraftType> getCapableAircraftTypesForRoute(Double maxDist);
    public List<Aircraft> getAvailableAircraftsByType(AircraftType type);
    public void assignFlightScheduleToAircraft(FlightSchedule flightSchedule);
    public void modifyFlightSchedule(FlightSchedule newFlightSchedule) throws ObjectDoesNotExistException;
    public void calcFlightDuration(AircraftType selectedModel, RouteHelper routeHelper);
 
}
