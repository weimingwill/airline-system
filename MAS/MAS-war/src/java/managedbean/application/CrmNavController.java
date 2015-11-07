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
@Named(value = "crmNavController")
@RequestScoped
public class CrmNavController implements Serializable {

    private final String REDIRECT = "?faces-redirect=true";
    private final String CRM_URL = "/views/internal/secured/crm/";
    /**
     * Creates a new instance of CrmNaviController
     */
    public CrmNavController() {
    }

    public String redirectToCRM() {
        return CRM_URL + "crmMain.xhtml" + REDIRECT;
    }

    public String redirectToCreateCampaign() {
        return CRM_URL + "campaign_management/createCampaign.xhtml" + REDIRECT;
    }

}
