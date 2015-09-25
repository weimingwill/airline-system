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
import ams.aps.entity.FlightSchedule;
import ams.aps.util.exception.NoSuchAircraftException;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import ams.aps.util.helper.ApsMessage;
import ams.aps.util.helper.Message;
import java.util.ArrayList;
import java.util.List;
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
public class FlightScheduleSession implements FlightScheduleSessionLocal {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<FlightSchedule> getAllFilghtSchedules() throws NoSuchFlightSchedulException {
        Query query = entityManager.createQuery("SELECT f FROM FlightSchedule f WHERE f.deleted = FALSE");
        List<FlightSchedule> flightSchedules = new ArrayList<>();
        try {
            flightSchedules = query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchFlightSchedulException(ApsMessage.NO_SUCH_FLIGHT_SCHEDULE_ERRRO);
        }
        return flightSchedules;
    }
    
    @Override
    public FlightSchedule getFlightScheduleById(Long id) throws NoSuchFlightSchedulException {
        Query query = entityManager.createQuery("SELECT f FROM FlightSchedule f WHERE f.flightScheduleId = :inId and f.deleted = FALSE");
        query.setParameter("inId", id);
        FlightSchedule flightSchedule = null;
        try {
            flightSchedule = (FlightSchedule) query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoSuchFlightSchedulException(ApsMessage.NO_SUCH_FLIGHT_SCHEDULE_ERRRO);
        }
        return flightSchedule;
    }

    @Override
    public Aircraft getFlightScheduleAircraft(Long id) throws NoSuchAircraftException {
        Query query = entityManager.createQuery("SELECT a FROM FlightSchedule f, Aircraft a WHERE f.flightScheduleId = :inId and f.aircraft.aircraftId = a.aircraftId and a.status <> 'Retired'");
        query.setParameter("inId", id);
        Aircraft aircraft = null;
        try {
            aircraft = (Aircraft) query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoSuchAircraftException(ApsMessage.NO_SUCH_AIRCRAFT_ERROR);
        }
        return aircraft;
    }   
    
    
    @Override
    public List<CabinClass> getFlightScheduleCabinCalsses(Long id) throws NoSuchCabinClassException {
        List<CabinClass> cabinClasses = new ArrayList<>();
        try {
            for (AircraftCabinClass aircraftCabinClass : SafeHelper.emptyIfNull(getFlightScheduleAircraft(id).getAircraftCabinClasses())) {
                cabinClasses.add(aircraftCabinClass.getCabinClass());
            }
        } catch (NoSuchAircraftException e ) {
            throw new NoSuchCabinClassException(AisMsg.NO_SUCH_CABIN_CLASS_ERROR);
        }
        return cabinClasses;
    }
    
    

}
