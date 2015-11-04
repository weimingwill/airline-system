/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.ais.entity.CabinClass;
import ams.ais.entity.FlightScheduleBookingClass;
import ams.ais.entity.TicketFamily;
import ams.ais.session.CabinClassSessionLocal;
import ams.ais.session.FlightScheduleSessionLocal;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.aps.entity.Airport;
import ams.aps.entity.FlightSchedule;
import ams.aps.util.helper.FlightSchedStatus;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
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

    @EJB
    private FlightScheduleSessionLocal flightSchedSession;
    @EJB
    private CabinClassSessionLocal cabinClassSession;

    @Override
    public List<List<FlightSchedule>> searchForOneWayFlights(Airport deptAirport, Airport arrAirport, Date deptDate, CabinClass cabinClass, int numOfPassenger) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(deptDate);
        DateHelper.setToStartOfDay(calendar);
        Date date = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date nextDate = calendar.getTime();

        List<FlightSchedule> directFlightScheds = searchForDirectFlights(deptAirport, arrAirport, date, nextDate, cabinClass, numOfPassenger);
        List<FlightSchedule> inDirectFlightScheds = searchForInDirectFlights(deptAirport, arrAirport, date, nextDate, cabinClass, numOfPassenger);

        List<List<FlightSchedule>> flightSchedules = new ArrayList<>();
        flightSchedules.add(directFlightScheds);
        flightSchedules.add(inDirectFlightScheds);
        return flightSchedules;
    }

    private List<FlightSchedule> searchForDirectFlights(Airport deptAirport, Airport arrAirport, Date date, Date nextDate, CabinClass cabinClass, int numOfPassenger) {
        Query query = em.createQuery("SELECT fb FROM FlightSchedule f, FlightScheduleBookingClass fb WHERE f.leg.departAirport.airportName = :inDeptAirport AND f.leg.arrivalAirport.airportName = :inArrAirport AND f.departDate BETWEEN :inDate AND :inNextDate AND fb.flightScheduleBookingClassId.flightScheduleId = f.flightScheduleId AND f.aircraft.aircraftCabinClasses.cabinClassId = :inCabinClass AND f.deleted = FALSE AND f.status = :inStatus");
        query.setParameter("inStatus", FlightSchedStatus.RELEASE);
        query.setParameter("inDeptAirport", deptAirport.getAirportName());
        query.setParameter("inArrAirport", arrAirport.getAirportName());
        query.setParameter("inDate", date);
        query.setParameter("inNextDate", nextDate);
        query.setParameter("inCabinClass", cabinClass.getCabinClassId());
        List<FlightSchedule> directFlightScheds = new ArrayList<>();
        try {
            directFlightScheds = (List<FlightSchedule>) query.getResultList();
        } catch (NoResultException e) {
        }
        return directFlightScheds;
    }

    private List<FlightSchedule> searchForInDirectFlights(Airport deptAirport, Airport arrAirport, Date date, Date nextDate, CabinClass cabinClass, int numOfPassenger) {
        Query query = em.createQuery("SELECT fb FROM FlightSchedule f, FlightSchedule f2, FlightScheduleBookingClass fb, CabinClass c WHERE f.leg.departAirport.airportName = :inDeptAirport AND f.leg.arrivalAirport.airportName = f2.leg.departAirport.airportName AND f2.leg.arrivalAirport.airportName = :inArrAirport AND f.departDate BETWEEN :inDate AND :inNextDate AND fb.flightScheduleBookingClassId.flightScheduleId = f.flightScheduleId AND f.aircraft.aircraftCabinClasses.cabinClassId = :inCabinClass AND fb.seatQty > fb.soldSeatQty + :inNumOfPassenger AND f.deleted = FALSE AND f.status = :inStatus");
        query.setParameter("inStatus", FlightSchedStatus.RELEASE);
        query.setParameter("inDeptAirport", deptAirport.getAirportName());
        query.setParameter("inArrAirport", arrAirport.getAirportName());
        query.setParameter("inDate", date);
        query.setParameter("inNextDate", nextDate);
        query.setParameter("inCabinClass", cabinClass.getCabinClassId());
        List<FlightSchedule> flightScheds = new ArrayList<>();
        try {
            flightScheds = (List<FlightSchedule>) query.getResultList();
        } catch (NoResultException e) {
        }
        return flightScheds;
    }

    @Override
    public List<TicketFamily> getFlightSchedLowesetTixFams(List<FlightSchedule> flightScheds, boolean premimum) {
        List<TicketFamily> tixFams = new ArrayList<>();
        try {
            for (FlightSchedule flightSched : flightScheds) {
                for (CabinClass cabinClass : flightSchedSession.getFlightScheduleCabinCalsses(flightSched.getFlightScheduleId())) {
                    if (premimum) {
                        if (cabinClass.getRank() < 3) {
                            addTixFams(tixFams, flightSched, cabinClass);
                        }
                    } else {
                        addTixFams(tixFams, flightSched, cabinClass);
                    }
                }
            }

        } catch (NoSuchCabinClassException e) {
        }
        return tixFams;
    }

    private List<TicketFamily> addTixFams(List<TicketFamily> tixFams, FlightSchedule flightSched, CabinClass cabinClass) {
        try {
            List<TicketFamily> ticketFamilys = cabinClassSession.getCabinClassTicketFamilysFromJoinTable(flightSched.getAircraft().getAircraftId(), cabinClass.getCabinClassId());
            for (TicketFamily ticketFamily : ticketFamilys) {
                if (tixFams.size() < 4) {
                    tixFams.add(ticketFamily);
                } else {
                    break;
                }
            }
        } catch (NoSuchTicketFamilyException e) {
        }
        return tixFams;
    }

}
