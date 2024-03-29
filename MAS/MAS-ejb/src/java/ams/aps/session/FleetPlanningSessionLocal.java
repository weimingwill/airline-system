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
import ams.aps.util.exception.ObjectExistsException;
import ams.aps.util.helper.AircraftCabinClassHelper;
import ams.aps.util.helper.AircraftModelFilterHelper;
import ams.aps.util.helper.RetireAircraftFilterHelper;
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
    public boolean updateAircraftInfo(Aircraft updatedAircraft);
    public boolean addNewAircraft(Aircraft newAircraft, List<AircraftCabinClassHelper> newAircraftCabinClassHelper);
    public boolean addNewAircraftModel(AircraftType newAircraftModel);
    public boolean updateAircraftModel(AircraftType updatedAircraftModel);
    public AircraftType getAircraftTypeById(Long id);
    public AircraftType getAircraftTypeByCode(String typeCode);
    public List<CabinClass> getAllCabinClasses() throws EmptyTableException;
    public CabinClass getCabinClassById(Long id);
    public boolean retireSelectedAircrafts(List<Aircraft> aircrafts);
    public List<Aircraft> filterAircraftsForRetire(RetireAircraftFilterHelper filters);
    public void initRetireAicraftFilter(RetireAircraftFilterHelper retireAircraftFilterHelper);
    public List<AircraftType> filterAircraftModels(AircraftModelFilterHelper filters);
    public void initAircraftModelFilter(AircraftModelFilterHelper aircraftModelFilterHelper);
    public List<String> getAllManufacturer();
    public List<String> getAllTypeFamily();
    public List<AircraftCabinClassHelper> getCabinClassByAircaftType(String typeCode);
    public void checkTailNoExist(String tailNo) throws ObjectExistsException;
}
