/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.session;

import ams.ais.entity.BookingClass;
import ams.ais.entity.CabinClass;
import ams.ais.entity.CabinClassTicketFamily;
import ams.ais.entity.FlightScheduleBookingClass;
import ams.ais.entity.TicketFamily;
import ams.ais.helper.CabinClassTicketFamilyId;
import ams.ais.session.CabinClassSessionLocal;
import ams.ais.session.TicketFamilySessionLocal;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.exception.NoSuchBookingClassHelperException;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchCabinClassTicketFamilyException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.ais.util.helper.AisMsg;
import ams.ais.util.helper.BookingClassHelper;
import ams.ais.util.helper.FlightSchCabinClsTicFamBookingClsHelper;
import ams.ais.util.helper.FlightScheduleBookingClassHelper;
import ams.ais.util.helper.SeatClassHelper;
import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftCabinClass;
import ams.aps.entity.FlightSchedule;
import ams.aps.helper.AircraftCabinClassId;
import ams.aps.util.exception.NoSuchAircraftCabinClassException;
import ams.aps.util.exception.NoSuchAircraftException;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import ams.aps.util.exception.NoSuchFlightScheduleBookingClassException;
import ams.aps.util.helper.ApsMessage;
import java.util.ArrayList;
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
    @EJB
    private CabinClassSessionLocal cabinClassSession;

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
        Query query = entityManager.createQuery("SELECT fb FROM FlightScheduleBookingClass fb WHERE fb.flightScheduleId = :inFid and fb.bookingClassId = :inBid and fb.bookingClass.deleted = FALSE");
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
        Query query = entityManager.createQuery("SELECT a FROM FlightSchedule f, Aircraft a WHERE f.flightScheduleId = :inId and f.aircraft.aircraftId = a.aircraftId and a.status <> 'Retired'");
        query.setParameter("inId", id);
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
                if (!cabinClass.isDeleted()) {
                    cabinClasses.add(cabinClass);
                }
            }
        } catch (NoSuchAircraftException e) {
            throw new NoSuchCabinClassException(AisMsg.NO_SUCH_CABIN_CLASS_ERROR);
        }
        return cabinClasses;
    }

    @Override
    public void addBookingClass(Long flightScheduleId, List<FlightScheduleBookingClassHelper> flightScheduleBoookingClassHelpers, List<SeatClassHelper> seatClassHelpers, String method)
            throws NoSuchAircraftCabinClassException, NoSuchTicketFamilyException, NoSuchAircraftException, NoSuchCabinClassException, NoSuchFlightSchedulException {
        Aircraft aircraft = entityManager.find(Aircraft.class, getFlightScheduleAircraft(flightScheduleId).getAircraftId());
        List<CabinClassTicketFamily> cabinClassTicketFamilys = new ArrayList<>();
        boolean isEdit = false;
        if (method.equals("edit")) {
            isEdit = true;
        }
        for (FlightScheduleBookingClassHelper flightScheduleBookingClassHelper : SafeHelper.emptyIfNull(flightScheduleBoookingClassHelpers)) {
            //Get CabinClass
            CabinClass cabinClass = entityManager.find(CabinClass.class, flightScheduleBookingClassHelper.getCabinClass().getCabinClassId());
            //Get aircraftCabinClass
            AircraftCabinClass aircraftCabinClass = aircraftSession.getAircraftCabinClassById(aircraft.getAircraftId(), cabinClass.getCabinClassId());
            //Create aircraftCabinClassId
            AircraftCabinClassId aircraftCabinClassId = new AircraftCabinClassId(aircraft.getAircraftId(), cabinClass.getCabinClassId());

            for (TicketFamily ticketFamily : SafeHelper.emptyIfNull(flightScheduleBookingClassHelper.getTicketFamilys())) {
                //Create CabinClassTicketFamilyId
                CabinClassTicketFamilyId cabinClassTicketFamilyId = new CabinClassTicketFamilyId(aircraftCabinClassId, ticketFamily.getTicketFamilyId());
                CabinClassTicketFamily cabinClassTicketFamily;
                cabinClassTicketFamily = entityManager.find(CabinClassTicketFamily.class, cabinClassTicketFamilyId);

                if (isEdit && cabinClassTicketFamily != null) {
//                    try {
//                        cabinClassTicketFamily = cabinClassSession.getCabinClassTicketFamilyJoinTable(aircraft.getAircraftId(), cabinClass.getCabinClassId(), ticketFamily.getTicketFamilyId());
//                    } catch (NoSuchCabinClassTicketFamilyException e) {
//                        cabinClassTicketFamily = new CabinClassTicketFamily();
//                    }
                    cabinClassTicketFamily.setAircraftCabinClass(aircraftCabinClass);
                    cabinClassTicketFamily.setCabinClassTicketFamilyId(cabinClassTicketFamilyId);
                    cabinClassTicketFamily.setTicketFamily(ticketFamily);
                    cabinClassTicketFamily.setSeatQty(0);
                    entityManager.merge(cabinClassTicketFamily);

                } else {
                    //Create CabinClassTicketFamily
                    cabinClassTicketFamily = new CabinClassTicketFamily(cabinClassTicketFamilyId, 0);
                    entityManager.persist(cabinClassTicketFamily);
                }

                //Add cabinClassFamily List
                cabinClassTicketFamilys.add(cabinClassTicketFamily);
            }

            //Set CabinClassTicketFamily to AircraftCabinClass
            aircraftCabinClass.setCabinClassTicketFamilys(cabinClassTicketFamilys);
            if (isEdit) {
                entityManager.persist(aircraftCabinClass);
            } else {
                entityManager.merge(aircraftCabinClass);
            }
        }
        assignFlightScheduleBookingClass(flightScheduleId, seatClassHelpers, isEdit);
    }

    @Override
    public void assignFlightScheduleBookingClass(Long flightScheduleId, List<SeatClassHelper> seatClassHelpers, boolean isEdit) throws NoSuchFlightSchedulException {
        FlightScheduleBookingClass flightScheduleBookingClass;
        for (SeatClassHelper seatClassHelper : SafeHelper.emptyIfNull(seatClassHelpers)) {
            for (BookingClass bookingClass : SafeHelper.emptyIfNull(seatClassHelper.getBookingClasses())) {
                BookingClass newBookingClass = entityManager.find(BookingClass.class, bookingClass.getBookingClassId());
                try {
                    flightScheduleBookingClass = getFlightScheduleBookingClass(flightScheduleId, bookingClass.getBookingClassId());
                } catch (NoSuchFlightScheduleBookingClassException e) {
                    flightScheduleBookingClass = null;
                }
                if (isEdit && flightScheduleBookingClass != null) {
                    flightScheduleBookingClass.setBookingClass(bookingClass);
                    entityManager.merge(flightScheduleBookingClass);
                } else {
                    flightScheduleBookingClass = new FlightScheduleBookingClass(flightScheduleId, bookingClass.getBookingClassId(), getFlightScheduleById(flightScheduleId), newBookingClass);
                    entityManager.persist(flightScheduleBookingClass);
                }
            }
        }
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
    public List<BookingClass> getFlightScheduleBookingClasses(Long flightScheduleId) throws NoSuchBookingClassException {
        Query query = entityManager.createQuery("SELECT b FROM FlightScheduleBookingClass fb, BookingClass b WHERE fb.flightScheduleId = :inId and fb.bookingClassId = b.bookingClassId and fb.bookingClass.deleted  = FALSE");
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
        Query query = entityManager.createQuery("SELECT b FROM FlightScheduleBookingClass fb, BookingClass b WHERE fb.flightScheduleId = :inFlightScheduleId "
                + "and fb.bookingClassId = b.bookingClassId and b.ticketFamily.ticketFamilyId = :inTicketFamilyId and b.deleted = FALSE and b.ticketFamily.deleted = FALSE");
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
    public List<FlightScheduleBookingClass> getFlightScheduleBookingClassJoinTables(Long flightScheduleId) throws NoSuchFlightScheduleBookingClassException {
        Query query = entityManager.createQuery("SELECT fb FROM FlightScheduleBookingClass fb, BookingClass b WHERE fb.flightScheduleId = :inId and fb.bookingClassId = b.bookingClassId and fb.bookingClass.deleted  = FALSE");
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
                AircraftCabinClass aircraftCabinClass = aircraftSession.getAircraftCabinClassById(flightScheduleId, cabinClass.getCabinClassId());
                FlightSchCabinClsTicFamBookingClsHelper helper = new FlightSchCabinClsTicFamBookingClsHelper(cabinClass, aircraftCabinClass.getSeatQty(), 
                        ticketFamilySession.getTicketFamilyBookingClassHelpers(flightScheduleId, aircraftCabinClass.getAircraftId(), cabinClass.getCabinClassId()));
                flightSchCabinClsTicFamBookingClsHelpers.add(helper);
            }
        } catch (NoSuchCabinClassException | NoSuchAircraftCabinClassException ex) {
        }
        return flightSchCabinClsTicFamBookingClsHelpers;
    }

    @Override
    public List<FlightScheduleBookingClass> getFlightScheduleBookingClassJoinTablesOfTicketFamily(Long flightScheduleId, Long ticketFamilyId) throws NoSuchFlightScheduleBookingClassException {
        Query query = entityManager.createQuery("SELECT fb FROM FlightScheduleBookingClass fb, BookingClass b WHERE fb.flightScheduleId = :inFlightScheduleId "
                + "and fb.bookingClassId = b.bookingClassId and b.ticketFamily.ticketFamilyId = :inTicketFamilyId and b.deleted = FALSE and b.ticketFamily.deleted = FALSE");
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
}
