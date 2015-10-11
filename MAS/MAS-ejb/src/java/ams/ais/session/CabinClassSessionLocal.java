/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.CabinClass;
import ams.ais.entity.CabinClassTicketFamily;
import ams.ais.entity.TicketFamily;
import ams.ais.util.exception.ExistSuchCabinClassNameException;
import ams.ais.util.exception.ExistSuchCabinClassTypeException;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchCabinClassTicketFamilyException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.ais.util.helper.CabinClassTicketFamilyHelper;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Bowen
 */
@Local
public interface CabinClassSessionLocal {

    public void createCabinClass(String type, String name) throws ExistSuchCabinClassNameException, ExistSuchCabinClassTypeException;

    public void verifyCabinClassExistence(String type, String name) throws ExistSuchCabinClassNameException, ExistSuchCabinClassTypeException;

    public void deleteCabinClass(String name) throws NoSuchCabinClassException;

    public void updateCabinClass(Long cabinclassId, String type, String name) throws NoSuchCabinClassException, ExistSuchCabinClassNameException, ExistSuchCabinClassTypeException;

    public List<CabinClass> getAllCabinClass();

    public CabinClass getCabinClassByName(String name) throws NoSuchCabinClassException;

    public List<String> getAllOtherCabinClassByName(String name);

    public List<String> getAllOtherCabinClassByType(String type);

    public List<CabinClass> getAllOtherCabinClass(String name);

    public List<TicketFamily> getCabinClassTicketFamilys(String type) throws NoSuchTicketFamilyException;

    public List<TicketFamily> getCabinClassTicketFamilysByName(String name) throws NoSuchTicketFamilyException;

    public List<TicketFamily> getCabinClassTicketFamilysFromJoinTable(Long aircraftId, Long cabinClassId) throws NoSuchTicketFamilyException;

    public List<String> getCabinClassTicketFamilyNames(String type);

    public CabinClassTicketFamily getCabinClassTicketFamilyJoinTable(Long aircraftId, Long cabinClassId, Long ticketFamilyId) throws NoSuchCabinClassTicketFamilyException;

    public List<CabinClassTicketFamily> getCabinClassTicketFamilyJoinTables(Long aircraftId, Long cabinClassId) throws NoSuchCabinClassTicketFamilyException;

    public List<CabinClassTicketFamilyHelper> getCabinClassTicketFamilyHelpers(Long aircraftId) throws NoSuchCabinClassException, NoSuchTicketFamilyException;
}
