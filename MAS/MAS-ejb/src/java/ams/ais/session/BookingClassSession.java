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
import ams.ais.util.exception.DuplicatePriceException;
import ams.ais.util.exception.ExistSuchBookingClassNameException;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.exception.WrongSumOfBookingClassSeatQtyException;
import ams.ais.util.exception.WrongSumOfTicketFamilySeatQtyException;
import ams.ais.util.helper.AisMsg;
import ams.ais.util.helper.BookingClassHelper;
import ams.ais.util.helper.FlightSchCabinClsTicFamBookingClsHelper;
import ams.ais.util.helper.TicketFamilyBookingClassHelper;
import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftCabinClass;
import ams.aps.helper.AircraftCabinClassId;
import ams.aps.util.exception.NoSuchAircraftCabinClassException;
import ams.aps.util.exception.NoSuchAircraftException;
import ams.aps.util.exception.NoSuchFlightScheduleBookingClassException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    public void createBookingClass(String name, TicketFamily selectedTicketFamily) throws ExistSuchBookingClassNameException {
        verifyBookingClassName(name, selectedTicketFamily);
        BookingClass bookingClass = new BookingClass();
        TicketFamily ticketFamily = entityManager.find(TicketFamily.class, selectedTicketFamily.getTicketFamilyId());
        bookingClass.create(name, ticketFamily);
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
    public void verifyBookingClassName(String name, TicketFamily ticketFamily) throws ExistSuchBookingClassNameException {
        List<BookingClass> bookingClasses = getAllBookingClasses();
        if (bookingClasses != null) {
            for (BookingClass bookingClass : bookingClasses) {
                if (name.toUpperCase().equals(bookingClass.getName())) {
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
                BookingClass bookingClass = getBookingClassById(flightScheduleBookingClass.getBookingClass().getBookingClassId());
                BookingClassHelper bookingClassHelper = new BookingClassHelper();
                bookingClassHelper.setBookingClass(bookingClass);
                bookingClassHelper.setSeatQty(flightScheduleBookingClass.getSeatQty());
                bookingClassHelper.setPriceCoefficient(flightScheduleBookingClass.getPriceCoefficient());
                bookingClassHelper.setPrice(flightScheduleBookingClass.getPrice());
                bookingClassHelper.setBasicPrice(flightScheduleBookingClass.getBasicPrice());
                bookingClassHelper.setDemandDev(flightScheduleBookingClass.getDemandDev());
                bookingClassHelper.setDemandMean(flightScheduleBookingClass.getDemandMean());
                bookingClassHelpers.add(bookingClassHelper);
            }
        } catch (NoSuchFlightScheduleBookingClassException | NoSuchBookingClassException e) {
        }
        return bookingClassHelpers;
    }

    @Override
    public void allocateSeats(Long flightScheduleId, List<FlightSchCabinClsTicFamBookingClsHelper> flightHelpers)
            throws NoSuchAircraftException, NoSuchAircraftCabinClassException, NoSuchFlightScheduleBookingClassException,
            WrongSumOfTicketFamilySeatQtyException, WrongSumOfBookingClassSeatQtyException {
        Aircraft aircraft = entityManager.find(Aircraft.class, flightScheduleSession.getFlightScheduleAircraft(flightScheduleId).getAircraftId());
        List<CabinClassTicketFamily> cabinClassTicketFamilys = new ArrayList<>();

        for (FlightSchCabinClsTicFamBookingClsHelper flightHelper : SafeHelper.emptyIfNull(flightHelpers)) {
            verifyTicketFamilySeatsSum(flightScheduleId, flightHelper);
            //Get CabinClass
            CabinClass cabinClass = entityManager.find(CabinClass.class, flightHelper.getCabinClass().getCabinClassId());
            //Get aircraftCabinClass
            AircraftCabinClass aircraftCabinClass = aircraftSession.getAircraftCabinClassById(aircraft.getAircraftId(), cabinClass.getCabinClassId());
            //Create aircraftCabinClassId
            AircraftCabinClassId aircraftCabinClassId = new AircraftCabinClassId(aircraft.getAircraftId(), cabinClass.getCabinClassId());
            for (TicketFamilyBookingClassHelper tfbcHelper : SafeHelper.emptyIfNull(flightHelper.getTicketFamilyBookingClassHelpers())) {
                verifyBookingClassSeatsSum(flightScheduleId, tfbcHelper);
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
                allocateBookingClassSeats(flightScheduleId, tfbcHelper);
            }
            //Set CabinClassTicketFamily to AircraftCabinClass
            aircraftCabinClass.setCabinClassTicketFamilys(cabinClassTicketFamilys);
            entityManager.merge(aircraftCabinClass);
        }
    }

    @Override
    public void allocateBookingClassSeats(Long flightScheduleId, TicketFamilyBookingClassHelper tfbcHelper)
            throws WrongSumOfBookingClassSeatQtyException, NoSuchFlightScheduleBookingClassException {
        for (BookingClassHelper bookingClassHelper : SafeHelper.emptyIfNull(tfbcHelper.getBookingClassHelpers())) {
            FlightScheduleBookingClass flightScheduleBookingClass = flightScheduleSession.getFlightScheduleBookingClass(flightScheduleId, bookingClassHelper.getBookingClass().getBookingClassId());
            flightScheduleBookingClass.setSeatQty(bookingClassHelper.getSeatQty());
            entityManager.merge(flightScheduleBookingClass);
        }
    }

    @Override
    public void priceBookingClasses(Long flightScheduleId, List<FlightSchCabinClsTicFamBookingClsHelper> flightHelpers, Map<Long, Float> priceMap)
            throws NoSuchFlightScheduleBookingClassException, DuplicatePriceException {
        verifyUniqueBookingClassPrices(priceMap);
        for (FlightSchCabinClsTicFamBookingClsHelper flightHelper : SafeHelper.emptyIfNull(flightHelpers)) {
            for (TicketFamilyBookingClassHelper tfbcHelper : SafeHelper.emptyIfNull(flightHelper.getTicketFamilyBookingClassHelpers())) {
                for (BookingClassHelper bookingClassHelper : SafeHelper.emptyIfNull(tfbcHelper.getBookingClassHelpers())) {
                    FlightScheduleBookingClass flightScheduleBookingClass = flightScheduleSession.getFlightScheduleBookingClass(flightScheduleId, bookingClassHelper.getBookingClass().getBookingClassId());
                    flightScheduleBookingClass.setBasicPrice(bookingClassHelper.getBasicPrice());
                    flightScheduleBookingClass.setPriceCoefficient(bookingClassHelper.getPriceCoefficient());
                    flightScheduleBookingClass.setPrice(priceMap.get(bookingClassHelper.getBookingClass().getBookingClassId()));
                    entityManager.merge(flightScheduleBookingClass);
                }
            }
        }
    }
    

    @Override
    public void updateBookingClass(Long bookingClassId, String bookingClassName) throws NoSuchBookingClassException, ExistSuchBookingClassNameException {
        
        BookingClass bookingClass = getBookingClassById(bookingClassId);
        if (bookingClass == null) {
            throw new NoSuchBookingClassException(AisMsg.NO_SUCH_BOOKING_CLASS_ERROR);
        } else {
            List<BookingClass> bookingClasses = getAllOtherBookingClassById(bookingClassId);

            if (bookingClasses != null) {

                for (BookingClass cc : bookingClasses) {

                    if (bookingClassName.toUpperCase().equals(cc.getName())) {
                        throw new ExistSuchBookingClassNameException(AisMsg.EXIST_SUCH_BOOKING_CLASS_ERROR);
                    }
                }
            }
        }
        bookingClass.setName(bookingClassName);
        entityManager.merge(bookingClass);
        entityManager.flush();
    }
    
    public List<BookingClass> getAllOtherBookingClassById(Long bookingClassId) {
       Query query = entityManager.createQuery("SELECT m FROM BookingClass m where m.bookingClassId <> :bookingClassId AND m.deleted = FALSE");
        query.setParameter("bookingClassId", bookingClassId);
        return query.getResultList();
    }
    
    public void verifyUniqueBookingClassPrices(Map<Long, Float> priceMap) throws DuplicatePriceException {
        Collection<Float> valueList = priceMap.values();
        Set<Float> valueSet = new HashSet<>(valueList);
        if (valueList.size() != valueSet.size()) {
            throw new DuplicatePriceException(AisMsg.DUPLICATE_PRICE_ERROR);
        }
    }

    public void verifyTicketFamilySeatsSum(Long flightScheduleId, FlightSchCabinClsTicFamBookingClsHelper flightHelper) throws WrongSumOfTicketFamilySeatQtyException {
        int cabinClassSeatQty = flightHelper.getSeatQty();
        int totalTicketFamilySeatQty = 0;
        for (TicketFamilyBookingClassHelper tfbcHelper : SafeHelper.emptyIfNull(flightHelper.getTicketFamilyBookingClassHelpers())) {
            totalTicketFamilySeatQty += tfbcHelper.getSeatQty();
        }
        if (cabinClassSeatQty != totalTicketFamilySeatQty) {
            throw new WrongSumOfTicketFamilySeatQtyException(AisMsg.WRONG_SUM_OF_TICKET_FAMILY_ERROR);
        }
    }

    public void verifyBookingClassSeatsSum(Long flightScheduleId, TicketFamilyBookingClassHelper tfbcHelper) throws WrongSumOfBookingClassSeatQtyException {
        int ticketFamilySeatQty = tfbcHelper.getSeatQty();
        int totalBookingClassSeatQty = 0;
        for (BookingClassHelper bcHelper : SafeHelper.emptyIfNull(tfbcHelper.getBookingClassHelpers())) {
            totalBookingClassSeatQty += bcHelper.getSeatQty();
        }
        if (ticketFamilySeatQty != totalBookingClassSeatQty) {
            throw new WrongSumOfBookingClassSeatQtyException(AisMsg.WRONG_SUM_OF_BOOKING_CLASS_ERROR);
        }
    }

    @Override
    public void setBookingClassDefaultPrice(Long flightScheduleId, Long ticketFamilyId, float ticketFamilyPrice) 
            throws NoSuchFlightScheduleBookingClassException {
        try {
            for (FlightScheduleBookingClass fsbc : flightScheduleSession.getFlightScheduleBookingClassJoinTablesOfTicketFamily(flightScheduleId, ticketFamilyId)) {
                FlightScheduleBookingClass flightScheduleBookingClass = getOriginalFlightScheduleBookingClass(fsbc);
                flightScheduleBookingClass.setPrice(ticketFamilyPrice);
                entityManager.merge(flightScheduleBookingClass);
            }
        } catch (NoSuchFlightScheduleBookingClassException ex) {
            throw new NoSuchFlightScheduleBookingClassException(AisMsg.NO_SUCH_BOOKING_CLASS_ERROR);
        }
    }
    
    public FlightScheduleBookingClass getOriginalFlightScheduleBookingClass(FlightScheduleBookingClass fsbc) {
        return entityManager.find(FlightScheduleBookingClass.class, fsbc.getFlightScheduleBookingClassId());
    }
}


