/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.aps.entity.Airport;
import ams.aps.entity.FlightSchedule;
import ams.aps.util.helper.FlightSchedStatus;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mas.util.helper.DateHelper;

/**
 *
 * @author weiming
 */
@Stateless
public class BookingSession implements BookingSessionLocal {

    @PersistenceContext(unitName = "MAS-ejbPU")
    private EntityManager em;

    @Override
    public List<List<FlightSchedule>> searchForOneWayFlights(Airport deptAirport, Airport arrAirport, Date deptDate, boolean showPremium, int numOfPassenger) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(deptDate);
        DateHelper.setToStartOfDay(calendar);
        Date date = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date nextDate = calendar.getTime();

        List<FlightSchedule> directFlightScheds = searchForDirectFlights(deptAirport, arrAirport, date, nextDate, showPremium, numOfPassenger);
        List<FlightSchedule> inDirectFlightScheds = searchForInDirectFlights(deptAirport, arrAirport, date, nextDate, showPremium, numOfPassenger);

        List<List<FlightSchedule>> flightSchedules = new ArrayList<>();
        flightSchedules.add(directFlightScheds);
        flightSchedules.add(inDirectFlightScheds);
        return flightSchedules;
    }

    private List<FlightSchedule> searchForDirectFlights(Airport deptAirport, Airport arrAirport, Date date, Date nextDate, boolean showPremium, int numOfPassenger) {
        Query query;
        if (showPremium) {
            query = em.createQuery("SELECT f FROM FlightSchedule f WHERE f.leg.departAirport.airportName = :inDeptAirport AND f.leg.arrivalAirport.airportName = :inArrAirport AND f.departDate BETWEEN :inDate AND :inNextDate AND f.deleted = FALSE AND f.status = :inStatus");
        } else {
            query = em.createQuery("SELECT f FROM FlightSchedule f WHERE f.leg.departAirport.airportName = :inDeptAirport AND f.leg.arrivalAirport.airportName = :inArrAirport AND f.departDate BETWEEN :inDate AND :inNextDate AND f.deleted = FALSE AND f.status = :inStatus");
        }
        query.setParameter("inStatus", FlightSchedStatus.RELEASE);
        query.setParameter("inDeptAirport", deptAirport.getAirportName());
        query.setParameter("inArrAirport", arrAirport.getAirportName());
        query.setParameter("inDate", date);
        query.setParameter("inNextDate", nextDate);
        List<FlightSchedule> directFlightScheds = new ArrayList<>();
        try {
            directFlightScheds = (List<FlightSchedule>) query.getResultList();
        } catch (NoResultException e) {
        }
        return directFlightScheds;
    }

    private List<FlightSchedule> searchForInDirectFlights(Airport deptAirport, Airport arrAirport, Date date, Date nextDate, boolean showPremium, int numOfPassenger) {
        Query query;
        if (showPremium) {
            query = em.createQuery("SELECT f FROM FlightSchedule f WHERE f.leg.departAirport.airportName = :inDeptAirport AND f.leg.arrivalAirport.airportName = :inArrAirport AND f.departDate BETWEEN :inDate AND :inNextDate AND f.deleted = FALSE AND f.status = :inStatus");
        } else {
            query = em.createQuery("SELECT f FROM FlightSchedule f WHERE f.leg.departAirport.airportName = :inDeptAirport AND f.leg.arrivalAirport.airportName = :inArrAirport AND f.departDate BETWEEN :inDate AND :inNextDate AND f.deleted = FALSE AND f.status = :inStatus");
        }
        query.setParameter("inStatus", FlightSchedStatus.RELEASE);
        query.setParameter("inDeptAirport", deptAirport.getAirportName());
        query.setParameter("inArrAirport", arrAirport.getAirportName());
        query.setParameter("inDate", date);
        query.setParameter("inNextDate", nextDate);
        List<FlightSchedule> flightScheds = new ArrayList<>();
        try {
            flightScheds = (List<FlightSchedule>) query.getResultList();
        } catch (NoResultException e) {
        }
        return flightScheds;
    }
    
}
