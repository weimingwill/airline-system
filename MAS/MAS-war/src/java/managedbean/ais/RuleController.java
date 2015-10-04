/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.ais;

import ams.ais.entity.Rule;
import ams.ais.session.RuleSessionLocal;
import ams.ais.util.exception.ExistSuchRuleException;
import ams.ais.util.exception.NoSuchRuleException;
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
@Named(value = "ruleController")
@RequestScoped
public class RuleController {
    @Inject
    private NavigationController navigationController;
    @Inject
    private MsgController msgController;
    
    @EJB RuleSessionLocal ruleSession;
    
    private String oldname;
    private String name;
    private Rule selectedRule;

    public Rule getSelectedRule() {
        return selectedRule;
    }

    public void setSelectedRule(Rule selectedRule) {
        this.selectedRule = selectedRule;
    }

    public String getOldname() {
        return oldname;
    }

    public void setOldname(String oldname) {
        this.oldname = oldname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    /**
     * Creates a new instance of RuleController
     */
    public RuleController() {
    }
    
    public String createRule (){
        try {
            ruleSession.createRule(name);
            msgController.addMessage("Create New Rule successfully!");
        } catch (ExistSuchRuleException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToCreateRule();
    }
    
    
    public List<Rule> getAllRule() {
        return ruleSession.getAllRule();
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
    
    public String updateRule() {
        try {
            ruleSession.updateRule(oldname,name);
            msgController.addMessage("Edit rule successfully!");
        }catch( ExistSuchRuleException | NoSuchRuleException  ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToViewAllRules();
        
    }
}
