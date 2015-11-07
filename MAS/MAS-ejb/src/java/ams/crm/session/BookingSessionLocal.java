/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.aps.entity.Airport;
import ams.aps.entity.FlightSchedule;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author weiming
 */
@Local
public interface BookingSessionLocal {
    public List<FlightSchedule> searchForOneWayFlights(Airport deptAirport, Airport arrAirport, Date deptDate, boolean showPremium, int numOfPassenger);
}
