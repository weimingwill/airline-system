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

    public String redirectToViewFeedbacks() {
        return CRM_URL + "customer_management/viewFeedbacks.xhtml" + REDIRECT;
    }

    public String redirectToCreateFeedback() {
        return CRM_URL + "customer_management/createFeedback.xhtml" + REDIRECT;
    }

    public String redirectToViewCustomers() {
        return CRM_URL + "customer_management/viewCustomers.xhtml" + REDIRECT;
    }

    public String redirectToViewCustomerParticulars() {
        return CRM_URL + "customer_management/viewCustomerParticulars.xhtml" + REDIRECT;
    }

    public String redirectToViewRegCustomerParticulars() {
        return CRM_URL + "customer_management/viewRegCustomerParticulars.xhtml" + REDIRECT;
    }

    public String redirectToViewCustomerLists() {
        return CRM_URL + "customer_management/viewCustomerList.xhtml" + REDIRECT;
    }

    public String redirectToSegmentCustomers() {
        return CRM_URL + "customer_management/segmentCustomers.xhtml" + REDIRECT;
    }

    public String redirectToCreateCampaign() {
        return CRM_URL + "campaign_management/createCampaign.xhtml" + REDIRECT;
    }

    public String redirectToViewCampaigns() {
        return CRM_URL + "campaign_management/viewCampaigns.xhtml" + REDIRECT;
    }

    public String redirectToGenderDistribution() {
        return CRM_URL + "analytics/genderDistribution.xhtml" + REDIRECT;
    }

    public String redirectToCustomerAnalysis() {
        return CRM_URL + "analytics/customerAnalysis.xhtml" + REDIRECT;
    }

    public String redirectToMarketingAnalysis() {
        return CRM_URL + "analytics/marketingAnalysis.xhtml" + REDIRECT;
    }

    public String redirectToSalesAnalysis() {
        return CRM_URL + "analytics/salesAnalysis.xhtml" + REDIRECT;
    }

    public String redirectToBehaviorAnalysis() {
        return CRM_URL + "analytics/behaviorAnalysis.xhtml" + REDIRECT;
    }

    public String redirectToViewCustomerList() {
        return CRM_URL + "campaign_management/viewSelectedCustomerList.xhtml" + REDIRECT;

    }

}
