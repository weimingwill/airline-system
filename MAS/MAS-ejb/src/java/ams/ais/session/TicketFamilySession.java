/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.BookingClass;
import ams.ais.entity.CabinClass;
import ams.ais.entity.Rule;
import ams.ais.entity.TicketFamily;
import ams.ais.entity.TicketFamilyRule;
import ams.ais.helper.TicketFamilyRuleHelper;
import ams.ais.util.exception.ExistSuchTicketFamilyException;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.ais.util.helper.AisMsg;
import ams.aps.util.exception.EmptyTableException;
import ams.aps.util.helper.Message;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Bowen
 */
@Stateless
public class TicketFamilySession implements TicketFamilySessionLocal {

    @EJB
    private CabinClassSessionLocal cabinClassSession;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public TicketFamily getTicketFamilyByName(String ticketFamilyName) {
        Query query = entityManager.createQuery("SELECT t FROM TicketFamily t WHERE t.name = :inticketFamilyName AND t.deleted = FALSE");
        query.setParameter("inticketFamilyName", ticketFamilyName);
        TicketFamily ticketFamily = null;
        try {
            ticketFamily = (TicketFamily) query.getSingleResult();
        } catch (NoResultException ex) {
            ticketFamily = null;
        }
        return ticketFamily;

    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public List<TicketFamily> getAllTicketFamily() {
        Query query = entityManager.createQuery("SELECT t FROM TicketFamily t WHERE t.deleted = false");
        return query.getResultList();
    }

    @Override
    public List<CabinClass> getAllCabinClass() {
        Query query = entityManager.createQuery("SELECT c FROM CabinClass c WHERE c.deleted = false");
        return query.getResultList();
    }
    
    
    @Override
    public void createTicketFamily(String type, String name, String cabinclassname,List<TicketFamilyRuleHelper> displayRuleList) throws ExistSuchTicketFamilyException {
        verifyTicketFamilyExistence(type, name, cabinclassname);
        TicketFamily ticketFamily = new TicketFamily();
        ticketFamily.create(type, name);
        System.out.print("The cabinclass name is: "+ cabinclassname);
        ticketFamily.setCabinClass(cabinClassSession.getCabinClassByName(cabinclassname));
        entityManager.persist(ticketFamily);
        entityManager.flush(); 
        addTicketFamilyRule(ticketFamily, displayRuleList);
    }
    
    private void addTicketFamilyRule(TicketFamily ticketFamily, List<TicketFamilyRuleHelper> newTicketFamilyRuleHelpers){
        Rule thisRule;
        List<TicketFamilyRule> ticketFamilyRuleList = new ArrayList(); //too be added at aircraft side

        for (TicketFamilyRuleHelper ticketFamilyRuleHelper : newTicketFamilyRuleHelpers) {
            
            TicketFamilyRule thisTicketFamilyRule = new TicketFamilyRule();
            thisRule = entityManager.find(Rule.class, ticketFamilyRuleHelper.getRuleId());
            // add necessary attributes to aircraftCabinClass object
            thisTicketFamilyRule.setTicketFamily(ticketFamily);
            thisTicketFamilyRule.setTicketFamilyId(ticketFamily.getTicketFamilyId());
            thisTicketFamilyRule.setRule(thisRule);
            thisTicketFamilyRule.setRuleId(thisRule.getRuleId());
            thisTicketFamilyRule.setRuleValue((float)ticketFamilyRuleHelper.getRuleValue());
            
            System.out.print("this display rule ticketfamily id is:" +ticketFamilyRuleHelper.getTicketFamilyId());
            System.out.print("this display rule id is: "+ticketFamilyRuleHelper.getRuleId());
            System.out.print("this display rule value is: "+ticketFamilyRuleHelper.getRuleValue());
            entityManager.persist(thisTicketFamilyRule);
            ticketFamilyRuleList.add(thisTicketFamilyRule);
        }
        ticketFamily.setTicketFamilyRules(ticketFamilyRuleList);
        entityManager.merge(ticketFamily);
        entityManager.flush();
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
            entityManager.merge(ticketfamily);
        }
    }

    @Override
    public void updateTicketFamily(String oldtype, String oldcabinclass, String type, String name, String cabinclassname) throws NoSuchTicketFamilyException, ExistSuchTicketFamilyException {
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
        c.setCabinClass(cabinClassSession.getCabinClassByName(cabinclassname));
        entityManager.merge(c);
        entityManager.flush();
    }

    @Override
    public List<TicketFamily> getAllOtherTicketFamily(String name) {
        Query query = entityManager.createQuery("SELECT c FROM TicketFamily c where c.name <> :name");
        query.setParameter("name", name);
        return query.getResultList();
    }

    @Override
    public TicketFamily getTicketFamilyByTypeAndCabinClass(String ticketFamilyType, String cabinClassName) {
        Query query = entityManager.createQuery("SELECT t FROM TicketFamily t WHERE t.type = :inticketFamilyType AND t.cabinClass.name = :inCabinClassName");
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
        Query query = entityManager.createQuery("SELECT t FROM TicketFamily t WHERE t.ticketFamilyId NOT IN (SELECT c.ticketFamilyId FROM TicketFamily c where c.type LIKE :oldtype AND c.cabinClass.name LIKE :cabinclassname)");
        query.setParameter("oldtype", oldtype);
        query.setParameter("cabinclassname", cabinclassname);
        return query.getResultList();
    }

