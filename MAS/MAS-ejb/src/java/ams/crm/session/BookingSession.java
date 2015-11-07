/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.ais.entity.CabinClass;
import ams.ais.entity.CabinClassTicketFamily;
import ams.ais.entity.FlightScheduleBookingClass;
import ams.ais.entity.TicketFamily;
import ams.ais.session.ProductDesignSessionLocal;
import ams.ais.session.RevMgmtSessionLocal;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.ais.util.helper.FlightSchedBookingClsHelper;
import ams.aps.entity.Airport;
import ams.aps.entity.FlightSchedule;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import ams.aps.util.helper.ApsMsg;
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
    private RevMgmtSessionLocal revMgmtSession;
    @EJB
    private ProductDesignSessionLocal productDesignSession;

    @Override
    public List<List<FlightSchedule>> searchForOneWayFlights(Airport deptAirport, Airport arrAirport, Date deptDate) throws NoSuchFlightSchedulException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(deptDate);
        DateHelper.setToStartOfDay(calendar);
        Date date = calendar.getTime();
        DateHelper.setToEndOfDay(calendar);
        Date nextDate = calendar.getTime();

        List<FlightSchedule> directFlightScheds = searchForDirectFlights(deptAirport, arrAirport, date, nextDate);
        List<FlightSchedule> inDirectFlightScheds = searchForInDirectFlights(deptAirport, arrAirport, date, nextDate);

        if (directFlightScheds.isEmpty() && inDirectFlightScheds.isEmpty()) {
            throw new NoSuchFlightSchedulException(ApsMsg.NO_SUCH_FLIGHT_SHCEDULE_ERROR);
        }

        List<List<FlightSchedule>> flightSchedules = new ArrayList<>();
        flightSchedules.add(directFlightScheds);
        flightSchedules.add(inDirectFlightScheds);
        return flightSchedules;
    }

    private List<FlightSchedule> searchForDirectFlights(Airport deptAirport, Airport arrAirport, Date date, Date nextDate) {
        Query query = em.createQuery("SELECT f FROM FlightSchedule f WHERE f.leg.departAirport.airportName = :inDeptAirport AND f.leg.arrivalAirport.airportName = :inArrAirport AND f.departDate BETWEEN :inDate AND :inNextDate AND f.deleted = FALSE AND f.status = :inStatus");
        query.setParameter("inStatus", FlightSchedStatus.RELEASE);
        query.setParameter("inDeptAirport", deptAirport.getAirportName());
        query.setParameter("inArrAirport", arrAirport.getAirportName());
        query.setParameter("inDate", date);
        query.setParameter("inNextDate", nextDate);
        System.out.println("inDate: " + date);
        System.out.println("inNextDate: " + nextDate);
        List<FlightSchedule> directFlightScheds = new ArrayList<>();
        try {
            directFlightScheds = (List<FlightSchedule>) query.getResultList();
        } catch (NoResultException e) {
        }
        return directFlightScheds;
    }

    private List<FlightSchedule> searchForInDirectFlights(Airport deptAirport, Airport arrAirport, Date date, Date nextDate) {
        Query query = em.createQuery("SELECT f FROM FlightSchedule f, FlightSchedule f2 WHERE f.leg.departAirport.airportName = :inDeptAirport AND f.leg.arrivalAirport.airportName = f2.leg.departAirport.airportName AND f2.leg.arrivalAirport.airportName = :inArrAirport AND f.departDate BETWEEN :inDate AND :inNextDate AND f.deleted = FALSE AND f2.deleted = FALSE AND f.status = :inStatus");
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

    @Override
    public List<TicketFamily> getFlightSchedLowestTixFams(List<FlightSchedule> flightScheds, CabinClass selectedCabinClass) {
        List<TicketFamily> tixFams = new ArrayList<>();
        try {
            for (FlightSchedule flightSched : flightScheds) {
                for (CabinClass cabinClass : revMgmtSession.getFlightScheduleCabinCalsses(flightSched.getFlightScheduleId())) {
                    if (selectedCabinClass.equals(cabinClass)) {
                        tixFams = productDesignSession.getCabinClassTicketFamilysFromJoinTable(flightSched.getAircraft().getAircraftId(), cabinClass.getCabinClassId());
//                        addTixFams(tixFams, flightSched, cabinClass);
                    }
                }
            }
        } catch (NoSuchCabinClassException | NoSuchTicketFamilyException e) {
        }
        return tixFams;
    }

    @Override
    public List<FlightSchedBookingClsHelper> getOpenedFlightSchedBookingClses(FlightSchedule flightSched, List<TicketFamily> tixFams, String channelName, int numOfTix) {
        List<FlightSchedBookingClsHelper> fbHelpers = new ArrayList<>();
        for (TicketFamily tixFam : tixFams) {
            fbHelpers.add(getOpenedFlightSchedBookingCls(flightSched, tixFam, channelName, numOfTix));
        }
        return fbHelpers;
    }
    
    private FlightSchedBookingClsHelper getOpenedFlightSchedBookingCls(FlightSchedule flightSched, TicketFamily tixFam, String channelName, int numOfTix) {
        Query query = em.createQuery("SELECT fb FROM FlightScheduleBookingClass fb, BookingClass b "
                + "WHERE fb.flightScheduleBookingClassId.flightScheduleId = :inFlightScheduleId "
                + "AND fb.flightScheduleBookingClassId.bookingClassId = b.bookingClassId "
                + "AND b.ticketFamily.ticketFamilyId = :inTicketFamilyId "
                + "AND fb.closed = FALSE "
                + "AND b.channel = :inChannel "
                + "AND b.deleted = FALSE "
                + "AND b.ticketFamily.deleted = FALSE "
                + "AND fb.deleted = FALSE");
        query.setParameter("inFlightScheduleId", flightSched.getFlightScheduleId());
        query.setParameter("inTicketFamilyId", tixFam.getTicketFamilyId());
        query.setParameter("inChannel", channelName);
        FlightSchedBookingClsHelper fbHelper = new FlightSchedBookingClsHelper();
        
        try {
            FlightScheduleBookingClass flightSchedBookingCls = (FlightScheduleBookingClass) query.getSingleResult();
            fbHelper.setFlightSchedBookingCls(flightSchedBookingCls);
            fbHelper.setTicketFamily(tixFam);
            fbHelper.setCabinClass(tixFam.getCabinClass());
            int remainedSeatQty = getRemainedSeatQty(flightSched, tixFam);
            fbHelper.setRemainedSeatQty(remainedSeatQty);
            if (remainedSeatQty > numOfTix) {
                fbHelper.setAvailable(true);
            } else {
                fbHelper.setAvailable(false);
            }
            
            if (remainedSeatQty > 10) {
                fbHelper.setShowLeftSeatQty(false);
            } else {
                fbHelper.setShowLeftSeatQty(true);
            }
        } catch (NoResultException e) {
        }
        return fbHelper;
    }

    private int getRemainedSeatQty(FlightSchedule flightSched, TicketFamily tixFam) {
        int totalSeatQty = 0;
        try {
            CabinClassTicketFamily cabinClassTixFam = productDesignSession.getCabinClassTicketFamilyJoinTable(flightSched.getAircraft().getAircraftId(), tixFam.getCabinClass().getCabinClassId(), tixFam.getTicketFamilyId());
            totalSeatQty = cabinClassTixFam.getSeatQty();
            for (FlightScheduleBookingClass fb : revMgmtSession.getFlightScheduleBookingClassJoinTablesOfTicketFamily(flightSched.getFlightScheduleId(), tixFam.getTicketFamilyId())) {
                totalSeatQty = totalSeatQty - fb.getSoldSeatQty();
            }
        } catch (Exception e) {
        }
        return totalSeatQty;
    }

//    private List<TicketFamily> addTixFams(List<TicketFamily> tixFams, FlightSchedule flightSched, CabinClass cabinClass) {
//        try {
//            List<TicketFamily> ticketFamilys = productDesignSession.getCabinClassTicketFamilysFromJoinTable(flightSched.getAircraft().getAircraftId(), cabinClass.getCabinClassId());
//            for (TicketFamily ticketFamily : ticketFamilys) {
//                if (tixFams.size() < 4) {
//                    tixFams.add(ticketFamily);
//                } else {
//                    break;
//                }
//            }
//        } catch (NoSuchTicketFamilyException e) {
//        }
//        return tixFams;
//    }
}
