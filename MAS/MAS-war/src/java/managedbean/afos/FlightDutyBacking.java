/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.afos;

import ams.afos.entity.FlightDuty;
import ams.afos.session.FlightCrewMgmtSessionLocal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author Lewis
 */
@Named(value = "flightDutyBacking")
@ViewScoped
public class FlightDutyBacking implements Serializable{
    @EJB
    private FlightCrewMgmtSessionLocal flightCrewMgmtSession;

    private List<FlightDuty> flightDuties;
    private FlightDuty selectedFlightDuty;
    
    
    @PostConstruct
    public void init(){
        setFlightDuties(new ArrayList());
    }
    
    
    public void generateFlightDuty(){
        System.out.println("FlightDutyBacking: generateFlightDuty");
        setFlightDuties(flightCrewMgmtSession.generateFlightDuties());
    }

    /**
     * Creates a new instance of FlightDutyBacking
     */
    public FlightDutyBacking() {
    }

    /**
     * @return the flightDuties
     */
    public List<FlightDuty> getFlightDuties() {
        return flightDuties;
    }

    /**
     * @param flightDuties the flightDuties to set
     */
    public void setFlightDuties(List<FlightDuty> flightDuties) {
        this.flightDuties = flightDuties;
    }

    /**
     * @return the selectedFlightDuty
     */
    public FlightDuty getSelectedFlightDuty() {
        return selectedFlightDuty;
    }

    /**
     * @param selectedFlightDuty the selectedFlightDuty to set
     */
    public void setSelectedFlightDuty(FlightDuty selectedFlightDuty) {
        this.selectedFlightDuty = selectedFlightDuty;
    }

}
