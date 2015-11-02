/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.application;

import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author ChuningLiu
 */
@Named(value = "dcsNavController")
@RequestScoped
public class DcsNavController {

    private final String REDIRECT = "?faces-redirect=true";
    private final String DCS_URL = "/views/internal/secured/dcs/";
    /**
     * Creates a new instance of DcsNavController
     */
    public DcsNavController() {
    }
    
    public String toSearchPassenger() {
        return DCS_URL + "searchPassenger.xhtml" + REDIRECT;
    }
    
    public String toCheckInPassenger() {
        return DCS_URL + "checkInPasseger.xhtml" + REDIRECT;
    }
    
    public String toSelectSeat() {
        return DCS_URL + "selectSeat.xhtml" + REDIRECT;
    }
    
    public String toCheckInLuggage() {
        return DCS_URL + "checkInLuggage.xhtml" + REDIRECT;
    }
}
