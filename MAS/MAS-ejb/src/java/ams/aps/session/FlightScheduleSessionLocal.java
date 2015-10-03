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
import ams.ais.util.exception.NeedBookingClassException;
import ams.ais.util.exception.NeedTicketFamilyException;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.ais.util.helper.FlightSchCabinClsTicFamBookingClsHelper;
import ams.ais.util.helper.FlightScheduleBookingClassHelper;
import ams.ais.util.helper.SeatClassHelper;
import ams.aps.entity.Aircraft;
import ams.aps.entity.FlightSchedule;
import ams.aps.util.exception.NoSuchAircraftCabinClassException;
import ams.aps.util.exception.NoSuchAircraftException;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import ams.aps.util.exception.NoSuchFlightScheduleBookingClassException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author winga_000
 */
@Local
public interface FlightScheduleSessionLocal {

    public List<FlightSchedule> getAllFilghtSchedules() throws NoSuchFlightSchedulException;

    public FlightSchedule getFlightScheduleById(Long id) throws NoSuchFlightSchedulException;

    public Aircraft getFlightScheduleAircraft(Long id) throws NoSuchAircraftException;

    public List<CabinClass> getFlightScheduleCabinCalsses(Long id) throws NoSuchCabinClassException;

    public void addBookingClass2(Long flightScheduleId, List<FlightScheduleBookingClassHelper> flightScheduleBoookingClassHelpers, List<SeatClassHelper> seatClassHelpers, String method)
            throws NoSuchAircraftCabinClassException, NoSuchTicketFamilyException, NoSuchAircraftException,
            NoSuchCabinClassException, NoSuchFlightSchedulException, NeedTicketFamilyException, NeedBookingClassException;

    public boolean haveBookingClass(Long flightScheduleId);

    public List<BookingClass> getFlightScheduleBookingClasses(Long flightScheduleId) throws NoSuchBookingClassException;

    public List<BookingClass> getFlightScheduleBookingClassesOfTicketFamily(Long flightScheduleId, Long ticketFamilyId) throws NoSuchBookingClassException;
    
//    public void assignFlightScheduleBookingClass(Long flightScheduleId, List<SeatClassHelper> seatClassHelpers, boolean isEdit) throws NoSuchFlightSchedulException, NoSuchFlightScheduleBookingClassException;

    public void assignFlightScheduleBookingClass(Long flightScheduleId, List<FlightSchCabinClsTicFamBookingClsHelper> helpers) throws NoSuchFlightSchedulException, NoSuchFlightScheduleBookingClassException;

    public List<BookingClass> getBookingClassesFromFlightSchCabinClsTicFamBookingClsHelpers(List<FlightSchCabinClsTicFamBookingClsHelper> helpers);
    
    public FlightScheduleBookingClass getFlightScheduleBookingClass(Long flightScheduleId, Long bookingClassId) throws NoSuchFlightScheduleBookingClassException;
    
    public List<FlightScheduleBookingClass> getFlightScheduleBookingClassJoinTables(Long flightScheduleId) throws NoSuchFlightScheduleBookingClassException;
    
    public List<FlightSchCabinClsTicFamBookingClsHelper> getFlightSchCabinClsTicFamBookingClsHelpers(Long flightScheduleId);
    
    public List<FlightScheduleBookingClass> getFlightScheduleBookingClassJoinTablesOfTicketFamily(Long flightScheduleId, Long ticketFamilyId) throws NoSuchFlightScheduleBookingClassException;

    public void verifyTicketFamilyHasBookingClass(TicketFamily ticketFamily, List<SeatClassHelper> seatClassHelpers) throws NeedBookingClassException;

    public boolean dislinkFlightScheduleBookingClass(CabinClassTicketFamily cabinClassTicketFamily);
    
}
