/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.BookingClass;
import ams.ais.entity.CabinClass;
import ams.ais.entity.FlightScheduleBookingClass;
import ams.ais.entity.NormalDistribution;
import ams.ais.entity.PhaseDemand;
import ams.ais.entity.SeatAllocationHistory;
import ams.ais.helper.FlightScheduleBookingClassId;
import ams.ais.util.exception.ExistSuchCheckPointException;
import ams.ais.util.exception.NeedBookingClassException;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchPhaseDemandException;
import ams.ais.util.helper.AisMsg;
import ams.ais.util.helper.FlightSchCabinClsTicFamBookingClsHelper;
import ams.aps.entity.City;
import ams.aps.entity.FlightSchedule;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import ams.aps.util.exception.NoSuchFlightScheduleBookingClassException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
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
    private FlightScheduleBookingClass fsbc;

    @Override
    public void addPhaseDemand(Long flightScheduleId, List<FlightSchCabinClsTicFamBookingClsHelper> helpers, int daysBeforeDeparture, float demandMean, float demandDev) throws ExistSuchCheckPointException {

        List<BookingClass> bookingClasses = null;
        try {
            bookingClasses = flightScheduleSession.getBookingClassesFromFlightSchCabinClsTicFamBookingClsHelpers(helpers);
        } catch (NeedBookingClassException ex) {
            Logger.getLogger(SeatReallocationSession.class.getName()).log(Level.SEVERE, null, ex);
        }

        long bookingClassId = bookingClasses.get(0).getBookingClassId();
        FlightScheduleBookingClassId fsbci = new FlightScheduleBookingClassId();
        fsbci.setBookingClassId(bookingClassId);
        fsbci.setFlightScheduleId(flightScheduleId);
        FlightScheduleBookingClass fsbc4 = new FlightScheduleBookingClass();
        fsbc4 = entityManager.find(FlightScheduleBookingClass.class, fsbci);
        List<PhaseDemand> pds = fsbc4.getPhaseDemands();
        for (PhaseDemand pdTemp : pds) {
            if (pdTemp.getDaysBeforeDeparture() == daysBeforeDeparture) {
                throw new ExistSuchCheckPointException(AisMsg.EXIST_SUCH_CHECK_POINT_ERROR);
            } else {
                PhaseDemand p = new PhaseDemand();
                p.setDaysBeforeDeparture(daysBeforeDeparture);
                p.setDemandMean(demandMean);
                p.setDemandDev(demandDev);
                pds.add(p);
                fsbc4.setPhaseDemands(pds);
                entityManager.persist(fsbc4);

            }
        }
    }

    @Override
    public void deletePhaseDemand(Long flightScheduleId, List<FlightSchCabinClsTicFamBookingClsHelper> helpers, int daysBeforeDeparture) throws NoSuchPhaseDemandException {
        List<BookingClass> bookingClasses = null;
        try {
            bookingClasses = flightScheduleSession.getBookingClassesFromFlightSchCabinClsTicFamBookingClsHelpers(helpers);
        } catch (NeedBookingClassException ex) {
            Logger.getLogger(SeatReallocationSession.class.getName()).log(Level.SEVERE, null, ex);
        }

        long bookingClassId = bookingClasses.get(0).getBookingClassId();
        FlightScheduleBookingClassId fsbci = new FlightScheduleBookingClassId();
        fsbci.setBookingClassId(bookingClassId);
        fsbci.setFlightScheduleId(flightScheduleId);
        FlightScheduleBookingClass fsbc3 = new FlightScheduleBookingClass();
        fsbc3 = entityManager.find(FlightScheduleBookingClass.class, fsbci);
        List<PhaseDemand> pds = fsbc3.getPhaseDemands();
        for (PhaseDemand pdtemp : pds) {
            if (pdtemp.getDaysBeforeDeparture() == daysBeforeDeparture) {
                pdtemp.setIsDeleted(true);
                pds.remove(pdtemp);
                entityManager.merge(pdtemp);

            }
        }

    }

