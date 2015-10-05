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
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ChuningLiu
 */
@Local
public interface RouteSchedulingSessionLocal {
    
    public boolean createFlight(String flightNo, Long routeID);
    
    public boolean checkFlightNotExisted(String flightNo, Long routeID);
    
    public boolean changeFlightNo(String flightNo, String newFlightNo);
    
    public List<AircraftType> getCapableAircraftTypesForRoute(List<Airport> allStops);
    
    public List<Aircraft> getAvailableAircraftsByType(AircraftType type);
    
    public double getMaxFlightFrequency(AircraftType type, Route r);
    
    public void assignFlightScheduleToAircraft(FlightSchedule flightSchedule);
      
    public void modifyFlightSchedule(FlightSchedule newFlightSchedule);

}
