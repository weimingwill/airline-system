/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.afos;

import java.io.Serializable;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author Lewis
 */
@Named(value = "flightDutyBacking")
@ViewScoped
public class FlightDutyBacking implements Serializable {

    /**
     * Creates a new instance of FlightDutyBacking
     */
    public FlightDutyBacking() {
    }

}
