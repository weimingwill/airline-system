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
import ams.ais.entity.CabinClassTicketFamily;
import ams.ais.entity.TicketFamily;
import ams.ais.helper.CabinClassTicketFamilyId;
import ams.ais.util.exception.ExistSuchTicketFamilyException;
import ams.ais.util.exception.NeedTicketFamilyException;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchCabinClassTicketFamilyException;
import ams.ais.util.exception.NoSuchRuleException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.ais.util.exception.NoSuchTicketFamilyRuleException;
import ams.ais.util.helper.AisMsg;
import ams.aps.util.exception.EmptyTableException;
import ams.aps.util.helper.Message;
import ams.ais.util.helper.BookingClassHelper;
import ams.ais.util.helper.CabinClassTicketFamilyHelper;
import ams.ais.util.helper.TicketFamilyBookingClassHelper;
import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftCabinClass;
import ams.aps.helper.AircraftCabinClassId;
import ams.aps.util.exception.NoSuchAircraftCabinClassException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mas.util.helper.SafeHelper;

/**
 *
 * @author Bowen
 */
@Stateless
public class TicketFamilySession implements TicketFamilySessionLocal {

    @EJB
    private CabinClassSessionLocal cabinClassSession;
    @EJB
    private BookingClassSessionLocal bookingClassSession;
    @EJB
    private AircraftSessionLocal aircraftSession;
    @EJB
    private FlightScheduleSessionLocal flightScheduleSession;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public TicketFamily getTicketFamilyById(Long id) throws NoSuchTicketFamilyException {
        Query query = entityManager.createQuery("SELECT t FROM TicketFamily t WHERE t.ticketFamilyId = :inId and t.deleted = FALSE");
        query.setParameter("inId", id);
        TicketFamily ticketFamily = null;
        try {
            ticketFamily = (TicketFamily) query.getSingleResult();
        } catch (NoResultException ex) {
            throw new NoSuchTicketFamilyException(AisMsg.NO_SUCH_TICKET_FAMILY_ERROR);
        }
        return ticketFamily;
    }

    @Override
    public TicketFamily getTicketFamilyByName(String ticketFamilyName) throws NoSuchTicketFamilyException {
        Query query = entityManager.createQuery("SELECT t FROM TicketFamily t WHERE t.name = :inticketFamilyName and t.deleted = FALSE");
        query.setParameter("inticketFamilyName", ticketFamilyName);
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
        Query query = entityManager.createQuery("SELECT t FROM TicketFamily t WHERE t.deleted = false");
        return query.getResultList();
    }

    @Override
    public List<CabinClass> getAllCabinClass() {
        Query query = entityManager.createQuery("SELECT c FROM CabinClass c WHERE c.deleted = false");
        return query.getResultList();
    }

    @Override
    public void createTicketFamily(String type, String name, String cabinclassname, List<TicketFamilyRuleHelper> displayRuleList)
            throws ExistSuchTicketFamilyException, NoSuchCabinClassException {
        verifyTicketFamilyExistence(type, name, cabinclassname);
        TicketFamily ticketFamily = new TicketFamily();
        ticketFamily.create(type, name);
        System.out.print("The cabinclass name is: " + cabinclassname);
        ticketFamily.setCabinClass(cabinClassSession.getCabinClassByName(cabinclassname));
        entityManager.persist(ticketFamily);
        entityManager.flush();
        addTicketFamilyRule(ticketFamily, displayRuleList);
    }

