/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.Rule;
import ams.ais.entity.TicketFamily;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import java.util.List;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Bowen
 */
@Stateless
public class RuleSession implements RuleSessionLocal {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    
   

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @Override
    public List<Rule> getAllRules() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void createRule(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteRule(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
