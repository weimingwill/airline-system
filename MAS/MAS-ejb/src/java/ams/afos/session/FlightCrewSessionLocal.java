/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.session;

import ams.afos.entity.FlightCrew;
import ams.aps.util.exception.EmptyTableException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Lewis
 */
@Local
public interface FlightCrewSessionLocal {
    public List<FlightCrew> getAllFlightCrew() throws EmptyTableException;
    
}
