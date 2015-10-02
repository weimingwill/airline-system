/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.ais;

import ams.ais.entity.CabinClass;
import ams.ais.entity.Rule;
import ams.ais.entity.TicketFamily;
import ams.ais.entity.TicketFamilyRule;
import ams.ais.helper.TicketFamilyRuleHelper;
import ams.ais.session.TicketFamilySessionLocal;
import ams.ais.util.exception.ExistSuchTicketFamilyException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.aps.util.exception.EmptyTableException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;

/**
 *
 * @author Bowen
 */
@Named(value = "ticketFamilyController")
@ViewScoped
public class TicketFamilyController implements Serializable {

    /**
     * Creates a new instance of TicketFamilyController
     */
    @Inject
    private NavigationController navigationController;
    @Inject
    private MsgController msgController;

    @EJB
    private TicketFamilySessionLocal ticketFamilySession;
    private String oldtype;
    private String oldCabinClassname;
    private String type;
    private String name;
    private String cabinclassname;
    private double ruleValue;
    private List<Rule> ruleList;
    private List<TicketFamilyRuleHelper> ticketFamilyRuleHelpers = new ArrayList();
    private TicketFamily selectedTicketFamily;
    private List<TicketFamilyRule> selectedticketFamilyRules;
    private List<TicketFamilyRuleHelper> displayRuleList = new ArrayList();

    
    @PostConstruct
    public void init(){
        getAvailableRules();
    }
    
    public List<TicketFamilyRuleHelper> getDisplayRuleList() {
        return displayRuleList;
    }

    public void setDisplayRuleList(List<TicketFamilyRuleHelper> displayRuleList) {
        this.displayRuleList = displayRuleList;
    }

    public void onEditBtnCick() {
        ticketFamilyRuleHelpers = new ArrayList();
        selectedticketFamilyRules = selectedTicketFamily.getTicketFamilyRules();
        Long ticketFamilyId = selectedTicketFamily.getTicketFamilyId();
        System.out.println("selectedTicketFamily =" + selectedTicketFamily);
        for (TicketFamilyRule thisTicketFamilyRule : selectedticketFamilyRules) {
            Long thisRuleId = thisTicketFamilyRule.getRuleId();
            String thisRuleName = thisTicketFamilyRule.getRule().getName();
            double thisRuleValue = thisTicketFamilyRule.getRuleValue();
            TicketFamilyRuleHelper newHelper = new TicketFamilyRuleHelper(ticketFamilyId, thisRuleId, thisRuleName, thisRuleValue);

            System.out.println("TFID" + newHelper.getTicketFamilyId());
            ticketFamilyRuleHelpers.add(newHelper);
        }

    }

    public void editRuleValues() {
        for (TicketFamilyRuleHelper thisHelper : ticketFamilyRuleHelpers) {
            System.out.println("id = " + thisHelper.getTicketFamilyId() + " " + thisHelper.getRuleId() + " " + thisHelper.getName() + ": " + thisHelper.getRuleValue());
            TicketFamilyRule updatedTicketFamilyRule = new TicketFamilyRule();

            updatedTicketFamilyRule.setTicketFamilyId(thisHelper.getTicketFamilyId());
            updatedTicketFamilyRule.setRuleId(thisHelper.getRuleId());
            updatedTicketFamilyRule.setRuleValue((float) thisHelper.getRuleValue());
            ticketFamilySession.updateTicketFamilyRuleVlaue(updatedTicketFamilyRule);
        }

    }

    public List<TicketFamilyRule> getSelectedticketFamilyRules() {
        return selectedticketFamilyRules;
    }

    public void setSelectedticketFamilyRules(List<TicketFamilyRule> selectedticketFamilyRules) {
        this.selectedticketFamilyRules = selectedticketFamilyRules;
    }

    public List<TicketFamilyRuleHelper> getTicketFamilyRuleHelpers() {
        return ticketFamilyRuleHelpers;
    }

    public void setTicketFamilyRuleHelpers(List<TicketFamilyRuleHelper> ticketFamilyRuleHelpers) {
        this.ticketFamilyRuleHelpers = ticketFamilyRuleHelpers;
    }

    public List<Rule> getRuleList() {
        return ruleList;
    }

    public void setRuleList(List<Rule> ruleList) {
        this.ruleList = ruleList;
    }

    public double getRuleValue() {
        return ruleValue;
    }

    public void setRuleValue(double ruleValue) {
        this.ruleValue = ruleValue;
    }

    public String getOldCabinClassname() {
        return oldCabinClassname;
    }

    public void setOldCabinClassname(String oldCabinClassname) {
        this.oldCabinClassname = oldCabinClassname;
    }

    public String getCabinclassname() {
        return cabinclassname;
    }

    public void setCabinclassname(String cabinclassname) {
        this.cabinclassname = cabinclassname;
    }

    public String getOldtype() {
        return oldtype;
    }

    public void setOldtype(String oldtype) {
        this.oldtype = oldtype;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TicketFamilyController() {
    }

    public void getAvailableRules() {
//       List<TicketFamilyRuleHelper> displayRuleList = new ArrayList();
        try {
            setRuleList(ticketFamilySession.getAllRules());
            for (Rule rule : ruleList) {
                displayRuleList.add(new TicketFamilyRuleHelper(rule.getRuleId(), rule.getName(), 0));
            }
        } catch (EmptyTableException ex) {
            Logger.getLogger(TicketFamilyController.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Number of Rules in database = "+displayRuleList.size());

    }

    public List<TicketFamilyRule> getTicketFamilyRuleByTicketFamilyId(long ticketFamilyId) {

        System.out.print("ticketFamilyId is " + ticketFamilyId);
        return ticketFamilySession.getTicketFamilyRuleByTicketFamilyId(ticketFamilyId);

    }

    public String createTicketFamily() {
//        System.out.println("type = " + type);
//        System.out.println("name = " + name);
//        System.out.println("cabin class name = " + cabinclassname);
//        System.out.println("Display Rulelist size = " + displayRuleList.size());
//        for (TicketFamilyRuleHelper thisHelper : displayRuleList) {
//            System.out.println("Rule name = " + thisHelper.getName());
//            System.out.println("Rule value = " + thisHelper.getRuleValue());
//        }
        try {
            ticketFamilySession.createTicketFamily(type, name,cabinclassname,displayRuleList);
            msgController.addMessage("Create ticket family successfully!");
        } catch (ExistSuchTicketFamilyException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToCreateTicketFamily();
    }

    public List<TicketFamily> getAllTicketFamily() {
        return ticketFamilySession.getAllTicketFamily();
    }

    public List<CabinClass> getAllCabinClass() {
        return ticketFamilySession.getAllCabinClass();

    }

    public void deleteTicketFamily() {
        try {
            ticketFamilySession.deleteTicketFamily(type, cabinclassname);
            msgController.addMessage("Delete ticket family successfully");
        } catch (NoSuchTicketFamilyException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }

    }

    public String updateTicketFamily() {
        try {
            ticketFamilySession.updateTicketFamily(oldtype, oldCabinClassname, type, name, cabinclassname);
            msgController.addMessage("Edit ticket family successfully!");
        } catch (ExistSuchTicketFamilyException | NoSuchTicketFamilyException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToViewAllTicketFamily();

    }

    /**
     * @return the selectedTicketFamily
     */
    public TicketFamily getSelectedTicketFamily() {
        return selectedTicketFamily;
    }

    /**
     * @param selectedTicketFamily the selectedTicketFamily to set
     */
    public void setSelectedTicketFamily(TicketFamily selectedTicketFamily) {
        this.selectedTicketFamily = selectedTicketFamily;
    }
}
