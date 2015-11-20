/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.afos;

import ams.afos.entity.FlightCrew;
import ams.afos.entity.FlightDuty;
import ams.afos.session.FlightCrewSessionLocal;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;

/**
 *
 * @author Lewis
 */
@Named(value = "flightCrewReportingBacking")
@ViewScoped
public class FlightCrewReportingBacking implements Serializable{
    @EJB
    private FlightCrewSessionLocal flightCrewSession;
    
    @Inject
    private FlightCrewBacking flightCrewBacking;
    
    private FlightCrew currCrew;
    private List<FlightDuty> crewDuties;
    private FlightDuty selectedDuty;

    /**
     * Creates a new instance of FlightCrewReportingBacking
     */
    public FlightCrewReportingBacking() {
    }
    
    @PostConstruct
    public void init(){
        setCurrCrew(flightCrewBacking.getCurrFlightCrew());
        setCrewDuties(getCrewCurrMonthDuties());
    }
    
    private List<FlightDuty> getCrewCurrMonthDuties(){
        return flightCrewSession.getCrewCurrMonthDuties(currCrew);
    }


    /**
     * @return the currCrew
     */
    public FlightCrew getCurrCrew() {
        return currCrew;
    }

    /**
     * @param currCrew the currCrew to set
     */
    public void setCurrCrew(FlightCrew currCrew) {
        this.currCrew = currCrew;
    }

    /**
     * @return the crewDuties
     */
    public List<FlightDuty> getCrewDuties() {
        return crewDuties;
    }

    /**
     * @param crewDuties the crewDuties to set
     */
    public void setCrewDuties(List<FlightDuty> crewDuties) {
        this.crewDuties = crewDuties;
    }

    /**
     * @return the selectedDuty
     */
    public FlightDuty getSelectedDuty() {
        return selectedDuty;
    }

    /**
     * @param selectedDuty the selectedDuty to set
     */
    public void setSelectedDuty(FlightDuty selectedDuty) {
        this.selectedDuty = selectedDuty;
    }
    
}
