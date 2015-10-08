/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.BookingClass;
import ams.ais.entity.CabinClass;
import ams.ais.entity.CabinClassTicketFamily;
import ams.ais.entity.FlightScheduleBookingClass;
import ams.ais.entity.TicketFamily;
import ams.ais.helper.FlightScheduleBookingClassId;
import ams.ais.util.exception.NeedBookingClassException;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.helper.AisMsg;
import ams.ais.util.helper.FlightSchCabinClsTicFamBookingClsHelper;
import ams.ais.util.helper.SeatClassHelper;
import ams.ais.util.helper.TicketFamilyBookingClassHelper;
import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftCabinClass;
import ams.aps.entity.FlightSchedule;
import ams.aps.util.exception.NoSuchAircraftCabinClassException;
import ams.aps.util.exception.NoSuchAircraftException;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import ams.aps.util.exception.NoSuchFlightScheduleBookingClassException;
import ams.aps.util.helper.AircraftStatus;
import ams.aps.util.helper.ApsMessage;
import java.util.ArrayList;
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
 * @author winga_000
 */
@Stateless
public class FlightScheduleSession implements FlightScheduleSessionLocal {

    @PersistenceContext
    private EntityManager entityManager;

    @EJB
    private TicketFamilySessionLocal ticketFamilySession;
    @EJB
    private AircraftSessionLocal aircraftSession;

    @Override
    public List<FlightSchedule> getAllFilghtSchedules() throws NoSuchFlightSchedulException {
        Query query = entityManager.createQuery("SELECT f FROM FlightSchedule f WHERE f.deleted = FALSE");
        List<FlightSchedule> flightSchedules = new ArrayList<>();
        try {
            flightSchedules = query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchFlightSchedulException(ApsMessage.NO_SUCH_FLIGHT_SCHEDULE_ERRRO);
        }
        return flightSchedules;
    }

    @Override
    public FlightScheduleBookingClass getFlightScheduleBookingClass(Long flightScheduleId, Long bookingClassId) throws NoSuchFlightScheduleBookingClassException {
        Query query = entityManager.createQuery("SELECT fb FROM FlightScheduleBookingClass fb "
                + "WHERE fb.flightScheduleBookingClassId.flightScheduleId = :inFid "
                + "AND fb.flightScheduleBookingClassId.bookingClassId = :inBid "
                + "AND fb.bookingClass.deleted = FALSE "
                + "AND fb.deleted = FALSE");
        query.setParameter("inFid", flightScheduleId);
        query.setParameter("inBid", bookingClassId);
        FlightScheduleBookingClass flightScheduleBookingClass = null;
        try {
            flightScheduleBookingClass = (FlightScheduleBookingClass) query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoSuchFlightScheduleBookingClassException(ApsMessage.No_SUCH_FLIGHTSCHEDULE_BOOKINGCLASS_ERROR);
        }
        return flightScheduleBookingClass;
    }

    @Override
    public FlightSchedule getFlightScheduleById(Long id) throws NoSuchFlightSchedulException {
        Query query = entityManager.createQuery("SELECT f FROM FlightSchedule f WHERE f.flightScheduleId = :inId and f.deleted = FALSE");
        query.setParameter("inId", id);
        FlightSchedule flightSchedule = null;
        try {
            flightSchedule = (FlightSchedule) query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoSuchFlightSchedulException(ApsMessage.NO_SUCH_FLIGHT_SCHEDULE_ERRRO);
        }
        return flightSchedule;
    }

    @Override
    public Aircraft getFlightScheduleAircraft(Long id) throws NoSuchAircraftException {
        Query query = entityManager.createQuery("SELECT a FROM FlightSchedule f, Aircraft a WHERE f.flightScheduleId = :inId and f.aircraft.aircraftId = a.aircraftId and a.status <> :inRetired and a.status <> :inCrashed");
        query.setParameter("inId", id);
        query.setParameter("inCrashed", AircraftStatus.CRASHED);
        query.setParameter("inRetired", AircraftStatus.RETIRED);
        Aircraft aircraft = null;
        try {
            aircraft = (Aircraft) query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoSuchAircraftException(ApsMessage.NO_SUCH_AIRCRAFT_ERROR);
        }
        return aircraft;
    }

