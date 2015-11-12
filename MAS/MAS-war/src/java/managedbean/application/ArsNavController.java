/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.application;

import java.io.Serializable;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author weiming
 */
@Named(value = "arsNavController")
@RequestScoped
public class ArsNavController implements Serializable {
    private final String REDIRECT = "?faces-redirect=true";
    private final String ARS_URL = "/views/internal/secured/ars/";
    /**
     * Creates a new instance of ArsNavController
     */
    public ArsNavController() {
    }
    
    public String redirectToARS() {
        return ARS_URL + "index.xhtml" + REDIRECT;
    }
    
    public String redirectToSearchFlightResult() {
        return ARS_URL + "flight_booking/searchResults.xhtml" + REDIRECT;
    }
    
    public String redirectToLoginBooking() {
        return ARS_URL + "flight_booking/loginBooking.xhtml" + REDIRECT;
    }
    
    public String redirectToPassengerInfo() {
        return ARS_URL + "flight_booking/passengerInfo.xhtml" + REDIRECT;
    }
    
    public String redirectToBookingSummary() {
        return ARS_URL + "flight_booking/bookingSummary.xhtml" + REDIRECT;
    } 
    
    
    
}
