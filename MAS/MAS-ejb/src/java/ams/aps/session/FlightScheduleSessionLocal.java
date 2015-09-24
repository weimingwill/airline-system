/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.session;

import ams.aps.entity.FlightSchedule;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import javax.ejb.Local;

/**
 *
 * @author winga_000
 */
@Local
public interface FlightScheduleSessionLocal {
    public FlightSchedule getFlightScheduleById(Long id) throws NoSuchFlightSchedulException;
}
