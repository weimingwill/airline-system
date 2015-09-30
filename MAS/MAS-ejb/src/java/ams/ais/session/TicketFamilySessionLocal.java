/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.BookingClass;
import ams.ais.entity.CabinClass;
import ams.ais.entity.TicketFamily;
import ams.ais.util.exception.ExistSuchTicketFamilyException;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.ais.util.helper.BookingClassHelper;
import ams.ais.util.helper.TicketFamilyBookingClassHelper;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Bowen
 */
@Local
public interface TicketFamilySessionLocal {
    public List<TicketFamily> getAllTicketFamily();
    public List<CabinClass> getAllCabinClass();
    public TicketFamily getTicketFamilyById(Long id) throws NoSuchTicketFamilyException;
    public TicketFamily getTicketFamilyByName (String ticketFamilyName) throws NoSuchTicketFamilyException;
    public TicketFamily getTicketFamilyByTypeAndCabinClass (String ticketFamilyType,String cabinClassName);
    public void createTicketFamily(String type, String name,String cabinclass) throws ExistSuchTicketFamilyException, NoSuchCabinClassException;
    public void verifyTicketFamilyExistence(String type, String name,String cabinclass) throws ExistSuchTicketFamilyException;
    public void deleteTicketFamily(String type, String cabinClassName) throws NoSuchTicketFamilyException;
    public void updateTicketFamily(String oldtype,String oldcabinclass, String type, String name, String cabinclassname) throws  NoSuchTicketFamilyException, NoSuchCabinClassException, ExistSuchTicketFamilyException;
    public List<TicketFamily> getAllOtherTicketFamily(String name);
    public List<TicketFamily> getAllOtherTicketFamilyByTypeAndCabinClass(String type, String cabinclassname);
    public List<BookingClass> getTicketFamilyBookingClasses(String cabinClassName, String ticketFamilyName) throws NoSuchBookingClassException;
    public List<String> getTicketFamilyBookingClassNames(String cabinClassName, String ticketFamilyName);
    public List<TicketFamilyBookingClassHelper> getTicketFamilyBookingClassHelpers(Long flightScheduleId, Long aircraftId, Long cabinClassId);
}
