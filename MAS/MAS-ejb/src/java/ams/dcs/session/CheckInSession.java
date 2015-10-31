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
    public List<FlightSchedule> getFSforCheckinByPassport(String passportNo) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, 48); // adds 48 hour
        Date checkInAlloweddate = cal.getTime(); // returns new date object, one hour in the future
        Date currentDate = new Date();
        List<FlightSchedule> flightSchedules = new ArrayList<>();

        Customer c = em.find(Customer.class, passportNo);
        Query q = em.createQuery("SELECT f FROM FlightSchedule f ORDER BY f.departDate ASC WHERE f.departDate BETWEEN :start AND :end AND f IN (SELECT b.flightSchedules FROM Booking b WHERE b IN (SELECT a.booking FROM Airticket a AND a.customer.passportNo =:pass))");
        q.setParameter("pass", passportNo);
        q.setParameter("start", currentDate);
        q.setParameter("end", checkInAlloweddate);
        try {
            flightSchedules = q.getResultList();
        } catch (Exception e) {
            return null;
        }
        return flightSchedules;
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
