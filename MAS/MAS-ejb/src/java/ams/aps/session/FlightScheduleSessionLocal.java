/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.session;

import ams.ais.entity.CabinClass;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.aps.entity.Aircraft;
import ams.aps.entity.FlightSchedule;
import ams.aps.util.exception.NoSuchAircraftException;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author winga_000
 */
@Local
public interface FlightScheduleSessionLocal {
    public List<FlightSchedule> getAllFilghtSchedules() throws NoSuchFlightSchedulException;
    
    public FlightSchedule getFlightScheduleById(Long id) throws NoSuchFlightSchedulException;
    
    public Aircraft getFlightScheduleAircraft(Long id) throws NoSuchAircraftException;

    public List<CabinClass> getFlightScheduleCabinCalsses(Long id) throws NoSuchCabinClassException;
}