    @Override
    public List<CabinClass> getFlightScheduleCabinCalsses(Long id) throws NoSuchCabinClassException {
        List<CabinClass> cabinClasses = new ArrayList<>();
        try {
            for (AircraftCabinClass aircraftCabinClass : SafeHelper.emptyIfNull(getFlightScheduleAircraft(id).getAircraftCabinClasses())) {
                CabinClass cabinClass = aircraftCabinClass.getCabinClass();
                if (!cabinClass.getDeleted()) {
                    cabinClasses.add(cabinClass);
                }
            }
        } catch (NoSuchAircraftException e) {
            throw new NoSuchCabinClassException(AisMsg.NO_SUCH_CABIN_CLASS_ERROR);
        }
        return cabinClasses;
    }

    @Override
    public List<FlightScheduleBookingClass> getFlightScheduleBookingClassJoinTables(Long flightScheduleId) throws NoSuchFlightScheduleBookingClassException {
        Query query = entityManager.createQuery("SELECT fb FROM FlightScheduleBookingClass fb, BookingClass b "
                + "WHERE fb.flightScheduleBookingClassId.flightScheduleId = :inId "
                + "AND fb.flightScheduleBookingClassId.bookingClassId = b.bookingClassId "
                + "AND fb.bookingClass.deleted  = FALSE "
                + "AND fb.deleted = FALSE");
        query.setParameter("inId", flightScheduleId);
        List<FlightScheduleBookingClass> flightScheduleBookingClasses = null;
        try {
            flightScheduleBookingClasses = (List<FlightScheduleBookingClass>) query.getResultList();
        } catch (NoResultException ex) {
            throw new NoSuchFlightScheduleBookingClassException(AisMsg.NO_SUCH_BOOKING_CLASS_ERROR);
        }
        return flightScheduleBookingClasses;
    }

    @Override
    public List<FlightSchCabinClsTicFamBookingClsHelper> getFlightSchCabinClsTicFamBookingClsHelpers(Long flightScheduleId) {
        List<FlightSchCabinClsTicFamBookingClsHelper> flightSchCabinClsTicFamBookingClsHelpers = new ArrayList<>();
        try {
            for (CabinClass cabinClass : getFlightScheduleCabinCalsses(flightScheduleId)) {
                Aircraft aircraft = getFlightScheduleAircraft(flightScheduleId);
                AircraftCabinClass aircraftCabinClass = aircraftSession.getAircraftCabinClassById(aircraft.getAircraftId(), cabinClass.getCabinClassId());
                FlightSchCabinClsTicFamBookingClsHelper helper = new FlightSchCabinClsTicFamBookingClsHelper();
                helper.setCabinClass(cabinClass);
                helper.setSeatQty(aircraftCabinClass.getSeatQty());
                helper.setTicketFamilyBookingClassHelpers(
                        ticketFamilySession.getTicketFamilyBookingClassHelpers(
                                flightScheduleId, aircraft.getAircraftId(), cabinClass.getCabinClassId()));
                flightSchCabinClsTicFamBookingClsHelpers.add(helper);
            }
        } catch (NoSuchCabinClassException | NoSuchAircraftCabinClassException | NoSuchAircraftException ex) {
            System.out.println(ex.getMessage());
        }
        return flightSchCabinClsTicFamBookingClsHelpers;
    }

    @Override
    public List<FlightScheduleBookingClass> getFlightScheduleBookingClassJoinTablesOfTicketFamily(Long flightScheduleId, Long ticketFamilyId) throws NoSuchFlightScheduleBookingClassException {
        Query query = entityManager.createQuery("SELECT fb FROM FlightScheduleBookingClass fb, BookingClass b "
                + "WHERE fb.flightScheduleBookingClassId.flightScheduleId = :inFlightScheduleId "
                + "AND fb.flightScheduleBookingClassId.bookingClassId = b.bookingClassId "
                + "AND b.ticketFamily.ticketFamilyId = :inTicketFamilyId "
                + "AND b.deleted = FALSE "
                + "AND b.ticketFamily.deleted = FALSE "
                + "AND fb.deleted = FALSE");
        query.setParameter("inFlightScheduleId", flightScheduleId);
        query.setParameter("inTicketFamilyId", ticketFamilyId);
        List<FlightScheduleBookingClass> flightScheduleBookingClasses = null;
        try {
            flightScheduleBookingClasses = (List<FlightScheduleBookingClass>) query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchFlightScheduleBookingClassException(AisMsg.NO_SUCH_BOOKING_CLASS_ERROR);
        }
        return flightScheduleBookingClasses;
    }

