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
import ams.ais.util.helper.AisMsg;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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
    public List<Rule> getAllRule() {
        Query query = entityManager.createQuery("SELECT r FROM Rule r WHERE r.deleted = false");
        return query.getResultList();
    }

    @Override
    public void createRule(String name) throws ExistSuchRuleException {
        verifyRuleExistence(name);
        Rule rule = new Rule();
        rule.create(name);
        entityManager.persist(rule);
    }

    @Override
    public void deleteRule(String name) throws NoSuchRuleException {
        Rule rule = getRuleByName(name);
        if (rule == null) {
            throw new NoSuchRuleException(AisMsg.NO_SUCH_RULE_ERROR);
        } else {
            rule.setDeleted(true);
            entityManager.merge(rule);
        }
    }

    @Override
    public void verifyRuleExistence(String name) throws ExistSuchRuleException {
        List<Rule> rules = getAllRule();
        if (rules != null) {
            for (Rule r : rules) {

                if (name.equals(r.getName())) {
                    throw new ExistSuchRuleException(AisMsg.EXIST_SUCH_RULE_ERROR);
                }
            }
        }
    }

    @Override
    public Rule getRuleByName(String name) {
        
        Query query = entityManager.createQuery("SELECT u FROM Rule u WHERE u.name = :inRuleName AND u.deleted = FALSE");
        query.setParameter("inRuleName", name);
        Rule r = null;
        try {
            r = (Rule) query.getSingleResult();
        } catch (NoResultException ex) {
            r = null;
        }
        return r;
    }

    @Override
    public void updateRule(String oldname, String name) throws NoSuchRuleException, ExistSuchRuleException {
        Rule rule = getRuleByName(oldname);
        if (rule == null) {
            throw new NoSuchRuleException(AisMsg.NO_SUCH_RULE_ERROR);
        } else {
            List<Rule> rules = getAllOtherRule(oldname);

            if (rules != null) {

                for (Rule cc : rules) {

                    if (name.equals(cc.getName())) {
                        throw new ExistSuchRuleException(AisMsg.EXIST_SUCH_RULE_ERROR);
                    }
                }
            }
        }
        rule.setName(name);

        entityManager.merge(rule);
        entityManager.flush();
    }

    @Override
    public List<Rule> getAllOtherRule(String name) {
        Query query = entityManager.createQuery("SELECT m FROM Rule m where m.name <> :name");
        query.setParameter("name", name);
        return query.getResultList();
    }
}
