/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.BookingClass;
import ams.ais.entity.CabinClass;
import ams.ais.entity.CabinClassTicketFamily;
import ams.ais.entity.Rule;
import ams.ais.entity.TicketFamily;
import ams.ais.entity.TicketFamilyRule;
import ams.ais.entity.helper.CabinClassTicketFamilyId;
import ams.ais.helper.TicketFamilyRuleHelper;
import ams.ais.util.exception.ExistSuchCabinClassNameException;
import ams.ais.util.exception.ExistSuchCabinClassTypeException;
import ams.ais.util.exception.ExistSuchRuleException;
import ams.ais.util.exception.ExistSuchTicketFamilyException;
import ams.ais.util.exception.NeedTicketFamilyException;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchCabinClassTicketFamilyException;
import ams.ais.util.exception.NoSuchRuleException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.ais.util.exception.NoSuchTicketFamilyRuleException;
import ams.ais.util.helper.AisMsg;
import ams.ais.util.helper.BookingClassHelper;
import ams.ais.util.helper.CabinClassTicketFamilyHelper;
import ams.ais.util.helper.TicketFamilyBookingClassHelper;
import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftCabinClass;
import ams.aps.entity.helper.AircraftCabinClassId;
import ams.aps.util.exception.NoSuchAircraftCabinClassException;
import ams.aps.util.exception.NoSuchAircraftException;
import ams.aps.util.helper.AircraftStatus;
import ams.aps.util.helper.ApsMessage;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mas.util.helper.SafeHelper;

/**
 *
 * @author weiming
 */
@Stateless
public class ProductDesignSession implements ProductDesignSessionLocal {
    
    @PersistenceContext
    private EntityManager em;

    @EJB
    private RevMgmtSessionLocal revMgmtSession;
    
    //Rules
    @Override
    public List<Rule> getAllRules() {
        Query query = em.createQuery("SELECT c FROM Rule c WHERE c.deleted=False");
        List<Rule> rules = new ArrayList<>();
        try {
            rules = (List<Rule>) query.getResultList();
        } catch (NoResultException ex) {
        }
        return rules;
    }

    @Override
    public Rule getRuleByName(String name) {
        Query query = em.createQuery("SELECT u FROM Rule u WHERE u.name = :inRuleName AND u.deleted = FALSE");
        query.setParameter("inRuleName", name);
        Rule r = null;
        try {
            r = (Rule) query.getSingleResult();
        } catch (NoResultException ex) {
        }
        return r;
    }

    @Override
    public void createRule(String name, String description) throws ExistSuchRuleException {
        verifyRuleExistence(name);
        Rule rule = new Rule();
        rule.create(name, description);
        em.persist(rule);
    }

    @Override
    public void deleteRule(String name) throws NoSuchRuleException {
        Rule rule = getRuleByName(name);
        if (rule == null) {
            throw new NoSuchRuleException(AisMsg.NO_SUCH_RULE_ERROR);
        } else {
            rule.setDeleted(true);
            em.merge(rule);
        }
    }

    @Override
    public void verifyRuleExistence(String name) throws ExistSuchRuleException {
        List<Rule> rules = getAllRules();
        if (rules != null) {
            for (Rule r : rules) {
                if (name.equals(r.getName())) {
                    throw new ExistSuchRuleException(AisMsg.EXIST_SUCH_RULE_ERROR);
                }
            }
        }
    }

    @Override
    public void updateRule(Long ruleId, String name, String description) throws NoSuchRuleException, ExistSuchRuleException {
        System.out.print("rule id is" + ruleId);
        System.out.print("Rule name is" + name);
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
        em.merge(rule);
        em.flush();
    }

    private Rule getRuleById(Long ruleId) {
        Query query = em.createQuery("SELECT u FROM Rule u WHERE u.ruleId = :inRuleId AND u.deleted = FALSE");
        query.setParameter("inRuleId", ruleId);
        Rule r = null;
        try {
            r = (Rule) query.getSingleResult();
        } catch (NoResultException ex) {
        }
        return r;
    }

    private List<Rule> getAllOtherRuleById(Long ruleId) {
        Query query = em.createQuery("SELECT m FROM Rule m where m.ruleId <> :ruleId AND m.deleted = FALSE");
        query.setParameter("ruleId", ruleId);
        return query.getResultList();
    }
    
    //Aircraft
    
        @Override
    public AircraftCabinClass getAircraftCabinClassById(Long aircraftId, Long cabinCalssId) throws NoSuchAircraftCabinClassException {
        Query query = em.createQuery("SELECT ac FROM AircraftCabinClass ac "
                + "WHERE ac.aircraftId = :inAircraftId "
                + "AND ac.cabinClassId = :inCabinClassId "
                + "AND ac.cabinClass.deleted = FALSE "
                + "AND ac.aircraft.status <> :inRetried");
        query.setParameter("inAircraftId", aircraftId);
        query.setParameter("inCabinClassId", cabinCalssId);
        query.setParameter("inRetried", AircraftStatus.RETIRED);
        AircraftCabinClass aircraftCabinClass = new AircraftCabinClass();
        try {
            aircraftCabinClass = (AircraftCabinClass) query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoSuchAircraftCabinClassException(ApsMessage.No_SUCH_AIRCRAFT_CABINCLASS_ERROR);
        }
        return aircraftCabinClass;
    }