    @Override
    public List<BookingClass> getFlightScheduleBookingClasses(Long flightScheduleId) throws NoSuchBookingClassException {
        Query query = entityManager.createQuery("SELECT b FROM FlightScheduleBookingClass fb, BookingClass b "
                + "WHERE fb.flightScheduleBookingClassId.flightScheduleId = :inId "
                + "AND fb.flightScheduleBookingClassId.bookingClassId = b.bookingClassId "
                + "AND fb.bookingClass.deleted  = FALSE "
                + "AND fb.deleted = FALSE");
        query.setParameter("inId", flightScheduleId);
        List<BookingClass> bookingClasses = null;
        try {
            bookingClasses = (List<BookingClass>) query.getResultList();
        } catch (NoResultException ex) {
            throw new NoSuchBookingClassException(AisMsg.NO_SUCH_BOOKING_CLASS_ERROR);
        }
        return bookingClasses;
    }

    @Override
    public List<BookingClass> getFlightScheduleBookingClassesOfTicketFamily(Long flightScheduleId, Long ticketFamilyId) throws NoSuchBookingClassException {
        Query query = entityManager.createQuery("SELECT b FROM FlightScheduleBookingClass fb, BookingClass b "
                + "WHERE fb.flightScheduleBookingClassId.flightScheduleId = :inFlightScheduleId "
                + "AND fb.flightScheduleBookingClassId.bookingClassId = b.bookingClassId "
                + "AND b.ticketFamily.ticketFamilyId = :inTicketFamilyId "
                + "AND b.deleted = FALSE "
                + "AND b.ticketFamily.deleted = FALSE "
                + "AND fb.deleted = FALSE");
        query.setParameter("inFlightScheduleId", flightScheduleId);
        query.setParameter("inTicketFamilyId", ticketFamilyId);
        List<BookingClass> bookingClasses = null;
        try {
            bookingClasses = (List<BookingClass>) query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchBookingClassException(AisMsg.NO_SUCH_BOOKING_CLASS_ERROR);
        }
        return bookingClasses;
    }

    @Override
    public List<BookingClass> getBookingClassesFromFlightSchCabinClsTicFamBookingClsHelpers(List<FlightSchCabinClsTicFamBookingClsHelper> helpers) throws NeedBookingClassException {
        List<BookingClass> bookingClasses = new ArrayList<>();
        for (FlightSchCabinClsTicFamBookingClsHelper helper : helpers) {
            for (TicketFamilyBookingClassHelper tfbcHelper : helper.getTicketFamilyBookingClassHelpers()) {
                if (tfbcHelper.getBookingClasses().isEmpty()) {
                    throw new NeedBookingClassException(AisMsg.NEED_BOOKING_CLASS_ERROR);
                }
                for (BookingClass bookingClass : tfbcHelper.getBookingClasses()) {
                    bookingClasses.add(bookingClass);
                }
            }
        }
        return bookingClasses;
    }

