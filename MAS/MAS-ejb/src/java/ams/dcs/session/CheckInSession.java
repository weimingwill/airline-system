/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.dcs.session;

import ams.aps.entity.FlightSchedule;
import ams.ars.entity.AirTicket;
import ams.ars.entity.Seat;
import ams.crm.entity.Customer;
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

        Query q = em.createQuery("SELECT a FROM Airticket a WHERE a.customer.passportNo =:pass AND a.status =:ready))");
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
    public boolean checkInPassenger(String ticketNo) {

        try {
            AirTicket at = em.find(AirTicket.class, ticketNo);
            Seat seat = at.getSeat();
            String seatNo = "";

            if (seat != null) {
                seatNo = seat.getRowNo().toString() + seat.getColNo();;
            } else {
                seatNo = selectSeat(ticketNo);
            }

            at.setStatus("Checked-in");
            em.merge(at);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean checkInuggage(String ticketNo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
