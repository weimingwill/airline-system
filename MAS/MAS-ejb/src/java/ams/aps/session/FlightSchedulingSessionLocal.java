/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.session;

import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftType;
import ams.aps.entity.FlightSchedule;
import ams.aps.entity.Route;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ChuningLiu
 */
@Local
public interface FlightSchedulingSessionLocal {

    public boolean createFlight(String flightNo, Long routeID);

    public boolean checkFlightNotExisted(String flightNo);

    public boolean checkFlightNoExistence(String flightNo);

    public boolean changeFlightNo(String flightNo, String newFlightNo);

    public List<AircraftType> getCapableAircraftTypesForRoute(Route route);

    public List<Aircraft> getAvailableAircraftsByType(AircraftType type);

    public double getMaxFlightFrequency(AircraftType type, Route r);

    public void assignFlightScheduleToAircraft(FlightSchedule flightSchedule);

    public void modifyFlightSchedule(FlightSchedule newFlightSchedule);

    public List<Route> getAvailableRoutes();

}
