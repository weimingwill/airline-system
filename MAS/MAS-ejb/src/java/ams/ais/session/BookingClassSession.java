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
import ams.ais.helper.CabinClassTicketFamilyId;
import ams.ais.util.exception.ExistSuchBookingClassNameException;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.helper.AisMsg;
import ams.ais.util.helper.BookingClassHelper;
import ams.ais.util.helper.FlightSchCabinClsTicFamBookingClsHelper;
import ams.ais.util.helper.TicketFamilyBookingClassHelper;
import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftCabinClass;
import ams.aps.helper.AircraftCabinClassId;
import ams.aps.session.AircraftSessionLocal;
import ams.aps.session.FlightScheduleSessionLocal;
import ams.aps.util.exception.NoSuchAircraftCabinClassException;
import ams.aps.util.exception.NoSuchAircraftException;
import ams.aps.util.exception.NoSuchFlightScheduleBookingClassException;
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
 * @author Tongtong
 */
@Stateless
public class BookingClassSession implements BookingClassSessionLocal {

    @EJB
    private FlightScheduleSessionLocal flightScheduleSession;
    @EJB
    private AircraftSessionLocal aircraftSession;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void createBookingClass(String name) throws ExistSuchBookingClassNameException {
        verifyBookingClassName(name);
        BookingClass bookingClass = new BookingClass();
        bookingClass.create(name);
        entityManager.persist(bookingClass);
    }

    @Override
    public void deleteBookingClass(String name) throws NoSuchBookingClassException {
        BookingClass bookingClassTemp = search(name);
        bookingClassTemp.setDeleted(true);
    }

    @Override
    public BookingClass search(String name) throws NoSuchBookingClassException {
        List<BookingClass> bookingClasses = getAllBookingClasses();
        if (bookingClasses == null) {
            throw new NoSuchBookingClassException(AisMsg.NO_SUCH_BOOKING_CLASS_ERROR);
        } else {
            for (BookingClass bookingClass : bookingClasses) {
                if (name.equals(bookingClass.getName())) {
                    return bookingClass;
                }
            }
            throw new NoSuchBookingClassException(AisMsg.NO_SUCH_BOOKING_CLASS_ERROR);
        }
    }

    @Override
    public void verifyBookingClassName(String name) throws ExistSuchBookingClassNameException {
        List<BookingClass> bookingClasses = getAllBookingClasses();
        if (bookingClasses != null) {
            for (BookingClass bookingClass : bookingClasses) {
                if (name.equals(bookingClass.getName())) {
                    throw new ExistSuchBookingClassNameException(AisMsg.EXIST_SUCH_BOOKING_CLASS_ERROR);
                }
            }
        }
    }

    @Override
    public List<BookingClass> getAllBookingClasses() {
        Query query = entityManager.createQuery("SELECT c FROM BookingClass c WHERE c.deleted = FALSE");
        return query.getResultList();
    }

    @Override
    public BookingClass getBookingClassById(Long id) throws NoSuchBookingClassException {
        Query query = entityManager.createQuery("SELECT b FROM BookingClass b WHERE b.bookingClassId = :inId and b.deleted = FALSE");
        query.setParameter("inId", id);
        BookingClass bookingClass = null;
        try {
            bookingClass = (BookingClass) query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoSuchBookingClassException(AisMsg.NO_SUCH_BOOKING_CLASS_ERROR);
        }
        return bookingClass;
    }

    @Override
    public BookingClassHelper getBookingClassHelperById(Long Id) throws NoSuchBookingClassException {
        BookingClassHelper bookingClassHelper = new BookingClassHelper();
        bookingClassHelper.setBookingClass(getBookingClassById(Id));
        return bookingClassHelper;
    }

