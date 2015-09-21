/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import ams.aps.entity.AircraftType;
import ams.aps.session.FleetPlanningSessionLocal;
import ams.aps.util.exception.EmptyTableException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author Lewis
 */
@Named(value = "fleetController")
@RequestScoped
public class FleetController {
    @EJB
    private FleetPlanningSessionLocal fleetPlanningSession;
    
    private List<AircraftType> aircraftModels;
    private AircraftType aircraftModel;
    private String msg="";
    
    /**
     * Creates a new instance of FleetController
     */
    public FleetController() {
    }
    
    @PostConstruct
    public void init(){
        getAvailableAircraftModels();
    }
    
    public void getAvailableAircraftModels(){
        try{
            setAircraftModels(fleetPlanningSession.getAircraftModels());
        }catch(EmptyTableException ex){
            setMsg(ex.getMessage());
        }
    }
    
    
    
    public void addNewAircraft(){
        
    }
    
    
    public AircraftType getAircraftModelById(Long id){
        return fleetPlanningSession.getAircraftTypeById(id);
    }
    
    /**
     * @return the aircraftModels
     */
    public List<AircraftType> getAircraftModels() {
        return aircraftModels;
    }

    /**
     * @param aircraftModels the aircraftModels to set
     */
    public void setAircraftModels(List<AircraftType> aircraftModels) {
        this.aircraftModels = aircraftModels;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * @return the aircraftModel
     */
    public AircraftType getAircraftModel() {
        return aircraftModel;
    }

    /**
     * @param aircraftModel the aircraftModel to set
     */
    public void setAircraftModel(AircraftType aircraftModel) {
        this.aircraftModel = aircraftModel;
    }
    
}
