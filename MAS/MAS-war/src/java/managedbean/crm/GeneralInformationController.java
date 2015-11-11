/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.crm;

import ams.ais.entity.Rule;
import ams.ais.entity.TicketFamily;
import ams.crm.session.GeneralInformationSessionLocal;
import java.util.List;
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
@Named(value = "generalInformationController")
@RequestScoped
public class GeneralInformationController {
    @Inject
    private NavigationController navigationController;
    @Inject
    private MsgController msgController;

    @EJB
    private GeneralInformationSessionLocal generalInformationSession;
    /**
     * Creates a new instance of GeneralInformationController
     */
    
    /**
     * Creates a new instance of GeneralInformationController
     * @return
     */
    public List<Rule> getNameChangeRules() {
        return generalInformationSession.getNameChangeRule();
    } 
    
    public List<Rule> getCancellationRules(){
        return generalInformationSession.getCancellationRule();
    }
    public List<Rule> getLuggageRules(){
        return generalInformationSession.getLuggage();
    }
    public List<Rule> getFlightChangeRules(){
        return generalInformationSession.getFlightChangeRules();
    }
    public List<Rule> getOtherServiceRules(){
        return generalInformationSession.getOtherServiceRules();
    }
    public List<Rule> getNoShowFeeRules(){
        return generalInformationSession.getNoShowFee();
    }
    public GeneralInformationController() {
    }
    
}
