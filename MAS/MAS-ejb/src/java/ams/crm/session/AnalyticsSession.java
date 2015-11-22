/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.ars.entity.Booking;
import ams.crm.entity.Customer;
import ams.crm.entity.MktCampaign;
import java.text.SimpleDateFormat;
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

/**
 *
 * @author Tongtong
 */
@Stateless
public class AnalyticsSession implements AnalyticsSessionLocal {

    @PersistenceContext(unitName = "MAS-ejbPU")
    private EntityManager em;

    @EJB
    private CustomerSessionLocal customerSession;

    @Override
    public int getTodayBookingNo() {
        long yourmilliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date date = new Date(yourmilliseconds);

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date);

        Query query = em.createQuery("SELECT b FROM Booking b");
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

        Query query = em.createQuery("SELECT b FROM Booking b");
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

        Query query = em.createQuery("SELECT b FROM Booking b");
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

        Query query = em.createQuery("SELECT b FROM Booking b");
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

        Query query = em.createQuery("SELECT b FROM Booking b");
        List<Booking> bookings = new ArrayList<>();
        try {
            bookings = (List<Booking>) query.getResultList();
        } catch (NoResultException e) {
        }

        cal1.setTime(date);
        double[] month = new double[12];
        for (int i = 0; i < 12; i++) {
            double sales = 0;

            for (Booking booking : bookings) {
                cal2.setTime(booking.getCreatedTime());
                boolean sameMonth = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal2.get(Calendar.MONTH) == i;
                if (sameMonth == true) {
                    sales = sales + booking.getPrice();
                }
            }
            System.out.println("Sales for month"+ (i+1)+"is"+sales);
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

        Query query = em.createQuery("SELECT b FROM Booking b");
        List<Booking> bookings = new ArrayList<>();
        try {
            bookings = (List<Booking>) query.getResultList();
        } catch (NoResultException e) {
        }

        cal1.setTime(date);
        int year = cal1.YEAR;
        double[] Year_5 = new double[5];
        for (int i = 4; i > 0; i--) {
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

    @Override
    public int[] getAllCustomerAges() {
        Query query = em.createQuery("SELECT c FROM Customer c");
        List<Customer> customers = new ArrayList<>();
        try {
            customers = (List<Customer>) query.getResultList();
        } catch (NoResultException e) {
        }

        int[] ages = new int[customers.size()];
        System.out.println("#ages are: " + ages.length);
        int[] count = new int[9];

        for (Customer c : customers) {
            int age = customerSession.getAge(c.getDob());
            if (age < 2) {
                count[0] = count[0] + 1;
            } else if (age >= 2 && age <= 11) {
                count[1] = count[1] + 1;
            } else if (age >= 12 && age <= 18) {
                count[2] = count[2] + 1;
            } else if (age >= 19 && age <= 24) {
                count[3] = count[3] + 1;
            } else if (age >= 25 && age <= 30) {
                count[4] = count[4] + 1;
            } else if (age >= 31 && age <= 40) {
                count[5] = count[5] + 1;
            } else if (age >= 41 && age <= 55) {
                count[6] = count[6] + 1;
            } else if (age >= 56 && age <= 65) {
                count[7] = count[7] + 1;
            } else if (age > 65) {
                count[8] = count[8] + 1;
            }

        }
        return count;
    }
}
