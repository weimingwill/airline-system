/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.dcs.session;

import ams.ais.entity.TicketFamilyRule;
import ams.aps.entity.FlightSchedule;
import ams.ars.entity.AirTicket;
import ams.ars.entity.BoardingPass;
import ams.ars.entity.Seat;
import ams.crm.entity.Customer;
import ams.dcs.entity.CheckInLuggage;
import ams.dcs.entity.Luggage;
import com.sun.xml.wss.util.DateUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
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

    @Override
    public List<AirTicket> getFSforCheckin(String passport) {
        List<AirTicket> airTickets = new ArrayList<>();

        Query q = em.createQuery("SELECT a FROM Airticket a ORDER BY a.flightSchedBookingClass.flightSchedule.departureDate ASC WHERE a.customer.passportNo =:pass AND a.status =:ready");
        q.setParameter("pass", passport);
        q.setParameter("ready", "Booking confirmed");
        try {
            airTickets = q.getResultList();
        } catch (Exception e) {
            return null;
        }
        return airTickets;
    }

    public String selectSeat(String ticketNo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.        at.g
    }

    @Override
    public boolean checkInPassenger(AirTicket ticket) {

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

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean checkInuggage(ArrayList<AirTicket> airtickets, ArrayList<CheckInLuggage> luggageList) {
        List<CheckInLuggage> lug = new ArrayList<>();
        try {
            for (CheckInLuggage c : luggageList) {
                em.persist(c);
                em.flush();
                lug.add(c);
            }

            for (AirTicket a : airtickets) {
                AirTicket at = em.find(AirTicket.class, a.getId());
                at.setLuggages(lug);
            }
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
        em.createQuery("SELECT l FROM Luggage l WHERE ");
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
