/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.CabinClass;
import ams.ais.entity.TicketFamily;
import ams.ais.util.exception.ExistSuchTicketFamilyException;
import ams.ais.util.exception.ExistSuchTicketFamilyNameException;
import ams.ais.util.exception.ExistSuchTicketFamilyTypeException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.ais.util.helper.AisMsg;
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
        Query query = entityManager.createQuery("SELECT t FROM TicketFamily t WHERE t.name = :inticketFamilyName");
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
    public void createTicketFamily(String type, String name, String cabinclassname) throws ExistSuchTicketFamilyException {
        verifyTicketFamilyExistence(type, name, cabinclassname);
        TicketFamily ticketFamily = new TicketFamily();
        ticketFamily.create(type, name);
        ticketFamily.setCabinClass(cabinClassSession.getCabinClassByName(cabinclassname));
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
    public void deleteTicketFamily(String type,String cabinClassName) throws NoSuchTicketFamilyException {
        TicketFamily ticketfamily = getTicketFamilyByTypeAndCabinClass(type,cabinClassName);
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
                System.out.println("tk = "+ tk);
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

}