//    @Override
//    public PhaseDemand getPhaseDemandByDays(Long flightScheduleId, List<FlightSchCabinClsTicFamBookingClsHelper> helpers, int daysBeforeDeparture) throws NoSuchPhaseDemandException {
//        Query query = entityManager.createQuery("SELECT p FROM PhaseDemand p WHERE p.daysBeforeDeparture = :inDaysBeforeDeparture and p.isDeleted = FALSE");
//        query.setParameter("inDaysBeforeDeparture", daysBeforeDeparture);
//        PhaseDemand phaseDemand = null;
//        try {
//            phaseDemand = (PhaseDemand) query.getSingleResult();
//        } catch (NoResultException ex) {
//            throw new NoSuchPhaseDemandException(AisMsg.NO_SUCH_PHASE_DEMAND_ERROR);
//        }
//        return phaseDemand;
//    }
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

    @Override
    public void reallocateSeatsforBookingClass(Long flightScheduleId, List<FlightSchCabinClsTicFamBookingClsHelper> helpers, float demandMean, float demandDev) throws NoSuchFlightSchedulException, NoSuchFlightScheduleBookingClassException, NeedBookingClassException {

        List<BookingClass> bookingClasses = flightScheduleSession.getBookingClassesFromFlightSchCabinClsTicFamBookingClsHelpers(helpers);
        long bookingClassId = bookingClasses.get(0).getBookingClassId();
        FlightScheduleBookingClassId fsbci = new FlightScheduleBookingClassId();
        fsbci.setBookingClassId(bookingClassId);
        fsbci.setFlightScheduleId(flightScheduleId);
        FlightScheduleBookingClass fsbc2 = new FlightScheduleBookingClass();
        fsbc2 = entityManager.find(FlightScheduleBookingClass.class, fsbci);
        reallocateBookingClassSeats(fsbc2, demandMean, demandDev);

    }

    @Override
    public List<SeatAllocationHistory> getBookingClassSeatAllocationHistory(Long flightScheduleId, List<FlightSchCabinClsTicFamBookingClsHelper> helpers) throws NoSuchFlightSchedulException, NoSuchFlightScheduleBookingClassException, NeedBookingClassException {
        List<BookingClass> bookingClasses = flightScheduleSession.getBookingClassesFromFlightSchCabinClsTicFamBookingClsHelpers(helpers);
        long bookingClassId = bookingClasses.get(0).getBookingClassId();
        FlightScheduleBookingClassId fsbci = new FlightScheduleBookingClassId();
        fsbci.setBookingClassId(bookingClassId);
        fsbci.setFlightScheduleId(flightScheduleId);
        FlightScheduleBookingClass fsbc0 = new FlightScheduleBookingClass();
        fsbc0 = entityManager.find(FlightScheduleBookingClass.class, fsbci);
        return fsbc0.getSeatAllocationHistory();
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
    public PhaseDemand getPhaseDemandbyId(Long id) {
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

    public class YieldMgtRunnable implements Runnable {

        private FlightScheduleBookingClass f;

        public YieldMgtRunnable(FlightScheduleBookingClass flightScheduleBookingClass) {
            this.f = flightScheduleBookingClass;
        }

        @Override
        public void run() {
            List<PhaseDemand> phaseDemands = f.getPhaseDemands();

            //sort phase demand in descending order according to DaysBeforeDeparture 
            Collections.sort(phaseDemands, new Comparator<PhaseDemand>() {
                public int compare(PhaseDemand pd1, PhaseDemand pd2) {
                    return pd2.getDaysBeforeDeparture() - pd1.getDaysBeforeDeparture();
                }
            });

            int checkPoints = phaseDemands.size();

            Date endDate = f.getFlightSchedule().getDepartDate();

            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.setTime(endDate);

            for (Date date = (Date) start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = (Date) start.getTime()) {
                int pdcount = 0;

                int daysBefore = phaseDemands.get(pdcount).getDaysBeforeDeparture();

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
