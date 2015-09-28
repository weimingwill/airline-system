/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.session;

import ams.ais.entity.CabinClass;
import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftType;
import ams.aps.util.exception.EmptyTableException;
import ams.aps.util.helper.AircraftCabinClassHelper;
import javax.ejb.Local;
import java.util.List;

/**
 *
 * @author ChuningLiu
 */
@Local
public interface FleetPlanningSessionLocal {
    public List<AircraftType> getAircraftModels() throws EmptyTableException;
    public List<Aircraft> getFleet(String status);
    public void updateAircraftInfo(Aircraft updatedAircraft);
    public boolean addNewAircraft(Aircraft newAircraft, List<AircraftCabinClassHelper> newAircraftCabinClassHelper);
    public void markAircraftAsRetired(String tailNo);
    public AircraftType getAircraftTypeById(Long id);
    public List<CabinClass> getAllCabinClasses() throws EmptyTableException;
    public CabinClass getCabinClassById(Long id);
}
