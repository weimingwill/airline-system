/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.dcs.session;

import ams.aps.entity.FlightSchedule;
import ams.ars.entity.AdditionalCharge;
import ams.ars.entity.AirTicket;
import ams.ars.entity.AirTicketAdditionalCharge;
import ams.ars.entity.BoardingPass;
import ams.ars.entity.Seat;
import ams.crm.entity.Customer;
import ams.dcs.entity.CheckInLuggage;
import ams.dcs.entity.Luggage;
import ams.dcs.util.exception.BoardingErrorException;
import ams.dcs.util.exception.FlightScheduleNotUpdatedException;
import ams.dcs.util.exception.LuggageNotRomovedException;
import ams.dcs.util.exception.NoSuchAirTicketException;
import ams.dcs.util.exception.NoSuchBoardingPassException;
import ams.dcs.util.exception.NoSuchPNRException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author ChuningLiu
 */
@Stateless
public class CheckInSession implements CheckInSessionLocal {

    @PersistenceContext
    EntityManager em;

    private final double WEIGHT_LEVEL_1 = 15.0;
    private final double WEIGHT_LEVEL_2 = 30.0;
    private final double LUGGAGE_PRICE_1 = 25.0;
    private final double LUGGAGE_PRICE_2 = 35.0;

    @Override
    public List<AirTicket> getFSforCheckin(String passport) {
        List<AirTicket> airTickets = new ArrayList();

        Query q = em.createQuery("SELECT a FROM AirTicket a WHERE a.customer.passportNo =:pass AND a.status =:ready ORDER BY a.flightSchedBookingClass.flightSchedule.departDate ASC");
        q.setParameter("pass", passport);
        q.setParameter("ready", "Booking confirmed");
        try {
            airTickets = q.getResultList();
        } catch (Exception e) {
            return null;
        }
        return airTickets;
    }

