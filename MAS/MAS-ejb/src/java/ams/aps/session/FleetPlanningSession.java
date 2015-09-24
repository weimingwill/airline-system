/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.session;

import ams.ais.entity.CabinClass;
import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftCabinClass;
import ams.aps.entity.AircraftType;
import ams.aps.util.exception.EmptyTableException;
import ams.aps.util.helper.AircraftCabinClassHelper;
import ams.aps.util.helper.AircraftStatus;
import ams.aps.util.helper.Message;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author ChuningLiu
 */
@Stateless
public class FleetPlanningSession implements FleetPlanningSessionLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<AircraftType> getAircraftModels() throws EmptyTableException {
        Query query = entityManager.createQuery("SELECT a FROM AircraftType a");
        List<AircraftType> aircraftModels = null;
        try {
            aircraftModels = (List<AircraftType>) query.getResultList();
        } catch (NoResultException ex) {
            throw new EmptyTableException(Message.EMPTY_TABLE);
        }
        return aircraftModels;
    }

    @Override
    public List<Aircraft> getFleet(String status) {
        Query query;
        if (status == null) {
            query = entityManager.createQuery("SELECT a FROM Aircraft a");
        } else {
            query = entityManager.createQuery("SELECT a FROM Aircraft a WHERE a.status = :inStatus");
            query.setParameter("inStatus", status);
        }
        List<Aircraft> fleet = new ArrayList<>();
        try {
            fleet = (List<Aircraft>) query.getResultList();
        } catch (NoResultException ex) {
        }
        return fleet;
    }

    @Override
    public void updateAircraftInfo(Aircraft updatedAircraft) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addNewAircraft(Aircraft newAircraft, List<AircraftCabinClassHelper> newAircraftCabinClassHelpers) {
        AircraftType model = newAircraft.getAircraftType();
        entityManager.merge(model);

        Calendar cal = Calendar.getInstance();
        Date addOnDate = new Date(cal.getTimeInMillis());
        Float avgUnitOilUsage = (model.getMaxFuelCapacity() / model.getRangeInKm() * 100) / model.getTypicalSeating();
        String status = AircraftStatus.IDLE;

        newAircraft.setAddOnDate(addOnDate);
        newAircraft.setAvgUnitOilUsage(avgUnitOilUsage);
        newAircraft.setStatus(status);
        newAircraft.setAircraftType(model);

        entityManager.persist(newAircraft);
        entityManager.flush();
        // Add cabin class configuration for aircraft
        return addAircraftCabinClass(newAircraft, newAircraftCabinClassHelpers);
    }

    @Override
    public void markAircraftAsRetired(String tailNo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AircraftType getAircraftTypeById(Long id) {
        return entityManager.find(AircraftType.class, id);
    }

    public AircraftType getAircraftTypeByCode(String typeCode) {
        Query query = entityManager.createQuery("SELECT t FROM AircraftType t WHERE t.typeCode = :inTypeCode");
        query.setParameter("inTypeCode", typeCode);
        AircraftType model = new AircraftType();
        try {
            model = (AircraftType) query.getSingleResult();
        } catch (NoResultException ex) {
        }
        return model;
    }

    @Override
    public List<CabinClass> getAllCabinClasses() throws EmptyTableException {
        Query query = entityManager.createQuery("SELECT c FROM CabinClass c WHERE c.deleted=FALSE");
        List<CabinClass> cabinClasses = null;
        try {
            cabinClasses = (List<CabinClass>) query.getResultList();
        } catch (NoResultException ex) {
            throw new EmptyTableException(Message.EMPTY_TABLE);
        }
        return cabinClasses;
    }

    @Override
    public CabinClass getCabinClassById(Long id) {
        return entityManager.find(CabinClass.class, id);
    }

    private boolean addAircraftCabinClass(Aircraft newAircraft, List<AircraftCabinClassHelper> newAircraftCabinClassHelpers) {
        CabinClass thisCabinClass;
        List<AircraftCabinClass> aircraftCabinClassList = new ArrayList(); //too be added at aircraft side

        for (AircraftCabinClassHelper aircraftCabinClassHelper : newAircraftCabinClassHelpers) {
            AircraftCabinClass thisAircraftCabinClass = new AircraftCabinClass();
            thisCabinClass = entityManager.find(CabinClass.class, aircraftCabinClassHelper.getId());
            // add necessary attributes to aircraftCabinClass object
            thisAircraftCabinClass.setAircraft(newAircraft);
            thisAircraftCabinClass.setAircraftId(newAircraft.getAircraftId());
            thisAircraftCabinClass.setCabinClass(thisCabinClass);
            thisAircraftCabinClass.setCabinClassId(thisCabinClass.getCabinClassId());
            thisAircraftCabinClass.setSeatQty(aircraftCabinClassHelper.getSeatQty());
            entityManager.persist(thisAircraftCabinClass);
            aircraftCabinClassList.add(thisAircraftCabinClass);
        }
        newAircraft.setAircraftCabinClasses(aircraftCabinClassList);
        entityManager.merge(newAircraft);
        entityManager.flush();
        return true;
    }
}
