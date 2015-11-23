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
import ams.ais.entity.PhaseDemand;
import ams.ais.entity.TicketFamily;
import ams.ais.entity.TicketFamilyRule;
import ams.ais.entity.helper.CabinClassTicketFamilyId;
import ams.ais.entity.helper.FlightScheduleBookingClassId;
import ams.ais.util.exception.DuplicatePriceException;
import ams.ais.util.exception.ExistSuchBookingClassNameException;
import ams.ais.util.exception.NeedBookingClassException;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.ais.util.exception.WrongSumOfBookingClassSeatQtyException;
import ams.ais.util.exception.WrongSumOfTicketFamilySeatQtyException;
import ams.ais.util.helper.AisMsg;
import ams.ais.util.helper.BookingClassHelper;
import ams.ais.util.helper.FlightSchCabinClsTicFamBookingClsHelper;
import ams.ais.util.helper.SeatClassHelper;
import ams.ais.util.helper.TicketFamilyBookingClassHelper;
import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftCabinClass;
import ams.aps.entity.AircraftType;
import ams.aps.entity.Airport;
import ams.aps.entity.FlightSchedule;
import ams.aps.entity.helper.AircraftCabinClassId;
import ams.aps.session.RoutePlanningSessionLocal;
import ams.aps.util.exception.NoSuchAircraftCabinClassException;
import ams.aps.util.exception.NoSuchAircraftException;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import ams.aps.util.exception.NoSuchFlightScheduleBookingClassException;
import ams.aps.util.helper.AircraftStatus;
import ams.aps.util.helper.ApsMessage;
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
 * @author weiming
 */
@Stateless
public class RevMgmtSession implements RevMgmtSessionLocal {

    @PersistenceContext
    private EntityManager em;

    @EJB
    private ProductDesignSessionLocal productDesignSession;
    @EJB
    private SeatReallocationSessionLocal seatReallocationSession;
    @EJB
    private RoutePlanningSessionLocal routePlanningSession;

