/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.ais.entity.CabinClass;
import ams.ais.entity.CabinClassTicketFamily;
import ams.ais.entity.FlightScheduleBookingClass;
import ams.ais.entity.TicketFamily;
import ams.ais.session.ProductDesignSessionLocal;
import ams.ais.session.RevMgmtSessionLocal;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.ais.util.helper.FlightSchedBookingClsHelper;
import ams.aps.entity.Airport;
import ams.aps.entity.FlightSchedule;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import ams.aps.util.exception.NoSuchFlightScheduleBookingClassException;
import ams.aps.util.helper.ApsMsg;
import ams.aps.util.helper.FlightSchedStatus;
import ams.ars.entity.AddOn;
import ams.ars.entity.AirTicket;
import ams.ars.entity.Booking;
import ams.ars.entity.PricingItem;
import ams.ars.util.helper.AddOnHelper;
import ams.crm.entity.Customer;
import ams.crm.entity.CustomerList;
import ams.crm.entity.MktCampaign;
import ams.crm.entity.PromotionCode;
import ams.crm.entity.RegCust;
import ams.crm.entity.SelectedCust;
import ams.crm.util.exception.InvalidPromoCodeException;
import ams.crm.util.helper.BookingHelper;
import ams.crm.util.helper.CrmMsg;
import ams.crm.util.helper.CustomerHelper;
import ams.dcs.entity.Luggage;
import ams.crm.util.exception.NoSuchBookingReferenceException;
import ams.crm.util.exception.NoSuchRegCustException;
import ams.crm.util.helper.BookingHelper;
import ams.crm.util.helper.CrmMsg;
import ams.dcs.util.helper.AirTicketStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
import mas.util.helper.DateHelper;
import mas.util.helper.NumberGenerator;

/**
 *
 * @author weiming
 */
@Stateless
public class BookingSession implements BookingSessionLocal {

    @PersistenceContext(unitName = "MAS-ejbPU")
    private EntityManager em;

    @EJB
    private RevMgmtSessionLocal revMgmtSession;
    @EJB
    private ProductDesignSessionLocal productDesignSession;

    @Override
    public List<Booking> getBookingsByEmail(String email) {

        Query query = em.createQuery("SELECT c FROM Booking c WHERE c.email=:inemail");
        query.setParameter("inemail", email);
        List<Booking> bookings = new ArrayList<>();
        try {
            bookings = (List<Booking>) query.getResultList();
        } catch (NoResultException ex) {
        }
        return bookings;
    }

    @Override
    public List<Booking> getCurrentBookingsByEmail(String email) {
        Query query = em.createQuery("SELECT DISTINCT b FROM Booking b, IN(b.airTickets) at WHERE b.email=:inemail AND at.flightSchedBookingClass.flightSchedule.departDate > CURRENT_TIMESTAMP ORDER BY at.flightSchedBookingClass.flightSchedule.departDate");
        query.setParameter("inemail", email);
        List<Booking> bookings = new ArrayList<>();
        try {
            bookings = (List<Booking>) query.getResultList();
        } catch (NoResultException ex) {
        }
        System.out.print("select current bookings" + bookings);
        return bookings;
    }

    @Override
    public List<Booking> getUnClaimedBookingsByEmail(String email) {
        Query query = em.createQuery("SELECT c FROM Booking c WHERE c.email=:inemail AND c.claimed=FALSE");
        query.setParameter("inemail", email);
        List<Booking> bookings = new ArrayList<>();
        try {
            bookings = (List<Booking>) query.getResultList();
        } catch (NoResultException ex) {
        }
        return bookings;
    }

    @Override
    public void updateBooking(String bookingRef) throws NoSuchBookingReferenceException {
        Booking b = getBookingByBookingRef(bookingRef);
        if (b == null) {
            throw new NoSuchBookingReferenceException(CrmMsg.NO_SUCH_BOOKING_REFERENCE_ERROR);
        } else {
            b.setClaimed(true);
            em.merge(b);
            em.flush();
        }
    }

