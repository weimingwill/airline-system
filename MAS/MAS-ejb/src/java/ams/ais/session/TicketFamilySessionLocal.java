/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.TicketFamily;
import ams.ais.util.exception.ExistSuchTicketFamilyNameException;
import ams.ais.util.exception.ExistSuchTicketFamilyTypeException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Bowen
 */
@Local
public interface TicketFamilySessionLocal {
    public List<TicketFamily> getAllTicketFamily();
    public TicketFamily getTicketFamilyByName (String ticketFamilyName);
    public void createTicketFamily(String type, String name) throws ExistSuchTicketFamilyNameException, ExistSuchTicketFamilyTypeException;
    public void verifyTicketFamilyExistence(String type, String name) throws ExistSuchTicketFamilyNameException, ExistSuchTicketFamilyTypeException;
    public void deleteTicketFamily(String name) throws NoSuchTicketFamilyException;
    public void updateTicketFamily(String oldname, String type, String name) throws NoSuchTicketFamilyException, ExistSuchTicketFamilyNameException,ExistSuchTicketFamilyTypeException;
    public List<TicketFamily> getAllOtherTicketFamily(String name);
}
