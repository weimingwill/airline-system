/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.ais.entity.FlightScheduleBookingClass;
import ams.aps.entity.FlightSchedule;
import ams.aps.util.helper.FlightSchedStatus;
import ams.ars.entity.Booking;
import ams.crm.entity.MktCampaign;
import java.text.SimpleDateFormat;
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
 * @author Tongtong
 */
@Stateless
public class AnalyticsSession implements AnalyticsSessionLocal {

    @PersistenceContext(unitName = "MAS-ejbPU")
    private EntityManager em;

    @Override
    public int getTodayBookingNo() {
        long yourmilliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date date = new Date(yourmilliseconds);

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date);

        Query query = em.createQuery("SELECT b FROM Bookings b");
        List<Booking> bookings = new ArrayList<>();
        try {
            bookings = (List<Booking>) query.getResultList();
        } catch (NoResultException e) {
        }

        List<Booking> bookingsCopy = new ArrayList<>();
        bookingsCopy = bookings;

        for (Booking booking : bookings) {
            cal2.setTime(booking.getCreatedTime());
            boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
            if (sameDay == false) {
                bookingsCopy.remove(booking);
            }
        }

        return bookingsCopy.size();
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public double getTodaySales() {
        double sales = 0;
        long yourmilliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date date = new Date(yourmilliseconds);

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date);

        Query query = em.createQuery("SELECT b FROM Bookings b");
        List<Booking> bookings = new ArrayList<>();
        try {
            bookings = (List<Booking>) query.getResultList();
        } catch (NoResultException e) {
        }

        List<Booking> bookingsCopy = new ArrayList<>();
        bookingsCopy = bookings;

        for (Booking booking : bookings) {
            cal2.setTime(booking.getCreatedTime());
            boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
            if (sameDay == true) {
                sales = sales + booking.getPrice();
            }
        }
        return sales;
    }