    @Override
    public Booking getBookingByBookingRef(String bookingRef) throws NoSuchBookingReferenceException {
        Query query = em.createQuery("SELECT b FROM Booking b WHERE b.referenceNo = :bookingRef AND b.claimed=FALSE");
        query.setParameter("bookingRef", bookingRef);
        Booking r = null;
        try {
            r = (Booking) query.getSingleResult();
        } catch (NoResultException ex) {
            throw new NoSuchBookingReferenceException(CrmMsg.NO_SUCH_BOOKING_REFERENCE_ERROR);
        }
        return r;
    }

    @Override
    public List<FlightSchedule> searchForOneWayFlights(Airport deptAirport, Airport arrAirport, Date deptDate, Map<Long, FlightSchedule> flightSchedMaps, String status) throws NoSuchFlightSchedulException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(deptDate);
        DateHelper.setToStartOfDay(calendar);
        Date date = calendar.getTime();
        DateHelper.setToEndOfDay(calendar);
        Date nextDate = calendar.getTime();
        List<FlightSchedule> directFlightScheds = getFlights(deptAirport, arrAirport, date, nextDate, true, status);
        List<FlightSchedule> inDirectFlightScheds = getFlights(deptAirport, arrAirport, date, nextDate, false, status);

        if (directFlightScheds.isEmpty() && inDirectFlightScheds.isEmpty()) {
            throw new NoSuchFlightSchedulException(ApsMsg.NO_SUCH_FLIGHT_SHCEDULE_ERROR);
        }

