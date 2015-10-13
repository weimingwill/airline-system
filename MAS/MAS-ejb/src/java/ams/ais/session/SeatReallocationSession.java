/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.BookingClass;
import ams.ais.entity.FlightScheduleBookingClass;
import ams.ais.entity.NormalDistribution;
import ams.ais.entity.PhaseDemand;
import ams.ais.entity.SeatAllocationHistory;
import ams.ais.helper.FlightScheduleBookingClassId;
import ams.ais.util.exception.ExistSuchCheckPointException;
import ams.ais.util.exception.NeedBookingClassException;
import ams.ais.util.exception.NoSuchPhaseDemandException;
import ams.ais.util.exception.TimeToReallocateException;
import ams.ais.util.helper.AisMsg;
import ams.ais.util.helper.FlightSchCabinClsTicFamBookingClsHelper;
import ams.aps.entity.FlightSchedule;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import ams.aps.util.exception.NoSuchFlightScheduleBookingClassException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mas.util.helper.SafeHelper;

/**
 *
 * @author Tongtong
 */
@Stateless
public class SeatReallocationSession implements SeatReallocationSessionLocal {

    @EJB
    FlightScheduleSessionLocal flightScheduleSession;

    @PersistenceContext
    private EntityManager entityManager;

    private FlightSchedule flightSchedule;
    private List<FlightScheduleBookingClass> fsbcs;
    private Date departureDate;
    private Long flightScheduleID;

    @Override
    public void addPhaseDemand(FlightScheduleBookingClass flightScheduleBookingClass, float daysBeforeDeparture, float demandMean, float demandDev) throws ExistSuchCheckPointException {

        List<PhaseDemand> pds = new ArrayList<>();
        pds = flightScheduleBookingClass.getPhaseDemands();

        for (PhaseDemand p : pds) {
            if (p.getDaysBeforeDeparture() == daysBeforeDeparture) {
                throw new ExistSuchCheckPointException(AisMsg.EXIST_SUCH_CHECK_POINT_ERROR);
            }
        }

        PhaseDemand pd = new PhaseDemand();
        pd.setDaysBeforeDeparture(daysBeforeDeparture);
        pd.setDemandMean(demandMean);
        pd.setDemandDev(demandDev);

        pds.add(pd);
        flightScheduleBookingClass.setPhaseDemands(pds);
        entityManager.merge(flightScheduleBookingClass);
    }

