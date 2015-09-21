/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.Rule;
import ams.ais.entity.TicketFamily;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Bowen
 */
@Local
public interface RuleSessionLocal {
    
   public List<Rule> getAllRules();
   public void createRule (String name);
   public void deleteRule (String name);
   
   
}