        Set<FlightSchedule> flightSchedsSet = new HashSet<>();
        List<FlightSchedule> flightSchedules = new ArrayList<>();
        flightSchedsSet.addAll(directFlightScheds);
        flightSchedsSet.addAll(inDirectFlightScheds);
        flightSchedules.addAll(flightSchedsSet);
        getInDirectFlightMaps(inDirectFlightScheds, arrAirport, flightSchedMaps);
        return flightSchedules;
    }

    private List<FlightSchedule> getFlights(Airport deptAirport, Airport arrAirport, Date date, Date nextDate, boolean isDirect, String status) {
        Query query;
        if (FlightSchedStatus.RELEASE.equals(status)) {
            if (isDirect) {
                query = em.createQuery("SELECT f FROM FlightSchedule f WHERE f.leg.departAirport.airportName = :inDeptAirport AND f.leg.arrivalAirport.airportName = :inArrAirport AND f.departDate BETWEEN :inDate AND :inNextDate AND f.deleted = FALSE AND f.status = :inStatus");
            } else {
                query = em.createQuery("SELECT f FROM FlightSchedule f, FlightSchedule f2 WHERE f.leg.departAirport.airportName = :inDeptAirport AND f.leg.arrivalAirport.airportName = f2.leg.departAirport.airportName AND f2.leg.arrivalAirport.airportName = :inArrAirport AND f.departDate BETWEEN :inDate AND :inNextDate AND f.deleted = FALSE AND f2.deleted = FALSE AND f.status = :inStatus");
            }
        } else {
            if (isDirect) {
                query = em.createQuery("SELECT f FROM FlightSchedule f WHERE f.leg.departAirport.airportName = :inDeptAirport AND f.leg.arrivalAirport.airportName = :inArrAirport AND f.departDate BETWEEN :inDate AND :inNextDate AND f.deleted = FALSE AND f.status <> :inStatus");
            } else {
                query = em.createQuery("SELECT f FROM FlightSchedule f, FlightSchedule f2 WHERE f.leg.departAirport.airportName = :inDeptAirport AND f.leg.arrivalAirport.airportName = f2.leg.departAirport.airportName AND f2.leg.arrivalAirport.airportName = :inArrAirport AND f.departDate BETWEEN :inDate AND :inNextDate AND f.deleted = FALSE AND f2.deleted = FALSE AND f.status <> :inStatus");
            }
        }
        query.setParameter("inStatus", status);
        query.setParameter("inDeptAirport", deptAirport.getAirportName());
        query.setParameter("inArrAirport", arrAirport.getAirportName());
        query.setParameter("inDate", date);
        query.setParameter("inNextDate", nextDate);
        List<FlightSchedule> flightScheds = new ArrayList<>();
        try {
            flightScheds = (List<FlightSchedule>) query.getResultList();
        } catch (NoResultException e) {
        }
        return flightScheds;
    }

    private void getInDirectFlightMaps(List<FlightSchedule> flightScheds, Airport arrAirport, Map<Long, FlightSchedule> flightSchedMaps) {
        for (FlightSchedule flightSched : flightScheds) {
            flightSchedMaps.put(flightSched.getFlightScheduleId(), getNextInDirectFlight(flightSched, arrAirport));
        }
    }

    private FlightSchedule getNextInDirectFlight(FlightSchedule flightSched, Airport arrAirport) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(flightSched.getArrivalDate());
        calendar.add(Calendar.MINUTE, 90); //The depart time of the consecutive flight should be more than 1.5h later than the arrival time of the previous one.
        Date startDate = calendar.getTime();
        calendar.add(Calendar.HOUR, 23);
        Date endDate = calendar.getTime();

        Query query = em.createQuery("SELECT f FROM FlightSchedule f WHERE f.leg.departAirport.airportName = :inDeptAirport AND f.leg.arrivalAirport.airportName = :inArrAirport AND f.departDate BETWEEN :inStartDate AND :inEndDate AND f.deleted = FALSE AND f.status = :inStatus");
        query.setParameter("inDeptAirport", flightSched.getLeg().getArrivalAirport().getAirportName());
        query.setParameter("inArrAirport", arrAirport.getAirportName());
        query.setParameter("inStartDate", startDate);
        query.setParameter("inEndDate", endDate);
        query.setParameter("inStatus", FlightSchedStatus.RELEASE);
        FlightSchedule flightSchedule = null;
        try {
            List<FlightSchedule> flightSchedules = (List<FlightSchedule>) query.getResultList();
            if (flightSchedules.size() > 0) {
                flightSchedule = flightSchedules.get(0);
            }
//            flightSchedule = (FlightSchedule) query.getSingleResult();
        } catch (NoResultException e) {
        }
        return flightSchedule;
    }

    @Override
    public List<TicketFamily> getFlightSchedLowestTixFams(List<FlightSchedule> flightScheds, CabinClass selectedCabinClass) {
        List<TicketFamily> tixFams = new ArrayList<>();
        try {
            for (FlightSchedule flightSched : flightScheds) {
                for (CabinClass cabinClass : revMgmtSession.getFlightScheduleCabinCalsses(flightSched.getFlightScheduleId())) {
                    if (selectedCabinClass.equals(cabinClass)) {
                        tixFams = productDesignSession.getCabinClassTicketFamilysFromJoinTable(flightSched.getAircraft().getAircraftId(), cabinClass.getCabinClassId());
//                        addTixFams(tixFams, flightSched, cabinClass);
                    }
                }
            }
        } catch (NoSuchCabinClassException | NoSuchTicketFamilyException e) {
        }
        return tixFams;
    }

//    @Override
//    public Map<String, FlightSchedBookingClsHelper> getFlightSchedBookingClsHelperMaps(List<FlightSchedule> flightScheds, List<TicketFamily> tixFams, String channelName, int numOfTix) {
//        Map<String, FlightSchedBookingClsHelper> flightSchedBookingClsMaps = new HashMap<>();
//        for (FlightSchedule flightSched : flightScheds) {
//            for (FlightSchedBookingClsHelper fbHelper : getOpenedFlightSchedBookingClses(flightSched, tixFams, channelName, numOfTix)) {
//                flightSchedBookingClsMaps.put(fbHelper.getFlightSchedBookingCls().getFlightScheduleBookingClassId().toString(), fbHelper);
//            }
//        }
//        return flightSchedBookingClsMaps;
//    }
    @Override
    public List<FlightSchedBookingClsHelper> getAllFlightSchedBookingClses(List<FlightSchedule> flightScheds, List<TicketFamily> tixFams, Airport arrAirport, String channelName, int numOfTix, Map<String, FlightScheduleBookingClass> fbMaps, Map<String, FlightSchedBookingClsHelper> fbHelperMaps) {
        List<FlightSchedBookingClsHelper> flightSchedBookingClsHelpers = new ArrayList<>();
        for (FlightSchedule flightSched : flightScheds) {
            for (TicketFamily tixFam : tixFams) {
                flightSchedBookingClsHelpers.add(getOpenedFlightSchedBookingCls(flightSched, tixFam, arrAirport, channelName, numOfTix, fbMaps, fbHelperMaps));
            }
        }
        return flightSchedBookingClsHelpers;
    }

