/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.CabinClass;
import ams.ais.entity.TicketFamily;
import ams.ais.util.exception.ExistSuchCabinClassNameException;
import ams.ais.util.exception.ExistSuchCabinClassTypeException;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.ais.util.helper.AisMsg;
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
public class CabinClassSession implements CabinClassSessionLocal {

    @EJB
    private TicketFamilySessionLocal ticketFamilySession;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void createCabinClass(String type, String name) throws ExistSuchCabinClassNameException, ExistSuchCabinClassTypeException {
        verifyCabinClassExistence(type, name);
        CabinClass cabinClass = new CabinClass();
        cabinClass.create(type, name);
        entityManager.persist(cabinClass);
    }

    @Override
    public List<CabinClass> getAllCabinClass() {
        Query query = entityManager.createQuery("SELECT c FROM CabinClass c WHERE c.deleted = false");

        return query.getResultList();
    }

    @Override
    public void verifyCabinClassExistence(String type, String name) throws ExistSuchCabinClassNameException, ExistSuchCabinClassTypeException {
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
            entityManager.merge(cabinclass);
        }
        //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CabinClass getCabinClassByName(String name) {
        Query query = entityManager.createQuery("SELECT c FROM CabinClass c WHERE c.name = :inCabinClassName AND c.deleted = FALSE");
        query.setParameter("inCabinClassName", name);
        CabinClass cabinclass = null;
        try {
            cabinclass = (CabinClass) query.getSingleResult();
        } catch (NoResultException ex) {
            cabinclass = null;
        }
        return cabinclass;
        // Add business logic below. (Right-click in editor and choose
        // "Insert Code > Add Business Method")
    }

    @Override
    public void updateCabinClass(String oldname, String type, String name) throws NoSuchCabinClassException, ExistSuchCabinClassNameException, ExistSuchCabinClassTypeException {

        CabinClass c = getCabinClassByName(oldname);
        if (c == null) {
            throw new NoSuchCabinClassException(AisMsg.NO_SUCH_CABIN_CLASS_ERROR);
        } else {
            List<CabinClass> cabinclasses = getAllOtherCabinClass(oldname);

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
        entityManager.merge(c);
        entityManager.flush();

    }

    @Override
    public List<String> getAllOtherCabinClassByName(String name) {
        Query query = entityManager.createQuery("SELECT c FROM CabinClass c where c.name <> :name");
        query.setParameter("name", name);
        List<CabinClass> cabinclass = query.getResultList();
        List<String> names = new ArrayList<>();
        if (cabinclass != null) {
            for (CabinClass cabin : cabinclass) {
                names.add(cabin.getName());
            }
        }
        return names;

    }

    @Override
    public List<String> getAllOtherCabinClassByType(String type) {
        Query query = entityManager.createQuery("SELECT c FROM CabinClass c where c.name <> :type");
        query.setParameter("type", type);
        List<CabinClass> cabinclass = query.getResultList();
        List<String> types = new ArrayList<>();
        if (cabinclass != null) {
            for (CabinClass cabin : cabinclass) {
                types.add(cabin.getType());
            }
        }
        return types;
    }

    @Override
    public List<CabinClass> getAllOtherCabinClass(String name) {
        Query query = entityManager.createQuery("SELECT c FROM CabinClass c where c.name <> :name");
        query.setParameter("name", name);
        return query.getResultList();

    }

    @Override
    public List<TicketFamily> getCabinClassTicketFamily(String type) throws NoSuchTicketFamilyException {
       Query query = entityManager.createQuery("SELECT t FROM CabinClass c, TicketFamily t WHERE c.type = :inType and c.cabinClassId = t.cabinClass.cabinClassId and c.deleted = FALSE and t.deleted = FALSE");
       query.setParameter("inType", type);
       List<TicketFamily> ticketFamilys = new ArrayList<>();
        try {
            ticketFamilys = query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchTicketFamilyException(AisMsg.NO_SUCH_TICKET_FAMILY_ERROR);
        }
        return ticketFamilys;
    }
}