    @Override
    public Aircraft getAircraftById(Long id) throws NoSuchAircraftException {
        Query query = em.createQuery("SELECT a FROM Aircraft a WHERE a.aircraftId = :inId AND a.status NOT LIKE :inRetired AND a.status NOT LIKE :inCrashed");
        query.setParameter("inId", id);
        query.setParameter("inRetired", AircraftStatus.RETIRED);
        query.setParameter("inCrashed", AircraftStatus.CRASHED);
        Aircraft aircraft = new Aircraft();
        try {
            aircraft = (Aircraft) query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoSuchAircraftException(ApsMessage.NO_SUCH_AIRCRAFT_ERROR);
        }
        return aircraft;
    }

    @Override
    public List<Aircraft> getScheduledAircrafts() throws NoSuchAircraftException {
        Query query = em.createQuery("SELECT a FROM Aircraft a WHERE a.status <> :inRetired AND a.status <> :inCrashed");
//        Query query = em.createQuery("SELECT a FROM Aircraft a WHERE a.isScheduled = TRUE AND a.status NOT LIKE :inRetired AND a.status NOT LIKE :inCrashed");
        query.setParameter("inRetired", AircraftStatus.RETIRED);
        query.setParameter("inCrashed", AircraftStatus.CRASHED);
        List<Aircraft> aircrafts = new ArrayList<>();
        try {
            aircrafts = (List<Aircraft>) query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchAircraftException(ApsMessage.NO_SUCH_AIRCRAFT_ERROR);
        }
        return aircrafts;
    }

    @Override
    public List<CabinClass> getAircraftCabinClasses(Long aircraftId) throws NoSuchCabinClassException {
        Query query = em.createQuery("SELECT c FROM Aircraft a, AircraftCabinClass ac, CabinClass c "
                + "WHERE ac.aircraftId = :inId "
                + "AND ac.cabinClassId = c.cabinClassId AND c.deleted = FALSE "
                + "AND ac.aircraftId = a.aircraftId AND a.status <> :inRetired AND a.status <> :inCrashed");
        query.setParameter("inId", aircraftId);
        query.setParameter("inRetired", AircraftStatus.RETIRED);
        query.setParameter("inCrashed", AircraftStatus.CRASHED);
        List<CabinClass> cabinClasses = new ArrayList<>();
        try {
            cabinClasses = (List<CabinClass>) query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchCabinClassException(AisMsg.NO_SUCH_CABIN_CLASS_ERROR);
        }
        return cabinClasses;
    }

    @Override
    public List<TicketFamily> getAircraftTicketFamilys(Long aircraftId) throws NoSuchTicketFamilyException {
        List<TicketFamily> ticketFamilys = new ArrayList<>();
        try {
            for (CabinClass cabinClass : getAircraftCabinClasses(aircraftId)) {
                if (cabinClass != null) {
                    for (TicketFamily ticketFamily : getCabinClassTicketFamilys(cabinClass.getType())) {
                        ticketFamilys.add(ticketFamily);
                    }
                }
            }
        } catch (NoSuchCabinClassException e) {
        }
        return ticketFamilys;
    }


    @Override
    public List<CabinClassTicketFamily> getAircraftCabinClassTicketFamilys(Long aircraftId) throws NoSuchCabinClassTicketFamilyException {
        Query query = em.createQuery("SELECT ct FROM CabinClassTicketFamily ct "
                + "WHERE ct.aircraftCabinClass.aircraftId = :inAircraftId "
                + "AND ct.aircraftCabinClass.cabinClass.deleted = FALSE "
                + "AND ct.ticketFamily.deleted = FALSE "
                + "AND ct.deleted = FALSE");
        query.setParameter("inAircraftId", aircraftId);
        List<CabinClassTicketFamily> cabinClassTicketFamilys = null;
        try {
            cabinClassTicketFamilys = (List<CabinClassTicketFamily>) query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchCabinClassTicketFamilyException(AisMsg.NO_SUCH_CABINCLASS_TICKETFAMILY_ERROR);
        }
        return cabinClassTicketFamilys;
    }

    @Override
    public void verifyTicketFamilyExistence(Long aircraftId) throws NoSuchCabinClassTicketFamilyException {
        if (getAircraftCabinClassTicketFamilys(aircraftId).isEmpty()) {
            throw new NoSuchCabinClassTicketFamilyException(AisMsg.NO_SUCH_CABINCLASS_TICKETFAMILY_ERROR);
        }
    }
    
    //CabinClass
        @Override
    public void createCabinClass(String type, String name) throws ExistSuchCabinClassNameException, ExistSuchCabinClassTypeException {
        verifyCabinClassExistence(type, name);
        CabinClass cabinClass = new CabinClass();
        cabinClass.create(type, name);
        em.persist(cabinClass);
    }

    private void verifyCabinClassExistence(String type, String name) throws ExistSuchCabinClassNameException, ExistSuchCabinClassTypeException {
        List<CabinClass> cabinClasses = getAllCabinClass();
        if (cabinClasses != null) {
            for (CabinClass cabinClass : cabinClasses) {
                if (type.equals(cabinClass.getType())) {
                    throw new ExistSuchCabinClassTypeException(AisMsg.EXIST_SUCH_CABIN_CLASS_TYPE_ERROR);
                }
                if (name.equals(cabinClass.getName())) {
                    throw new ExistSuchCabinClassNameException(AisMsg.EXIST_SUCH_CABIN_CLASS_NAME_ERROR);
                }
            }
        }
    }

