/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.BookingClass;
import ams.ais.entity.CabinClass;
import ams.ais.entity.CabinClassTicketFamily;
import ams.ais.entity.Rule;
import ams.ais.entity.TicketFamily;
import ams.ais.entity.TicketFamilyRule;
import ams.ais.helper.TicketFamilyRuleHelper;
import ams.ais.util.exception.ExistSuchCabinClassNameException;
import ams.ais.util.exception.ExistSuchCabinClassTypeException;
import ams.ais.util.exception.ExistSuchRuleException;
import ams.ais.util.exception.ExistSuchTicketFamilyException;
import ams.ais.util.exception.NeedTicketFamilyException;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchCabinClassTicketFamilyException;
import ams.ais.util.exception.NoSuchRuleException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.ais.util.exception.NoSuchTicketFamilyRuleException;
import ams.ais.util.helper.BookingClassHelper;
import ams.ais.util.helper.CabinClassTicketFamilyHelper;
import ams.ais.util.helper.TicketFamilyBookingClassHelper;
import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftCabinClass;
import ams.aps.util.exception.NoSuchAircraftCabinClassException;
import ams.aps.util.exception.NoSuchAircraftException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author weiming
 */
@Local
public interface ProductDesignSessionLocal {
    //
    //Rules
    //
    public List<Rule> getAllRules();

    public void createRule(String name, String description) throws ExistSuchRuleException;

    public void deleteRule(String name) throws NoSuchRuleException;

    public void verifyRuleExistence(String name) throws ExistSuchRuleException;

    public Rule getRuleByName(String name);

    public void updateRule(Long ruleId, String name, String description) throws NoSuchRuleException, ExistSuchRuleException;

    public AircraftCabinClass getAircraftCabinClassById(Long aircraftId, Long cabinCalssId) throws NoSuchAircraftCabinClassException;

    public Aircraft getAircraftById(Long id) throws NoSuchAircraftException;

    public List<Aircraft> getScheduledAircrafts() throws NoSuchAircraftException;

    public List<CabinClass> getAircraftCabinClasses(Long aircraftId) throws NoSuchCabinClassException;

    public List<TicketFamily> getAircraftTicketFamilys(Long aircraftId) throws NoSuchTicketFamilyException;


    public List<CabinClassTicketFamily> getAircraftCabinClassTicketFamilys(Long aircraftId) throws NoSuchCabinClassTicketFamilyException;

    public void verifyTicketFamilyExistence(Long aircraftId) throws NoSuchCabinClassTicketFamilyException;

    //
    //Cabin Class
    //
    public void createCabinClass(String type, String name) throws ExistSuchCabinClassNameException, ExistSuchCabinClassTypeException;

    public void deleteCabinClass(String name) throws NoSuchCabinClassException;

    public void updateCabinClass(Long cabinclassId, String type, String name) throws NoSuchCabinClassException, ExistSuchCabinClassNameException, ExistSuchCabinClassTypeException;

    public List<CabinClass> getAllCabinClass();

    public CabinClass getCabinClassByName(String name) throws NoSuchCabinClassException;

    public List<TicketFamily> getCabinClassTicketFamilys(String type) throws NoSuchTicketFamilyException;

    public List<TicketFamily> getCabinClassTicketFamilysFromJoinTable(Long aircraftId, Long cabinClassId) throws NoSuchTicketFamilyException;

    public List<String> getCabinClassTicketFamilyNames(String type);

    public CabinClassTicketFamily getCabinClassTicketFamilyJoinTable(Long aircraftId, Long cabinClassId, Long ticketFamilyId) throws NoSuchCabinClassTicketFamilyException;

    public List<CabinClassTicketFamily> getCabinClassTicketFamilyJoinTables(Long aircraftId, Long cabinClassId) throws NoSuchCabinClassTicketFamilyException;

    public List<CabinClassTicketFamilyHelper> getCabinClassTicketFamilyHelpers(Long aircraftId) throws NoSuchCabinClassException, NoSuchTicketFamilyException;
    
    //
    //Ticket Family
    //
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

    public List<TicketFamily> getAllOtherTicketFamilyByTypeAndCabinClass(String type, String cabinclassname);

    public List<TicketFamilyRule> getTicketFamilyRuleByTicketFamilyId(long ticketFamilyId);

    public List<Rule> getRulesByTicketFmailyId(Long ticketFamilyId) throws NoSuchRuleException;
    
    public TicketFamilyRule getTicketFamilyRuleById(Long ticketFamilyId, Long RuleId) 
            throws NoSuchTicketFamilyRuleException;
    
    public TicketFamily getTicketFamilyById(Long id) throws NoSuchTicketFamilyException;

    public TicketFamily getTicketFamilyByTypeAndCabinClass(String ticketFamilyType, String cabinClassName);

    public List<BookingClass> getTicketFamilyBookingClasses(String cabinClassName, String ticketFamilyName) throws NoSuchBookingClassException;

    public List<BookingClassHelper> getTicketFamilyBookingClassHelpers(String cabinClassName, String ticketFamilyName);

    public List<String> getTicketFamilyBookingClassNames(String cabinClassName, String ticketFamilyName);

    public List<TicketFamilyBookingClassHelper> getTicketFamilyBookingClassHelpers(Long flightScheduleId, Long aircraftId, Long cabinClassId);

    public void assignAircraftTicketFamily(Aircraft aircraft, List<CabinClassTicketFamilyHelper> cabinClassTicketFamilyHelpers)
            throws NeedTicketFamilyException, NoSuchAircraftCabinClassException, NoSuchCabinClassTicketFamilyException;

    public void disLinkCabinClassTicketFamily(List<CabinClassTicketFamily> cabinClassTicketFamilys);

    public CabinClassTicketFamily getOriginalCabinClassTicketFamily(Long aircraftId, Long cabinClassId, Long ticketFamilyId);
}
