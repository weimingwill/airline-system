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
    public void createRule(String name, String description) throws ExistSuchRuleException {
        verifyRuleExistence(name);
        Rule rule = new Rule();
        rule.create(name,description);
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
    public void updateRule(Long ruleId, String name,String description) throws NoSuchRuleException, ExistSuchRuleException {
        System.out.print("rule id is" + ruleId);
        System.out.print("Rule name is" +name);
        Rule rule = getRuleById(ruleId);
        if (rule == null) {
            throw new NoSuchRuleException(AisMsg.NO_SUCH_RULE_ERROR);
        } else {
            List<Rule> rules = getAllOtherRuleById(ruleId);

            if (rules != null) {

                for (Rule cc : rules) {

                    if (name.equals(cc.getName())) {
                        throw new ExistSuchRuleException(AisMsg.EXIST_SUCH_RULE_ERROR);
                    }
                }
            }
        }
        rule.setName(name);
        rule.setDescription(description);
        entityManager.merge(rule);
        entityManager.flush();
    }

    @Override
    public List<Rule> getAllOtherRule(String name) {
        Query query = entityManager.createQuery("SELECT m FROM Rule m where m.name <> :name AND m.deleted = FALSE");
        query.setParameter("name", name);
        return query.getResultList();
    }

    @Override
    public Rule getRuleById(Long ruleId) {
        Query query = entityManager.createQuery("SELECT u FROM Rule u WHERE u.ruleId = :inRuleId AND u.deleted = FALSE");
        query.setParameter("inRuleId", ruleId);
        Rule r = null;
        try {
            r = (Rule) query.getSingleResult();
        } catch (NoResultException ex) {
            r = null;
        }
        return r;
    }

    @Override
    public List<Rule> getAllOtherRuleById(Long ruleId) {
       Query query = entityManager.createQuery("SELECT m FROM Rule m where m.ruleId <> :ruleId AND m.deleted = FALSE");
        query.setParameter("ruleId", ruleId);
        return query.getResultList();
    }
}
