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
 * @author winga_000
 */
@Named(value = "aasNavController")
@RequestScoped
public class AasNavController implements Serializable {

    private final String REDIRECT = "?faces-redirect=true";
    private final String AAS_URL = "/views/internal/secured/aas/";

    /**
     * Creates a new instance of CrmNaviController
     */
    public AasNavController() {
    }
    
    

    public String redirectToViewRevenueReport() {
        return AAS_URL + "viewRevenueReport.xhtml" + REDIRECT;
    }
  
   
}