//    @Override
//    public FlightSchedule getMostPopularFlightSchedule() {
//        Query query = em.createQuery("SELECT f FROM FlightScheduleBookingClass f WHERE f.closed = :false");
//        query.setParameter("false", false);
//        List<FlightScheduleBookingClass> fsbcs = new ArrayList<>();
//        FlightScheduleBookingClass fsbc = new FlightScheduleBookingClass();
//        fsbc.setSoldSeatQty(0);
//                
//        for(FlightScheduleBookingClass f:fsbcs){
//            if(f.getSoldSeatQty()>fsbc.getSoldSeatQty()){
//                fsbc = f;
//            }            
//        }
//        
//        return fsbc.getFlightSchedule();
//       
//    }
    @Override
    public double getCurrentMonthSales() {
        double sales = 0;
        long yourmilliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date date = new Date(yourmilliseconds);

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date);

        Query query = em.createQuery("SELECT b FROM Bookings b");
        List<Booking> bookings = new ArrayList<>();
        try {
            bookings = (List<Booking>) query.getResultList();
        } catch (NoResultException e) {
        }

        List<Booking> bookingsCopy = new ArrayList<>();
        bookingsCopy = bookings;

        for (Booking booking : bookings) {
            cal2.setTime(booking.getCreatedTime());
            boolean sameMonth = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
            if (sameMonth == true) {
                sales = sales + booking.getPrice();
            }
        }
        return sales;
    }

    @Override
    public double getCurrentYearSales() {
        double sales = 0;
        long yourmilliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date date = new Date(yourmilliseconds);

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date);

        Query query = em.createQuery("SELECT b FROM Bookings b");
        List<Booking> bookings = new ArrayList<>();
        try {
            bookings = (List<Booking>) query.getResultList();
        } catch (NoResultException e) {
        }

        List<Booking> bookingsCopy = new ArrayList<>();
        bookingsCopy = bookings;

        for (Booking booking : bookings) {
            cal2.setTime(booking.getCreatedTime());
            boolean sameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
            if (sameYear == true) {
                sales = sales + booking.getPrice();
            }
        }
        return sales;
    }

    @Override
    public double[] getMonthlySales() {
        long yourmilliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date date = new Date(yourmilliseconds);
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        Query query = em.createQuery("SELECT b FROM Bookings b");
        List<Booking> bookings = new ArrayList<>();
        try {
            bookings = (List<Booking>) query.getResultList();
        } catch (NoResultException e) {
        }
        List<Booking> bookingsCopy = new ArrayList<>();
        bookingsCopy = bookings;

        cal1.setTime(date);
        int monthNo = cal1.MONTH;
        double[] month = new double[12];
        for (int i = 0; i < monthNo; i++) {
            double sales = 0;

            for (Booking booking : bookings) {
                cal2.setTime(booking.getCreatedTime());
                boolean sameMonth = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                        && cal2.get(Calendar.MONTH) == (i + 1);
                if (sameMonth == true) {
                    sales = sales + booking.getPrice();
                }
            }
            month[i] = sales;
        }
        return month;
    }

    @Override
    public double[] getYearlySales() {
        long yourmilliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date date = new Date(yourmilliseconds);
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        Query query = em.createQuery("SELECT b FROM Bookings b");
        List<Booking> bookings = new ArrayList<>();
        try {
            bookings = (List<Booking>) query.getResultList();
        } catch (NoResultException e) {
        }
        List<Booking> bookingsCopy = new ArrayList<>();
        bookingsCopy = bookings;

        cal1.setTime(date);
        int year = cal1.YEAR;
        double[] Year_5 = new double[5];
        for (int i = 4; i >= 0; i--) {
            double sales = 0;

            for (Booking booking : bookings) {
                cal2.setTime(booking.getCreatedTime());
                boolean sameMonth = cal2.get(Calendar.YEAR) == (year - i);
                if (sameMonth == true) {
                    sales = sales + booking.getPrice();
                }
            }
            Year_5[i] = sales;
        }
        return Year_5;
    }

    @Override
    public int getTotalCampaignNo() {
        Query query = em.createQuery("SELECT m FROM MktCampaign m");
        List<MktCampaign> campaigns = new ArrayList<>();
        try {
            campaigns = (List<MktCampaign>) query.getResultList();
        } catch (NoResultException e) {
        }

        return campaigns.size();
    }

    @Override
    public int getOngoingCampaignNo() {
        Query query = em.createQuery("SELECT m FROM MktCampaign m");
        List<MktCampaign> campaigns = new ArrayList<>();
        try {
            campaigns = (List<MktCampaign>) query.getResultList();
        } catch (NoResultException e) {
        }

        long yourmilliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date date = new Date(yourmilliseconds);
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.setTime(date);
        List<MktCampaign> campaignsCopy = new ArrayList<>();

        for (MktCampaign mktCampaign : campaigns) {
            boolean ongoing = mktCampaign.getStartTime().before(date) && mktCampaign.getEndTime().after(date);
            if (ongoing != true) {
                campaignsCopy.remove(mktCampaign);
            }
        }
        return campaignsCopy.size();
    }

    @Override
    public List<MktCampaign> getAllOngoingCampaigns() {
        Query query = em.createQuery("SELECT m FROM MktCampaign m");
        List<MktCampaign> campaigns = new ArrayList<>();
        try {
            campaigns = (List<MktCampaign>) query.getResultList();
        } catch (NoResultException e) {
        }

        long yourmilliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date date = new Date(yourmilliseconds);
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.setTime(date);
        List<MktCampaign> campaignsCopy = new ArrayList<>();

        for (MktCampaign mktCampaign : campaigns) {
            boolean ongoing = mktCampaign.getStartTime().before(date) && mktCampaign.getEndTime().after(date);
            if (ongoing != true) {
                campaignsCopy.remove(mktCampaign);
            }
        }
        return campaignsCopy;
    }

    @Override
    public List<MktCampaign> getAllCampaigns() {
        Query query = em.createQuery("SELECT m FROM MktCampaign m");
        List<MktCampaign> campaigns = new ArrayList<>();
        try {
            campaigns = (List<MktCampaign>) query.getResultList();
        } catch (NoResultException e) {
        }
        return campaigns;
    }
}
