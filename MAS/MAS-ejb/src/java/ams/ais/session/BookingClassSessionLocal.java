/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.BookingClass;
import ams.ais.util.exception.DuplicatePriceException;
import ams.ais.util.exception.ExistSuchBookingClassNameException;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.exception.WrongSumOfBookingClassSeatQtyException;
import ams.ais.util.exception.WrongSumOfTicketFamilySeatQtyException;
import ams.ais.util.helper.BookingClassHelper;
import ams.ais.util.helper.FlightSchCabinClsTicFamBookingClsHelper;
import ams.ais.util.helper.TicketFamilyBookingClassHelper;
import ams.aps.util.exception.NoSuchAircraftCabinClassException;
import ams.aps.util.exception.NoSuchAircraftException;
import ams.aps.util.exception.NoSuchFlightScheduleBookingClassException;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author Tongtong
 */
@Local
public interface BookingClassSessionLocal {

    public List<BookingClass> getAllBookingClasses();

    public BookingClass getBookingClassById(Long id) throws NoSuchBookingClassException;

    public BookingClassHelper getBookingClassHelperById(Long Id) throws NoSuchBookingClassException;

    public List<BookingClassHelper> getBookingClassHelpers(Long flightScheduleId, Long ticketFamilyId);

    public void createBookingClass(String name) throws ExistSuchBookingClassNameException;

    public void verifyBookingClassName(String name) throws ExistSuchBookingClassNameException;

    public void deleteBookingClass(String name) throws NoSuchBookingClassException;

    public BookingClass search(String name) throws NoSuchBookingClassException;

    public void allocateSeats(Long flightScheduleId, List<FlightSchCabinClsTicFamBookingClsHelper> flightHelpers)
            throws NoSuchAircraftException, NoSuchAircraftCabinClassException, NoSuchFlightScheduleBookingClassException,
            WrongSumOfBookingClassSeatQtyException, WrongSumOfTicketFamilySeatQtyException;

    public void allocateBookingClassSeats(Long flightScheduleId, TicketFamilyBookingClassHelper tfbcHelper)
            throws WrongSumOfBookingClassSeatQtyException, NoSuchFlightScheduleBookingClassException;
    
    public void priceBookingClasses(Long flightScheduleId, List<FlightSchCabinClsTicFamBookingClsHelper> flightSchCabinClsTicFamBookingClsHelpers, Map<Long, Float> priceMap)
            throws NoSuchFlightScheduleBookingClassException, DuplicatePriceException;

    public void verifyUniqueBookingClassPrices(Map<Long, Float> priceMap) throws DuplicatePriceException;

    public void verifyBookingClassSeatsSum(Long flightScheduleId, TicketFamilyBookingClassHelper tfbcHelper) 
            throws WrongSumOfBookingClassSeatQtyException;

    public void verifyTicketFamilySeatsSum(Long flightScheduleId, FlightSchCabinClsTicFamBookingClsHelper flightHelper)
            throws WrongSumOfTicketFamilySeatQtyException;
}
