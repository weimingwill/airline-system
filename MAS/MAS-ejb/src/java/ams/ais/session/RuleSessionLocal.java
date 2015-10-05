/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.Rule;
import ams.ais.entity.TicketFamily;
import ams.ais.util.exception.ExistSuchRuleException;
import ams.ais.util.exception.NoSuchRuleException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Bowen
 */
@Local
public interface RuleSessionLocal {
    
   public List<Rule> getAllRule();
   public void createRule (String name) throws ExistSuchRuleException;
   public void deleteRule (String name) throws NoSuchRuleException; 
   public void verifyRuleExistence(String name) throws ExistSuchRuleException;
   
   public Rule getRuleByName(String name);
   public void updateRule(String oldname, String name) throws NoSuchRuleException, ExistSuchRuleException;
   public List<Rule> getAllOtherRule(String name);
   
   
}