//    private List<FlightSchedBookingClsHelper> getOpenedFlightSchedBookingClses(FlightSchedule flightSched, List<TicketFamily> tixFams, String channelName, int numOfTix) {
//        List<FlightSchedBookingClsHelper> flightSchedBookingClsHelpers = new ArrayList<>();
//        for (TicketFamily tixFam : tixFams) {
//            flightSchedBookingClsHelpers.add(getOpenedFlightSchedBookingCls(flightSched, tixFam, channelName, numOfTix, new HashMap<>()));
//        }
//        return flightSchedBookingClsHelpers;
//    }
    private FlightSchedBookingClsHelper getOpenedFlightSchedBookingCls(FlightSchedule flightSched, TicketFamily tixFam, Airport arrAirport, String channelName, int numOfTix, Map<String, FlightScheduleBookingClass> fbMaps, Map<String, FlightSchedBookingClsHelper> fbHelperMaps) {
        Query query = em.createQuery("SELECT fb FROM FlightScheduleBookingClass fb, BookingClass b "
                + "WHERE fb.flightScheduleBookingClassId.flightScheduleId = :inFlightScheduleId "
                + "AND fb.flightScheduleBookingClassId.bookingClassId = b.bookingClassId "
                + "AND b.ticketFamily.ticketFamilyId = :inTicketFamilyId "
                + "AND fb.closed = FALSE "
                + "AND b.channel = :inChannel "
                + "AND b.deleted = FALSE "
                + "AND b.ticketFamily.deleted = FALSE "
                + "AND fb.deleted = FALSE");
        query.setParameter("inFlightScheduleId", flightSched.getFlightScheduleId());
        query.setParameter("inTicketFamilyId", tixFam.getTicketFamilyId());
        query.setParameter("inChannel", channelName);
        FlightSchedBookingClsHelper fbHelper = new FlightSchedBookingClsHelper();
        try {
            FlightScheduleBookingClass flightSchedBookingCls = (FlightScheduleBookingClass) query.getSingleResult();
            fbHelper.setFlightSchedBookingCls(flightSchedBookingCls);
            fbHelper.setTicketFamily(tixFam);
            fbHelper.setCabinClass(tixFam.getCabinClass());
            FlightSchedule nextFlightSched = getNextInDirectFlight(flightSched, arrAirport);
            int remainedSeatQty = getRemainedSeatQty(flightSched, tixFam);
            float price = flightSchedBookingCls.getPrice();
            if (nextFlightSched != null) {
                FlightScheduleBookingClass nextFb = revMgmtSession.getFlightScheduleBookingClass(nextFlightSched.getFlightScheduleId(), flightSchedBookingCls.getBookingClass().getBookingClassId());
                remainedSeatQty = getSmallerInt(remainedSeatQty, getRemainedSeatQty(nextFlightSched, tixFam));
                price += nextFb.getPrice();
                fbMaps.put(flightSchedBookingCls.getFlightScheduleBookingClassId().toString(), nextFb);
            }
            fbHelper.setPrice(price);
            fbHelper.setRemainedSeatQty(remainedSeatQty);
            if (remainedSeatQty > numOfTix) {
                fbHelper.setAvailable(true);
            } else {
                fbHelper.setAvailable(false);
            }

            if (remainedSeatQty > 10) {
                fbHelper.setShowLeftSeatQty(false);
            } else {
                fbHelper.setShowLeftSeatQty(true);
            }

            fbHelperMaps.put(flightSchedBookingCls.getFlightScheduleBookingClassId().toString(), fbHelper);

        } catch (NoResultException e) {
        } catch (NoSuchFlightScheduleBookingClassException ex) {
        }
        return fbHelper;
    }

    private int getRemainedSeatQty(FlightSchedule flightSched, TicketFamily tixFam) {
        int totalSeatQty = 0;
        try {
            CabinClassTicketFamily cabinClassTixFam = productDesignSession.getCabinClassTicketFamilyJoinTable(flightSched.getAircraft().getAircraftId(), tixFam.getCabinClass().getCabinClassId(), tixFam.getTicketFamilyId());
            totalSeatQty = cabinClassTixFam.getSeatQty();
            for (FlightScheduleBookingClass fb : revMgmtSession.getFlightScheduleBookingClassJoinTablesOfTicketFamily(flightSched.getFlightScheduleId(), tixFam.getTicketFamilyId())) {
                totalSeatQty = totalSeatQty - fb.getSoldSeatQty();
            }
        } catch (Exception e) {
        }
        return totalSeatQty;
    }

    private int getSmallerInt(int a, int b) {
        if (a > b) {
            return b;
        } else {
            return a;
        }
    }

