/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.afos;

import ams.afos.entity.FlightCrew;
import ams.afos.session.FlightCrewSessionLocal;
import ams.aps.util.exception.EmptyTableException;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import managedbean.application.MsgController;

/**
 *
 * @author Lewis
 */
@Named(value = "flightCrewBacking")
@ViewScoped
public class FlightCrewBacking implements Serializable{
    @EJB
    private FlightCrewSessionLocal flightCrewSession;
    
    @Inject
    private MsgController msgController;
    
    private List<FlightCrew> flightCrews;
    private List<FlightCrew> filteredCrews;
    private FlightCrew selectedCrew;
    
    /**
     * Creates a new instance of FlightCrewBacking
     */
    public FlightCrewBacking() {
    }
    
    @PostConstruct
    public void init(){
        getAllFlightCrew();
    }
    
    public void getAllFlightCrew(){
        try {
            setFlightCrews(flightCrewSession.getAllFlightCrew());
        } catch (EmptyTableException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
    }

    /**
     * @return the flightCrews
     */
    public List<FlightCrew> getFlightCrews() {
        return flightCrews;
    }

    /**
     * @param flightCrews the flightCrews to set
     */
    public void setFlightCrews(List<FlightCrew> flightCrews) {
        this.flightCrews = flightCrews;
    }

    /**
     * @return the filteredCrews
     */
    public List<FlightCrew> getFilteredCrews() {
        return filteredCrews;
    }

    /**
     * @param filteredCrews the filteredCrews to set
     */
    public void setFilteredCrews(List<FlightCrew> filteredCrews) {
        this.filteredCrews = filteredCrews;
    }

    /**
     * @return the selectedCrew
     */
    public FlightCrew getSelectedCrew() {
        return selectedCrew;
    }

    /**
     * @param selectedCrew the selectedCrew to set
     */
    public void setSelectedCrew(FlightCrew selectedCrew) {
        this.selectedCrew = selectedCrew;
    }


    
}
