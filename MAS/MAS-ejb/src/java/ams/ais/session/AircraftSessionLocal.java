/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.CabinClass;
import ams.ais.entity.TicketFamily;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftCabinClass;
import ams.aps.util.exception.NoSuchAircraftCabinClassException;
import ams.aps.util.exception.NoSuchAircraftException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author winga_000
 */
@Local
public interface AircraftSessionLocal {
//    public List<CabinClass> getAircraftCabinClasses(Long id) throws NoSuchCabinClassException, NoSuchAircraftException;
    public AircraftCabinClass getAircraftCabinClassById(Long aircraftId, Long cabinCalssId) throws NoSuchAircraftCabinClassException;
    public Aircraft getAircraftById(Long id) throws NoSuchAircraftException;
    public List<Aircraft> getScheduledAircrafts() throws NoSuchAircraftException;
    public List<CabinClass> getAircraftCabinClasses(Long aircraftId) throws NoSuchCabinClassException;
    public List<TicketFamily> getAircraftTicketFamilys(Long aircraftId) throws NoSuchTicketFamilyException;
}
