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
        return DCS_URL + "checkInPassenger.xhtml" + REDIRECT;
    }
    
    public String toSelectSeat() {
        return DCS_URL + "selectSeat.xhtml" + REDIRECT; //not implemented yet
    }
    
    public String toCheckInLuggage() {
        return DCS_URL + "checkInLuggage.xhtml" + REDIRECT;
    }
    
    public String toPayLuggage() {
        return DCS_URL + "payLuggage.xhtml" + REDIRECT;
    }
    
    public String toAddLuggage() {
        return DCS_URL + "addLuggage.xhtml" + REDIRECT;
    }
    
    public String toBoardPassenger() {
        return DCS_URL + "boardPassenger.xhtml" + REDIRECT;
    }
    
    public String toConfirmBoarding() {
        return DCS_URL + "boardPassenger.xhtml" + REDIRECT;
    }
    
    public String toBoardingPass() {
        return DCS_URL + "boardingPass.xhtml";
    }
    
    public String toSearchCheckedInPNR(){
        return DCS_URL + "searchCheckedInPNR.xhtml" + REDIRECT;
    }
}
