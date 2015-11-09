/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.ais.entity.CabinClass;
import ams.ais.entity.FlightScheduleBookingClass;
import ams.ais.entity.TicketFamily;
import ams.ais.util.helper.FlightSchedBookingClsHelper;
import ams.aps.entity.Airport;
import ams.aps.entity.FlightSchedule;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author weiming
 */
@Local
public interface BookingSessionLocal {

    public List<FlightSchedule> searchForOneWayFlights(Airport deptAirport, Airport arrAirport, Date deptDate, Map<FlightSchedule,FlightSchedule> flightSchedMaps) throws NoSuchFlightSchedulException;

    public List<TicketFamily> getFlightSchedLowestTixFams(List<FlightSchedule> flightScheds, CabinClass cabinClass);

    public List<FlightScheduleBookingClass> getOpenedFlightSchedBookingClses(FlightSchedule flightSched, List<TicketFamily> tixFams, String channelName, int numOfTix, Map<FlightScheduleBookingClass, FlightSchedBookingClsHelper> flightSchedBookingClsMaps);
}
