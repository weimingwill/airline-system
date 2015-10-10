/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 *
 * @author winga_000
 */
@Named(value = "flightScheduleManager")
@SessionScoped
public class FlightScheduleManager implements Serializable {

    /**
     * Creates a new instance of FlightScheduleManager
     */
    public FlightScheduleManager() {
    }
    
}