//    private List<TicketFamily> addTixFams(List<TicketFamily> tixFams, FlightSchedule flightSched, CabinClass cabinClass) {
//        try {
//            List<TicketFamily> ticketFamilys = productDesignSession.getCabinClassTicketFamilysFromJoinTable(flightSched.getAircraft().getAircraftId(), cabinClass.getCabinClassId());
//            for (TicketFamily ticketFamily : ticketFamilys) {
//                if (tixFams.size() < 4) {
//                    tixFams.add(ticketFamily);
//                } else {
//                    break;
//                }
//            }
//        } catch (NoSuchTicketFamilyException e) {
//        }
//        return tixFams;
//    }
    @Override
    public Booking bookingFlight(BookingHelper bookingHelper) throws InvalidPromoCodeException {

        //create booking
        Booking booking = bookingHelper.getBooking();
        System.out.println("Booking: " + booking.getEmail());
        booking.setCreatedTime(new Date());
        booking.setChannel(bookingHelper.getChannel());
        booking.setPaid(true);

        //Update flightSchedule booking class
        booking.setPrice(bookingHelper.getTotalPrice());
        em.persist(booking);
        em.flush();

        List<CustomerHelper> customerHelpers = new ArrayList<>();
        //Customer
        customerHelpers.addAll(bookingHelper.getCustomers());

        //Airticket
        List<AirTicket> airTickets = createAirTickets(bookingHelper.getFlightSchedBookingClses(), customerHelpers, booking);

        String eTicketNo = "MLA";
        eTicketNo += NumberGenerator.airTicketFormNumber();
        eTicketNo += NumberGenerator.airTicketSerialNumber();

        //Need to check whether the number exist or not, if yes, loop to create a new one. But just generate in current case.
        booking.seteTicketNo(eTicketNo);
        booking.setReferenceNo(NumberGenerator.bookingReference());
        booking.setAirTickets(airTickets);

        //Customer value calc
        //Select seat
        //Promotion: 1. for all users, no need login. 2. for specific users, need login.
        String promoCodeName = bookingHelper.getPromoCode();

        booking.setPromoPrice(0.0);
        if (!promoCodeName.equals("")) {
            RegCust regCust = bookingHelper.getCustomers().get(0).getRegCust();
            verifyPromoCodeUsability(promoCodeName, regCust);
            PromotionCode promoCode = getPromotionCodeByName(promoCodeName);
            //Use the promoCode, set promoPrice and deduct from total price.
            double promodePrice = promoCode.getPromoValue();
            booking.setPromoPrice(promodePrice);
            booking.setPrice(booking.getPrice() - promodePrice);

            //Remove customer from customer lists
            updateMktCampaignCustomerList(promoCode.getMktCampaign(), regCust);
        }
        em.merge(booking);
        em.flush();

        //Generate pnr
        return booking;
    }

    @Override
    public void verifyPromoCodeUsability(String promoCodeName, RegCust regCust) throws InvalidPromoCodeException {
        if (!"".equals(promoCodeName)) {
            PromotionCode promoCode = getPromotionCodeByName(promoCodeName);
            MktCampaign mktCampaign = promoCode.getMktCampaign();
            if (mktCampaign.getEndTime().before(new Date())) {
                throw new InvalidPromoCodeException(CrmMsg.PROMOCODE_EXPIRE_ERROR);
            }
            if (mktCampaign.getCustomerLists() != null && regCust == null) {
                throw new InvalidPromoCodeException(CrmMsg.PROMOCODE_WITHOUT_LOGIN_ERROR);
            }
            if (mktCampaign.getCustomerLists() != null && regCust != null) {
                boolean hasRight = false;
                for (CustomerList customerList : mktCampaign.getCustomerLists()) {
                    for (SelectedCust selectedCust : customerList.getSelectedCusts()) {
                        if (regCust.getEmail().equals(selectedCust.getEmail())) {
                            hasRight = true;
                            break;
                        }
                    }
                }
                if (!hasRight) {
                    throw new InvalidPromoCodeException(CrmMsg.PROMOCODE_UNAUTHORISED_ERROR);
                }
            }
        } else {
        }
    }

    private void updateMktCampaignCustomerList(MktCampaign mktCampaign, RegCust regCust) {
        mktCampaign = em.find(MktCampaign.class, mktCampaign.getId());
        List<CustomerList> newCustomerLists = new ArrayList<>();
        List<CustomerList> customerLists = mktCampaign.getCustomerLists();
        for (CustomerList customerList : customerLists) {
            List<SelectedCust> selectedCusts = customerList.getSelectedCusts();
            for (SelectedCust selectedCust : customerList.getSelectedCusts()) {
                if (regCust.getEmail().equals(selectedCust.getEmail())) {
                    selectedCusts.remove(selectedCust);
                }
            }
            customerList.setSelectedCusts(selectedCusts);
            newCustomerLists.add(customerList);
        }
        mktCampaign.setCustomerLists(newCustomerLists);
        em.merge(mktCampaign);
        em.flush();
    }

    private PromotionCode getPromotionCodeByName(String name) {
        Query query = em.createQuery("SELECT p FROM PromotionCode p WHERE p.name = :name");
        query.setParameter("name", name);
        PromotionCode promotionCode = null;
        try {
            promotionCode = (PromotionCode) query.getSingleResult();
        } catch (Exception e) {
        }
        return promotionCode;
    }

    private List<AirTicket> createAirTickets(List<FlightScheduleBookingClass> fbs, List<CustomerHelper> customerHelpers, Booking booking) {
        List<AirTicket> airTickets = new ArrayList<>();
        for (CustomerHelper customerHelper : customerHelpers) {
            List<AirTicket> custAirTickets = new ArrayList<>();

            List<AddOn> addOns = new ArrayList<>();
            addOns.add(getAddOnByDescription(customerHelper.getMeal().getDescription()));
            if (customerHelper.isInsurance()) {
                addOns.add(getTravelInsurance());
            }
            Luggage luggage = getLuggageByMaxWeight(customerHelper.getLuggage().getMaxWeight());

            Customer customer = createCustomer(customerHelper);
            for (FlightScheduleBookingClass fb : fbs) {
                fb = updateFlightSchedBookingCls(fb);
                AirTicket airTicket = createAirTicket(fb, customer, booking, addOns, luggage);
                airTickets.add(airTicket);
                custAirTickets.add(airTicket);
            }
            customer.setAirTickets(custAirTickets);
        }
        return airTickets;
    }

    private AirTicket createAirTicket(FlightScheduleBookingClass fb, Customer customer, Booking booking, List<AddOn> addOn, Luggage luggage) {
        AirTicket airTicket = new AirTicket();
        airTicket.setStatus(AirTicketStatus.PAID);
        airTicket.setCustomer(customer);
        airTicket.setBooking(booking);
        airTicket.setFlightSchedBookingClass(fb);
        airTicket.setAddOns(addOn);
        airTicket.setPurchasedLuggage(luggage);
        airTicket.setPricingItems(getPricingItems());
        em.persist(airTicket);
        em.flush();
        return airTicket;
    }

    private Customer createCustomer(CustomerHelper customerHelper) {
        Customer cust = verifyCustomerExistence(customerHelper.getCustomer());
        if (cust == null) {
            em.persist(customerHelper.getCustomer());
            em.flush();
            return customerHelper.getCustomer();
        } else {
            return cust;
        }
    }

    private FlightScheduleBookingClass updateFlightSchedBookingCls(FlightScheduleBookingClass flightSchedBookingCls) {
        flightSchedBookingCls = em.find(FlightScheduleBookingClass.class, flightSchedBookingCls.getFlightScheduleBookingClassId());
        flightSchedBookingCls.setSoldSeatQty(flightSchedBookingCls.getSoldSeatQty() + 1);
        em.merge(flightSchedBookingCls);
        em.flush();
        return flightSchedBookingCls;
    }

    private Customer verifyCustomerExistence(Customer customer) {
        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.passportNo = :passportNo");
        query.setParameter("passportNo", customer.getPassportNo());
        Customer cust = null;
        try {
            cust = (Customer) query.getSingleResult();
        } catch (NoResultException e) {
        }
        return cust;
    }

    @Override
    public List<AddOn> getMeals() {
        Query query = em.createQuery("SELECT a FROM AddOn a WHERE a.name = :name");
        query.setParameter("name", AddOnHelper.MEAL);
        List<AddOn> addOns = new ArrayList<>();
        try {
            addOns = (List<AddOn>) query.getResultList();
        } catch (Exception e) {
        }
        return addOns;
    }

    @Override
    public List<Luggage> getLuggages() {
        Query query = em.createQuery("SELECT l FROM Luggage l WHERE l.name = :name");
        query.setParameter("name", AddOnHelper.LUGGAGE);
        List<Luggage> luggages = new ArrayList<>();
        try {
            luggages = (List<Luggage>) query.getResultList();
        } catch (Exception e) {
        }
        return luggages;
    }

    @Override
    public AddOn getTravelInsurance() {
        Query query = em.createQuery("SELECT a FROM AddOn a WHERE a.name = :name");
        query.setParameter("name", AddOnHelper.INSURANCE);
        AddOn addOn = new AddOn();
        try {
            addOn = (AddOn) query.getSingleResult();
        } catch (Exception e) {
        }
        return addOn;
    }

    @Override
    public List<PricingItem> getPricingItems() {
        Query query = em.createQuery("SELECT p FROM PricingItem p");
        List<PricingItem> pricingItems = new ArrayList<>();
        try {
            pricingItems = (List<PricingItem>) query.getResultList();
        } catch (Exception e) {
        }
        return pricingItems;
    }

    @Override
    public AddOn getAddOnById(long id) {
        return em.find(AddOn.class, id);
    }

    @Override
    public Luggage getLuggageById(long id) {
        return em.find(Luggage.class, id);
    }

    @Override
    public PricingItem getPricingItemById(long id) {
        return em.find(PricingItem.class, id);
    }

    private AddOn getAddOnByDescription(String description) {
        Query query = em.createQuery("SELECT a FROM AddOn a WHERE a.description = :description");
        query.setParameter("description", description);
        AddOn addOn = new AddOn();
        try {
            addOn = (AddOn) query.getSingleResult();
        } catch (Exception e) {
        }
        return addOn;
    }

    private Luggage getLuggageByMaxWeight(double maxWeight) {
        Query query = em.createQuery("SELECT l FROM Luggage l WHERE l.maxWeight = :maxWeight");
        query.setParameter("maxWeight", maxWeight);
        Luggage luggage = new Luggage();
        try {
            luggage = (Luggage) query.getSingleResult();
        } catch (Exception e) {
        }
        return luggage;
    }

    public Booking getBookingByBookingRefString(String bookingRef) {
        Booking booking = new Booking();
        return booking;
    }

}