    @Override
    public boolean selectSeat(long ticketNo, Seat seat) {
        Query q = em.createQuery("SELECT s FROM AirTicket a, IN(a.flightSchedBookingClass.flightSchedule.flightSchedSeats) fss, IN(fss.seats) s WHERE a.id = :aid AND s.colNo = :col AND s.rowNo = :row");
        q.setParameter("aid", ticketNo);
        q.setParameter("row", seat.getRowNo());
        q.setParameter("col", seat.getColNo());
        
        try {
            Seat s = (Seat)q.getSingleResult();
            AirTicket a = em.find(AirTicket.class, ticketNo);

            s.setReserved(Boolean.TRUE);
            a.setSeat(s);

            em.merge(s);
            em.merge(a);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public BoardingPass checkInPassenger(AirTicket ticket) {

        try {
            AirTicket at = em.find(AirTicket.class, ticket.getId());

            BoardingPass bp = new BoardingPass();
            FlightSchedule f = at.getFlightSchedBookingClass().getFlightSchedule();

            Calendar cal = Calendar.getInstance();
            cal.setTime(f.getDepartDate());
            cal.add(Calendar.MINUTE, -30); // minus 0.5 hour
            Date boardingTime = cal.getTime();

            bp.setBoardingGate(f.getDepartGate());
            bp.setBoardingTime(boardingTime);

            em.persist(bp);
            at.setBoardingPass(bp);
            at.setStatus("Checked-in");
            em.merge(at);

            return bp;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean checkInLuggage(AirTicket airticket, List<CheckInLuggage> luggageList, Double price) {
        List<CheckInLuggage> lug = new ArrayList<>();
        try {
            for (CheckInLuggage c : luggageList) {
                em.persist(c);
                em.flush();
                lug.add(c);
            }
            
            Query q = em.createQuery("SELECT ac FROM AdditionalCharge ac WHERE ac.name = :name");
            q.setParameter("name", "Excessive Luggage");

            AdditionalCharge ac = (AdditionalCharge)q.getSingleResult();
            
            AirTicketAdditionalCharge atpi = new AirTicketAdditionalCharge();
            atpi.setAirTicket(airticket);
            atpi.setPrice(price);
            atpi.setAdditionalCharge(ac);
            em.persist(atpi);
            em.flush();

            AirTicket at = em.find(AirTicket.class, airticket.getId());
            List<AirTicketAdditionalCharge> atpiList = at.getAirTicketAdditionalCharges();
            atpiList.add(atpi);
            at.setAirTicketAdditionalCharges(atpiList);
            at.setLuggages(lug);
            em.merge(at);

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public Customer getCustomerByPassport(String passportNo) {
        Query q = em.createQuery("SELECT c FROM Customer c WHERE c.passportNo =:pass");
        q.setParameter("pass", passportNo);
        try {
            return (Customer) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Luggage> getCustomerLuggages(Customer cust, FlightSchedule schedule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Double calculateLuggagePrice(AirTicket airTicket, List<CheckInLuggage> luggageList) {
        Double price = 0.0;

        for (CheckInLuggage l : luggageList) {
            if (l.getRealWeight() > airTicket.getPurchasedLuggage().getMaxWeight()) {
                Double ex = l.getRealWeight() - airTicket.getPurchasedLuggage().getMaxWeight();
                if (ex <= WEIGHT_LEVEL_1) {
                    price += ex * LUGGAGE_PRICE_1;
                } else if (ex <= WEIGHT_LEVEL_2) {
                    price += LUGGAGE_PRICE_2 * (ex - WEIGHT_LEVEL_1) + LUGGAGE_PRICE_1 * WEIGHT_LEVEL_1;
                } else {
                    price += 0.0;
                }
            }
        }
        return price;
    }

    @Override
    public AirTicket getAirTicketByPassID(long passID) throws NoSuchBoardingPassException {
        Query q = em.createQuery("SELECT a FROM AirTicket a WHERE a.boardingPass.id =:pass");
        q.setParameter("pass", passID);

        try {
            return (AirTicket) q.getSingleResult();
        } catch (NoResultException e) {
            throw new NoSuchBoardingPassException();
        }
    }

    @Override
    public void boardPassenger(AirTicket airTicket) throws BoardingErrorException {
        try {
            AirTicket a = em.find(AirTicket.class, airTicket.getId());
            a.setStatus("Boarded");
            em.merge(a);
        } catch (Exception e) {
            throw new BoardingErrorException();
        }
    }

    @Override
    public AirTicket searchTicketByID(long ticketID) throws NoSuchPNRException {
        AirTicket a = new AirTicket();

        try {
            a = em.find(AirTicket.class, ticketID);
        } catch (Exception e) {
            throw new NoSuchPNRException();
        }
        return a;
    }

    @Override
    public List<Seat> getSeatsByTicket(AirTicket airTicket) {
        Query q = em.createQuery("SELECT s FROM AirTicket a, IN(a.flightSchedBookingClass.flightSchedule.flightSchedSeats) fss, IN(fss.seats) s WHERE a.id = :aid");
        q.setParameter("aid", airTicket.getId());
        try {
            return (List<Seat>) q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Integer> getRowList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getColList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Seat getSeatByID(long id) {
        Query q = em.createQuery("SELECT s FROM Seat s WHERE s.id =:seatID");
        q.setParameter("seatID", id);
        try {
            return (Seat) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<FlightSchedule> getFlightSchedulesForDeparture() {
        Date now = new Date();
        Date aDay = getADateLater();

        Query q = em.createQuery("SELECT f FROM FlightSchedule f WHERE f.departDate BETWEEN :begin AND :end");
        q.setParameter("begin", now);
        q.setParameter("end", aDay);

        try {
            return q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<FlightSchedule> getFlightSchedulesForArrival() {
        Date now = new Date();
        Date aDay = getADateLater();

        Query q = em.createQuery("SELECT f FROM FlightSchedule f WHERE f.arrivalDate BETWEEN :begin AND :end");
        q.setParameter("begin", now);
        q.setParameter("end", aDay);

        try {
            return q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    private Date getADateLater() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, 24); // add 24 hours
        return cal.getTime();
    }

    @Override
    public void updateFlightSchedule(FlightSchedule fs) throws FlightScheduleNotUpdatedException {
        try {
            FlightSchedule origin = em.find(FlightSchedule.class, fs.getFlightScheduleId());
            origin.setActualArrivalDate(fs.getActualArrivalDate());
            origin.setActualDepartDate(fs.getActualDepartDate());
            origin.setArrivalGate(fs.getArrivalGate());
            origin.setArrivalTerminal(fs.getArrivalTerminal());
            origin.setDepartGate(fs.getDepartGate());
            origin.setDepartTerminal(fs.getDepartTerminal());

            em.merge(origin);
        } catch (Exception e) {
            throw new FlightScheduleNotUpdatedException();
        }
    }

    @Override
    public AirTicket getAirTicketByID(long ticketID) throws NoSuchAirTicketException {
        Query q = em.createQuery("SELECT a FROM AirTicket a WHERE a.id = :tID");
        q.setParameter("tID", ticketID);
        try {
            return (AirTicket) q.getSingleResult();
        } catch (Exception e) {
            throw new NoSuchAirTicketException();
        }
    }

    @Override
    public void removeLuggage(AirTicket airTicket, List<CheckInLuggage> luggages) throws LuggageNotRomovedException {
        try {
            AirTicket a = em.find(AirTicket.class, airTicket.getId());
            List<CheckInLuggage> lugList = a.getLuggages();

            for (CheckInLuggage l : lugList) {
                if (luggages.contains(l)) {
                    lugList.remove(l);
                }
            }
            
            a.setLuggages(lugList);
            em.merge(a);
        } catch (Exception e) {
            throw new LuggageNotRomovedException();
        }
    }
}
