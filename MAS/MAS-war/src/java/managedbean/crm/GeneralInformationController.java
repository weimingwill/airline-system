/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.crm;

import ams.ais.entity.Rule;
import ams.crm.entity.Membership;
import ams.crm.entity.Privilege;
import ams.crm.entity.PrivilegeValue;
import ams.crm.session.GeneralInformationSessionLocal;
import ams.crm.session.PrivilegeSessionLocal;
import ams.crm.util.exception.NoSuchPrivilegeValueException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author Bowen
 */
@Named(value = "generalInformationController")
@RequestScoped
public class GeneralInformationController {

    @EJB
    private GeneralInformationSessionLocal generalInformationSession;
    @EJB
    private PrivilegeSessionLocal privilegeSession;

   
    /**
     * Creates a new instance of GeneralInformationController
     */
    
    /**
     * Creates a new instance of GeneralInformationController
     * @return
     */
    public List<Privilege> getPrivileges(){
        return privilegeSession.getAllPrivileges();
    }
    
    public List<Membership> getMemberships(){
        return privilegeSession.getAllMemberships();
    }
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
    
    public PrivilegeValue getPVById(Long pId, Long mid) {
        PrivilegeValue pv = new PrivilegeValue();
        try {
            pv = privilegeSession.getPVById(pId, mid);
        } catch (NoSuchPrivilegeValueException e) {
        }
        return pv;
    }
    
    public String getPrivilegeMembershipValue(Long pId, Long mid) {
        System.out.println("pId = " + pId + " mid = " + mid);
        
        PrivilegeValue pv = getPVById(pId, mid);
        double value = pv.getPrivilegeValue();
        if(value == 0){
            return "Not Applicable";
        }
        else if(value == 1)
        {
            return "Yes";
        }
        else{
            DecimalFormat df = new DecimalFormat("#%");;
            String formatedValue = " of Original Miles";
            formatedValue = df.format(value) + formatedValue;
            return formatedValue;
        }
        
    }
    
}