    @Override
    public BookingClass createBookingClass(String name, TicketFamily selectedTicketFamily, String channelName) throws ExistSuchBookingClassNameException {
        verifyBookingClassName(name, selectedTicketFamily);
        BookingClass bookingClass = new BookingClass();
        TicketFamily ticketFamily = em.find(TicketFamily.class, selectedTicketFamily.getTicketFamilyId());
        bookingClass.setName(name);
        bookingClass.setTicketFamily(ticketFamily);
        bookingClass.setDeleted(false);
        bookingClass.setChannel(channelName);
        em.persist(bookingClass);
        em.flush();
        em.flush();
        return bookingClass;
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
                if (name.toUpperCase().equals(bookingClass.getName().toUpperCase())) {
                    throw new ExistSuchBookingClassNameException(AisMsg.EXIST_SUCH_BOOKING_CLASS_ERROR);
                }
            }
        }
    }

    @Override
    public List<BookingClass> getAllBookingClasses() {
        Query query = em.createQuery("SELECT c FROM BookingClass c WHERE c.deleted = FALSE");
        return query.getResultList();
    }

    @Override
    public BookingClass getBookingClassById(Long id) throws NoSuchBookingClassException {
        Query query = em.createQuery("SELECT b FROM BookingClass b WHERE b.bookingClassId = :inId and b.deleted = FALSE");
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
    public BookingClass getBookingClassByName(String name) {
        Query query = em.createQuery("SELECT b FROM BookingClass b WHERE b.name = :inName and b.deleted = FALSE");
        query.setParameter("inName", name);
        BookingClass bookingClass = new BookingClass();
        try {
            bookingClass = (BookingClass) query.getSingleResult();
        } catch (NoResultException e) {
        }
        return bookingClass;
    }

    @Override
    public List<BookingClassHelper> getBookingClassHelpers(Long flightScheduleId, Long ticketFamilyId) {
        List<BookingClassHelper> bookingClassHelpers = new ArrayList<>();
        try {
            List<FlightScheduleBookingClass> flightScheduleBookingClasses;
            if (ticketFamilyId == null) {
                flightScheduleBookingClasses = getFlightScheduleBookingClassJoinTables(flightScheduleId);
            } else {
                flightScheduleBookingClasses = getFlightScheduleBookingClassJoinTablesOfTicketFamily(flightScheduleId, ticketFamilyId);
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
        Aircraft aircraft = em.find(Aircraft.class, getFlightScheduleAircraft(flightScheduleId).getAircraftId());
        List<CabinClassTicketFamily> cabinClassTicketFamilys = new ArrayList<>();

        for (FlightSchCabinClsTicFamBookingClsHelper flightHelper : SafeHelper.emptyIfNull(flightHelpers)) {
            verifyTicketFamilySeatsSum(flightScheduleId, flightHelper);
            //Get CabinClass
            CabinClass cabinClass = em.find(CabinClass.class, flightHelper.getCabinClass().getCabinClassId());
            //Get aircraftCabinClass
            AircraftCabinClass aircraftCabinClass = productDesignSession.getAircraftCabinClassById(aircraft.getAircraftId(), cabinClass.getCabinClassId());
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
                cabinClassTicketFamily = em.find(CabinClassTicketFamily.class, cabinClassTicketFamilyId);
                cabinClassTicketFamily.setSeatQty(tfbcHelper.getSeatQty());
                em.merge(cabinClassTicketFamily);

                //Add cabinClassFamily List
                cabinClassTicketFamilys.add(cabinClassTicketFamily);
                allocateBookingClassSeats(flightScheduleId, tfbcHelper);
            }
            //Set CabinClassTicketFamily to AircraftCabinClass
            aircraftCabinClass.setCabinClassTicketFamilys(cabinClassTicketFamilys);
            em.merge(aircraftCabinClass);
        }
    }

    @Override
    public void allocateBookingClassSeats(Long flightScheduleId, TicketFamilyBookingClassHelper tfbcHelper)
            throws WrongSumOfBookingClassSeatQtyException, NoSuchFlightScheduleBookingClassException {
        for (BookingClassHelper bookingClassHelper : SafeHelper.emptyIfNull(tfbcHelper.getBookingClassHelpers())) {
            FlightScheduleBookingClass flightScheduleBookingClass = getFlightScheduleBookingClass(flightScheduleId, bookingClassHelper.getBookingClass().getBookingClassId());
            flightScheduleBookingClass.setSeatQty(bookingClassHelper.getSeatQty());
            em.merge(flightScheduleBookingClass);
        }
    }

    @Override
    public void priceBookingClasses(Long flightScheduleId, List<FlightSchCabinClsTicFamBookingClsHelper> flightHelpers, Map<Long, Float> priceMap)
            throws NoSuchFlightScheduleBookingClassException, DuplicatePriceException {
        verifyUniqueBookingClassPrices(priceMap);
        for (FlightSchCabinClsTicFamBookingClsHelper flightHelper : SafeHelper.emptyIfNull(flightHelpers)) {
            for (TicketFamilyBookingClassHelper tfbcHelper : SafeHelper.emptyIfNull(flightHelper.getTicketFamilyBookingClassHelpers())) {
                for (BookingClassHelper bookingClassHelper : SafeHelper.emptyIfNull(tfbcHelper.getBookingClassHelpers())) {
                    FlightScheduleBookingClass flightScheduleBookingClass = getFlightScheduleBookingClass(flightScheduleId, bookingClassHelper.getBookingClass().getBookingClassId());
                    flightScheduleBookingClass.setBasicPrice(bookingClassHelper.getBasicPrice());
                    flightScheduleBookingClass.setPriceCoefficient(bookingClassHelper.getPriceCoefficient());
                    flightScheduleBookingClass.setPrice(priceMap.get(bookingClassHelper.getBookingClass().getBookingClassId()));
                    em.merge(flightScheduleBookingClass);
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
        em.merge(bookingClass);
        em.flush();
    }

    public List<BookingClass> getAllOtherBookingClassById(Long bookingClassId) {
        Query query = em.createQuery("SELECT m FROM BookingClass m where m.bookingClassId <> :bookingClassId AND m.deleted = FALSE");
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

    private FlightScheduleBookingClass getOriginalFlightScheduleBookingClass(FlightScheduleBookingClass fsbc) {
        return em.find(FlightScheduleBookingClass.class, fsbc.getFlightScheduleBookingClassId());
    }

    @Override
    public List<FlightSchedule> getAllFilghtSchedules() throws NoSuchFlightSchedulException {
        Query query = em.createQuery("SELECT f FROM FlightSchedule f WHERE f.deleted = FALSE");
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
        Query query = em.createQuery("SELECT fb FROM FlightScheduleBookingClass fb "
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
        Query query = em.createQuery("SELECT f FROM FlightSchedule f WHERE f.flightScheduleId = :inId and f.deleted = FALSE");
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
        Query query = em.createQuery("SELECT a FROM FlightSchedule f, Aircraft a WHERE f.flightScheduleId = :inId and f.aircraft.aircraftId = a.aircraftId and a.status <> :inRetired and a.status <> :inCrashed");
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
        Query query = em.createQuery("SELECT c FROM FlightSchedule f, Aircraft a, CabinClass c, AircraftCabinClass ac "
                + "WHERE f.flightScheduleId = :inId "
                + "AND f.aircraft.aircraftId = a.aircraftId "
                + "AND a.aircraftId = ac.aircraftId "
                + "AND ac.cabinClassId = c.cabinClassId "
                + "AND c.deleted = FALSE "
                + "AND a.status <> :inRetired AND a.status <> :inCrashed "
                + "ORDER BY c.rank DESC");
        query.setParameter("inId", id);
        query.setParameter("inCrashed", AircraftStatus.CRASHED);
        query.setParameter("inRetired", AircraftStatus.RETIRED);
        try {
            cabinClasses = (List<CabinClass>) query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchCabinClassException(AisMsg.NO_SUCH_CABIN_CLASS_ERROR);
        }
        return cabinClasses;
    }

    @Override
    public List<TicketFamily> getFlightScheduleTixFams(Long flightScheduleId) {
        List<TicketFamily> tixFams = new ArrayList<>();
        Query query = em.createQuery("SELECT DISTINCT t FROM FlightSchedule f, Aircraft a, AircraftCabinClass ac, CabinClassTicketFamily ct, TicketFamily t WHERE f.flightScheduleId = :inId AND f.aircraft.aircraftId = a.aircraftId AND a.aircraftId = ac.aircraftId AND ac.cabinClassId = ct.aircraftCabinClass.cabinClassId AND ct.ticketFamily.ticketFamilyId = t.ticketFamilyId AND t.deleted = FALSE AND ct.aircraftCabinClass.cabinClass.deleted = FALSE AND a.status <> :inRetired AND a.status <> :inCrashed ORDER BY ct.aircraftCabinClass.cabinClass.rank DESC, t.rank DESC");
        query.setParameter("inId", flightScheduleId);
        query.setParameter("inCrashed", AircraftStatus.CRASHED);
        query.setParameter("inRetired", AircraftStatus.RETIRED);
        try {
            tixFams = (List<TicketFamily>) query.getResultList();
        } catch (NoResultException e) {
        }
        return tixFams;
    }

    @Override
    public List<FlightScheduleBookingClass> getFlightScheduleBookingClassJoinTables(Long flightScheduleId) throws NoSuchFlightScheduleBookingClassException {
        Query query = em.createQuery("SELECT fb FROM FlightScheduleBookingClass fb, BookingClass b "
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
                AircraftCabinClass aircraftCabinClass = productDesignSession.getAircraftCabinClassById(aircraft.getAircraftId(), cabinClass.getCabinClassId());
                FlightSchCabinClsTicFamBookingClsHelper helper = new FlightSchCabinClsTicFamBookingClsHelper();
                helper.setCabinClass(cabinClass);
                helper.setSeatQty(aircraftCabinClass.getSeatQty());
                helper.setTicketFamilyBookingClassHelpers(
                        productDesignSession.getTicketFamilyBookingClassHelpers(
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
        Query query = em.createQuery("SELECT fb FROM FlightScheduleBookingClass fb, BookingClass b "
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
        Query query = em.createQuery("SELECT b FROM FlightScheduleBookingClass fb, BookingClass b "
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
        Query query = em.createQuery("SELECT b FROM FlightScheduleBookingClass fb, BookingClass b "
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
        flightSchedule = em.find(FlightSchedule.class, flightSchedule.getFlightScheduleId());

        List<BookingClass> bookingClasses = getBookingClassesFromFlightSchCabinClsTicFamBookingClsHelpers(helpers);

        /**
         * Get original flightScheduleBookingClasses Set their deleted to true,
         * Later wherever exist, set deleted to false
         */
        List<FlightScheduleBookingClass> originFlightScheduleBookingClasses = getFlightScheduleBookingClassJoinTables(flightScheduleId);
        dislinkFlightScheduleBookingClass(originFlightScheduleBookingClasses);

        for (BookingClass bc : bookingClasses) {
            BookingClass bookingClass = em.find(BookingClass.class, bc.getBookingClassId());

            //get flightScheduleBookingClassId
            FlightScheduleBookingClassId flightScheduleBookingClassId = new FlightScheduleBookingClassId();
            flightScheduleBookingClassId.setFlightScheduleId(flightScheduleId);
            flightScheduleBookingClassId.setBookingClassId(bookingClass.getBookingClassId());

            FlightScheduleBookingClass flightScheduleBookingClass = em.find(FlightScheduleBookingClass.class, flightScheduleBookingClassId);
            if (flightScheduleBookingClass != null) {
                flightScheduleBookingClass.setDeleted(false);
                em.merge(flightScheduleBookingClass);
            } else {
                createFlightSchedBookingCls(flightSchedule, bookingClass, flightScheduleBookingClassId);
            }

            flightScheduleBookingClasses.add(flightScheduleBookingClass);
        }
        flightSchedule.setFlightScheduleBookingClasses(flightScheduleBookingClasses);
        em.merge(flightSchedule);
    }

    @Override
    public FlightScheduleBookingClass createFlightSchedBookingCls(FlightSchedule flightSched, BookingClass bookingCls, FlightScheduleBookingClassId flightSchedBookingClsId) {
        try {
            for (FlightScheduleBookingClass fb : getFlightScheduleBookingClassJoinTables(flightSched.getFlightScheduleId())) {
                if (flightSchedBookingClsId.equals(fb.getFlightScheduleBookingClassId())) {
                    return em.find(FlightScheduleBookingClass.class, flightSchedBookingClsId);
                }
            }
        } catch (NoSuchFlightScheduleBookingClassException e) {
        }
        flightSched = em.find(FlightSchedule.class, flightSched.getFlightScheduleId());
        bookingCls = em.find(BookingClass.class, bookingCls.getBookingClassId());

        FlightScheduleBookingClass flightScheduleBookingClass = new FlightScheduleBookingClass();
        flightScheduleBookingClass.setFlightSchedule(flightSched);
        flightScheduleBookingClass.setFlightScheduleBookingClassId(flightSchedBookingClsId);
        flightScheduleBookingClass.setBookingClass(bookingCls);
        flightScheduleBookingClass.setSeatQty(0);
        flightScheduleBookingClass.setSoldSeatQty(0);
        flightScheduleBookingClass.setDeleted(false);
        flightScheduleBookingClass.setBasicPrice((float) 0);
        flightScheduleBookingClass.setPrice((float) 0);
        flightScheduleBookingClass.setPriceCoefficient((float) 0);
        flightScheduleBookingClass.setDemandDev((float) 0);
        flightScheduleBookingClass.setDemandMean((float) 0);
        flightScheduleBookingClass.setClosed(false);

        //create default check points (phase demand) 
        List<PhaseDemand> pds = seatReallocationSession.getAllPhaseDemands();
        flightScheduleBookingClass.setPhaseDemands(pds);

        em.persist(flightScheduleBookingClass);
        return flightScheduleBookingClass;
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
            FlightScheduleBookingClass flightScheduleBookingClass = em.find(FlightScheduleBookingClass.class, fsbc.getFlightScheduleBookingClassId());
            flightScheduleBookingClass.setDeleted(true);
        }
    }

    @Override
    public List<FlightScheduleBookingClass> dislinkFlightScheduleBookingClass(CabinClassTicketFamily cabinClassTicketFamily) {
        List<FlightScheduleBookingClass> bookingClasses = new ArrayList<>();
        try {
            Aircraft aircraft = productDesignSession.getAircraftById(cabinClassTicketFamily.getAircraftCabinClass().getAircraftId());
            for (FlightSchedule fs : aircraft.getFlightSchedules()) {
                FlightSchedule flightSchedule = em.find(FlightSchedule.class, fs.getFlightScheduleId());
                List<FlightScheduleBookingClass> fsbcs
                        = getFlightScheduleBookingClassJoinTablesOfTicketFamily(flightSchedule.getFlightScheduleId(), cabinClassTicketFamily.getTicketFamily().getTicketFamilyId());
                dislinkFlightScheduleBookingClass(fsbcs);
            }
        } catch (NoSuchAircraftException | NoSuchFlightScheduleBookingClassException ex) {
            System.out.println("Dislink Flight Schedule Booking Class: " + ex.getMessage());
        }
        return bookingClasses;
    }
//
//    @Override
//    public void suggestTicketFamilyPrice(Long flightScheduleId)
//            throws NoSuchAircraftException, NoSuchCabinClassException, NoSuchCabinClassTicketFamilyException, NoSuchFlightScheduleBookingClassException {
//        Aircraft aircraft = getFlightScheduleAircraft(flightScheduleId);
//        List<CabinClass> cabinClasses = productDesignSession.getAircraftCabinClasses(flightScheduleId);
//        for (CabinClass cabinClass : cabinClasses) {
//            List<CabinClassTicketFamily> cabinClassTicketFamilys
//                    = productDesignSession.getCabinClassTicketFamilyJoinTables(aircraft.getAircraftId(), cabinClass.getCabinClassId());
//            for (CabinClassTicketFamily cctf : cabinClassTicketFamilys) {
//                Long ticketFamilyId = cctf.getTicketFamily().getTicketFamilyId();
//                CabinClassTicketFamily cabinClassTicketFamily
//                        = productDesignSession.getOriginalCabinClassTicketFamily(aircraft.getAircraftId(), cabinClass.getCabinClassId(), ticketFamilyId);
//                double basicPrice = calTicketFamilyPrice(flightScheduleId, ticketFamilyId);
//                cabinClassTicketFamily.setPrice((float) basicPrice);
////                setBookingClassDefaultPrice(flightScheduleId, ticketFamilyId, (float) basicPrice);
//                em.merge(cabinClassTicketFamily);
//            }
//        }
//    }

    @Override
    public double calTicketFamilyPrice(Long flightScheduleId, Long ticketFamilyId) {
        double basicCost = calcFlightScheduleBasicCostPerRoundTrip(flightScheduleId);
        System.out.println("basic cost is" + basicCost);
        double ticketFamilyRuleCost = calTicketFamilyCostBasedOnRule(ticketFamilyId);
        System.out.println("ticket family rule cost is" + ticketFamilyRuleCost);
        return basicCost + ticketFamilyRuleCost;
    }

    //Including cost: fuelCostPerRoundTrip: Fuel cost + purchased/rental cost
    private double calcFlightScheduleBasicCostPerRoundTrip(Long flightScheduleId) {
        System.out.println("calcFlightFuelCostPerRoundTrip" + calcFlightFuelCostPerRoundTrip(flightScheduleId));
        System.out.println("calcAircraftCostPerRoundTrip" + calcAircraftCostPerRoundTrip(flightScheduleId));

        return calcFlightFuelCostPerRoundTrip(flightScheduleId) + calcAircraftCostPerRoundTrip(flightScheduleId);
    }

    //distance * fuel cost per km
    private double calcFlightFuelCostPerRoundTrip(Long flightScheduleId) {
        try {
            FlightSchedule flightSchedule = getFlightScheduleById(flightScheduleId);
            Airport departureAirport = flightSchedule.getLeg().getDepartAirport();
            Airport arriveAirport = flightSchedule.getLeg().getArrivalAirport();
            double distance = routePlanningSession.distance(departureAirport, arriveAirport);
            System.out.println("distance" + distance);
            System.out.println("calcAircraftFuelCostPerKm" + calcAircraftFuelCostPerKm(getFlightScheduleAircraft(flightScheduleId).getAircraftId()));
            return (distance/100) * calcAircraftFuelCostPerKm(getFlightScheduleAircraft(flightScheduleId).getAircraftId())/(getFlightScheduleAircraft(flightScheduleId).getAircraftType().getTypicalSeating());
        } catch (NoSuchFlightSchedulException | NoSuchAircraftException e) {
            return 0;
        }
    }

    //Aircraft purchase/rental cost per round trip
    private float calcAircraftCostPerRoundTrip(Long flightScheduleId) {
        try {
            FlightSchedule flgihtSchedule = getFlightScheduleById(flightScheduleId);
            float weeklyFreq = flgihtSchedule.getFlight().getWeeklyFrequency();
            Aircraft aircraft = getFlightScheduleAircraft(flightScheduleId);
            int seatQty = getAircraftSeatQty(aircraft);
            float cost = aircraft.getCost();
            float lifetime = aircraft.getLifetime();
            if (cost != 0 && lifetime != 0 && weeklyFreq != 0) {
                System.out.println("AircraftCostPerRoundTrip" + (cost / ((lifetime * (365 / 7)) * weeklyFreq * seatQty)));
                return cost / (((lifetime * (365 / 7)) * weeklyFreq * seatQty)*(getFlightScheduleAircraft(flightScheduleId).getAircraftType().getTypicalSeating())) ;
            }
        } catch (NoSuchFlightSchedulException | NoSuchAircraftException e) {
        }
        return 0;
    }

    private int getAircraftSeatQty(Aircraft aircraft) {
        int seatQty = 0;
        for (AircraftCabinClass ac : aircraft.getAircraftCabinClasses()) {
            seatQty += ac.getSeatQty();
        }
        return seatQty;
    }

    //Aircraft fuel cost per km per seat
    private float calcAircraftFuelCostPerKm(Long flightScheduleId) {
        try {
            Aircraft aircraft = getFlightScheduleAircraft(flightScheduleId);
            int seatQty = getAircraftSeatQty(aircraft);
            AircraftType aircraftType = aircraft.getAircraftType();
            //modified
            return (aircraft.getAvgUnitOilUsage() * (seatQty / 100));
        } catch (NoSuchAircraftException e) {
        }
        return 0;
    }

    //Calculate rule cost: calculate number of free items (0 stands for free item) * basic price for each item.
    private double calTicketFamilyCostBasedOnRule(Long ticketFamilyId) {
        TicketFamily ticketFamily = new TicketFamily();
        try {
            ticketFamily = productDesignSession.getTicketFamilyById(ticketFamilyId);
        } catch (NoSuchTicketFamilyException ex) {
        }

        int numOfFreeItem = 0;
        float basicPrice = 50;

        List<TicketFamilyRule> tfrs = ticketFamily.getTicketFamilyRules();
        for (TicketFamilyRule tfr : tfrs) {
            if (tfr.getRuleValue() == 0) {
                numOfFreeItem++;
            }
        }
        return numOfFreeItem * basicPrice;
    }

    @Override
    public void verifyFlightScheduleBookingClassExistence(Long flightScheduleId) throws NoSuchFlightScheduleBookingClassException {
        if (getFlightScheduleBookingClassJoinTables(flightScheduleId).isEmpty()) {
            throw new NoSuchFlightScheduleBookingClassException(AisMsg.NO_SUCH_BOOKING_CLASS_ERROR);
        }
    }
}
