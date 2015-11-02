/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.aps.entity.Airport;
import ams.aps.entity.FlightSchedule;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author weiming
 */
@Stateless
public class BookingSession implements BookingSessionLocal {
    @PersistenceContext(unitName = "MAS-ejbPU")
    private EntityManager em;
    
    @Override
    public List<FlightSchedule> searchForOneWayFlights(Airport deptAirport, Airport arrAirport, Date deptDate, boolean showPremium, int numOfPassenger) {
        Query query = em.createQuery("SELECT f FROM FlightSchedule f WHERE f.leg.departAirport.airportName");
        List<FlightSchedule> flightScheds = new ArrayList<>();
        
        return flightScheds;
    }

}
