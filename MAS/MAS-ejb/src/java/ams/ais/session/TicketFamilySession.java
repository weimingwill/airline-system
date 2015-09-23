/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.TicketFamily;
import ams.ais.util.exception.ExistSuchTicketFamilyNameException;
import ams.ais.util.exception.ExistSuchTicketFamilyTypeException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.ais.util.helper.AisMsg;
import java.util.List;
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
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public TicketFamily getTicketFamilyByName(String ticketFamilyName) {
        Query query = entityManager.createQuery("SELECT t FROM TicketFamily t WHERE t.name = :inticketFamilyName");
        query.setParameter("inticketFamilyName", ticketFamilyName);
        TicketFamily ticketFamily = null;
        try {
            ticketFamily = (TicketFamily) query.getSingleResult();
        } 
        catch (NoResultException ex) {
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
    public void createTicketFamily(String type, String name) throws ExistSuchTicketFamilyNameException, ExistSuchTicketFamilyTypeException {
        verifyTicketFamilyExistence(type, name);
        TicketFamily ticketFamily = new TicketFamily();
        ticketFamily.create(type, name);
        entityManager.persist(ticketFamily);
    }

    @Override
    public void verifyTicketFamilyExistence(String type, String name) throws ExistSuchTicketFamilyNameException, ExistSuchTicketFamilyTypeException {
        List<TicketFamily> ticketFamilys = getAllTicketFamily();
        if (ticketFamilys != null) {
            for (TicketFamily tk : ticketFamilys) {
                if (type.equals(tk.getType())) {
                    throw new ExistSuchTicketFamilyTypeException(AisMsg.EXIST_SUCH_TICKET_FAMILY_TYPE_ERROR);
                }
                if (name.equals(tk.getName())) {
                    throw new ExistSuchTicketFamilyNameException(AisMsg.EXIST_SUCH_TICKET_FAMILY_NAME_ERROR);
                }
            }
        }
    }

    @Override
    public void deleteTicketFamily(String name) throws NoSuchTicketFamilyException {
         TicketFamily ticketfamily = getTicketFamilyByName(name);
        if (ticketfamily == null) {
            throw new NoSuchTicketFamilyException(AisMsg.NO_SUCH_TICKET_FAMILY_ERROR);
        } else {
            ticketfamily.setDeleted(true);
            entityManager.merge(ticketfamily);
        }
    }

    @Override
    public void updateTicketFamily(String oldname, String type, String name) throws NoSuchTicketFamilyException, ExistSuchTicketFamilyNameException, ExistSuchTicketFamilyTypeException {
        TicketFamily c = getTicketFamilyByName(oldname);
        if (c == null) {
            throw new NoSuchTicketFamilyException(AisMsg.NO_SUCH_TICKET_FAMILY_ERROR);
        } else {
            List<TicketFamily> ticketfamilys = getAllOtherTicketFamily(oldname);

            if (ticketfamilys != null) {

                for (TicketFamily tf : ticketfamilys) {
                    if (type.equals(tf.getType())) {
                        throw new ExistSuchTicketFamilyTypeException(AisMsg.EXIST_SUCH_TICKET_FAMILY_TYPE_ERROR);
                    }
                    if (name.equals(tf.getName())) {
                        throw new ExistSuchTicketFamilyNameException(AisMsg.EXIST_SUCH_TICKET_FAMILY_NAME_ERROR);
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
    public List<TicketFamily> getAllOtherTicketFamily(String name) {
        Query query = entityManager.createQuery("SELECT c FROM TicketFamily c where c.name <> :name");
        query.setParameter("name", name);
        return query.getResultList();
    }
}
