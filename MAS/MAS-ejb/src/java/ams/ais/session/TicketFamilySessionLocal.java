/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.BookingClass;
import ams.ais.entity.CabinClass;
import ams.ais.entity.Rule;
import ams.ais.entity.TicketFamily;
import ams.ais.helper.TicketFamilyRuleHelper;
import ams.ais.util.exception.ExistSuchTicketFamilyException;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.aps.util.exception.EmptyTableException;
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
    public TicketFamily getTicketFamilyByName (String ticketFamilyName);
    public TicketFamily getTicketFamilyByTypeAndCabinClass (String ticketFamilyType,String cabinClassName);
    public void createTicketFamily(String type, String name, String cabinclassname,List<TicketFamilyRuleHelper> newTicketFamilyRuleHelpers) throws ExistSuchTicketFamilyException;
    public void verifyTicketFamilyExistence(String type, String name,String cabinclass) throws ExistSuchTicketFamilyException;
    public void deleteTicketFamily(String type, String cabinClassName) throws NoSuchTicketFamilyException;
    public void updateTicketFamily(String oldtype,String oldcabinclass, String type, String name, String cabinclassname) throws  NoSuchTicketFamilyException, ExistSuchTicketFamilyException;
    public List<TicketFamily> getAllOtherTicketFamily(String name);
    public List<TicketFamily> getAllOtherTicketFamilyByTypeAndCabinClass(String type, String cabinclassname);
    public List<BookingClass> getTicketFamilyBookingClass(String name) throws NoSuchBookingClassException;
    public List<Rule> getAllRules() throws EmptyTableException;
}