    @Override
    public void deletePhaseDemand(FlightScheduleBookingClass flightScheduleBookingClass, PhaseDemand phaseDemand) throws NoSuchPhaseDemandException {
        List<PhaseDemand> phaseDemands = new ArrayList<>();
        phaseDemands = flightScheduleBookingClass.getPhaseDemands();
        phaseDemands.remove(phaseDemand);
        flightScheduleBookingClass.setPhaseDemands(phaseDemands);
        entityManager.merge(flightScheduleBookingClass);

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

    public void reallocateBookingClassSeats(List<FlightScheduleBookingClass> restFlightScheduleBookingClasses, FlightScheduleBookingClass f, float newDemandMean, float newDemandDev)
            throws NoSuchFlightScheduleBookingClassException {
        float price = f.getPrice();
        System.out.println(price);

        int sum = 0;
        for (FlightScheduleBookingClass flightScheduleBookingClassTemp : restFlightScheduleBookingClasses) {

            int count = 0;
            if (flightScheduleBookingClassTemp.getPrice() < price) {

                float p = flightScheduleBookingClassTemp.getPrice() / price;

                int lowerClassSeatNo = flightScheduleBookingClassTemp.getSeatQty();
                System.out.println("The booking class with lower price has " + lowerClassSeatNo + " seats.");

                for (int i = 0; i <= lowerClassSeatNo; i++) {
                    float zScore = (i - newDemandMean) / newDemandDev;
                    int zScoreTemp = (int) (zScore * 100);
                    zScore = zScoreTemp / 100;
                    System.out.println("The z score is:" + zScore);

                    if (zScore <= 0) {

                        float absZScore = zScore * -1;
                        List<NormalDistribution> normalDistributions = getAllNormalDistributions();
                        for (NormalDistribution normalDistribution : SafeHelper.emptyIfNull(normalDistributions)) {
                            if (normalDistribution.getzScore() == absZScore) {
                                float pI = normalDistribution.getP();

//                                float pI = 1 - normalDistribution.getP();
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
                                float pI = 1 - normalDistribution.getP();

//                                float pI = normalDistribution.getP();
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

                int currentSeatNo = flightScheduleBookingClassTemp.getSeatQty() - count;

                //create seat reallocation history for lower-price booking class
                SeatAllocationHistory sah = new SeatAllocationHistory();

                long yourmilliseconds = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
                Date date = new Date(yourmilliseconds);
                sah.setAllocateTime(date);

                FlightScheduleBookingClass fsbc = entityManager.find(FlightScheduleBookingClass.class, flightScheduleBookingClassTemp.getFlightScheduleBookingClassId());

                sah.setSeatNoBefore(fsbc.getSeatQty());

                fsbc.setSeatQty(currentSeatNo);
                sah.setSeatNoAfter(fsbc.getSeatQty());

                entityManager.persist(sah);

                List<SeatAllocationHistory> sahs = fsbc.getSeatAllocationHistory();

                sahs.add(sah);

                fsbc.setSeatAllocationHistory(sahs);

                entityManager.merge(fsbc);
            }
        }

//                if (count > f.getSeatQty()) {
//                    diff = count - f.getSeatQty();
//                    int currentSeatNo = flightScheduleBookingClassTemp.getSeatQty() - diff;
//                    if (currentSeatNo < 0) {
//                        currentSeatNo = 0;
//                        diff = flightScheduleBookingClassTemp.getSeatQty();
//                    }
//                    
//                    SeatAllocationHistory sah = new SeatAllocationHistory();
//                    long yourmilliseconds = System.currentTimeMillis();
//                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
//                    Date date = new Date(yourmilliseconds);
//                    sah.setAllocateTime(date);
//                    FlightScheduleBookingClass fsbc = entityManager.find(FlightScheduleBookingClass.class, flightScheduleBookingClassTemp.getFlightScheduleBookingClassId());
//                    sah.setSeatNoBefore(fsbc.getSeatQty());
//
//                    fsbc.setSeatQty(currentSeatNo);
//                    sah.setSeatNoAfter(fsbc.getSeatQty());
//
//                    entityManager.persist(sah);
//
//                    List<SeatAllocationHistory> sahs = fsbc.getSeatAllocationHistory();
//
//                    sahs.add(sah);
//
//                    fsbc.setSeatAllocationHistory(sahs);
//
//                    entityManager.merge(fsbc);
//                }
//
//            }
//
//        }
//
            entityManager.flush();

            int seatNoAfter = f.getSeatQty() + sum;
            System.out.println("seatNoAfter is: " + seatNoAfter);
            int seatNoBefore = f.getSeatQty();
            System.out.println("SeatNoBefore is: " + seatNoBefore);
            createSeatReallocationRecord(seatNoBefore, seatNoAfter, f);

        }

    

    

    private void createSeatReallocationRecord(int seatNoBefore, int seatNoAfter, FlightScheduleBookingClass f) {

        //generate a seat reallocation record
        SeatAllocationHistory seatAllocationHistory = new SeatAllocationHistory();
        System.out.println("seat reallocation histroy is created");
        long yourmilliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date date = new Date(yourmilliseconds);
        System.out.println(sdf.format(date));
        seatAllocationHistory.setAllocateTime(date);
        seatAllocationHistory.setSeatNoBefore(seatNoBefore);
        seatAllocationHistory.setSeatNoAfter(seatNoAfter);
        entityManager.persist(seatAllocationHistory);
        System.out.println("seat reallocation histroy content is set");
        System.out.println("Seat reallocation history creation date is: " + seatAllocationHistory.getAllocateTime());

        //update the seat allocation history of a booking class
        FlightScheduleBookingClass fsbc = entityManager.find(FlightScheduleBookingClass.class, f.getFlightScheduleBookingClassId());
        List<SeatAllocationHistory> seatAllocationHistorys = fsbc.getSeatAllocationHistory();
        seatAllocationHistorys.add(seatAllocationHistory);
        fsbc.setSeatAllocationHistory(seatAllocationHistorys);
        fsbc.setSeatQty(seatNoAfter);
        entityManager.merge(fsbc);

        System.out.println("#seat reallocation history:" + f.getSeatAllocationHistory().size());

    }

    @Override
    public void reallocateSeatsforBookingClass(FlightScheduleBookingClass fsbc, float demandMean, float demandDev)
            throws NoSuchFlightScheduleBookingClassException {

        List<FlightScheduleBookingClass> fsbcs
                = flightScheduleSession.getFlightScheduleBookingClassJoinTablesOfTicketFamily(fsbc.getFlightSchedule().getFlightScheduleId(), fsbc.getBookingClass().getTicketFamily().getTicketFamilyId());
        fsbcs.remove(fsbc);
        List<FlightScheduleBookingClass> restFlightScheduleBookingClasses = fsbcs;

        System.out.println("There are " + restFlightScheduleBookingClasses.size() + " more flight schedule booking class for the schedule flight.");

        reallocateBookingClassSeats(restFlightScheduleBookingClasses, fsbc, demandMean, demandDev);

    }

    @Override
    public List<SeatAllocationHistory> getBookingClassSeatAllocationHistory(FlightScheduleBookingClass flightScheduleBookingClass
    ) {
        System.out.println("The flight schedule booking class passed in to view reallocation history:" + flightScheduleBookingClass.getBookingClass().getName());
        return flightScheduleBookingClass.getSeatAllocationHistory();
    }

    @Override
    public List<PhaseDemand> getPhaseDemands(Long flightScheduleId, List<FlightSchCabinClsTicFamBookingClsHelper> helpers) throws NoSuchFlightSchedulException, NoSuchFlightScheduleBookingClassException, NeedBookingClassException {
        List<BookingClass> bookingClasses = flightScheduleSession.getBookingClassesFromFlightSchCabinClsTicFamBookingClsHelpers(helpers);
        long bookingClassId = bookingClasses.get(0).getBookingClassId();
        FlightScheduleBookingClassId fsbci = new FlightScheduleBookingClassId();
        fsbci.setBookingClassId(bookingClassId);
        fsbci.setFlightScheduleId(flightScheduleId);
        FlightScheduleBookingClass fsbc1 = new FlightScheduleBookingClass();
        fsbc1 = entityManager.find(FlightScheduleBookingClass.class, fsbci);
        return fsbc1.getPhaseDemands();
    }

    @Override
    public PhaseDemand getPhaseDemandbyId(Long id
    ) {
        Query query = entityManager.createQuery("SELECT p from PhaseDemand p where p.id =:inID");
        query.setParameter("inID", id);
        PhaseDemand pd = null;
        try {
            pd = (PhaseDemand) query.getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
        }
        return pd;
    }

    @Override
    public List<PhaseDemand> getAllPhaseDemands() {
        Query query = entityManager.createQuery("SELECT p FROM PhaseDemand p");
        return query.getResultList();

    }

// reallocate seats for all exsiting booking classes 
    @Override
    public List<Date> yieldManagement(Long flightScheduleId) {
        flightScheduleID = flightScheduleId;

        try {
            flightSchedule = flightScheduleSession.getFlightScheduleById(flightScheduleId);
        } catch (NoSuchFlightSchedulException ex) {
            System.out.println(ex.getMessage());
        }

        try {
            fsbcs = flightScheduleSession.getFlightScheduleBookingClassJoinTables(flightScheduleId);
        } catch (NoSuchFlightScheduleBookingClassException ex) {
            System.out.println(ex.getMessage());
        }

        departureDate = flightSchedule.getDepartDate();

        long departureDateInMs = departureDate.getTime();

        List<Float> allCheckPoints = new ArrayList<>();
        for (FlightScheduleBookingClass f : fsbcs) {
            List<PhaseDemand> phaseDemands = f.getPhaseDemands();
            for (PhaseDemand p : phaseDemands) {
                if (!allCheckPoints.contains(p.getDaysBeforeDeparture())) {
                    allCheckPoints.add(p.getDaysBeforeDeparture());
                }

            }

        }

        List<Date> allCheckDates = new ArrayList<>();

        for (Float f : allCheckPoints) {
            Long temp = f.longValue();
            temp = temp * 86400000;
            Date d = new Date(departureDateInMs - temp);
            allCheckDates.add(d);
        }

        return allCheckDates;

    }
}

//        Timer[] timers = new Timer[allCheckDates.size()];
//        int count = 0;
//        for (Date date : allCheckDates) {
//            timers[count] = new Timer();
//            timers[count].schedule(new YieldMgtTask(), date);
//
//        }
//
//    public class YieldMgtTask extends TimerTask {
//
//        @Override
//        public void run() {
//
//            try {
//                throw new TimeToReallocateException("It's time to perform yiled management for FlightSchedule " + flightScheduleID);
//            } catch (TimeToReallocateException ex) {
//                
//            }
//
//        }
//
//    }
//
//}
//        try {
//            flightSchedule = flightScheduleSession.getFlightScheduleById(flightScheduleId);
//        } catch (NoSuchFlightSchedulException ex) {
//            System.out.println(ex.getMessage());
//        }
//
//        try {
//            fsbcs = flightScheduleSession.getFlightScheduleBookingClassJoinTables(flightScheduleId);
//        } catch (NoSuchFlightScheduleBookingClassException ex) {
//            System.out.println(ex.getMessage());
//        }
//
//        departureDate = flightSchedule.getDepartDate();
//
//        Timer timer = new Timer();
//
//        Date date = new Date();
//
//        timer.scheduleAtFixedRate(new ReallocationTask(), date, TimeUnit.DAYS.toMillis(1));
//
//        return "";
//
//    }
//
//    class ReallocationTask extends TimerTask {
//
//        public void run() {
//            Date currentDate = new Date();
//            long diff = Math.abs(currentDate.getTime() - departureDate.getTime());
//
//            if (diff == 0) {
//                
//            } else {
//                for (FlightScheduleBookingClass f : fsbcs) {
//                    List<PhaseDemand> phaseDemands = f.getPhaseDemands();
//                    for (PhaseDemand p : phaseDemands) {
//                        if (p.getDaysBeforeDeparture() == diff) {
//
//                        }
//                    }
//
//                }
//
//            }
////            long diffDays = diff / (24 * 60 * 60 * 1000);
//
//        }
//    }
//        Old YiledManagement() function!!!!!!!!!!!!!!!!!!!!
//        System.out.println("Yield management is started!");
//        int threadCount = 0;
//        Thread[] threads;
//        threads = new Thread[1000000];
//
//        List<FlightScheduleBookingClass> fsbcs = new ArrayList<>();
//        try {
//            fsbcs = flightScheduleSession.getFlightScheduleBookingClassJoinTables(flightScheduleId);
//
//        } catch (NoSuchFlightScheduleBookingClassException ex) {
//
//        }
//
//        for (FlightScheduleBookingClass flightScheduleBookingClass : SafeHelper.emptyIfNull(fsbcs)) {
//
//            YieldMgtRunnable y = new YieldMgtRunnable(flightScheduleBookingClass);
//
//            threads[threadCount] = new Thread(y, "Thread" + threadCount);
//
//            threads[threadCount].start();
//
//            threadCount++;
//
//        }
//    public class YieldMgtRunnable implements Runnable {
//
//        private FlightScheduleBookingClass f;
//
//        public YieldMgtRunnable(FlightScheduleBookingClass flightScheduleBookingClass) {
//            this.f = flightScheduleBookingClass;
//        }
//
//        @Override
//        public void run() {
//            System.out.println("Yield mgt runnable is running!");
//            List<PhaseDemand> phaseDemands = f.getPhaseDemands();
//
//            //sort phase demand in descending order according to DaysBeforeDeparture 
//            Collections.sort(phaseDemands, new Comparator<PhaseDemand>() {
//                public int compare(PhaseDemand pd1, PhaseDemand pd2) {
//                    return Float.compare(pd1.getDaysBeforeDeparture(), pd2.getDaysBeforeDeparture());
//
//                }
//            });
//
//            int checkPoints = phaseDemands.size();
//
//            Date endDate = f.getFlightSchedule().getDepartDate();
//
//            Calendar start = Calendar.getInstance();
//            Calendar end = Calendar.getInstance();
//            end.setTime(endDate);
//
//            for (Date date = (Date) start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = (Date) start.getTime()) {
//                System.out.println();
//                int pdcount = 0;
//
//                float daysBefore = phaseDemands.get(pdcount).getDaysBeforeDeparture();
//
//                Calendar checkPoint = new GregorianCalendar();
//                checkPoint.setTime(endDate);
//                checkPoint.add(Calendar.DAY_OF_YEAR, Math.round(-daysBefore));
//
//                if (date.equals(checkPoint.getTime())) {
//                    float demandMean = phaseDemands.get(pdcount).getDemandMean();
//                    float demandDev = phaseDemands.get(pdcount).getDemandDev();
//                    List<FlightScheduleBookingClass> fsbcs = getAllFlightScheduleBookingClasses();
//                    List<FlightScheduleBookingClass> restFlightScheduleBookingClasses = new ArrayList<>();
//                    for (FlightScheduleBookingClass fsbc : fsbcs) {
//                        if (fsbc.getFlightScheduleBookingClassId().getFlightScheduleId() == f.getFlightScheduleBookingClassId().getFlightScheduleId()) {
//                            restFlightScheduleBookingClasses.add(fsbc);
//                        }
//
//                    }
//
//                    restFlightScheduleBookingClasses.remove(f);
//
//                    try {
//                        reallocateSeatsforBookingClass(f, demandMean, demandDev);
//                    } catch (NoSuchFlightScheduleBookingClassException ex) {
//                    }
//
////                    reallocateBookingClassSeats(restFlightScheduleBookingClasses, f, demandMean, demandDev);
//                    if (pdcount < checkPoints) {
//                        pdcount++;
//                    }
//
//                    break;
//                }
//
//            }
//        }
//    }

