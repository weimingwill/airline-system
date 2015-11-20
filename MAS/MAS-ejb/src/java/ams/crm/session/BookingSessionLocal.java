/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.ais.entity.CabinClass;
import ams.ais.entity.FlightScheduleBookingClass;
import ams.ais.entity.TicketFamily;
import ams.ais.util.helper.FlightSchedBookingClsHelper;
import ams.aps.entity.Airport;
import ams.aps.entity.FlightSchedule;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import ams.ars.entity.AddOn;
import ams.ars.entity.PricingItem;
import ams.crm.entity.RegCust;
import ams.crm.util.exception.InvalidPromoCodeException;
import ams.ars.entity.Booking;
import ams.crm.util.exception.NoSuchBookingException;
import ams.crm.util.helper.BookingHelper;
import ams.dcs.entity.Luggage;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author weiming
 */
@Local
public interface BookingSessionLocal {

    public List<FlightSchedule> searchForOneWayFlights(Airport deptAirport, Airport arrAirport, Date deptDate, Map<Long, FlightSchedule> flightSchedMaps, String method) throws NoSuchFlightSchedulException;

    public List<TicketFamily> getFlightSchedLowestTixFams(List<FlightSchedule> flightScheds, CabinClass cabinClass);

//    public List<FlightSchedBookingClsHelper> getOpenedFlightSchedBookingClses(FlightSchedule flightSched, List<TicketFamily> tixFams, String channelName, int numOfTix);
    public List<FlightSchedBookingClsHelper> getAllFlightSchedBookingClses(List<FlightSchedule> flightScheds, List<TicketFamily> tixFams, Airport arrAirport, String channelName, int numOfTix, Map<String, FlightScheduleBookingClass> fbMaps, Map<String, FlightSchedBookingClsHelper> fbHelperMaps);
//
//     public Map<String, FlightSchedBookingClsHelper> getFlightSchedBookingClsHelperMaps
//        (List<FlightSchedule> flightScheds, List<TicketFamily> tixFams, String channelName, int numOfTix);

    public Booking bookingFlight(BookingHelper bookingHelper) throws InvalidPromoCodeException;

    public List<AddOn> getMeals();

    public List<Luggage> getLuggages();

    public AddOn getTravelInsurance();

    public List<PricingItem> getPricingItems();

    public AddOn getAddOnById(long id);

    public Luggage getLuggageById(long id);

    public PricingItem getPricingItemById(long id);

    public void verifyPromoCodeUsability(String promoCodeName, RegCust regCust) throws InvalidPromoCodeException;

    public List<Booking> getBookingsByEmail(String email);

    public List<Booking> getUnClaimedBookingsByEmail(String email);

    public List<Booking> getCurrentBookingsByEmail(String email);

    public Booking getUnClaimedBookingByBookingRef(String bookingRef) throws NoSuchBookingException;

    public Booking getBookingByBookingRef(String bookingRef) throws NoSuchBookingException;

    public Booking getBookingByETicketNo(String eTicketNo) throws NoSuchBookingException;

    public void claimBooking(String bookingRef) throws NoSuchBookingException;

    public List<FlightSchedule> searchFlightStatusByFlightNo(String flightNo, Date date, Airport airport, String choice, Map<Long, FlightSchedule> flightSchedMaps)
            throws NoSuchFlightSchedulException;
}
