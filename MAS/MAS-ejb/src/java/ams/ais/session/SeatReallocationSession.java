/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.FlightScheduleBookingClass;
import ams.ais.entity.NormalDistribution;
import ams.ais.entity.PhaseDemand;
import ams.ais.entity.SeatAllocationHistory;
import ams.ais.util.exception.ExistSuchCheckPointException;
import ams.ais.util.exception.NoSuchPhaseDemandException;
import ams.ais.util.helper.AisMsg;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mas.util.helper.SafeHelper;

/**
 *
 * @author Tongtong
 */
@Stateless
public class SeatReallocationSession implements SeatReallocationSessionLocal {

    @PersistenceContext
    private EntityManager entityManager;
    private FlightScheduleBookingClass fsbc;

    @Override
    public void addPhaseDemand(FlightScheduleBookingClass f, int daysBeforeDeparture, float demandMean, float demandDev) throws ExistSuchCheckPointException {

        validatePhaseDemand(daysBeforeDeparture, f);

        PhaseDemand p = new PhaseDemand();
        p.setDaysBeforeDeparture(Float.parseFloat("" + daysBeforeDeparture));
        p.setDemandMean(demandMean);
        p.setDemandDev(demandDev);
        List<PhaseDemand> phaseDemands = f.getPhaseDemands();
        phaseDemands.add(p);
        f.setPhaseDemands(phaseDemands);
    }

    @Override
    public void deletePhaseDemand(int daysBeforeDeparture) throws NoSuchPhaseDemandException {
        PhaseDemand phaseDemand = searchPhaseDemand(daysBeforeDeparture);

        if (phaseDemand == null) {
            throw new NoSuchPhaseDemandException(AisMsg.NO_SUCH_PHASE_DEMAND_ERROR);
        } else {
            phaseDemand.setIsDeleted(true);
            entityManager.merge(phaseDemand);
        }

    }

    @Override
    public PhaseDemand searchPhaseDemand(int daysBeforeDeparture) throws NoSuchPhaseDemandException {
        List<PhaseDemand> phaseDemands = getAllPhaseDemands();
        if (phaseDemands == null) {
            throw new NoSuchPhaseDemandException(AisMsg.NO_SUCH_PHASE_DEMAND_ERROR);
        } else {
            for (PhaseDemand phaseDemand : phaseDemands) {
                if (daysBeforeDeparture == phaseDemand.getDaysBeforeDeparture()) {
                    return phaseDemand;
                }
            }
            throw new NoSuchPhaseDemandException(AisMsg.NO_SUCH_PHASE_DEMAND_ERROR);
        }
    }

    @Override
    public void validatePhaseDemand(int daysBeforeDeparture, FlightScheduleBookingClass f) throws ExistSuchCheckPointException {
        List<PhaseDemand> phaseDemands = f.getPhaseDemands();
        for (PhaseDemand phaseDemand : SafeHelper.emptyIfNull(phaseDemands)) {
            if (phaseDemand.getDaysBeforeDeparture() == daysBeforeDeparture) {
                throw new ExistSuchCheckPointException(AisMsg.EXIST_SUCH_CHECK_POINT_ERROR);
            }
        }

    }

    @Override
    public List<FlightScheduleBookingClass> getAllFlightScheduleBookingClasses() {
        Query query = entityManager.createQuery("SELECT c FROM FlightScheduleBookingClass c");
        return query.getResultList();

    }

