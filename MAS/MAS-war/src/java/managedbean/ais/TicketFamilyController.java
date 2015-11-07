/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.ais;

import ams.ais.entity.BookingClass;
import ams.ais.entity.CabinClass;
import ams.ais.entity.Rule;
import ams.ais.entity.TicketFamily;
import ams.ais.entity.TicketFamilyRule;
import ams.ais.helper.TicketFamilyRuleHelper;
import java.util.ArrayList;
import ams.ais.session.CabinClassSessionLocal;
import ams.ais.session.RuleSessionLocal;
import ams.ais.session.TicketFamilySessionLocal;
import ams.ais.util.exception.ExistSuchTicketFamilyException;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.ais.util.helper.BookingClassHelper;
import ams.ais.util.exception.NoSuchRuleException;
import ams.ais.util.exception.NoSuchTicketFamilyRuleException;
import java.io.Serializable;
import java.util.List;
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
    @EJB
    private CabinClassSessionLocal cabinClaSession;
    @EJB
    private RuleSessionLocal ruleSession;

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
    private List<TicketFamily> ticketFamilys = new ArrayList();
    private List<String> bookingClassNames;
    private List<BookingClassHelper> bookingClassHelpers;

    @PostConstruct
    public void init() {
        getAvailableRules();
    }

    public TicketFamilyController() {
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
        updateTicketFamilyByType();
        System.out.print("we are here");
        for (TicketFamilyRuleHelper thisHelper : ticketFamilyRuleHelpers) {
            System.out.println("id = " + thisHelper.getTicketFamilyId() + " " + thisHelper.getRuleId() + " " + thisHelper.getName() + ": " + thisHelper.getRuleValue());
            TicketFamilyRule updatedTicketFamilyRule = new TicketFamilyRule();

            updatedTicketFamilyRule.setTicketFamilyId(thisHelper.getTicketFamilyId());
            updatedTicketFamilyRule.setRuleId(thisHelper.getRuleId());
            updatedTicketFamilyRule.setRuleValue((float) thisHelper.getRuleValue());
            ticketFamilySession.updateTicketFamilyRuleVlaue(updatedTicketFamilyRule);
        }

    }

    public String updateTicketFamily() {
        try {
            ticketFamilySession.updateTicketFamily(oldtype, oldCabinClassname, type, name, cabinclassname);
            msgController.addMessage("Edit ticket family successfully!");
        } catch (ExistSuchTicketFamilyException | NoSuchTicketFamilyException | NoSuchCabinClassException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToViewAllTicketFamily();
    }

    public List<BookingClass> getTicketFamilyBookingClasses(String cabinClassName, String ticketFamilyName) {
        List<BookingClass> bookingClasses;
        try {
            bookingClasses = ticketFamilySession.getTicketFamilyBookingClasses(cabinClassName, ticketFamilyName);
        } catch (NoSuchBookingClassException e) {
            return null;
        }
        return bookingClasses;
    }

    public void getTicketFamilyBookingClassHelpers(String cabinClassName, String ticketFamilyName) {
        System.out.println("Get BookingClass Helper");
        bookingClassHelpers = ticketFamilySession.getTicketFamilyBookingClassHelpers(cabinClassName, ticketFamilyName);
    }

    public List<String> getTicketFamilyBookingClassNames(String cabinClassName, String ticketFamilyName) {
        return ticketFamilySession.getTicketFamilyBookingClassNames(cabinClassName, ticketFamilyName);
    }

    public void OnTicketFamilyChange(String cabinClassName, String ticketFamilyName) {
        bookingClassNames = ticketFamilySession.getTicketFamilyBookingClassNames(cabinClassName, ticketFamilyName);
    }

    public void getAvailableRules() {
        setRuleList(ruleSession.getAllRules());
        for (Rule rule : ruleList) {
            displayRuleList.add(new TicketFamilyRuleHelper(rule.getRuleId(), rule.getName(), rule.getDescription(), 0));
        }
    }

    public List<TicketFamilyRule> getTicketFamilyRuleByTicketFamilyId(long ticketFamilyId) {
        return ticketFamilySession.getTicketFamilyRuleByTicketFamilyId(ticketFamilyId);
    }

    public List<Rule> getRulesByTicketFmailyId(Long ticketFamilyId) {
        List<Rule> rules = new ArrayList<>();
        try {
            rules = ticketFamilySession.getRulesByTicketFmailyId(ticketFamilyId);
        } catch (NoSuchRuleException e) {
        }
        return rules;
    }

    public List<Rule> getAllRules() {
        return ruleSession.getAllRules();
    }

    public TicketFamilyRule getTicketFamilyRuleById(Long tfId, Long ruleid) {
        TicketFamilyRule ticketFamilyRule = new TicketFamilyRule();
        try {
            ticketFamilyRule = ticketFamilySession.getTicketFamilyRuleById(tfId, ruleid);
        } catch (NoSuchTicketFamilyRuleException e) {
        }
        return ticketFamilyRule;
    }

    public float getTicketFamilyRuleValue(Long tfId, Long ruleid) {
        return getTicketFamilyRuleById(tfId, ruleid).getRuleValue();
    }

    public String createTicketFamily() {
        try {
            ticketFamilySession.createTicketFamily(type, name, cabinclassname, displayRuleList);
            msgController.addMessage("Create ticket family successfully!");
        } catch (ExistSuchTicketFamilyException | NoSuchCabinClassException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToCreateTicketFamily();
    }

    public List<TicketFamily> getAllTicketFamily() {
        return ticketFamilySession.getAllTicketFamily();
    }

    public List<CabinClass> getAllCabinClass() {
        return cabinClaSession.getAllCabinClass();

    }

    public void deleteTicketFamily() {
        try {
            System.out.print("we are in delete ticket Family!");
            System.out.print("the deleted select ticket family name is: " + selectedTicketFamily);
            ticketFamilySession.deleteTicketFamilyByType(selectedTicketFamily.getType());
            msgController.addMessage("Delete ticket family successfully");
        } catch (NoSuchTicketFamilyException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }

    }

    // For update Ticket Family (edit ticket Family name and type
    public String updateTicketFamilyByType() {
        try {
            ticketFamilySession.updateTicketFamilyByType(selectedTicketFamily.getTicketFamilyId(), selectedTicketFamily.getCabinClass().getName(), selectedTicketFamily.getType(), selectedTicketFamily.getName());
            msgController.addMessage("Edit ticket family successfully!");
        } catch (ExistSuchTicketFamilyException | NoSuchTicketFamilyException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToViewAllTicketFamily();
    }

    //Getter and setter
    public String getOldtype() {
        return oldtype;
    }

    public void setOldtype(String oldtype) {
        this.oldtype = oldtype;
    }

    public String getOldCabinClassname() {
        return oldCabinClassname;
    }

    public void setOldCabinClassname(String oldCabinClassname) {
        this.oldCabinClassname = oldCabinClassname;
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

    public String getCabinclassname() {
        return cabinclassname;
    }

    public void setCabinclassname(String cabinclassname) {
        this.cabinclassname = cabinclassname;
    }

    public List<String> getBookingClassNames() {
        return bookingClassNames;
    }

    public void setBookingClassNames(List<String> bookingClassNames) {
        this.bookingClassNames = bookingClassNames;
    }

    public List<BookingClassHelper> getBookingClassHelpers() {
        return bookingClassHelpers;
    }

    public void setBookingClassHelpers(List<BookingClassHelper> bookingClassHelpers) {
        this.bookingClassHelpers = bookingClassHelpers;
    }

    public double getRuleValue() {
        return ruleValue;
    }

    public void setRuleValue(double ruleValue) {
        this.ruleValue = ruleValue;
    }

    public List<Rule> getRuleList() {
        return ruleList;
    }

    public void setRuleList(List<Rule> ruleList) {
        this.ruleList = ruleList;
    }

    public List<TicketFamilyRuleHelper> getTicketFamilyRuleHelpers() {
        return ticketFamilyRuleHelpers;
    }

    public void setTicketFamilyRuleHelpers(List<TicketFamilyRuleHelper> ticketFamilyRuleHelpers) {
        this.ticketFamilyRuleHelpers = ticketFamilyRuleHelpers;
    }

    public List<TicketFamilyRule> getSelectedticketFamilyRules() {
        return selectedticketFamilyRules;
    }

    public void setSelectedticketFamilyRules(List<TicketFamilyRule> selectedticketFamilyRules) {
        this.selectedticketFamilyRules = selectedticketFamilyRules;
    }

    public List<TicketFamilyRuleHelper> getDisplayRuleList() {
        return displayRuleList;
    }

    public void setDisplayRuleList(List<TicketFamilyRuleHelper> displayRuleList) {
        this.displayRuleList = displayRuleList;
    }

    public List<TicketFamily> getTicketFamilys() {
        return ticketFamilys;
    }

    public void setTicketFamilys(List<TicketFamily> ticketFamilys) {
        this.ticketFamilys = ticketFamilys;
    }

}
