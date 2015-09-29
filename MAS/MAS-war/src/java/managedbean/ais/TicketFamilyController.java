/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.ais;

import ams.ais.entity.CabinClass;
import ams.ais.entity.Rule;
import ams.ais.entity.TicketFamily;
import ams.ais.helper.TicketFamilyRuleHelper;
import ams.ais.session.TicketFamilySessionLocal;
import ams.ais.util.exception.ExistSuchTicketFamilyException;
import ams.ais.util.exception.ExistSuchTicketFamilyNameException;
import ams.ais.util.exception.ExistSuchTicketFamilyTypeException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.aps.util.exception.EmptyTableException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;

/**
 *
 * @author Bowen
 */
@Named(value = "ticketFamilyController")
@RequestScoped
public class TicketFamilyController {

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
    private List<TicketFamilyRuleHelper> ticketFamilyRuleHelpers = new ArrayList<>();
    
    @PostConstruct
    public void init()  {
        getAvailableRules();
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
    
    public void getAvailableRules()   {
       
        try {
            setRuleList(ticketFamilySession.getAllRules());
            for (Rule rule : ruleList) {
                ticketFamilyRuleHelpers.add(new TicketFamilyRuleHelper(rule.getRuleId(), rule.getName(), 0));
            }
        } catch (EmptyTableException ex) {
            Logger.getLogger(TicketFamilyController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public String createTicketFamily() {
        try {
            ticketFamilySession.createTicketFamily(type, name,cabinclassname,ticketFamilyRuleHelpers);
            
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
        try{
        ticketFamilySession.deleteTicketFamily(type,cabinclassname);
        msgController.addMessage("Delete ticket family successfully");
        } catch (NoSuchTicketFamilyException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
          
    }
    public String updateTicketFamily() {
        try {
            ticketFamilySession.updateTicketFamily(oldtype,oldCabinClassname,type,name,cabinclassname);
            msgController.addMessage("Edit ticket family successfully!");
        }catch( ExistSuchTicketFamilyException | NoSuchTicketFamilyException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToViewAllTicketFamily();
        
    }

   
    
    
}