    @Override
    public FlightScheduleBookingClass getFlightScheduleBookingClassbyFlightScheduleIDandBookingClassID(Long flightScheduleID, Long bookingClassID) {
        Query query = entityManager.createQuery("SELECT c FROM FlightScheduleBookingClass c where c.bookingClassId = bookingClassId AND c.flightScheduleId = flightScheduleID");
        return (FlightScheduleBookingClass) query.getResultList();

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

    @Override
    public List<PhaseDemand> getAllPhaseDemands() {
        Query query = entityManager.createQuery("SELECT c FROM PhaseDemand c");
        return query.getResultList();
    }

    @Override
    public boolean reallocateBookingClassSeats(FlightScheduleBookingClass f, float newDemandMean, float newDemandDev) {
        fsbc = f;
        float price = fsbc.getPrice();
        List<FlightScheduleBookingClass> flightScheduleBookingClasses = getAllFlightScheduleBookingClasses();

        int sum = 0;
        for (FlightScheduleBookingClass flightScheduleBookingClassTemp : SafeHelper.emptyIfNull(flightScheduleBookingClasses)) {
            int count = 0;
            if (flightScheduleBookingClassTemp.getFlightScheduleBookingClassId().getFlightScheduleId() == fsbc.getFlightScheduleBookingClassId().getFlightScheduleId()) {
                if (flightScheduleBookingClassTemp.getFlightScheduleBookingClassId().getBookingClassId() != fsbc.getFlightScheduleBookingClassId().getBookingClassId()) {
                    if (flightScheduleBookingClassTemp.getPrice() < fsbc.getPrice()) {
                        float lowerClassPrice = flightScheduleBookingClassTemp.getPrice();
                        float p = lowerClassPrice / price;

                        int lowerClassSeatNo = flightScheduleBookingClassTemp.getSeatQty();
                        for (int i = 0; i <= lowerClassSeatNo; i++) {
                            float zScore = (i - newDemandMean) / newDemandDev;
                            if (zScore <= 0) {
                                float absZScore = zScore * -1;
                                List<NormalDistribution> normalDistributions = getAllNormalDistributions();
                                for (NormalDistribution normalDistribution : SafeHelper.emptyIfNull(normalDistributions)) {
                                    if (normalDistribution.getzScore() == absZScore) {
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
                                for (NormalDistribution normalDistribution : SafeHelper.emptyIfNull(normalDistributions)) {
                                    if (normalDistribution.getzScore() == zScore) {
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
        seatAllocationHistory.setSeatNoBefore(fsbc.getSeatQty());
        seatAllocationHistory.setSeatNoAfter(sum + fsbc.getSeatQty());
//        seatAllocationHistory.setFlightScheduleBookingClass(fsbc);

        //update the seat allocation history of a booking class
        List<SeatAllocationHistory> seatAllocationHistorys = fsbc.getSeatAllocationHistory();
        seatAllocationHistorys.add(seatAllocationHistory);
        fsbc.setSeatAllocationHistory(seatAllocationHistorys);
        fsbc.setSeatQty(sum + fsbc.getSeatQty());

        //determine the seat reallocation variance 
        //seatAllocationHistory.modified  
        //0: not modified
        //1: increase
        //2: decrease
//        if (seatAllocationHistory.getNumber() > seatAllocationHistory.getFlightScheduleBookingClass().getSeatQty()) {
//            seatAllocationHistory.setModified(2);
//        } else if (seatAllocationHistory.getNumber() < seatAllocationHistory.getFlightScheduleBookingClass().getSeatQty()) {
//            seatAllocationHistory.setModified(1);
//        } else {
//            seatAllocationHistory.setModified(0);
//        }
        return true;
    }

// reallocate seats for all exsiting booking classes 
    @Override
    public void yieldManagement() {
        int threadCount = 0;
        Thread[] threads;
        threads = new Thread[1000000];

        List<FlightScheduleBookingClass> flightScheduleBookingClasses = getAllFlightScheduleBookingClasses();
        for (FlightScheduleBookingClass flightScheduleBookingClass : SafeHelper.emptyIfNull(flightScheduleBookingClasses)) {

//            fsbc = flightScheduleBookingClass;
            YieldMgtRunnable y = new YieldMgtRunnable(flightScheduleBookingClass);

            threads[threadCount] = new Thread(y, "Thread" + threadCount);

            threads[threadCount].start();

            threadCount++;

        }

    }

    public class YieldMgtRunnable implements Runnable {

        private FlightScheduleBookingClass f;

        public YieldMgtRunnable(FlightScheduleBookingClass flightScheduleBookingClass) {
            this.f = flightScheduleBookingClass;
        }

        public void run() {
            List<PhaseDemand> phaseDemands = f.getPhaseDemands();

            //sort phase demand in descending order according to DaysBeforeDeparture 
            Collections.sort(phaseDemands, new Comparator<PhaseDemand>() {
                public int compare(PhaseDemand pd1, PhaseDemand pd2) {
                    return Integer.parseInt("" + (pd2.getDaysBeforeDeparture() - pd1.getDaysBeforeDeparture()));
                }
            });

            int checkPoints = phaseDemands.size();

            Date endDate = f.getFlightSchedule().getDepartDate();

            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.setTime(endDate);

            for (Date date = (Date) start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = (Date) start.getTime()) {
                int pdcount = 0;

                int daysBefore = Integer.parseInt("" + phaseDemands.get(pdcount).getDaysBeforeDeparture());
                Calendar checkPoint = new GregorianCalendar();
                checkPoint.setTime(endDate);
                checkPoint.add(Calendar.DAY_OF_YEAR, -daysBefore);

                if (date.equals(checkPoint.getTime())) {
                    float demandMean = phaseDemands.get(pdcount).getDemandMean();
                    float demandDev = phaseDemands.get(pdcount).getDemandDev();

                    reallocateBookingClassSeats(f, demandMean, demandDev);

                    if (pdcount < checkPoints) {
                        pdcount++;
                    }

                    break;
                }

            }
        }
    }

}
