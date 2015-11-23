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
    
    public String redirectToViewCostReport() {
        return AAS_URL + "viewCostReport.xhtml" + REDIRECT;
    }
    
    public String redirectToViewFinancialReport() {
        return AAS_URL + "viewFinancialReport.xhtml" + REDIRECT;
    }
    
    public String redirectToViewFeulCostReport() {
        return AAS_URL + "viewFeulCostReport.xhtml" + REDIRECT;
    }
    
    public String redirectToViewFleetCostReport() {
        return AAS_URL + "viewFleetCostReport.xhtml" + REDIRECT;
    }
    
    public String redirectToViewFlightOpsCostReport() {
        return AAS_URL + "viewFlightOpsCostReport.xhtml" + REDIRECT;
    }
    
    public String redirectToViewHrCostReport() {
        return AAS_URL + "viewHrCostReport.xhtml" + REDIRECT;
    }
    
    public String redirectToViewMktCostReport() {
        return AAS_URL + "viewMktCostReport.xhtml" + REDIRECT;
    }
    
    public String redirectToReportDashboard() {
        return AAS_URL + "generateRevenueReport.xhtml" + REDIRECT;
    }
    
    public String redirectToCrewPayroll() {
        return AAS_URL + "payroll/crewPayroll.xhtml" + REDIRECT;
    }
  
   
}
