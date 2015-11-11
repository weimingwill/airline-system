/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.ais.entity.Rule;
import ams.ais.entity.TicketFamily;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Bowen
 */
@Stateless
public class GeneralInformationSession implements GeneralInformationSessionLocal {
    @PersistenceContext
    private EntityManager entityManager;
    public String nameChange = "NameChange";

  
    @Override
    public List<Rule> getNameChangeRule() {
        System.out.println("IN SESSION BEAN");
        Query query = entityManager.createQuery("SELECT c FROM Rule c WHERE c.name LIKE 'NameChange%'");
        List<Rule> rules;
        System.out.println("Perform ");
        
        try {
            rules = (List<Rule>) query.getResultList();
        } catch (NoResultException ex) {
            rules = new ArrayList<>();
        }
        System.out.println("rules"+rules);
        return rules;
    }
    @Override
    public List<Rule> getCancellationRule() {
      
        Query query = entityManager.createQuery("SELECT c FROM Rule c WHERE c.name LIKE 'Cancellation%'");
//        query.setParameter("inNameChange", nameChange);
        List<Rule> rules;
        
        try {
            rules = (List<Rule>) query.getResultList();
        } catch (NoResultException ex) {
            rules = new ArrayList<>();
        }
        System.out.println("rules"+rules);
        return rules;
    }
    
    @Override
    public List<Rule> getLuggage() {
        Query query = entityManager.createQuery("SELECT c FROM Rule c WHERE c.name LIKE '%Luggage%'");
        List<Rule> rules;
        System.out.println("Perform ");
        
        try {
            rules = (List<Rule>) query.getResultList();
        } catch (NoResultException ex) {
            rules = new ArrayList<>();
        }
        System.out.println("rules"+rules);
        return rules;
    }
    
    @Override
    public List<Rule> getFlightChangeRules(){
        Query query = entityManager.createQuery("SELECT c FROM Rule c WHERE c.name LIKE '%Re%'");
        List<Rule> rules;
        System.out.println("Perform ");
        
        try {
            rules = (List<Rule>) query.getResultList();
        } catch (NoResultException ex) {
            rules = new ArrayList<>();
        }
        System.out.println("rules"+rules);
        return rules;
    }
    
    @Override
    public List<Rule> getOtherServiceRules() {
        Query query = entityManager.createQuery("SELECT c FROM Rule c WHERE c.name LIKE '%Meal%' OR c.name LIKE '%Hotel%'");
        List<Rule> rules;
        System.out.println("Perform ");
        
        try {
            rules = (List<Rule>) query.getResultList();
        } catch (NoResultException ex) {
            rules = new ArrayList<>();
        }
        System.out.println("rules"+rules);
        return rules;
    }
    
    @Override
    public List<Rule> getNoShowFee() {
        Query query = entityManager.createQuery("SELECT c FROM Rule c WHERE c.name LIKE 'NoShowFee'");
        List<Rule> rules;
        System.out.println("Perform ");
        
        try {
            rules = (List<Rule>) query.getResultList();
        } catch (NoResultException ex) {
            rules = new ArrayList<>();
        }
        System.out.println("rules"+rules);
        return rules;
    }
    @Override
    public List<TicketFamily> getNameChangeTicketFamily() {
        Query query = entityManager.createQuery("SELECT t FROM TicketFamily t WHERE t.ticketFamilyRules.rule.name LIKE 'NameChange%'");
        return query.getResultList();
    }
     public String getNameChange() {
        return nameChange;
    }

    public void setNameChange(String nameChange) {
        this.nameChange = nameChange;
    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    

}
