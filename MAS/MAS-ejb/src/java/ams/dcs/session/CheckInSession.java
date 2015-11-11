/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.dcs.session;

import ams.aps.entity.FlightSchedule;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import ams.ars.entity.AirTicket;
import ams.ars.entity.AirTicketPricingItem;
import ams.ars.entity.BoardingPass;
import ams.ars.entity.PricingItem;
import ams.crm.entity.Customer;
import ams.dcs.entity.CheckInLuggage;
import ams.dcs.entity.Luggage;
import ams.dcs.util.exception.BoardingErrorException;
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

    private final int WEIGHT_LEVEL_1 = 15;
    private final int WEIGHT_LEVEL_2 = 30;
    private final int LUGGAGE_PRICE_1 = 25;
    private final int LUGGAGE_PRICE_2 = 35;

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
    public boolean checkInLuggage(AirTicket airticket, List<CheckInLuggage> luggageList, Double price) {
        List<CheckInLuggage> lug = new ArrayList<>();
        try {
            for (CheckInLuggage c : luggageList) {
                em.persist(c);
                em.flush();
                lug.add(c);
            }

            PricingItem pricingItem = em.find(PricingItem.class, "Excess Luggage"); //Pre-defined name;

            AirTicketPricingItem atpi = new AirTicketPricingItem();
            atpi.setAirTicket(airticket);
            atpi.setPrice(price);
            atpi.setPricingItem(pricingItem);
            em.persist(atpi);
            em.flush();

            AirTicket at = em.find(AirTicket.class, airticket.getId());
            List<AirTicketPricingItem> atpiList = at.getAirTicketPricingItems();
            atpiList.add(atpi);
            at.setAirTicketPricingItems(atpiList);
            at.setLuggages(lug);
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
                    price += 0;
                }
            }
        }
        return price;
    }

    @Override
    public AirTicket getAirTicketByPassID(long passID) throws NoSuchBoardingPassException {
        Query q = em.createQuery("SELECT a FROM AirTicket a WHERE a.boardingPass.id =:pass");
        q.setParameter("pass", passID);
        AirTicket a = new AirTicket();

        try {
            a = (AirTicket) q.getSingleResult();
        } catch (NoResultException e) {
            throw new NoSuchBoardingPassException();
        }
        return a;
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
        
        try{
            a = em.find(AirTicket.class, ticketID);
        }catch(Exception e){
            throw new NoSuchPNRException();
        }
        return a;
    }
}
