/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.session;

import ams.ais.entity.CabinClass;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.helper.AisMsg;
import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftCabinClass;
import ams.aps.util.exception.NoSuchAircraftCabinClassException;
import ams.aps.util.exception.NoSuchAircraftException;
import ams.aps.util.helper.AircraftStatus;
import ams.aps.util.helper.ApsMessage;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mas.util.helper.SafeHelper;

/**
 *
 * @author winga_000
 */
@Stateless
public class AircraftSession implements AircraftSessionLocal {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public AircraftCabinClass getAircraftCabinClassById(Long aircraftId, Long cabinCalssId) throws NoSuchAircraftCabinClassException {
        Query query = entityManager.createQuery("SELECT ac FROM AircraftCabinClass ac "
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
        Query query = entityManager.createQuery("SELECT a FROM Aircraft a WHERE a.aircraftId = :inId AND a.status NOT LIKE :inRetired AND a.status NOT LIKE :inCrashed");
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
        Query query = entityManager.createQuery("SELECT a FROM Aircraft a WHERE a.status <> :inRetired AND a.status <> :inCrashed");
//        Query query = entityManager.createQuery("SELECT a FROM Aircraft a WHERE a.isScheduled = TRUE AND a.status NOT LIKE :inRetired AND a.status NOT LIKE :inCrashed");
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
        try {
            List<CabinClass> cabinClasses = new ArrayList<>();
            for (AircraftCabinClass aircraftCabinClass : SafeHelper.emptyIfNull(getAircraftById(aircraftId).getAircraftCabinClasses())) {
                CabinClass cabinClass = aircraftCabinClass.getCabinClass();
                if (!cabinClass.isDeleted()) {
                    cabinClasses.add(cabinClass);
                }
            }
            return cabinClasses;
        } catch (NoSuchAircraftException ex) {
            throw new NoSuchCabinClassException(AisMsg.NO_SUCH_CABIN_CLASS_ERROR);
        }
    }
}
