/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.crm;

import ams.aps.entity.Airport;
import ams.crm.entity.CustomerList;
import ams.crm.entity.MktCampaign;
import ams.crm.entity.RegCust;
import ams.crm.entity.SelectedCust;
import ams.crm.session.CampaignSessionLocal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import managedbean.application.CrmNavController;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;
import managedbean.common.EmailController;


/**
 *
 * @author weiming
 */
@Named(value = "campaignController")
@RequestScoped
public class CampaignController {

    @Inject
    private MsgController msgController;

    @Inject
    private CrmNavController crmNavController;

    @Inject
    private EmailController emailController;

    @EJB
    private CampaignSessionLocal campaignSession;

    private List<MktCampaign> campaigns;
    private MktCampaign selectedCampaign;
    private String campaignName;
    private String campaignType;
    private String campaignDescription;
    private Date startTime;
    private Date endTime;
    private String budget;
    private List<String> campaignTypes;
    private String promotionCode;
    private List<String> promotionCodeTypes;
    private String promotionCodeType;
    private String promotionPercentage;
    private String promotionValue;
    private List<CustomerList> customerLists;
    private CustomerList selectedCustomerList;

    /**
     * Creates a new instance of MktCampaignController
     */
    public CampaignController() {
    }

    @PostConstruct
    public void init() {
        campaigns = campaignSession.getAllCampaigns();
        List<String> types = new ArrayList<String>();
        types.add("Acquisition");
        types.add("Retension");
        campaignTypes = types;
        List<String> promotionTypes = new ArrayList<>();
        promotionTypes.add("Percenteage discount");
        promotionTypes.add("Value discount");
        promotionCodeTypes = promotionTypes;
        customerLists = campaignSession.getAllCustomerLists();
    }

    public String createCampaign() {
        campaignSession.createCampaign(campaignName, campaignType, campaignDescription, startTime, endTime, budget, promotionCode, promotionCodeTypes, promotionCodeType, promotionPercentage, promotionValue, selectedCustomerList);
        return crmNavController.redirectToViewCampaigns();
    }

    public String toCreateCampaign() {
        return crmNavController.redirectToCreateCampaign();
    }

    public CustomerList getCustomerListById(Long id) {
        return campaignSession.getCustomerListById(id);
    }

    public void sendEmail() {
        String promotioncode = selectedCampaign.getPromotionCodes().get(0).getName();
        List<SelectedCust> selectedCusts = new ArrayList<>();
        selectedCusts = selectedCampaign.getCustomerLists().get(0).getSelectedCusts();
        String promotionSubject = selectedCampaign.getName();
        for (SelectedCust selectedCust : selectedCusts) {
            String content = new String();
            content = content.concat("Dear " + selectedCust.getTitle() + " " + selectedCust.getLastName() + ",\n");
            content = content.concat("We have a promotion specially for you!\n");
            content = content.concat("Please use this promotion code during you booking for discount: " + promotioncode + "\n");
            content = content.concat("Looing forward to seeing you soon!\n");
            content = content.concat("Best regards,\n");
            content = content.concat("Merlion Airline\n");
            emailController.sendEmail(promotionSubject, content, selectedCust.getEmail());
        }

    }
    
//    public void updateCampaign(){
//        campaignSession.updateMktCampaign(selectedCampaign,selectedCampaign.getStartTime(),selectedCampaign.getEndTime(),selectedCampaign.getBudget());
//        
//    }
        
        

    //*******************getters & setters************************
    public MsgController getMsgController() {
        return msgController;
    }

    public void setMsgController(MsgController msgController) {
        this.msgController = msgController;
    }

    public CampaignSessionLocal getCampaignSession() {
        return campaignSession;
    }

    public void setCampaignSession(CampaignSessionLocal campaignSession) {
        this.campaignSession = campaignSession;
    }

    public List<MktCampaign> getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(List<MktCampaign> campaigns) {
        this.campaigns = campaigns;
    }

    public MktCampaign getSelectedCampaign() {
        return selectedCampaign;
    }

    public void setSelectedCampaign(MktCampaign selectedCampaign) {
        this.selectedCampaign = selectedCampaign;
    }

    public CrmNavController getCrmNavController() {
        return crmNavController;
    }

    public void setCrmNavController(CrmNavController crmNavController) {
        this.crmNavController = crmNavController;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getCampaignType() {
        return campaignType;
    }

    public void setCampaignType(String campaignType) {
        this.campaignType = campaignType;
    }

    public String getCampaignDescription() {
        return campaignDescription;
    }

    public void setCampaignDescription(String campaignDescription) {
        this.campaignDescription = campaignDescription;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public List<String> getCampaignTypes() {
        return campaignTypes;
    }

    public void setCampaignTypes(List<String> campaignTypes) {
        this.campaignTypes = campaignTypes;
    }

    public String getPromotionCode() {
        return promotionCode;
    }

    public void setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode;
    }

    public List<String> getPromotionCodeTypes() {
        return promotionCodeTypes;
    }

    public void setPromotionCodeTypes(List<String> promotionCodeTypes) {
        this.promotionCodeTypes = promotionCodeTypes;
    }

    public String getPromotionCodeType() {
        return promotionCodeType;
    }

    public void setPromotionCodeType(String promotionCodeType) {
        this.promotionCodeType = promotionCodeType;
    }

    public String getPromotionPercentage() {
        return promotionPercentage;
    }

    public void setPromotionPercentage(String promotionPercentage) {
        this.promotionPercentage = promotionPercentage;
    }

    public String getPromotionValue() {
        return promotionValue;
    }

    public void setPromotionValue(String promotionValue) {
        this.promotionValue = promotionValue;
    }

    public List<CustomerList> getCustomerLists() {
        return customerLists;
    }

    public void setCustomerLists(List<CustomerList> customerLists) {
        this.customerLists = customerLists;
    }

    public CustomerList getSelectedCustomerList() {
        return selectedCustomerList;
    }

    public void setSelectedCustomerList(CustomerList selectedCustomerList) {
        this.selectedCustomerList = selectedCustomerList;
    }

}
