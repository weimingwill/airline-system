/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import ams.aps.entity.Aircraft;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import managedbean.application.MsgController;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

/**
 *
 * @author Lewis
 */
@Named(value = "fleetTableController")
@ViewScoped
public class FleetTableController implements Serializable{

    @Inject
    FleetController fleetController;
    
    @Inject
    MsgController msgController;

    private List<Aircraft> fleetList;
    private Aircraft selectedAircraft;

    /**
     * Creates a new instance of FleetTableController
     */
    public FleetTableController() {
    }

    @PostConstruct
    public void init() {
        System.out.println("FleetTableController(): init()");
        getFleet(null);
    }

    public void getFleet(String status) {
        setFleetList(fleetController.getFleet(status));
    }
    
    public void onRowSelect(SelectEvent event) {
        msgController.addMessage("Aircraft selected: "+((Aircraft) event.getObject()).getAircraftId());
    }
 
    public void onRowUnselect(UnselectEvent event) {

    }
    
    /**
     * @return the fleetList
     */
    public List<Aircraft> getFleetList() {
        return fleetList;
    }

    /**
     * @param fleetList the fleetList to set
     */
    public void setFleetList(List<Aircraft> fleetList) {
        this.fleetList = fleetList;
    }

    /**
     * @return the selectedAircraft
     */
    public Aircraft getSelectedAircraft() {
        return selectedAircraft;
    }

    /**
     * @param selectedAircraft the selectedAircraft to set
     */
    public void setSelectedAircraft(Aircraft selectedAircraft) {
        this.selectedAircraft = selectedAircraft;
    }

}
