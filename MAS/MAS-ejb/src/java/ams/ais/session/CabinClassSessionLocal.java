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
import ams.aps.helper.AircraftCabinClassId;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Bowen
 */
@Local
public interface CabinClassSessionLocal {
     
   public List<CabinClass> getAllCabinClass();
   public void createCabinClass(String type, String name) throws ExistSuchCabinClassNameException, ExistSuchCabinClassTypeException;
   public void verifyCabinClassExistence(String type, String name) throws ExistSuchCabinClassNameException, ExistSuchCabinClassTypeException;
   public void deleteCabinClass(String name) throws NoSuchCabinClassException;
   public CabinClass getCabinClassByName(String name) throws NoSuchCabinClassException;
   public void updateCabinClass(String oldname, String type, String name) throws NoSuchCabinClassException, ExistSuchCabinClassNameException,ExistSuchCabinClassTypeException;
   public List<String> getAllOtherCabinClassByName (String name);
   public List<String> getAllOtherCabinClassByType (String type);
   public List<CabinClass> getAllOtherCabinClass(String name);
   public List<TicketFamily> getCabinClassTicketFamilys(String type) throws NoSuchTicketFamilyException;
   public List<TicketFamily> getCabinClassTicketFamilysByName(String name) throws NoSuchTicketFamilyException;
   public List<TicketFamily> getCabinClassTicketFamilyFromCTJoinTable(Long aircraftId, Long cabinClassId) throws NoSuchTicketFamilyException;
   public List<String> getCabinClassTicketFamilyNames(String type);
//   public List<TicketFamilyBookingClassHelper> getCabinClassTicketFamilyBookingClassList(String type);
   public CabinClassTicketFamily getCabinClassTicketFamilyJoinTable(Long aircraftId, Long cabinClassId, Long ticketFamilyId) throws NoSuchCabinClassTicketFamilyException;
//   public List<CabinClassTicketFamily> getCabinClassTicketFamilyJoinTables(AircraftCabinClassId aircraftCaCbinClassId) throws NoSuchCabinClassTicketFamilyException;
   public List<CabinClassTicketFamily> getCabinClassTicketFamilyJoinTables(Long aircraftId, Long cabinClassId) throws NoSuchCabinClassTicketFamilyException;
}
