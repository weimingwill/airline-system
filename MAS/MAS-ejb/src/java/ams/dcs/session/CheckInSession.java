/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.dcs.session;

import ams.ars.entity.AirTicket;
import ams.ars.entity.Seat;
import ams.crm.entity.Customer;
import java.util.ArrayList;
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
    public List<AirTicket> getValidAirTicketsByCustomer(String passportNo) {
        Customer c = em.find(Customer.class, passportNo);
        Query q = em.createQuery("SELECT at FROM AirTicket at WHERE at.status = paid' AND IN (SELECT c FROM Customer c WHERE c.passportNo =:passport) ");
        q.setParameter("passport", passportNo);
        List<AirTicket> airTickets = new ArrayList<>();
        
        try {
            airTickets = q.getResultList();
            for(AirTicket at :airTickets){
                
            }
        } catch (Exception e) {
        }
        
        return c.getAirTickets();
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
    

}