    @Override
    public void deleteCabinClass(String name) throws NoSuchCabinClassException {
        CabinClass cabinclass = getCabinClassByName(name);
        if (cabinclass == null) {
            throw new NoSuchCabinClassException(AisMsg.NO_SUCH_CABIN_CLASS_ERROR);
        } else {
            cabinclass.setDeleted(true);
            em.merge(cabinclass);
        }
    }

    @Override
    public void updateCabinClass(Long cabinClassId, String type, String name) throws NoSuchCabinClassException, ExistSuchCabinClassNameException, ExistSuchCabinClassTypeException {
        CabinClass c = getCabinClassById(cabinClassId);
        if (c == null) {
            throw new NoSuchCabinClassException(AisMsg.NO_SUCH_CABIN_CLASS_ERROR);
        } else {
            List<CabinClass> cabinclasses = getAllOtherCabinClassById(cabinClassId);
            if (cabinclasses != null) {
                for (CabinClass cc : cabinclasses) {
                    if (type.equals(cc.getType())) {
                        throw new ExistSuchCabinClassTypeException(AisMsg.EXIST_SUCH_CABIN_CLASS_TYPE_ERROR);
                    }
                    if (name.equals(cc.getName())) {
                        throw new ExistSuchCabinClassNameException(AisMsg.EXIST_SUCH_CABIN_CLASS_NAME_ERROR);
                    }
                }
            }
        }
        c.setName(name);
        c.setType(type);
        em.merge(c);
        em.flush();
    }

    @Override
    public List<CabinClass> getAllCabinClass() {
        Query query = em.createQuery("SELECT c FROM CabinClass c WHERE c.deleted = false");
        return query.getResultList();
    }

    @Override
    public CabinClass getCabinClassByName(String name) throws NoSuchCabinClassException {
        Query query = em.createQuery("SELECT c FROM CabinClass c WHERE c.name = :inCabinClassName and c.deleted = FALSE");
        query.setParameter("inCabinClassName", name);
        CabinClass cabinclass = null;
        try {
            cabinclass = (CabinClass) query.getSingleResult();
        } catch (NoResultException ex) {
            throw new NoSuchCabinClassException(AisMsg.NO_SUCH_CABIN_CLASS_ERROR);
        }catch(NonUniqueResultException e){
            
        }
        return cabinclass;
    }

    private CabinClass getCabinClassById(Long cabinClassId) throws NoSuchCabinClassException{
        Query query = em.createQuery("SELECT c FROM CabinClass c WHERE c.cabinClassId = :incabinClassId and c.deleted = FALSE");
        query.setParameter("incabinClassId", cabinClassId);
        CabinClass cabinclass = null;
        try {
            cabinclass = (CabinClass) query.getSingleResult();
        } catch (NoResultException ex) {
            throw new NoSuchCabinClassException(AisMsg.NO_SUCH_CABIN_CLASS_ERROR);
        }
        return cabinclass;
    }
    
    private List<CabinClass> getAllOtherCabinClassById (Long cabinClassId){
        Query query = em.createQuery("SELECT c FROM CabinClass c where c.cabinClassId <> :cabinClassId AND c.deleted=FALSE");
        query.setParameter("cabinClassId", cabinClassId);
        return query.getResultList();
    }
    @Override
    public List<TicketFamily> getCabinClassTicketFamilys(String type) throws NoSuchTicketFamilyException {
        Query query = em.createQuery("SELECT t FROM CabinClass c, TicketFamily t WHERE c.type = :inType and c.cabinClassId = t.cabinClass.cabinClassId and c.deleted = FALSE and t.deleted = FALSE");
        query.setParameter("inType", type);
        List<TicketFamily> ticketFamilys = new ArrayList<>();
        try {
            ticketFamilys = query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchTicketFamilyException(AisMsg.NO_SUCH_TICKET_FAMILY_ERROR);
        }
        return ticketFamilys;
    }

    @Override
    public List<String> getCabinClassTicketFamilyNames(String type) {
        List<String> ticketFamilyNames = new ArrayList<>();
        try {
            for (TicketFamily ticketFamily : SafeHelper.emptyIfNull(getCabinClassTicketFamilys(type))) {
                ticketFamilyNames.add(ticketFamily.getName());
            }
        } catch (NoSuchTicketFamilyException e) {
            return null;
        }
        return ticketFamilyNames;
    }

