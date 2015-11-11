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
@Named(value = "crmExNavController")
@RequestScoped
public class CrmExNavController implements Serializable {
    private final String REDIRECT = "?faces-redirect=true";
    private final String CRM_URL = "/views/external/unsecured/crm_web/";
    /**
     * Creates a new instance of ArsNavController
     */
    public CrmExNavController() {
    }
    
    public String redirectToCrmEx() {
        return CRM_URL + "index.xhtml" + REDIRECT;
    }
    
    public String redirectToSearchFlightResult() {
        return CRM_URL + "flight_booking/searchResults.xhtml" + REDIRECT;
    }
    
    
    
}
