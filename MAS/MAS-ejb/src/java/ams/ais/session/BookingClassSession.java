/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.BookingClass;
import ams.ais.entity.FlightScheduleBookingClass;
import ams.ais.util.exception.ExistSuchBookingClassNameException;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.helper.AisMsg;
import ams.ais.util.helper.BookingClassHelper;
import ams.aps.session.FlightScheduleSessionLocal;
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
                BookingClassHelper bookingClassHelper = 
                        new BookingClassHelper(bookingClass, flightScheduleBookingClass.getSeatQty(), flightScheduleBookingClass.getPrice(), 
                                flightScheduleBookingClass.getPriceCoefficient(), flightScheduleBookingClass.getDemand());
                bookingClassHelpers.add(bookingClassHelper);
            }
        } catch (NoSuchFlightScheduleBookingClassException | NoSuchBookingClassException e) {
        }
        return bookingClassHelpers;    
    }
    
    
}
