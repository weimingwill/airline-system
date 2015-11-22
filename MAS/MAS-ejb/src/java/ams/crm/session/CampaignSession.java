/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.crm.entity.Customer;
import ams.crm.entity.CustomerList;
import ams.crm.entity.MktCampaign;
import ams.crm.entity.PromotionCode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author winga_000
 */
@Stateless
public class CampaignSession implements CampaignSessionLocal {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<MktCampaign> getAllCampaigns() {
        Query query = em.createQuery("SELECT m FROM MktCampaign m");
        List<MktCampaign> campaigns = new ArrayList<>();
        try {
            campaigns = (List<MktCampaign>) query.getResultList();
        } catch (NoResultException ex) {
        }
        return campaigns;

    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public MktCampaign createCampaign(String campaignName, String campaignType, String campaignDescription, Date startTime, Date endTime, String budget, String promotionCode, List<String> promotionCodeTypes, String promotionCodeType, String promotionPercentage, String promotionValue, CustomerList customerList) {
        MktCampaign campaign = new MktCampaign();
        campaign.setName(campaignName);
        campaign.setType(campaignType);
        campaign.setDescription(campaignDescription);
        campaign.setStartTime(startTime);
        campaign.setEndTime(endTime);
        campaign.setBudget(Double.parseDouble(budget));

        PromotionCode pc = new PromotionCode();
        pc.setName(promotionCode);
        pc.setPercentage(Double.parseDouble(promotionPercentage));
        pc.setPromoValue(Double.parseDouble(promotionValue));
        pc.setMktCampaign(campaign);

        List<PromotionCode> pcs = new ArrayList<>();
        pcs.add(pc);
        campaign.setPromotionCodes(pcs);
        List<CustomerList> customerLists = new ArrayList<>();
        customerLists.add(customerList);
        campaign.setCustomerLists(customerLists);
        int size = customerLists.size();
        campaign.setAudienceSize(size);

        em.persist(campaign);
        em.persist(pc);

        return campaign;

    }

    @Override
    public List<CustomerList> getAllCustomerLists() {
        Query query = em.createQuery("SELECT c FROM CustomerList c");
        List<CustomerList> customerLists = new ArrayList<>();
        try {
            customerLists = (List<CustomerList>) query.getResultList();
        } catch (NoResultException ex) {
        }
        return customerLists;

    }

    @Override
    public CustomerList getCustomerListById(Long id) {
        Query query = em.createQuery("SELECT c FROM CustomerList c WHERE c.id = :id");
        query.setParameter("id", id);
        CustomerList customerList = new CustomerList();
        try {
            customerList = (CustomerList) query.getSingleResult();
        } catch (NoResultException ex) {
        }
        return customerList;
    }

    @Override
    public MktCampaign updateMktCampaign(MktCampaign mktCampaign, Date startTime, Date endTime, double budget) {
        MktCampaign mc = new MktCampaign();
        mc = em.find(MktCampaign.class, mktCampaign);
        mc.setStartTime(startTime);
        mc.setEndTime(endTime);
        mc.setBudget(budget);

        em.merge(mc);

        return mc;

    }

    @Override
    public int getCampaignNo() {
        Query query = em.createQuery("SELECT m FROM MktCampaign m");
        List<MktCampaign> campaigns = new ArrayList<>();
        try {
            campaigns = (List<MktCampaign>) query.getResultList();
        } catch (NoResultException ex) {
        }
        return campaigns.size();
    }

    @Override
    public int getOngoingCampaignNo() {
        Query query = em.createQuery("SELECT m FROM MktCampaign m");
        List<MktCampaign> campaigns = new ArrayList<>();
        try {
            campaigns = (List<MktCampaign>) query.getResultList();
        } catch (NoResultException ex) {
        }

        Calendar today = Calendar.getInstance();

        List<MktCampaign> campaignCopy = new ArrayList<>(campaigns);

        for (MktCampaign mc : campaigns) {
            Calendar startDate = Calendar.getInstance();
            startDate.setTime(mc.getStartTime());
            Calendar endDate = Calendar.getInstance();
            endDate.setTime(mc.getEndTime());

            if (today.after(endDate) || today.before(startDate)) {
                campaignCopy.remove(mc);
            }

        }
        return campaignCopy.size();
    }

    @Override
    public String getPromoCodeByCampaign(MktCampaign mktCampaign) {
        
        MktCampaign mc = new MktCampaign();
        
        mc = em.find(MktCampaign.class, mktCampaign.getId());
        
        return mc.getPromotionCodes().get(0).getName();
    }
}
