/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.BookingClass;
import ams.ais.entity.FlightScheduleBookingClass;
import ams.ais.entity.NormalDistribution;
import ams.ais.entity.SeatAllocationHistory;
import ams.aps.entity.FlightSchedule;
import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Tongtong
 */
@Stateless
public class SeatReallocationSession implements SeatReallocationSessionLocal {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void reallocateSeats() {
        List<FlightScheduleBookingClass> flightScheduleBookingClasses = getAllFlightScheduleBookingClasses();
        for (FlightScheduleBookingClass flightScheduleBookingClass : flightScheduleBookingClasses) {
            BookingClass bookingClass = flightScheduleBookingClass.getBookingClass();
            float demandMean = flightScheduleBookingClass.getDemandMean();
            float demandDev = flightScheduleBookingClass.getDemandDev();
            float price = flightScheduleBookingClass.getPrice();
            int sum = 0;
            for (FlightScheduleBookingClass flightScheduleBookingClassTemp : flightScheduleBookingClasses) {
                int count = 0;
                if (flightScheduleBookingClassTemp.getFlightScheduleId() == flightScheduleBookingClass.getBookingClassId()) {
                    if (flightScheduleBookingClassTemp.getBookingClassId() != flightScheduleBookingClass.getBookingClassId()) {
                        if (flightScheduleBookingClassTemp.getPrice() < flightScheduleBookingClass.getPrice()) {
                            float lowerClassPrice = flightScheduleBookingClassTemp.getPrice();
                            float p = lowerClassPrice / price;

                            int lowerClassSeatNo = flightScheduleBookingClassTemp.getSeatQty();
                            for (int i = 0; i <= lowerClassSeatNo; i++) {
                                float zScore = (i - demandMean) / demandDev;
                                if (zScore <= 0) {
                                    float absZScore = zScore * -1;
                                    List<NormalDistribution> normalDistributions = getAllNormalDistributions();
                                    for (NormalDistribution normalDistribution : normalDistributions) {
                                        if (normalDistribution.getZscore() == absZScore) {
                                            float pI = 1 - normalDistribution.getP();
                                            if (pI >= p) {
                                                if (i > count) {
                                                    count = i;
                                                }
                                            }
                                        }
                                    }

                                } else {
                                    List<NormalDistribution> normalDistributions = getAllNormalDistributions();
                                    for (NormalDistribution normalDistribution : normalDistributions) {
                                        if (normalDistribution.getZscore() == zScore) {
                                            float pI = normalDistribution.getP();
                                            if (pI >= p) {
                                                if (i > count) {
                                                    count = i;
                                                }
                                            }
                                        }
                                    }

                                }

                            }

                            sum = sum + count;

                            flightScheduleBookingClassTemp.setSeatQty(flightScheduleBookingClassTemp.getSeatQty() - count);
                        }
                    }

                }

            }

            //generate a seat reallocation record for each booking class
            SeatAllocationHistory seatAllocationHistory = new SeatAllocationHistory();
            long yourmilliseconds = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
            Date date = new Date(yourmilliseconds);
            System.out.println(sdf.format(date));
            seatAllocationHistory.setAllocateTime(date);
            seatAllocationHistory.setNumber(flightScheduleBookingClass.getSeatQty());
            seatAllocationHistory.setFlightScheduleBookingClass(flightScheduleBookingClass);

            //update the seat allocation history of a booking class
            List<SeatAllocationHistory> seatAllocationHistorys = flightScheduleBookingClass.getSeatAllocationHistory();
            seatAllocationHistorys.add(seatAllocationHistory);
            flightScheduleBookingClass.setSeatAllocationHistory(seatAllocationHistorys);
            flightScheduleBookingClass.setSeatQty(sum + flightScheduleBookingClass.getSeatQty());

            //determine the seat reallocation variance 
            //seatAllocationHistory.modified  
            //0: not modified
            //1: increase
            //2: decrease
            if (seatAllocationHistory.getNumber() > seatAllocationHistory.getFlightScheduleBookingClass().getSeatQty()) {
                seatAllocationHistory.setModified(2);
            } else if (seatAllocationHistory.getNumber() < seatAllocationHistory.getFlightScheduleBookingClass().getSeatQty()) {
                seatAllocationHistory.setModified(1);
            } else {
                seatAllocationHistory.setModified(0);
            }

        }
    }

    @Override
    public void yieldManagement(List<Integer> checkpoints) {
        List<FlightScheduleBookingClass> flightScheduleBookingClasses = getAllFlightScheduleBookingClasses();
        for (FlightScheduleBookingClass flightScheduleBookingClass : flightScheduleBookingClasses) {
            FlightSchedule flightSchedule = flightScheduleBookingClass.getFlightSchedule();
            Date date = flightSchedule.getDepartDate();
            Calendar cal = null;
            cal.setTime(date);

            Collections.sort(checkpoints);
            Collections.reverse(checkpoints);
            int i = checkpoints.size();
            for (int a = 0; a <= i; a++) {
                int daysBefore = checkpoints.get(a);
                int departure_day_of_the_year = cal.DAY_OF_YEAR;
                if (daysBefore >= departure_day_of_the_year) {
                    if ((cal.YEAR - 1) % 4 == 0) {
                        cal.set(cal.YEAR, cal.YEAR - 1);
                        int days_changed = (366 + cal.DAY_OF_YEAR) - daysBefore;
                        cal.set(cal.DAY_OF_YEAR, days_changed);
                    }
                    else {
                        cal.set(cal.YEAR, cal.YEAR - 1);
                        int days_changed = (365 + cal.DAY_OF_YEAR) - daysBefore;
                        cal.set(cal.DAY_OF_YEAR, days_changed);     
                    }
                }
                else{
                    cal.set(cal.DAY_OF_YEAR,(cal.DAY_OF_YEAR - daysBefore));
                }

                //the Date and time at which you want to execute
                DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date checkDate = (Date) cal.getTime();
                
                //Now create the time and schedule it
                Timer timer = new Timer();

                //Use this if you want to execute it once
                timer.schedule(new MyTimeTask(), checkDate);

            }

        }

    }

    @Override
    public List<FlightScheduleBookingClass> getAllFlightScheduleBookingClasses() {
        Query query = entityManager.createQuery("SELECT c FROM FlightScheduleBookingClass c");
        return query.getResultList();

    }

    @Override
    public List<NormalDistribution> getAllNormalDistributions() {
        Query query = entityManager.createQuery("SELECT c FROM NormalDistribution c");
        return query.getResultList();
    }

    @Override
    public List<SeatAllocationHistory> getAllReallocationHitorys() {
        Query query = entityManager.createQuery("SELECT c FROM SeatAllocationHistory c");
        return query.getResultList();

    }

}
