/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import ams.aps.entity.Flight;
import javax.inject.Named;

import java.io.Serializable;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Lewis
 */
@Named(value = "flightManager")
@SessionScoped
public class FlightManager implements Serializable {
    
    private Flight flight;
    
    
    
    
    /**
     * Creates a new instance of FlightManager
     */
    public FlightManager() {
    }
    
    @PostConstruct
    public void init(){
        getAddedFlight();
        System.out.println("init(): flight = " +flight);
    }
    

    private void getAddedFlight(){
        Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        setFlight((Flight) map.get("flight"));
    }

    /**
     * @return the flight
     */
    public Flight getFlight() {
        return flight;
    }

    /**
     * @param flight the flight to set
     */
    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    
}
