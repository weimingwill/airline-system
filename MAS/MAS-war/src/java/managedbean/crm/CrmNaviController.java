/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.crm;

import java.io.Serializable;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author winga_000
 */
@Named(value = "crmNaviController")
@RequestScoped
public class CrmNaviController implements Serializable{

    private final String REDIRECT = "?faces-redirect=true";
    /**
     * Creates a new instance of CrmNaviController
     */
    public CrmNaviController() {
    }
    
    public String redirectToCreateCampaign() {
        return "/views/secured/crm/campaign_management/createCampaign.xhtml" + REDIRECT;
    }
    
}