    @Override
    public List<BookingClassHelper> getBookingClassHelpers(Long flightScheduleId, Long ticketFamilyId) {
        List<BookingClassHelper> bookingClassHelpers = new ArrayList<>();
        try {
            List<FlightScheduleBookingClass> flightScheduleBookingClasses;
            if (ticketFamilyId == null) {
                flightScheduleBookingClasses = flightScheduleSession.getFlightScheduleBookingClassJoinTables(flightScheduleId);
            } else {
                flightScheduleBookingClasses = flightScheduleSession.getFlightScheduleBookingClassJoinTablesOfTicketFamily(flightScheduleId, ticketFamilyId);
            }
            for (FlightScheduleBookingClass flightScheduleBookingClass : flightScheduleBookingClasses) {
                BookingClass bookingClass = getBookingClassById(flightScheduleBookingClass.getBookingClassId());
                BookingClassHelper bookingClassHelper
                        = new BookingClassHelper(bookingClass, flightScheduleBookingClass.getSeatQty(), flightScheduleBookingClass.getPrice(),
                                flightScheduleBookingClass.getPriceCoefficient(), flightScheduleBookingClass.getDemand());
                bookingClassHelpers.add(bookingClassHelper);
            }
        } catch (NoSuchFlightScheduleBookingClassException | NoSuchBookingClassException e) {
        }
        return bookingClassHelpers;
    }

    @Override
    public void allocateSeats(Long flightScheduleId, List<FlightSchCabinClsTicFamBookingClsHelper> flightHelpers)
            throws NoSuchAircraftException, NoSuchAircraftCabinClassException, NoSuchFlightScheduleBookingClassException {
        Aircraft aircraft = entityManager.find(Aircraft.class, flightScheduleSession.getFlightScheduleAircraft(flightScheduleId).getAircraftId());
        List<CabinClassTicketFamily> cabinClassTicketFamilys = new ArrayList<>();

        for (FlightSchCabinClsTicFamBookingClsHelper flightHelper : SafeHelper.emptyIfNull(flightHelpers)) {
            //Get CabinClass
            CabinClass cabinClass = entityManager.find(CabinClass.class, flightHelper.getCabinClass().getCabinClassId());
            //Get aircraftCabinClass
            AircraftCabinClass aircraftCabinClass = aircraftSession.getAircraftCabinClassById(aircraft.getAircraftId(), cabinClass.getCabinClassId());
            //Create aircraftCabinClassId
            AircraftCabinClassId aircraftCabinClassId = new AircraftCabinClassId(aircraft.getAircraftId(), cabinClass.getCabinClassId());
            for (TicketFamilyBookingClassHelper tfbcHelper : SafeHelper.emptyIfNull(flightHelper.getTicketFamilyBookingClassHelpers())) {
                TicketFamily ticketFamily = tfbcHelper.getTicketFamily();
                if (ticketFamily == null) {
                    ticketFamily = new TicketFamily();
                }
                //Create CabinClassTicketFamilyId
                CabinClassTicketFamilyId cabinClassTicketFamilyId = new CabinClassTicketFamilyId(aircraftCabinClassId, ticketFamily.getTicketFamilyId());
                CabinClassTicketFamily cabinClassTicketFamily;
                cabinClassTicketFamily = entityManager.find(CabinClassTicketFamily.class, cabinClassTicketFamilyId);
                cabinClassTicketFamily.setSeatQty(tfbcHelper.getSeatQty());
                entityManager.merge(cabinClassTicketFamily);

                //Add cabinClassFamily List
                cabinClassTicketFamilys.add(cabinClassTicketFamily);

                FlightScheduleBookingClass flightScheduleBookingClass;
                for (BookingClassHelper bookingClassHelper : SafeHelper.emptyIfNull(tfbcHelper.getBookingClassHelpers())) {
                    flightScheduleBookingClass = flightScheduleSession.getFlightScheduleBookingClass(flightScheduleId, bookingClassHelper.getBookingClass().getBookingClassId());
                    flightScheduleBookingClass.setSeatQty(bookingClassHelper.getSeatQty());
                    entityManager.merge(flightScheduleBookingClass);
                }
            }
            //Set CabinClassTicketFamily to AircraftCabinClass
            aircraftCabinClass.setCabinClassTicketFamilys(cabinClassTicketFamilys);
            entityManager.merge(aircraftCabinClass);
        }
    }

}