    @Override
    public List<BookingClass> getTicketFamilyBookingClass(String name) throws NoSuchBookingClassException {
        Query query = entityManager.createQuery("SELECT b FROM TicketFamily t, BookingClass b WHERE t.name = :inName and b.ticketFamily.ticketFamilyId = t.ticketFamilyId and b.deleted = FALSE and t.deleted = FALSE");
        query.setParameter("inName", name);
        List<BookingClass> bookingClasses = new ArrayList<>();
        try {
            bookingClasses = query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchBookingClassException(AisMsg.EXIST_SUCH_BOOKING_CLASS_ERROR);
        }
        return bookingClasses;
    }

    @Override
    public List<Rule> getAllRules() throws EmptyTableException {
        Query query = entityManager.createQuery("SELECT c FROM Rule c WHERE c.deleted=False");
        List<Rule> rules = null;
        try {
            rules = (List<Rule>) query.getResultList();
        } catch (NoResultException ex) {
            throw new EmptyTableException(Message.EMPTY_TABLE);
        }
        return rules;
    }
    
    
    @Override
    public void updateTicketFamilyRuleVlaue(TicketFamilyRule ticketFamilyRule){
        TicketFamilyRule thisTicketFamilyRule = getTicketFamilyRuleByTwoId(ticketFamilyRule.getTicketFamilyId(),ticketFamilyRule.getRuleId());
        thisTicketFamilyRule.setRuleValue(ticketFamilyRule.getRuleValue());
        entityManager.merge(thisTicketFamilyRule);
        entityManager.flush();
        
    }
   
    public TicketFamilyRule getTicketFamilyRuleByTwoId(long ticketFamilyId, long ruleId){
        
        Query query = entityManager.createQuery("SELECT u FROM TicketFamilyRule u WHERE u.ticketFamilyId = :inticketFamilyId AND u.ruleId = :inruleId");
        query.setParameter("inticketFamilyId", ticketFamilyId);
        query.setParameter("inruleId",ruleId);
        TicketFamilyRule tfr = null;
        try {
            tfr = (TicketFamilyRule) query.getSingleResult();
        } catch (NoResultException ex) {
            tfr = null;
        }
        return tfr;
    }
    
//    private boolean addTicketFamilyRule(TicketFamily newTicketFamily, List<TicketFamilyRuleHelper> newAircraftCabinClassHelpers) {
//        CabinClass thisCabinClass;
//        List<AircraftCabinClass> aircraftCabinClassList = new ArrayList(); //too be added at aircraft side
//
//        for (AircraftCabinClassHelper aircraftCabinClassHelper : newAircraftCabinClassHelpers) {
//            AircraftCabinClass thisAircraftCabinClass = new AircraftCabinClass();
//            thisCabinClass = entityManager.find(CabinClass.class, aircraftCabinClassHelper.getId());
//            // add necessary attributes to aircraftCabinClass object
//            thisAircraftCabinClass.setAircraft(newAircraft);
//            thisAircraftCabinClass.setAircraftId(newAircraft.getAircraftId());
//            thisAircraftCabinClass.setCabinClass(thisCabinClass);
//            thisAircraftCabinClass.setCabinClassId(thisCabinClass.getCabinClassId());
//            thisAircraftCabinClass.setSeatQty(aircraftCabinClassHelper.getSeatQty());
//            entityManager.persist(thisAircraftCabinClass);
//            aircraftCabinClassList.add(thisAircraftCabinClass);
//        }
//        newAircraft.setAircraftCabinClasses(aircraftCabinClassList);
//        entityManager.merge(newAircraft);
//        entityManager.flush();
//        return true;
//    }

    @Override
    public List<TicketFamilyRule> getTicketFamilyRuleByTicketFamilyId(long ticketFamilyId) {
        Query query = entityManager.createQuery("SELECT u FROM TicketFamilyRule u WHERE u.ticketFamilyId = :inticketFamilyId AND u.rule.deleted = FALSE");
        query.setParameter("inticketFamilyId", ticketFamilyId);
        List<TicketFamilyRule> ticketFamilyRules = null;
        
        
            ticketFamilyRules = (List<TicketFamilyRule>) query.getResultList();
           
        
        return ticketFamilyRules;
    }

    @Override
    public TicketFamilyRule getTicketFamilyRuleByTicketFamilyType(String ticketFamilyType) {
       Query query = entityManager.createQuery("SELECT u FROM TicketFamilyRule u WHERE u.type = :inticketFamilyType");
        query.setParameter("inticketFamilyType", ticketFamilyType);
        TicketFamilyRule ticketFamilyRule = null;
       
        
            ticketFamilyRule = (TicketFamilyRule) query.getSingleResult();
           
        
        return ticketFamilyRule;
    }

    @Override
    public void deleteTicketFamilyByType(String type) throws NoSuchTicketFamilyException {
        TicketFamily ticketfamily = getTicketFamilyByType(type);
        if (ticketfamily == null) {
            throw new NoSuchTicketFamilyException(AisMsg.NO_SUCH_TICKET_FAMILY_ERROR);
        } else {
            ticketfamily.setDeleted(true);
            entityManager.merge(ticketfamily);
        }
    }

    private TicketFamily getTicketFamilyByType(String type) {
        Query query = entityManager.createQuery("SELECT t FROM TicketFamily t WHERE t.type = :inticketFamilyType AND t.deleted = FALSE");
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
    public TicketFamily getTicketFamilyById(long ticketFamilyId) {
        return entityManager.find(TicketFamily.class,ticketFamilyId );
    }

    @Override
    public void updateTicketFamilyByType(long ticketFamilyId, String oldCabinClassName, String type, String name) throws ExistSuchTicketFamilyException,NoSuchTicketFamilyException{
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
        
        entityManager.merge(c);
        entityManager.flush();
    } 
    }