    @Override
    public void assignFlightScheduleBookingClass(Long flightScheduleId, List<FlightSchCabinClsTicFamBookingClsHelper> helpers)
            throws NoSuchFlightSchedulException, NoSuchFlightScheduleBookingClassException, NeedBookingClassException {

        List<FlightScheduleBookingClass> flightScheduleBookingClasses = new ArrayList<>();

        //Get Flight Schedule
        FlightSchedule flightSchedule = getFlightScheduleById(flightScheduleId);
        flightSchedule = entityManager.find(FlightSchedule.class, flightSchedule.getFlightScheduleId());

        List<BookingClass> bookingClasses = getBookingClassesFromFlightSchCabinClsTicFamBookingClsHelpers(helpers);

        /**
         * Get original flightScheduleBookingClasses Set their deleted to true,
         * Later wherever exist, set deleted to false
         */
        List<FlightScheduleBookingClass> originFlightScheduleBookingClasses = getFlightScheduleBookingClassJoinTables(flightScheduleId);
        dislinkFlightScheduleBookingClass(originFlightScheduleBookingClasses);

        for (BookingClass bc : bookingClasses) {
            BookingClass bookingClass = entityManager.find(BookingClass.class, bc.getBookingClassId());

            //get flightScheduleBookingClassId
            FlightScheduleBookingClassId flightScheduleBookingClassId = new FlightScheduleBookingClassId();
            flightScheduleBookingClassId.setFlightScheduleId(flightScheduleId);
            flightScheduleBookingClassId.setBookingClassId(bookingClass.getBookingClassId());

            FlightScheduleBookingClass flightScheduleBookingClass = entityManager.find(FlightScheduleBookingClass.class, flightScheduleBookingClassId);
            if (flightScheduleBookingClass != null) {
                flightScheduleBookingClass.setDeleted(false);
                entityManager.merge(flightScheduleBookingClass);
            } else {
                flightScheduleBookingClass = new FlightScheduleBookingClass();
                flightScheduleBookingClass.setFlightSchedule(flightSchedule);
                flightScheduleBookingClass.setFlightScheduleBookingClassId(flightScheduleBookingClassId);
                flightScheduleBookingClass.setBookingClass(bookingClass);
                flightScheduleBookingClass.setSeatQty(0);
                flightScheduleBookingClass.setDeleted(false);
                flightScheduleBookingClass.setBasicPrice((float)0);
                flightScheduleBookingClass.setPrice((float) 0);
                flightScheduleBookingClass.setPriceCoefficient((float) 0);
                flightScheduleBookingClass.setDemandDev((float) 0);
                flightScheduleBookingClass.setDemandMean((float) 0);
                entityManager.persist(flightScheduleBookingClass);
            }

            flightScheduleBookingClasses.add(flightScheduleBookingClass);
        }
        flightSchedule.setFlightScheduleBookingClasses(flightScheduleBookingClasses);
        entityManager.merge(flightSchedule);
    }

    @Override
    public boolean haveBookingClass(Long flightScheduleId) {
        try {
            List<BookingClass> bookingClasses = getFlightScheduleBookingClasses(flightScheduleId);
            return bookingClasses != null && !bookingClasses.isEmpty();
        } catch (NoSuchBookingClassException ex) {
            return false;
        }
    }

    @Override
    public void verifyTicketFamilyHasBookingClass(TicketFamily ticketFamily, List<SeatClassHelper> seatClassHelpers) throws NeedBookingClassException {
        if (!seatClassHelpers.isEmpty()) {
            for (SeatClassHelper seatClassHelper : seatClassHelpers) {
                if (seatClassHelper.getTicketFamilyName().equals(ticketFamily.getName())) {
                    if (seatClassHelper.getBookingClasses().isEmpty()) {
                        throw new NeedBookingClassException(AisMsg.NEED_BOOKING_CLASS_ERROR);
                    }
                }
            }
        } else {
            throw new NeedBookingClassException(AisMsg.NEED_BOOKING_CLASS_ERROR);
        }
    }

    @Override
    public void dislinkFlightScheduleBookingClass(List<FlightScheduleBookingClass> flightScheduleBookingClasses) {
        for (FlightScheduleBookingClass fsbc : flightScheduleBookingClasses) {
            FlightScheduleBookingClass flightScheduleBookingClass = entityManager.find(FlightScheduleBookingClass.class, fsbc.getFlightScheduleBookingClassId());
            flightScheduleBookingClass.setDeleted(true);
        }
    }

    @Override
    public List<FlightScheduleBookingClass> dislinkFlightScheduleBookingClass(CabinClassTicketFamily cabinClassTicketFamily) {
        List<FlightScheduleBookingClass> bookingClasses = new ArrayList<>();
        try {
            Aircraft aircraft = aircraftSession.getAircraftById(cabinClassTicketFamily.getAircraftCabinClass().getAircraftId());
            for (FlightSchedule fs : aircraft.getFlightSchedules()) {
                FlightSchedule flightSchedule = entityManager.find(FlightSchedule.class, fs.getFlightScheduleId());
                List<FlightScheduleBookingClass> fsbcs
                        = getFlightScheduleBookingClassJoinTablesOfTicketFamily(flightSchedule.getFlightScheduleId(), cabinClassTicketFamily.getTicketFamily().getTicketFamilyId());
                dislinkFlightScheduleBookingClass(fsbcs);
            }
        } catch (NoSuchAircraftException | NoSuchFlightScheduleBookingClassException ex) {
            System.out.println("Dislink Flight Schedule Booking Class: " + ex.getMessage());
        }
        return bookingClasses;
    }
}