    @Override
    public List<TicketFamily> getCabinClassTicketFamilysFromJoinTable(Long aircraftId, Long cabinClassId) throws NoSuchTicketFamilyException {
        Query query = em.createQuery("SELECT t FROM CabinClassTicketFamily ct, TicketFamily t "
                + "WHERE ct.aircraftCabinClass.aircraftId = :inAircraftId "
                + "AND ct.aircraftCabinClass.aircraft.status <> :inRetired AND ct.aircraftCabinClass.aircraft.status <> :inCrashed "
                + "AND ct.aircraftCabinClass.cabinClassId = :inCabinClassId AND ct.aircraftCabinClass.cabinClass.deleted = FALSE "
                + "AND ct.ticketFamily.ticketFamilyId = t.ticketFamilyId AND t.deleted = FALSE "
                + "AND ct.deleted = FALSE "
                + "ORDER BY ct.aircraftCabinClass.cabinClass.rank DESC, t.rank DESC");
        query.setParameter("inAircraftId", aircraftId);
        query.setParameter("inCabinClassId", cabinClassId);
        query.setParameter("inRetired", AircraftStatus.RETIRED);
        query.setParameter("inCrashed", AircraftStatus.CRASHED);
        List<TicketFamily> ticketFamilys = new ArrayList<>();
        try {
            ticketFamilys = (List<TicketFamily>) query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchTicketFamilyException(AisMsg.NO_SUCH_TICKET_FAMILY_ERROR);
        }
        return ticketFamilys;
    }

    @Override
    public CabinClassTicketFamily getCabinClassTicketFamilyJoinTable(Long aircraftId, Long cabinClassId, Long ticketFamilyId) throws NoSuchCabinClassTicketFamilyException {
        Query query = em.createQuery("SELECT ct FROM CabinClassTicketFamily ct "
                + "WHERE ct.aircraftCabinClass.aircraftId = :inAircraftId "
                + "AND ct.aircraftCabinClass.cabinClassId = :inCabinClassId "
                + "AND ct.ticketFamily.ticketFamilyId = :inTicketFamilyId "
                + "AND ct.aircraftCabinClass.cabinClass.deleted = FALSE "
                + "AND ct.aircraftCabinClass.aircraft.status <> :inRetired AND ct.aircraftCabinClass.aircraft.status <> :inCrashed "
                + "AND ct.ticketFamily.deleted = FALSE "
                + "AND ct.deleted = FALSE");
        query.setParameter("inAircraftId", aircraftId);
        query.setParameter("inCabinClassId", cabinClassId);
        query.setParameter("inTicketFamilyId", ticketFamilyId);
        query.setParameter("inRetired", AircraftStatus.RETIRED);
        query.setParameter("inCrashed", AircraftStatus.CRASHED);
        CabinClassTicketFamily cabinClassTicketFamily = null;
        try {
            cabinClassTicketFamily = (CabinClassTicketFamily) query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoSuchCabinClassTicketFamilyException(AisMsg.NO_SUCH_CABINCLASS_TICKETFAMILY_ERROR);
        }
        return cabinClassTicketFamily;
    }

    @Override
    public List<CabinClassTicketFamily> getCabinClassTicketFamilyJoinTables(Long aircraftId, Long cabinClassId) throws NoSuchCabinClassTicketFamilyException {
        Query query = em.createQuery("SELECT ct FROM CabinClassTicketFamily ct "
                + "WHERE ct.aircraftCabinClass.aircraftId = :inAircraftId "
                + "AND ct.aircraftCabinClass.cabinClassId = :inCabinClassId "
                + "AND ct.aircraftCabinClass.cabinClass.deleted = FALSE "
                + "AND ct.ticketFamily.deleted = FALSE "
                + "AND ct.deleted = FALSE "
                + "ORDER BY ct.aircraftCabinClass.cabinClass.rank ASC, ct.ticketFamily.rank ASC");
        query.setParameter("inAircraftId", aircraftId);
        query.setParameter("inCabinClassId", cabinClassId);
        List<CabinClassTicketFamily> cabinClassTicketFamilys = null;
        try {
            cabinClassTicketFamilys = (List<CabinClassTicketFamily>) query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchCabinClassTicketFamilyException(AisMsg.NO_SUCH_CABINCLASS_TICKETFAMILY_ERROR);
        }
        return cabinClassTicketFamilys;
    }

