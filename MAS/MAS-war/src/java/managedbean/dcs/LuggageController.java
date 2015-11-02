/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.dcs;

import ams.dcs.entity.CheckInLuggage;
import ams.dcs.entity.Luggage;
import ams.dcs.session.CheckInSessionLocal;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import managedbean.application.DcsNavController;
import managedbean.application.MsgController;

/**
 *
 * @author ChuningLiu
 */
@Named(value = "luggageController")
@RequestScoped
public class LuggageController implements Serializable {

    @Inject
    private MsgController msgController;

    @Inject
    private DcsNavController dcsNavController;

    @EJB
    private CheckInSessionLocal checkInSession;

    private CheckInLuggage ciLuggage;
    private Luggage luggage;
    /**
     * Creates a new instance of LuggageController
     */
    public LuggageController() {
    }
    
    public void checkInLuggage(){
        
    }

    /**
     * @return the luggage
     */
    public Luggage getLuggage() {
        return luggage;
    }

    /**
     * @param luggage the luggage to set
     */
    public void setLuggage(Luggage luggage) {
        this.luggage = luggage;
    }

    /**
     * @return the ciLuggage
     */
    public CheckInLuggage getCiLuggage() {
        return ciLuggage;
    }

    /**
     * @param ciLuggage the ciLuggage to set
     */
    public void setCiLuggage(CheckInLuggage ciLuggage) {
        this.ciLuggage = ciLuggage;
    }
    
    
}
