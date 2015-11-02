/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.aps.entity.Airport;
import java.util.Date;
import javax.ejb.Local;

/**
 *
 * @author weiming
 */
@Local
public interface BookingSessionLocal {
    public void searchForOneWayFlights(Airport deptAirport, Airport arrAirport, Date deptDate, boolean showPremium, int numOfPassenger);
}