    @Override
    public List<CabinClassTicketFamilyHelper> getCabinClassTicketFamilyHelpers(Long aircraftId) throws NoSuchCabinClassException, NoSuchTicketFamilyException {
        List<CabinClassTicketFamilyHelper> helpers = new ArrayList<>();
        for (CabinClass cabinClass : getAircraftCabinClasses(aircraftId)) {
            CabinClassTicketFamilyHelper helper = new CabinClassTicketFamilyHelper();
            List<TicketFamily> ticketFamilys = getCabinClassTicketFamilysFromJoinTable(aircraftId, cabinClass.getCabinClassId());
            if (!ticketFamilys.isEmpty()) {
                helper.setHaveTicketFamily(true);
            }
            helper.setCabinClass(cabinClass);
            helper.setTicketFamilys(ticketFamilys);
            helpers.add(helper);
        }
        return helpers;
    }
    
    
    @Override
    public TicketFamily getTicketFamilyById(Long id) throws NoSuchTicketFamilyException {
        Query query = em.createQuery("SELECT t FROM TicketFamily t WHERE t.ticketFamilyId = :inId and t.deleted = FALSE");
        query.setParameter("inId", id);
        TicketFamily ticketFamily = null;
        try {
            ticketFamily = (TicketFamily) query.getSingleResult();
        } catch (NoResultException ex) {
            throw new NoSuchTicketFamilyException(AisMsg.NO_SUCH_TICKET_FAMILY_ERROR);
        }
        return ticketFamily;
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public List<TicketFamily> getAllTicketFamily() {
        Query query = em.createQuery("SELECT t FROM TicketFamily t WHERE t.deleted = false");
        return query.getResultList();
    }
    
    @Override
    public void createTicketFamily(String type, String name, String cabinclassname, List<TicketFamilyRuleHelper> displayRuleList)
            throws ExistSuchTicketFamilyException, NoSuchCabinClassException {
        verifyTicketFamilyExistence(type, name, cabinclassname);
        TicketFamily ticketFamily = new TicketFamily();
        ticketFamily.create(type, name);
        System.out.print("The cabinclass name is: " + cabinclassname);
        ticketFamily.setCabinClass(getCabinClassByName(cabinclassname));
        em.persist(ticketFamily);
        em.flush();
        addTicketFamilyRule(ticketFamily, displayRuleList);
    }

    private void addTicketFamilyRule(TicketFamily ticketFamily, List<TicketFamilyRuleHelper> newTicketFamilyRuleHelpers) {
        Rule thisRule;
        List<TicketFamilyRule> ticketFamilyRuleList = new ArrayList(); //too be added at aircraft side

        for (TicketFamilyRuleHelper ticketFamilyRuleHelper : newTicketFamilyRuleHelpers) {

            TicketFamilyRule thisTicketFamilyRule = new TicketFamilyRule();
            thisRule = em.find(Rule.class, ticketFamilyRuleHelper.getRuleId());
            // add necessary attributes to aircraftCabinClass object
            thisTicketFamilyRule.setTicketFamily(ticketFamily);
            thisTicketFamilyRule.setTicketFamilyId(ticketFamily.getTicketFamilyId());
            thisTicketFamilyRule.setRule(thisRule);
            thisTicketFamilyRule.setRuleId(thisRule.getRuleId());
            thisTicketFamilyRule.setRuleValue((float) ticketFamilyRuleHelper.getRuleValue());

            System.out.print("this display rule ticketfamily id is:" + ticketFamilyRuleHelper.getTicketFamilyId());
            System.out.print("this display rule id is: " + ticketFamilyRuleHelper.getRuleId());
            System.out.print("this display rule value is: " + ticketFamilyRuleHelper.getRuleValue());
            em.persist(thisTicketFamilyRule);
            ticketFamilyRuleList.add(thisTicketFamilyRule);
        }
        ticketFamily.setTicketFamilyRules(ticketFamilyRuleList);
        em.merge(ticketFamily);
        em.flush();
    }

    @Override
    public void verifyTicketFamilyExistence(String type, String name, String cabinclassname) throws ExistSuchTicketFamilyException {

        System.out.print(cabinclassname);
        System.out.print(type);
        System.out.print(name);
        List<TicketFamily> ticketFamilys = getAllTicketFamily();
        if (ticketFamilys != null) {
            for (TicketFamily tk : ticketFamilys) {
                if (type.equals(tk.getType()) && tk.getCabinClass().getName().equals(cabinclassname)) {
                    throw new ExistSuchTicketFamilyException(AisMsg.EXIST_SUCH_TICKET_FAMILY_ERROR);
                }

            }
        }
    }

    @Override
    public void deleteTicketFamily(String type, String cabinClassName) throws NoSuchTicketFamilyException {
        TicketFamily ticketfamily = getTicketFamilyByTypeAndCabinClass(type, cabinClassName);
        if (ticketfamily == null) {
            throw new NoSuchTicketFamilyException(AisMsg.NO_SUCH_TICKET_FAMILY_ERROR);
        } else {
            ticketfamily.setDeleted(true);
            em.merge(ticketfamily);
        }
    }

    @Override
    public void updateTicketFamily(String oldtype, String oldcabinclass, String type, String name, String cabinclassname) throws NoSuchTicketFamilyException, ExistSuchTicketFamilyException, NoSuchCabinClassException {
        TicketFamily c = getTicketFamilyByTypeAndCabinClass(oldtype, oldcabinclass);
        if (c == null) {
            throw new NoSuchTicketFamilyException(AisMsg.NO_SUCH_TICKET_FAMILY_ERROR);
        } else {
            List<TicketFamily> ticketFamilys = getAllOtherTicketFamilyByTypeAndCabinClass(oldtype, oldcabinclass);
            System.out.print("we are here");
            System.out.println("ticketFamlyList: " + ticketFamilys);
            for (TicketFamily tk : ticketFamilys) {
                System.out.println("tk = " + tk);
                if (tk != null) {
                    System.out.print(tk.getType());
                    System.out.print(tk.getName());
                    System.out.print("CabinClass = " + tk.getCabinClass());
                } else {
                    System.out.println("Tk is null");
                }

            }
            if (ticketFamilys != null) {
                for (TicketFamily tk : ticketFamilys) {
                    if (type.equals(tk.getType()) && tk.getCabinClass().getName().equals(cabinclassname)) {
                        throw new ExistSuchTicketFamilyException(AisMsg.EXIST_SUCH_TICKET_FAMILY_ERROR);

                    }

                }
            }
        }
        c.setName(name);
        c.setType(type);
        c.setCabinClass(getCabinClassByName(cabinclassname));
        em.merge(c);
        em.flush();
    }

    @Override
    public TicketFamily getTicketFamilyByTypeAndCabinClass(String ticketFamilyType, String cabinClassName) {
        Query query = em.createQuery("SELECT t FROM TicketFamily t WHERE t.type = :inticketFamilyType AND t.cabinClass.name = :inCabinClassName");
        query.setParameter("inticketFamilyType", ticketFamilyType);
        query.setParameter("inCabinClassName", cabinClassName);
        TicketFamily ticketFamily = null;
        try {
            ticketFamily = (TicketFamily) query.getSingleResult();
        } catch (NoResultException ex) {
            ticketFamily = null;
        }
        return ticketFamily;
    }

    @Override
    public List<TicketFamily> getAllOtherTicketFamilyByTypeAndCabinClass(String oldtype, String cabinclassname) {
        Query query = em.createQuery("SELECT t FROM TicketFamily t WHERE t.ticketFamilyId NOT IN (SELECT c.ticketFamilyId FROM TicketFamily c where c.type LIKE :oldtype AND c.cabinClass.name LIKE :cabinclassname)");
        query.setParameter("oldtype", oldtype);
        query.setParameter("cabinclassname", cabinclassname);
        return query.getResultList();
    }

    @Override
    public List<String> getTicketFamilyBookingClassNames(String cabinClassName, String ticketFamilyName) {
        List<String> bookingClassNames = new ArrayList<>();
        try {
            for (BookingClass bookingClass : SafeHelper.emptyIfNull(getTicketFamilyBookingClasses(cabinClassName, ticketFamilyName))) {
                bookingClassNames.add(bookingClass.getName());
            }
        } catch (NoSuchBookingClassException e) {
            return null;
        }
        return bookingClassNames;
    }

    @Override
    public List<BookingClass> getTicketFamilyBookingClasses(String cabinClassName, String ticketFamilyName) throws NoSuchBookingClassException {
        Query query = em.createQuery("SELECT b FROM TicketFamily t, BookingClass b WHERE t.cabinClass.name = :inCabinClassName and t.name = :inTicketFamilyName and b.ticketFamily.ticketFamilyId = t.ticketFamilyId and b.deleted = FALSE and t.deleted = FALSE");
        query.setParameter("inCabinClassName", cabinClassName);
        query.setParameter("inTicketFamilyName", ticketFamilyName);
        List<BookingClass> bookingClasses = new ArrayList<>();
        try {
            bookingClasses = (List<BookingClass>) query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchBookingClassException(AisMsg.NO_SUCH_BOOKING_CLASS_ERROR);
        }
        return bookingClasses;
    }

    @Override
    public void updateTicketFamilyRuleVlaue(TicketFamilyRule ticketFamilyRule) {
        TicketFamilyRule thisTicketFamilyRule = getTicketFamilyRuleByTwoId(ticketFamilyRule.getTicketFamilyId(), ticketFamilyRule.getRuleId());
        thisTicketFamilyRule.setRuleValue(ticketFamilyRule.getRuleValue());
        em.merge(thisTicketFamilyRule);
        em.flush();

    }

    public TicketFamilyRule getTicketFamilyRuleByTwoId(long ticketFamilyId, long ruleId) {
        Query query = em.createQuery("SELECT u FROM TicketFamilyRule u WHERE u.ticketFamilyId = :inticketFamilyId AND u.ruleId = :inruleId AND u.rule.deleted = FALSE AND u.ticketFamily.deleted = FALSE");
        query.setParameter("inticketFamilyId", ticketFamilyId);
        query.setParameter("inruleId", ruleId);
        TicketFamilyRule tfr = null;
        try {
            tfr = (TicketFamilyRule) query.getSingleResult();
        } catch (NoResultException ex) {
            tfr = null;
        }
        return tfr;
    }

    @Override
    public TicketFamilyRule getTicketFamilyRuleById(Long ticketFamilyId, Long ruleId) throws NoSuchTicketFamilyRuleException {
        Query query = em.createQuery("SELECT u FROM TicketFamilyRule u WHERE u.ticketFamilyId = :inticketFamilyId AND u.ruleId = :inruleId AND u.rule.deleted = FALSE AND u.ticketFamily.deleted = FALSE");
        query.setParameter("inticketFamilyId", ticketFamilyId);
        query.setParameter("inruleId", ruleId);
        TicketFamilyRule tfr;
        try {
            tfr = (TicketFamilyRule) query.getSingleResult();
        } catch (NoResultException ex) {
            throw new NoSuchTicketFamilyRuleException(AisMsg.NO_SUCH_TICKETFAMILY_RULE_ERROR);
        }
        return tfr;
    }

    @Override
    public List<TicketFamilyRule> getTicketFamilyRuleByTicketFamilyId(long ticketFamilyId) {
        Query query = em.createQuery("SELECT u FROM TicketFamilyRule u WHERE u.ticketFamilyId = :inticketFamilyId AND u.rule.deleted = FALSE");
        query.setParameter("inticketFamilyId", ticketFamilyId);
        List<TicketFamilyRule> ticketFamilyRules = null;

        ticketFamilyRules = (List<TicketFamilyRule>) query.getResultList();

        return ticketFamilyRules;
    }

    @Override
    public List<Rule> getRulesByTicketFmailyId(Long ticketFamilyId) throws NoSuchRuleException {
        Query query = em.createQuery("SELECT r FROM TicketFamilyRule tr, Rule r WHERE tr.ticketFamilyId = :inticketFamilyId AND tr.ruleId = r.ruleId AND tr.rule.deleted = FALSE AND tr.ticketFamily.deleted = FALSE");
        query.setParameter("inticketFamilyId", ticketFamilyId);
        List<Rule> rules = new ArrayList<>();
        try {
            rules = (List<Rule>) query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchRuleException(AisMsg.NO_SUCH_RULE_ERROR);
        }
        return rules;
    }

    @Override
    public void deleteTicketFamilyByType(String type) throws NoSuchTicketFamilyException {
        TicketFamily ticketfamily = getTicketFamilyByType(type);
        if (ticketfamily == null) {
            throw new NoSuchTicketFamilyException(AisMsg.NO_SUCH_TICKET_FAMILY_ERROR);
        } else {
            ticketfamily.setDeleted(true);
            em.merge(ticketfamily);
        }
    }

    private TicketFamily getTicketFamilyByType(String type) {
        Query query = em.createQuery("SELECT t FROM TicketFamily t WHERE t.type = :inticketFamilyType AND t.deleted = FALSE");
        query.setParameter("inticketFamilyType", type);
        TicketFamily ticketFamily = null;
        try {
            ticketFamily = (TicketFamily) query.getSingleResult();
        } catch (NoResultException ex) {
            ticketFamily = null;
        }
        return ticketFamily;
    }

    @Override
    public void updateTicketFamilyByType(long ticketFamilyId, String oldCabinClassName, String type, String name) throws ExistSuchTicketFamilyException, NoSuchTicketFamilyException {
        TicketFamily c = getTicketFamilyById(ticketFamilyId);
        if (c == null) {
            throw new NoSuchTicketFamilyException(AisMsg.NO_SUCH_TICKET_FAMILY_ERROR);
        } else {
            List<TicketFamily> ticketFamilys = getAllOtherTicketFamilyByTypeAndCabinClass(c.getType(), oldCabinClassName);
            System.out.print("we are here");
            System.out.println("ticketFamlyList: " + ticketFamilys);
            for (TicketFamily tk : ticketFamilys) {
                System.out.println("tk = " + tk);
                if (tk != null) {
                    System.out.print(tk.getType());
                    System.out.print(tk.getName());
                    System.out.print("CabinClass = " + tk.getCabinClass());
                } else {
                    System.out.println("Tk is null");
                }

            }
            if (ticketFamilys != null) {
                for (TicketFamily tk : ticketFamilys) {
                    if (type.equals(tk.getType()) && tk.getCabinClass().getName().equals(oldCabinClassName)) {
                        throw new ExistSuchTicketFamilyException(AisMsg.EXIST_SUCH_TICKET_FAMILY_ERROR);

                    }
                    if (name.equals(tk.getName()) && tk.getCabinClass().getName().equals(oldCabinClassName)) {
                        throw new ExistSuchTicketFamilyException(AisMsg.EXIST_SUCH_TICKET_FAMILY_ERROR);

                    }
                    if (type.equals(tk.getType())) {
                        throw new ExistSuchTicketFamilyException(AisMsg.EXIST_SUCH_TICKET_FAMILY_ERROR);

                    }
                }
            }
        }
        c.setName(name);
        c.setType(type);

        em.merge(c);
        em.flush();
    }

    @Override
    public List<BookingClassHelper> getTicketFamilyBookingClassHelpers(String cabinClassName, String ticketFamilyName) {
        List<BookingClassHelper> bookingClassHelpers = new ArrayList<>();
        try {
            for (BookingClass bookingClass : getTicketFamilyBookingClasses(cabinClassName, ticketFamilyName)) {
                BookingClassHelper bookingClassHelper = new BookingClassHelper();
                bookingClassHelper.setBookingClass(bookingClass);
                bookingClassHelpers.add(bookingClassHelper);
            }
        } catch (NoSuchBookingClassException e) {
            System.out.println(e.getMessage());
        }
        return bookingClassHelpers;
    }

    @Override
    public List<TicketFamilyBookingClassHelper> getTicketFamilyBookingClassHelpers(Long flightScheduleId, Long aircraftId, Long cabinClassId) {
        List<CabinClassTicketFamily> cabinClassTicketFamilys;
        try {
            cabinClassTicketFamilys = getCabinClassTicketFamilyJoinTables(aircraftId, cabinClassId);
        } catch (NoSuchCabinClassTicketFamilyException e) {
            cabinClassTicketFamilys = new ArrayList<>();
        }

        List<TicketFamilyBookingClassHelper> ticketFamilyBookingClassHelpers = new ArrayList<>();
        for (CabinClassTicketFamily cabinClassTicketFamily : cabinClassTicketFamilys) {
            Long ticketFamilyId = cabinClassTicketFamily.getTicketFamily().getTicketFamilyId();
            TicketFamilyBookingClassHelper helper = new TicketFamilyBookingClassHelper();
            helper.setSeatQty(cabinClassTicketFamily.getSeatQty());
            helper.setTicketFamily(cabinClassTicketFamily.getTicketFamily());
            helper.setPrice(cabinClassTicketFamily.getPrice());
            helper.setBookingClassHelpers(revMgmtSession.getBookingClassHelpers(flightScheduleId, ticketFamilyId));
            try {
                helper.setBookingClasses(revMgmtSession.getFlightScheduleBookingClassesOfTicketFamily(flightScheduleId, ticketFamilyId));
            } catch (NoSuchBookingClassException ex) {
            }
            ticketFamilyBookingClassHelpers.add(helper);
        }
        return ticketFamilyBookingClassHelpers;
    }

    @Override
    public void assignAircraftTicketFamily(Aircraft aircraft, List<CabinClassTicketFamilyHelper> cabinClassTicketFamilyHelpers)
            throws NeedTicketFamilyException, NoSuchAircraftCabinClassException, NoSuchCabinClassTicketFamilyException {

        List<CabinClassTicketFamily> cabinClassTicketFamilys = new ArrayList<>();

        for (CabinClassTicketFamilyHelper helper : cabinClassTicketFamilyHelpers) {

            //Get CabinClass
            CabinClass cabinClass = em.find(CabinClass.class, helper.getCabinClass().getCabinClassId());

            /**
             * Get original CabinClassTicketFamily DisLink all
             * CabinClassTicketFamily (set their deleted to true.), later if
             * cabinClass exist, just set deleted to false
             */
            List<CabinClassTicketFamily> originCabinClassTicketFamilys
                    = getCabinClassTicketFamilyJoinTables(aircraft.getAircraftId(), cabinClass.getCabinClassId());
            disLinkCabinClassTicketFamily(originCabinClassTicketFamilys);

            //Get aircraftCabinClass
            AircraftCabinClass aircraftCabinClass = getAircraftCabinClassById(aircraft.getAircraftId(), cabinClass.getCabinClassId());
            //Create aircraftCabinClassId
            AircraftCabinClassId aircraftCabinClassId = new AircraftCabinClassId(aircraft.getAircraftId(), cabinClass.getCabinClassId());
            List<TicketFamily> ticketFamilys = helper.getTicketFamilys();
            if (ticketFamilys.isEmpty()) {
                throw new NeedTicketFamilyException(AisMsg.NEED_TICKET_FAMILY_ERROR);
            }
            for (TicketFamily ticketFamily : ticketFamilys) {
                TicketFamily originTicketFamily = em.find(TicketFamily.class, ticketFamily.getTicketFamilyId());
                //Create CabinClassTicketFamilyId
                CabinClassTicketFamilyId cabinClassTicketFamilyId = new CabinClassTicketFamilyId(aircraftCabinClassId, originTicketFamily.getTicketFamilyId());
                CabinClassTicketFamily cabinClassTicketFamily = em.find(CabinClassTicketFamily.class, cabinClassTicketFamilyId);
                if (cabinClassTicketFamily != null) {
                    cabinClassTicketFamily.setDeleted(false);
                    em.merge(cabinClassTicketFamily);
                } else {
                    //Create CabinClassTicketFamily
                    cabinClassTicketFamily = new CabinClassTicketFamily();
                    cabinClassTicketFamily.setAircraftCabinClass(aircraftCabinClass);
                    cabinClassTicketFamily.setCabinClassTicketFamilyId(cabinClassTicketFamilyId);
                    cabinClassTicketFamily.setTicketFamily(originTicketFamily);
                    cabinClassTicketFamily.setSeatQty(0);
                    cabinClassTicketFamily.setPrice((float) 0);
                    cabinClassTicketFamily.setDeleted(false);
                    em.persist(cabinClassTicketFamily);
                    em.flush();
                }
                /**
                 ** DisLink booking class relationship with flight schedule,
                 * whose ticket family has been deleted.
                 *
                 */
                for (CabinClassTicketFamily cctf : originCabinClassTicketFamilys) {
                    if (cctf.getDeleted()) {
                        revMgmtSession.dislinkFlightScheduleBookingClass(cctf);
                    }
                }
                //Add cabinClassFamily List
                cabinClassTicketFamilys.add(cabinClassTicketFamily);
            }

            //Set CabinClassTicketFamily to AircraftCabinClass
            aircraftCabinClass.setCabinClassTicketFamilys(cabinClassTicketFamilys);
            em.merge(aircraftCabinClass);
        }
    }

    @Override
    public void disLinkCabinClassTicketFamily(List<CabinClassTicketFamily> cabinClassTicketFamilys) {
        for (CabinClassTicketFamily cctf : cabinClassTicketFamilys) {
            CabinClassTicketFamily cabinClassTicketFamily = em.find(CabinClassTicketFamily.class, cctf.getCabinClassTicketFamilyId());
            cabinClassTicketFamily.setDeleted(true);
        }
    }

    @Override
    public CabinClassTicketFamily getOriginalCabinClassTicketFamily(Long aircraftId, Long cabinClassId, Long ticketFamilyId) {
        AircraftCabinClassId aircraftCabinClassId = new AircraftCabinClassId(aircraftId, cabinClassId);
        CabinClassTicketFamilyId cabinClassTicketFamilyId = new CabinClassTicketFamilyId(aircraftCabinClassId, ticketFamilyId);
        CabinClassTicketFamily cabinClassTicketFamily = em.find(CabinClassTicketFamily.class, cabinClassTicketFamilyId);
        return cabinClassTicketFamily;
    }
}
