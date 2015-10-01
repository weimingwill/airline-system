/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.session;

import ams.aps.entity.AircraftCabinClass;
import ams.aps.util.exception.NoSuchAircraftCabinClassException;
import ams.aps.util.helper.ApsMessage;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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
        Query query = entityManager.createQuery("SELECT ac FROM AircraftCabinClass ac WHERE ac.aircraftId = :inAircraftId and ac.cabinClassId = :inCabinClassId and ac.cabinClass.deleted = FALSE and ac.aircraft.status NOT LIKE 'Retired'");
        query.setParameter("inAircraftId", aircraftId);
        query.setParameter("inCabinClassId", cabinCalssId);
        AircraftCabinClass aircraftCabinClass = new AircraftCabinClass();
        try {
            aircraftCabinClass = (AircraftCabinClass)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoSuchAircraftCabinClassException(ApsMessage.No_SUCH_AIRCRAFT_CABINCLASS_ERROR);
        }
        return aircraftCabinClass;
    }

}
