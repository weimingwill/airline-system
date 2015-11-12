/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.crm.entity.CustomerList;
import ams.crm.entity.MktCampaign;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author winga_000
 */
@Local
public interface CampaignSessionLocal {
    
    public List<MktCampaign> getAllCampaigns();
    
    public MktCampaign createCampaign(String campaignName,String campaignType,String campaignDescription,Date startTime,Date endTime,String budget,String promotionCode,List<String> promotionCodeTypes,String promotionCodeType,String promotionPercentage,String promotionValue, CustomerList customerList);
    
    public List<CustomerList> getAllCustomerLists();
    
    public CustomerList getCustomerListById(Long id);
    
    public MktCampaign updateMktCampaign(MktCampaign mktCampaign, Date startTime, Date endTime, double budget);
}
