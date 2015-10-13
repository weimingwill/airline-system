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
import ams.ais.util.helper.AisMsg;
import ams.ais.util.helper.FlightSchCabinClsTicFamBookingClsHelper;
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
    private EntityManager em;

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
            System.out.println("Sum before protecting from this booking class is: " + sum);

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
                System.out.println("Sum after protect from current booking class is: " + sum);
                System.out.println("Current count is: " + count);

                int currentSeatNo = flightScheduleBookingClassTemp.getSeatQty() - count;
                System.out.println("m1");

                //create seat reallocation history for lower-price booking class
                SeatAllocationHistory sah = new SeatAllocationHistory();
                System.out.println("m2");

                long yourmilliseconds = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
                Date date = new Date(yourmilliseconds);
                sah.setAllocateTime(date);
                System.out.println("m3");

                FlightScheduleBookingClass fsbc = entityManager.find(FlightScheduleBookingClass.class, flightScheduleBookingClassTemp.getFlightScheduleBookingClassId());

                sah.setSeatNoBefore(fsbc.getSeatQty());
                System.out.println("Original seat no for the lower-price booking class is: " + sah.getSeatNoBefore());

                System.out.println("FSBC ID:" + fsbc.getFlightSchedule().getFlightScheduleId() + ":" + fsbc.getBookingClass().getBookingClassId());
                System.out.println("Current SeatNo: " + currentSeatNo);
                fsbc.setSeatQty(currentSeatNo);
                System.out.println("m4");
                sah.setSeatNoAfter(fsbc.getSeatQty());
                System.out.println("m5");
                if (sah == null) {
                    System.out.println("the newly created seat allocation history is null");
                }

                entityManager.persist(sah);
                System.out.println("Current seat no for the lower-price booking class is: " + sah.getSeatNoAfter());

                List<SeatAllocationHistory> sahs = fsbc.getSeatAllocationHistory();
                System.out.println("m7");

                sahs.add(sah);
                System.out.println("Size of current history list: " + sahs.size());

                fsbc.setSeatAllocationHistory(sahs);
                System.out.println("Lower-price booking class's seat reallocation history list size is: " + fsbc.getSeatAllocationHistory().size());

                entityManager.merge(fsbc);
                System.out.println("m10");

                System.out.println("m11");

                System.out.println("The booking class's current seat no is: " + fsbc.getSeatQty());
                System.out.println("fsbc = " + fsbc);
                System.out.println("sah = " + sah);
            }

        }

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

        //update the seat allocation history of a booking class
        FlightScheduleBookingClass fsbc = entityManager.find(FlightScheduleBookingClass.class, f.getFlightScheduleBookingClassId());
        List<SeatAllocationHistory> seatAllocationHistorys = fsbc.getSeatAllocationHistory();
        seatAllocationHistorys.add(seatAllocationHistory);
        fsbc.setSeatAllocationHistory(seatAllocationHistorys);
        fsbc.setSeatQty(seatNoAfter);
        entityManager.merge(fsbc);

        System.out.println("#seat reallocation history:" + f.getSeatAllocationHistory().size());
    }

    // reallocate seats for all exsiting booking classes 
    @Override
    public void yieldManagement() {
        int threadCount = 0;
        Thread[] threads;
        threads = new Thread[1000000];

        List<FlightScheduleBookingClass> flightScheduleBookingClasses = getAllFlightScheduleBookingClasses();
        for (FlightScheduleBookingClass flightScheduleBookingClass : SafeHelper.emptyIfNull(flightScheduleBookingClasses)) {
            YieldMgtRunnable y = new YieldMgtRunnable(flightScheduleBookingClass);
            threads[threadCount] = new Thread(y, "Thread" + threadCount);
            threads[threadCount].start();
            threadCount++;
        }

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
                    return Integer.parseInt(""+(pd2.getDaysBeforeDeparture() - pd1.getDaysBeforeDeparture()));
                }
            });

            int checkPoints = phaseDemands.size();

            Date endDate = f.getFlightSchedule().getDepartDate();

            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.setTime(endDate);

            for (Date date = (Date) start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = (Date) start.getTime()) {
                int pdcount = 0;

                float daysBefore = phaseDemands.get(pdcount).getDaysBeforeDeparture();

                Calendar checkPoint = new GregorianCalendar();
                checkPoint.setTime(endDate);
                checkPoint.add(Calendar.DAY_OF_YEAR, Float.parseFloat(-daysBefore));

                if (date.equals(checkPoint.getTime())) {
                    float demandMean = phaseDemands.get(pdcount).getDemandMean();
                    float demandDev = phaseDemands.get(pdcount).getDemandDev();
                    List<FlightScheduleBookingClass> fsbcs = getAllFlightScheduleBookingClasses();
                    List<FlightScheduleBookingClass> restFlightScheduleBookingClasses = new ArrayList<>();
                    for (FlightScheduleBookingClass fsbc : fsbcs) {
                        if (fsbc.getFlightScheduleBookingClassId().getFlightScheduleId() == f.getFlightScheduleBookingClassId().getFlightScheduleId()) {
                            restFlightScheduleBookingClasses.add(fsbc);
                        }

                    }

                    restFlightScheduleBookingClasses.remove(f);

                    if (pdcount < checkPoints) {
                        pdcount++;
                    }

                    break;
                }

            }
        }
    }

}
