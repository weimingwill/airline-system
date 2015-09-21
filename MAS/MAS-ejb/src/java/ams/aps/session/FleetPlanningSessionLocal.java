/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.session;

import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftType;
import ams.aps.entity.SeatConfig;
import ams.aps.util.exception.EmptyTableException;
import javax.ejb.Local;
import java.util.List;

/**
 *
 * @author ChuningLiu
 */
@Local
public interface FleetPlanningSessionLocal {
    public List<AircraftType> getAircraftModels() throws EmptyTableException;
    public List<Aircraft> getOwnedAircrafts();
    public List<Aircraft> getObsoleteAircrafts();
    public void updateAircraftInfo(Aircraft updatedAircraft);
    public void addNewAircraft(Aircraft newAircraft);
    public List<SeatConfig> getAircraftConfigs(String aircraftType);
    public void markAircraftAsRetired(String tailNo);
    public AircraftType getAircraftTypeById(Long id);
    public void testQuery();
}
