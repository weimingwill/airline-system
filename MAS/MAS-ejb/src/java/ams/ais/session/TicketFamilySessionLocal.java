/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.BookingClass;
import ams.ais.entity.CabinClass;
import ams.ais.entity.Rule;
import ams.ais.entity.CabinClassTicketFamily;
import ams.ais.entity.TicketFamily;
import ams.ais.entity.TicketFamilyRule;
import ams.ais.helper.TicketFamilyRuleHelper;
import ams.ais.util.exception.ExistSuchTicketFamilyException;
import ams.ais.util.exception.NeedTicketFamilyException;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchCabinClassTicketFamilyException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.aps.util.exception.EmptyTableException;
import ams.ais.util.helper.BookingClassHelper;
import ams.ais.util.helper.CabinClassTicketFamilyHelper;
import ams.ais.util.helper.TicketFamilyBookingClassHelper;
import ams.aps.entity.Aircraft;
import ams.aps.util.exception.NoSuchAircraftCabinClassException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Bowen
 */
@Local
public interface TicketFamilySessionLocal {

    public void createTicketFamily(String type, String name, String cabinclassname, List<TicketFamilyRuleHelper> newTicketFamilyRuleHelpers)
            throws ExistSuchTicketFamilyException, NoSuchCabinClassException;

    public void verifyTicketFamilyExistence(String type, String name, String cabinclass) throws ExistSuchTicketFamilyException;

    public void deleteTicketFamily(String type, String cabinClassName) throws NoSuchTicketFamilyException;

    public void updateTicketFamily(String oldtype, String oldcabinclass, String type, String name, String cabinclassname)
            throws NoSuchTicketFamilyException, NoSuchCabinClassException, ExistSuchTicketFamilyException;

    public void updateTicketFamilyRuleVlaue(TicketFamilyRule ticketFamilyRule);

    public void deleteTicketFamilyByType(String type) throws NoSuchTicketFamilyException;

    public void updateTicketFamilyByType(long ticketFamilyId, String oldCabinClassName, String type, String name)
            throws ExistSuchTicketFamilyException, NoSuchTicketFamilyException;

    public List<TicketFamily> getAllTicketFamily();

    public List<CabinClass> getAllCabinClass();

    public List<TicketFamily> getAllOtherTicketFamily(String name);

    public List<TicketFamily> getAllOtherTicketFamilyByTypeAndCabinClass(String type, String cabinclassname);

    public List<Rule> getAllRules() throws EmptyTableException;

    public List<TicketFamilyRule> getTicketFamilyRuleByTicketFamilyId(long ticketFamilyId);

    public TicketFamilyRule getTicketFamilyRuleByTicketFamilyType(String ticketFamilyType);

    public TicketFamily getTicketFamilyById(Long id) throws NoSuchTicketFamilyException;

    public TicketFamily getTicketFamilyByName(String ticketFamilyName) throws NoSuchTicketFamilyException;

    public TicketFamily getTicketFamilyByTypeAndCabinClass(String ticketFamilyType, String cabinClassName);

    public List<BookingClass> getTicketFamilyBookingClasses(String cabinClassName, String ticketFamilyName) throws NoSuchBookingClassException;

    public List<BookingClass> getTicketFamilyBookingClasses(Long cabinClassId, Long ticketFamilyId) throws NoSuchBookingClassException;

    public List<BookingClassHelper> getTicketFamilyBookingClassHelpers(String cabinClassName, String ticketFamilyName);

    public List<String> getTicketFamilyBookingClassNames(String cabinClassName, String ticketFamilyName);

    public List<TicketFamilyBookingClassHelper> getTicketFamilyBookingClassHelpers(Long flightScheduleId, Long aircraftId, Long cabinClassId);

    public void assignAircraftTicketFamily(Aircraft aircraft, List<CabinClassTicketFamilyHelper> cabinClassTicketFamilyHelpers)
            throws NeedTicketFamilyException, NoSuchAircraftCabinClassException, NoSuchCabinClassTicketFamilyException;

    public void disLinkCabinClassTicketFamily(List<CabinClassTicketFamily> cabinClassTicketFamilys);
}
