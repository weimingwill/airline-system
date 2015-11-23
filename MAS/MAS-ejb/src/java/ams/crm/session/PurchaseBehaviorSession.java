/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.ars.entity.AirTicket;
import ams.ars.entity.Booking;
import ams.crm.entity.Customer;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Tongtong
 */
@Stateless
public class PurchaseBehaviorSession implements PurchaseBehaviorSessionLocal {

    @PersistenceContext(unitName = "MAS-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public int getBizTravellerNo() {
        Query query = em.createQuery("SELECT b FROM Booking b");
        List<Booking> bookings = new ArrayList<>();
        try {
            bookings = (List<Booking>) query.getResultList();
        } catch (NoResultException e) {
        }

        int count = 0;

        for (Booking b : bookings) {
            List<AirTicket> ats = b.getAirTickets();
            for (AirTicket at : ats) {
                String cabinClass = at.getFlightSchedBookingClass().getBookingClass().getTicketFamily().getCabinClass().getName();
                if (cabinClass.equals("Business") || cabinClass.equals("Merlion Sky Palace (First)")) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public int getLeisureTravellerNo() {
        Query query = em.createQuery("SELECT b FROM Booking b");
        List<Booking> bookings = new ArrayList<>();
        try {
            bookings = (List<Booking>) query.getResultList();
        } catch (NoResultException e) {
        }

        int count = 0;

        for (Booking b : bookings) {
            List<AirTicket> ats = b.getAirTickets();
            for (AirTicket at : ats) {
                String cabinClass = at.getFlightSchedBookingClass().getBookingClass().getTicketFamily().getCabinClass().getName();
                if (cabinClass.equals("Economy") || cabinClass.equals("Premium Economy")) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public int getNewCustomerNo() {
        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.passportNo != NULL");
        List<Customer> customers = new ArrayList<>();
        try {
            customers = (List<Customer>) query.getResultList();
        } catch (NoResultException e) {
        }
        int count = 0;
        for (Customer c : customers) {
            if (c.getAirTickets().size() == 1) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int getReturningCustomerNo() {
        Query query = em.createQuery("SELECT c FROM Customer c");
        List<Customer> customers = new ArrayList<>();
        try {
            customers = (List<Customer>) query.getResultList();
        } catch (NoResultException e) {
        }
        int count = 0;
        for (Customer c : customers) {
            if (c.getAirTickets().size() > 1) {
                count++;
            }
        }
        return count;
    }

    @Override
    public double getAddonPurchaseRate() {
        Query query = em.createQuery("SELECT a FROM AirTicket a");
        List<AirTicket> airTickets = new ArrayList<>();
        try {
            airTickets = (List<AirTicket>) query.getResultList();
        } catch (NoResultException e) {
        }
        int count = 0;

        for (AirTicket a : airTickets) {
            if (a.getAddOns() != null) {
                count++;
            }
        }
        return count / airTickets.size();
    }

    @Override
    public int getArsBookingNo() {
        Query query = em.createQuery("SELECT b FROM Booking b");
        List<Booking> bs = new ArrayList<>();
        try {
            bs = (List<Booking>) query.getResultList();
        } catch (NoResultException e) {
        }
        int count = 0;

        for (Booking booking : bs) {
            if (booking.getChannel().equals("ARS")) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int getDdsBookingNo() {
        Query query = em.createQuery("SELECT b FROM Booking b");
        List<Booking> bs = new ArrayList<>();
        try {
            bs = (List<Booking>) query.getResultList();
        } catch (NoResultException e) {
        }
        int count = 0;

        for (Booking booking : bs) {
            if (booking.getChannel().equals("DDS")) {
                count++;
            }
        }
        return count;
    }
}
