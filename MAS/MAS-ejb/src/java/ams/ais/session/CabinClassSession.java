/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.CabinClass;
import ams.ais.entity.CabinClassTicketFamily;
import ams.ais.entity.TicketFamily;
import ams.ais.util.exception.ExistSuchCabinClassNameException;
import ams.ais.util.exception.ExistSuchCabinClassTypeException;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchCabinClassTicketFamilyException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.ais.util.helper.AisMsg;
import ams.ais.util.helper.CabinClassTicketFamilyHelper;
import ams.aps.session.AircraftSessionLocal;
import ams.aps.util.helper.AircraftStatus;
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
 * @author Bowen
 */
@Stateless
public class CabinClassSession implements CabinClassSessionLocal {

    @EJB
    private AircraftSessionLocal aircraftSession;

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
    public List<CabinClass> getAllCabinClass() {
        Query query = entityManager.createQuery("SELECT c FROM CabinClass c WHERE c.deleted = false");
        return query.getResultList();
    }

    @Override
    public CabinClass getCabinClassByName(String name) throws NoSuchCabinClassException {
        Query query = entityManager.createQuery("SELECT c FROM CabinClass c WHERE c.name = :inCabinClassName and c.deleted = FALSE");
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
        Query query = entityManager.createQuery("SELECT c FROM CabinClass c where c.name <> :name AND c.deleted=FALSE");
        query.setParameter("name", name);
        return query.getResultList();

    }

    @Override
    public List<TicketFamily> getCabinClassTicketFamilys(String type) throws NoSuchTicketFamilyException {
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

    @Override
    public List<TicketFamily> getCabinClassTicketFamilysByName(String name) throws NoSuchTicketFamilyException {
        Query query = entityManager.createQuery("SELECT t FROM CabinClass c, TicketFamily t WHERE c.name = :inName and c.cabinClassId = t.cabinClass.cabinClassId and c.deleted = FALSE and t.deleted = FALSE");
        query.setParameter("inName", name);
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
        Query query = entityManager.createQuery("SELECT t FROM CabinClassTicketFamily ct, TicketFamily t "
                + "WHERE ct.aircraftCabinClass.aircraftId = :inAircraftId "
                + "AND ct.aircraftCabinClass.aircraft.status <> :inRetired AND ct.aircraftCabinClass.aircraft.status <> :inCrashed "
                + "AND ct.aircraftCabinClass.cabinClassId = :inCabinClassId AND ct.aircraftCabinClass.cabinClass.deleted = FALSE "
                + "AND ct.ticketFamily.ticketFamilyId = t.ticketFamilyId AND t.deleted = FALSE "
                + "AND ct.deleted = FALSE");
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
        Query query = entityManager.createQuery("SELECT ct FROM CabinClassTicketFamily ct "
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
        Query query = entityManager.createQuery("SELECT ct FROM CabinClassTicketFamily ct "
                + "WHERE ct.aircraftCabinClass.aircraftId = :inAircraftId "
                + "AND ct.aircraftCabinClass.cabinClassId = :inCabinClassId "
                + "AND ct.aircraftCabinClass.cabinClass.deleted = FALSE "
                + "AND ct.ticketFamily.deleted = FALSE "
                + "AND ct.deleted = FALSE");
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
        for (CabinClass cabinClass : aircraftSession.getAircraftCabinClasses(aircraftId)) {
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
}
