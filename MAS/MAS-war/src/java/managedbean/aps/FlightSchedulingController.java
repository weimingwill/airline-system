/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author winga_000
 */
@Named(value = "flightSchedulingController")
@RequestScoped
public class FlightSchedulingController {

    /**
     * Creates a new instance of FlightSchedulingController
     */
    public FlightSchedulingController() {
    }
    
}
