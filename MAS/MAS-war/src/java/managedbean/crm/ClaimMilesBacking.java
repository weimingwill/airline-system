/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.crm;

import ams.crm.util.exception.NoSuchBookingReferenceException;
import ams.crm.util.exception.NoSuchRegCustException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import managedbean.application.MsgController;

/**
 *
 * @author Bowen
 */
@Named(value = "claimMilesBacking")
@ViewScoped
public class ClaimMilesBacking implements Serializable{
    @Inject
    private CustomerController customerController;
    @Inject
    private MsgController msgController;
    @Inject
    /**
     * Creates a new instance of ClaimMilesBacking
     */
    public ClaimMilesBacking() {
    }
    public void claimMile() throws NoSuchBookingReferenceException{
        try {
            customerController.claimMiles();
        } catch (NoSuchRegCustException ex) {
            Logger.getLogger(ClaimMilesBacking.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        msgController.addMessage("Congratulations! You have successfully claim your points!");
    }
}
