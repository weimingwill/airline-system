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
import ams.ais.entity.helper.FlightScheduleBookingClassId;
import ams.ais.util.exception.DuplicatePriceException;
import ams.ais.util.exception.ExistSuchBookingClassNameException;
import ams.ais.util.exception.NeedBookingClassException;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.WrongSumOfBookingClassSeatQtyException;
import ams.ais.util.exception.WrongSumOfTicketFamilySeatQtyException;
import ams.ais.util.helper.BookingClassHelper;
import ams.ais.util.helper.FlightSchCabinClsTicFamBookingClsHelper;
import ams.ais.util.helper.SeatClassHelper;
import ams.ais.util.helper.TicketFamilyBookingClassHelper;
import ams.aps.entity.Aircraft;
import ams.aps.entity.FlightSchedule;
import ams.aps.util.exception.NoSuchAircraftCabinClassException;
import ams.aps.util.exception.NoSuchAircraftException;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import ams.aps.util.exception.NoSuchFlightScheduleBookingClassException;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author weiming
 */
@Local
public interface RevMgmtSessionLocal {

    public List<BookingClass> getAllBookingClasses();

    public BookingClass getBookingClassById(Long id) throws NoSuchBookingClassException;

    public BookingClassHelper getBookingClassHelperById(Long Id) throws NoSuchBookingClassException;

    public List<BookingClassHelper> getBookingClassHelpers(Long flightScheduleId, Long ticketFamilyId);

    public BookingClass createBookingClass(String name, TicketFamily ticketFamily, String channelName) throws ExistSuchBookingClassNameException;

    public BookingClass getBookingClassByName(String name);

    public void verifyBookingClassName(String name, TicketFamily ticketFamily) throws ExistSuchBookingClassNameException;

    public void deleteBookingClass(String name) throws NoSuchBookingClassException;

    public BookingClass search(String name) throws NoSuchBookingClassException;

    public void allocateSeats(Long flightScheduleId, List<FlightSchCabinClsTicFamBookingClsHelper> flightHelpers)
            throws NoSuchAircraftException, NoSuchAircraftCabinClassException, NoSuchFlightScheduleBookingClassException,
            WrongSumOfBookingClassSeatQtyException, WrongSumOfTicketFamilySeatQtyException;

    public void allocateBookingClassSeats(Long flightScheduleId, TicketFamilyBookingClassHelper tfbcHelper)
            throws WrongSumOfBookingClassSeatQtyException, NoSuchFlightScheduleBookingClassException;

    public void priceBookingClasses(Long flightScheduleId, List<FlightSchCabinClsTicFamBookingClsHelper> flightSchCabinClsTicFamBookingClsHelpers, Map<Long, Float> priceMap)
            throws NoSuchFlightScheduleBookingClassException, DuplicatePriceException;

//    public void setBookingClassDefaultPrice(Long flightScheduleId, Long ticketFamilyId, float ticketFamilyPrice)
//            throws NoSuchFlightScheduleBookingClassException;
    public void updateBookingClass(Long bookingClassId, String bookingClassName) throws NoSuchBookingClassException, ExistSuchBookingClassNameException;

    public List<FlightSchedule> getAllFilghtSchedules() throws NoSuchFlightSchedulException;

    public FlightSchedule getFlightScheduleById(Long id) throws NoSuchFlightSchedulException;

    public Aircraft getFlightScheduleAircraft(Long id) throws NoSuchAircraftException;

    public List<CabinClass> getFlightScheduleCabinCalsses(Long id) throws NoSuchCabinClassException;

    public List<BookingClass> getFlightScheduleBookingClasses(Long flightScheduleId) throws NoSuchBookingClassException;

    public List<BookingClass> getFlightScheduleBookingClassesOfTicketFamily(Long flightScheduleId, Long ticketFamilyId) throws NoSuchBookingClassException;

    public List<BookingClass> getBookingClassesFromFlightSchCabinClsTicFamBookingClsHelpers(List<FlightSchCabinClsTicFamBookingClsHelper> helpers) throws NeedBookingClassException;

    public FlightScheduleBookingClass getFlightScheduleBookingClass(Long flightScheduleId, Long bookingClassId) throws NoSuchFlightScheduleBookingClassException;

    public List<FlightScheduleBookingClass> getFlightScheduleBookingClassJoinTables(Long flightScheduleId) throws NoSuchFlightScheduleBookingClassException;

    public List<FlightSchCabinClsTicFamBookingClsHelper> getFlightSchCabinClsTicFamBookingClsHelpers(Long flightScheduleId);

    public List<FlightScheduleBookingClass> getFlightScheduleBookingClassJoinTablesOfTicketFamily(Long flightScheduleId, Long ticketFamilyId) throws NoSuchFlightScheduleBookingClassException;

    public boolean haveBookingClass(Long flightScheduleId);

    public FlightScheduleBookingClass createFlightSchedBookingCls(FlightSchedule flightSched, BookingClass bookingCls, FlightScheduleBookingClassId flightSchedBookingClsId);

    public void assignFlightScheduleBookingClass(Long flightScheduleId, List<FlightSchCabinClsTicFamBookingClsHelper> helpers)
            throws NoSuchFlightSchedulException, NoSuchFlightScheduleBookingClassException, NeedBookingClassException;

    public void verifyTicketFamilyHasBookingClass(TicketFamily ticketFamily, List<SeatClassHelper> seatClassHelpers) throws NeedBookingClassException;

    public List<FlightScheduleBookingClass> dislinkFlightScheduleBookingClass(CabinClassTicketFamily cabinClassTicketFamily);

    public void dislinkFlightScheduleBookingClass(List<FlightScheduleBookingClass> flightScheduleBookingClasses);

//    public void suggestTicketFamilyPrice(Long flightScheduleId) 
//            throws NoSuchAircraftException, NoSuchCabinClassException, NoSuchCabinClassTicketFamilyException, NoSuchFlightScheduleBookingClassException;
    public double calTicketFamilyPrice(Long flightScheduleId, Long ticketFamilyId);

    public void verifyFlightScheduleBookingClassExistence(Long flightScheduleId) throws NoSuchFlightScheduleBookingClassException;

    public List<TicketFamily> getFlightScheduleTixFams(Long flightScheduleId);
}
