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
import ams.ais.session.BookingClassSession;
import ams.ais.session.BookingClassSessionLocal;
import ams.ais.session.CabinClassSessionLocal;
import ams.ais.session.RuleSessionLocal;
import ams.ais.session.TicketFamilySessionLocal;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchRuleException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import java.io.Serializable;
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
@Named(value = "productDesignDeleteController")
@ViewScoped
public class ProductDesignDeleteController implements Serializable{

    /**
     * Creates a new instance of ProductDesignDeleteController
     */
    
    @Inject
    private NavigationController navigationController;
    @Inject
    private MsgController msgController;
    
    @EJB
    private CabinClassSessionLocal cabinClassSession;
    
    @EJB
    private RuleSessionLocal ruleSession;
    
    @EJB
    private BookingClassSessionLocal bookingClassSession;
    
    @EJB
    private TicketFamilySessionLocal ticketFamilySession;
    
    private CabinClass selectedCabinClass;
    private Rule selectedRule;
    private TicketFamily selectedTicketFamily;
    private BookingClass selectedBookingClass;

    public BookingClass getSelectedBookingClass() {
        return selectedBookingClass;
    }

    public void setSelectedBookingClass(BookingClass selectedBookingClass) {
        this.selectedBookingClass = selectedBookingClass;
    }

    
    
    public ProductDesignDeleteController() {
    }
    
    public void deleteCabinClass() {
        try{
        cabinClassSession.deleteCabinClass(selectedCabinClass.getName());
        msgController.addMessage("Delete cabin class successfully");
        } catch (NoSuchCabinClassException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
          
    }
    
     public void deleteRule() {
        try{
        ruleSession.deleteRule(selectedRule.getName());
        System.out.printf("name is :"+selectedRule.getName());
        msgController.addMessage("Delete rule successfully");
        } catch (NoSuchRuleException ex) {
        msgController.addErrorMessage(ex.getMessage());
        }    
    }
     
     public String deleteBookingClass() {
        try {
            System.out.printf("selected booking class is: "+selectedBookingClass);
            bookingClassSession.deleteBookingClass(selectedBookingClass.getName());
            msgController.addMessage("Booking class is deleted successfully!");
        } catch (NoSuchBookingClassException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToDeleteBookingClass();
    }
    
    public void deleteTicketFamily() {
        try {
            System.out.print("we are in delete ticket Family!");
            System.out.print("the deleted select ticket family name is: "+selectedTicketFamily);
            ticketFamilySession.deleteTicketFamilyByType(selectedTicketFamily.getType());
            msgController.addMessage("Delete ticket family successfully");
        } catch (NoSuchTicketFamilyException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }

    }
     public CabinClass getSelectedCabinClass() {
        return selectedCabinClass;
    }

    public void setSelectedCabinClass(CabinClass selectedCabinClass) {
        this.selectedCabinClass = selectedCabinClass;
    }

    public Rule getSelectedRule() {
        return selectedRule;
    }

    public void setSelectedRule(Rule selectedRule) {
        this.selectedRule = selectedRule;
    }
    
    public TicketFamily getSelectedTicketFamily() {
        return selectedTicketFamily;
    }

    public void setSelectedTicketFamily(TicketFamily selectedTicketFamily) {
        this.selectedTicketFamily = selectedTicketFamily;
    }
}