    private void addTicketFamilyRule(TicketFamily ticketFamily, List<TicketFamilyRuleHelper> newTicketFamilyRuleHelpers) {
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
            thisTicketFamilyRule.setRuleValue((float) ticketFamilyRuleHelper.getRuleValue());

            System.out.print("this display rule ticketfamily id is:" + ticketFamilyRuleHelper.getTicketFamilyId());
            System.out.print("this display rule id is: " + ticketFamilyRuleHelper.getRuleId());
            System.out.print("this display rule value is: " + ticketFamilyRuleHelper.getRuleValue());
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
        Query query = entityManager.createQuery("SELECT b FROM TicketFamily t, BookingClass b WHERE t.cabinClass.name = :inCabinClassName and t.name = :inTicketFamilyName and b.ticketFamily.ticketFamilyId = t.ticketFamilyId and b.deleted = FALSE and t.deleted = FALSE");
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
    public List<BookingClass> getTicketFamilyBookingClasses(Long cabinClassId, Long ticketFamilyId) throws NoSuchBookingClassException {
        Query query = entityManager.createQuery("SELECT b FROM TicketFamily t, BookingClass b WHERE t.cabinClass.cabinClassId = :inCabinClassId and t.ticketFamilyId = :inTicketFamilyId and b.ticketFamily.ticketFamilyId = t.ticketFamilyId and b.deleted = FALSE and t.deleted = FALSE");
        query.setParameter("inCabinClassId", cabinClassId);
        query.setParameter("inTicketFamilyId", ticketFamilyId);
        List<BookingClass> bookingClasses = new ArrayList<>();
        try {
            bookingClasses = (List<BookingClass>) query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchBookingClassException(AisMsg.NO_SUCH_BOOKING_CLASS_ERROR);
        }
        return bookingClasses;
    }

    @Override
    public List<Rule> getAllRules() throws NoSuchRuleException {
        Query query = entityManager.createQuery("SELECT c FROM Rule c WHERE c.deleted=False");
        List<Rule> rules = new ArrayList<>();
        try {
            rules = (List<Rule>) query.getResultList();
        } catch (NoResultException ex) {
            throw new NoSuchRuleException(AisMsg.NO_SUCH_RULE_ERROR);
        }
        return rules;
    }

    @Override
    public void updateTicketFamilyRuleVlaue(TicketFamilyRule ticketFamilyRule) {
        TicketFamilyRule thisTicketFamilyRule = getTicketFamilyRuleByTwoId(ticketFamilyRule.getTicketFamilyId(), ticketFamilyRule.getRuleId());
        thisTicketFamilyRule.setRuleValue(ticketFamilyRule.getRuleValue());
        entityManager.merge(thisTicketFamilyRule);
        entityManager.flush();

    }

    public TicketFamilyRule getTicketFamilyRuleByTwoId(long ticketFamilyId, long ruleId){
        Query query = entityManager.createQuery("SELECT u FROM TicketFamilyRule u WHERE u.ticketFamilyId = :inticketFamilyId AND u.ruleId = :inruleId AND u.rule.deleted = FALSE AND u.ticketFamily.deleted = FALSE");
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
        Query query = entityManager.createQuery("SELECT u FROM TicketFamilyRule u WHERE u.ticketFamilyId = :inticketFamilyId AND u.ruleId = :inruleId AND u.rule.deleted = FALSE AND u.ticketFamily.deleted = FALSE");
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
    public List<Rule> getRulesByTicketFmailyId(Long ticketFamilyId) throws NoSuchRuleException {
        Query query = entityManager.createQuery("SELECT r FROM TicketFamilyRule tr, Rule r WHERE tr.ticketFamilyId = :inticketFamilyId AND tr.ruleId = r.ruleId AND tr.rule.deleted = FALSE AND tr.ticketFamily.deleted = FALSE");
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

        entityManager.merge(c);
        entityManager.flush();
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
            cabinClassTicketFamilys = cabinClassSession.getCabinClassTicketFamilyJoinTables(aircraftId, cabinClassId);
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
            helper.setBookingClassHelpers(bookingClassSession.getBookingClassHelpers(flightScheduleId, ticketFamilyId));
            try {
                helper.setBookingClasses(flightScheduleSession.getFlightScheduleBookingClassesOfTicketFamily(flightScheduleId, ticketFamilyId));
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
            CabinClass cabinClass = entityManager.find(CabinClass.class, helper.getCabinClass().getCabinClassId());

            /**
             * Get original CabinClassTicketFamily DisLink all
             * CabinClassTicketFamily (set their deleted to true.), later if
             * cabinClass exist, just set deleted to false
             */
            List<CabinClassTicketFamily> originCabinClassTicketFamilys
                    = cabinClassSession.getCabinClassTicketFamilyJoinTables(aircraft.getAircraftId(), cabinClass.getCabinClassId());
            disLinkCabinClassTicketFamily(originCabinClassTicketFamilys);

            //Get aircraftCabinClass
            AircraftCabinClass aircraftCabinClass = aircraftSession.getAircraftCabinClassById(aircraft.getAircraftId(), cabinClass.getCabinClassId());
            //Create aircraftCabinClassId
            AircraftCabinClassId aircraftCabinClassId = new AircraftCabinClassId(aircraft.getAircraftId(), cabinClass.getCabinClassId());
            List<TicketFamily> ticketFamilys = helper.getTicketFamilys();
            if (ticketFamilys.isEmpty()) {
                throw new NeedTicketFamilyException(AisMsg.NEED_TICKET_FAMILY_ERROR);
            }
            for (TicketFamily ticketFamily : ticketFamilys) {
                TicketFamily originTicketFamily = entityManager.find(TicketFamily.class, ticketFamily.getTicketFamilyId());
                //Create CabinClassTicketFamilyId
                CabinClassTicketFamilyId cabinClassTicketFamilyId = new CabinClassTicketFamilyId(aircraftCabinClassId, originTicketFamily.getTicketFamilyId());
                CabinClassTicketFamily cabinClassTicketFamily;
                cabinClassTicketFamily = entityManager.find(CabinClassTicketFamily.class, cabinClassTicketFamilyId);
                if (cabinClassTicketFamily != null) {
                    cabinClassTicketFamily.setDeleted(false);
                    entityManager.merge(cabinClassTicketFamily);
                } else {
                    //Create CabinClassTicketFamily
                    cabinClassTicketFamily = new CabinClassTicketFamily();
                    cabinClassTicketFamily.setAircraftCabinClass(aircraftCabinClass);
                    cabinClassTicketFamily.setCabinClassTicketFamilyId(cabinClassTicketFamilyId);
                    cabinClassTicketFamily.setTicketFamily(originTicketFamily);
                    cabinClassTicketFamily.setSeatQty(0);
                    cabinClassTicketFamily.setPrice((float) 0);
                    cabinClassTicketFamily.setDeleted(false);
                    entityManager.persist(cabinClassTicketFamily);
                    entityManager.flush();
                }
                /**
                 ** DisLink booking class relationship with flight schedule,
                 * whose ticket family has been deleted.
                 *
                 */
                for (CabinClassTicketFamily cctf : originCabinClassTicketFamilys) {
                    if (cctf.getDeleted()) {
                        flightScheduleSession.dislinkFlightScheduleBookingClass(cctf);
                    }
                }
                //Add cabinClassFamily List
                cabinClassTicketFamilys.add(cabinClassTicketFamily);
            }

            //Set CabinClassTicketFamily to AircraftCabinClass
            aircraftCabinClass.setCabinClassTicketFamilys(cabinClassTicketFamilys);
            entityManager.merge(aircraftCabinClass);
        }
    }

    @Override
    public void disLinkCabinClassTicketFamily(List<CabinClassTicketFamily> cabinClassTicketFamilys) {
        for (CabinClassTicketFamily cctf : cabinClassTicketFamilys) {
            CabinClassTicketFamily cabinClassTicketFamily = entityManager.find(CabinClassTicketFamily.class, cctf.getCabinClassTicketFamilyId());
            cabinClassTicketFamily.setDeleted(true);
        }
    }

}
