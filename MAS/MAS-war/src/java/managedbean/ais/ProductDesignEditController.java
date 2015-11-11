/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.ais;

import ams.ais.entity.BookingClass;
import ams.ais.entity.CabinClass;
import ams.ais.entity.Rule;
import ams.ais.session.RevMgmtSessionLocal;
import ams.ais.session.ProductDesignSessionLocal;
import ams.ais.util.exception.ExistSuchBookingClassNameException;
import ams.ais.util.exception.ExistSuchCabinClassNameException;
import ams.ais.util.exception.ExistSuchCabinClassTypeException;
import ams.ais.util.exception.ExistSuchRuleException;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchRuleException;
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
@Named(value = "productDesignEditController")
@ViewScoped
public class ProductDesignEditController implements Serializable {

    /**
     * Creates a new instance of EditCabinClassController
     */
    @Inject
    private NavigationController navigationController;
    @Inject
    private MsgController msgController;

    @EJB
    private ProductDesignSessionLocal productDesignSession;

    @EJB
    private RevMgmtSessionLocal revMgmtSession;

    private CabinClass selectedCabinClass;
    private Rule selectedRule;
    private BookingClass selectedBookingClass;

    public ProductDesignEditController() {
    }

    public String updateCabinClass() {
        try {
            System.out.println("selected cabin class name is: " + selectedCabinClass.getType());
            productDesignSession.updateCabinClass(selectedCabinClass.getCabinClassId(), selectedCabinClass.getType(), selectedCabinClass.getName());
            msgController.addMessage("Edit cabin class successfully!");
        } catch (ExistSuchCabinClassNameException | NoSuchCabinClassException | ExistSuchCabinClassTypeException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToViewAllCabinClass();

    }

    public String updateRule() {
        try {

            System.out.printf("selected rule id is: " + selectedRule);
            System.out.printf("selected rule name is " + selectedRule.getName());
            System.out.println("rule session =" + productDesignSession);

            productDesignSession.updateRule(selectedRule.getRuleId(), selectedRule.getName(), selectedRule.getDescription());
            msgController.addMessage("Edit rule successfully!");
        } catch (ExistSuchRuleException | NoSuchRuleException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToViewAllRules();

    }

    public String updateBookingClass() {
        try {
            System.out.printf("BookingClass is" + selectedBookingClass);
            revMgmtSession.updateBookingClass(selectedBookingClass.getBookingClassId(), selectedBookingClass.getName());
            msgController.addMessage("Edit Booking Class successfully!");
        } catch (ExistSuchBookingClassNameException | NoSuchBookingClassException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToViewAllBookingClass();

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

    public BookingClass getSelectedBookingClass() {
        return selectedBookingClass;
    }

    public void setSelectedBookingClass(BookingClass selectedBookingClass) {
        this.selectedBookingClass = selectedBookingClass;
    }
}
