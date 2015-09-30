/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.BookingClass;
import ams.ais.entity.CabinClass;
import ams.ais.entity.CabinClassTicketFamily;
import ams.ais.entity.TicketFamily;
import ams.ais.util.exception.ExistSuchTicketFamilyException;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchCabinClassTicketFamilyException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.ais.util.helper.AisMsg;
import ams.ais.util.helper.TicketFamilyBookingClassHelper;
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
    public TicketFamily getTicketFamilyByName(String ticketFamilyName) throws NoSuchTicketFamilyException{
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
    public void createTicketFamily(String type, String name, String cabinClassName) throws ExistSuchTicketFamilyException, NoSuchCabinClassException {
        verifyTicketFamilyExistence(type, name, cabinClassName);
        TicketFamily ticketFamily = new TicketFamily();
        ticketFamily.create(type, name);
        ticketFamily.setCabinClass(cabinClassSession.getCabinClassByName(cabinClassName));
        entityManager.persist(ticketFamily);
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
    public List<BookingClass> getTicketFamilyBookingClasses(String cabinClassName, String ticketFamilyName) throws NoSuchBookingClassException {
        Query query = entityManager.createQuery("SELECT b FROM TicketFamily t, BookingClass b WHERE t.cabinClass.name = :inCabinClassName and t.name = :inTicketFamilyName and b.ticketFamily.ticketFamilyId = t.ticketFamilyId and b.deleted = FALSE and t.deleted = FALSE");
        query.setParameter("inCabinClassName", cabinClassName);
        query.setParameter("inTicketFamilyName", ticketFamilyName);
        List<BookingClass> bookingClasses = new ArrayList<>();
        try {
            bookingClasses = query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchBookingClassException(AisMsg.EXIST_SUCH_BOOKING_CLASS_ERROR);
        }
        return bookingClasses;
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
    public List<TicketFamilyBookingClassHelper> getTicketFamilyBookingClassHelpers(Long flightScheduleId, Long aircraftId, Long cabinClassId) {
        List<CabinClassTicketFamily> cabinClassTicketFamilys;
        try {
            cabinClassTicketFamilys = cabinClassSession.getCabinClassTicketFamilyJoinTables(aircraftId, cabinClassId);
        } catch (NoSuchCabinClassTicketFamilyException e) {
            cabinClassTicketFamilys = new ArrayList<>();
        }
        List<TicketFamilyBookingClassHelper> ticketFamilyBookingClassHelpers = new ArrayList<>();
        for (CabinClassTicketFamily cabinClassTicketFamily : cabinClassTicketFamilys) {
            TicketFamilyBookingClassHelper helper = 
                    new TicketFamilyBookingClassHelper(cabinClassTicketFamily.getTicketFamily(), cabinClassTicketFamily.getSeatQty(), 
                            bookingClassSession.getBookingClassHelpers(flightScheduleId, cabinClassTicketFamily.getTicketFamily().getTicketFamilyId()));
            ticketFamilyBookingClassHelpers.add(helper);
        }
        return ticketFamilyBookingClassHelpers;
    }
    
    

}
