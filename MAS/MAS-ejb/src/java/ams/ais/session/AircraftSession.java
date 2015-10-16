/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.CabinClass;
import ams.ais.entity.CabinClassTicketFamily;
import ams.ais.entity.TicketFamily;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchCabinClassTicketFamilyException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.ais.util.helper.AisMsg;
import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftCabinClass;
import ams.aps.entity.AircraftType;
import ams.aps.entity.FlightSchedule;
import ams.aps.util.exception.NoSuchAircraftCabinClassException;
import ams.aps.util.exception.NoSuchAircraftException;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import ams.aps.util.helper.AircraftStatus;
import ams.aps.util.helper.ApsMessage;
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
 * @author winga_000
 */
@Stateless
public class AircraftSession implements AircraftSessionLocal {

    @PersistenceContext
    private EntityManager entityManager;

    @EJB
    private CabinClassSessionLocal cabinClassSession;
    @EJB
    private FlightScheduleSessionLocal flightScheduleSession;

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
        Query query = entityManager.createQuery("SELECT c FROM Aircraft a, AircraftCabinClass ac, CabinClass c "
                + "WHERE ac.aircraftId = :inId "
                + "AND ac.cabinClassId = c.cabinClassId AND c.deleted = FALSE "
                + "AND ac.aircraftId = a.aircraftId AND a.status <> :inRetired AND a.status <> :inCrashed");
        query.setParameter("inId", aircraftId);
        query.setParameter("inRetired", AircraftStatus.RETIRED);
        query.setParameter("inCrashed", AircraftStatus.CRASHED);
        List<CabinClass> cabinClasses = new ArrayList<>();
        try {
            cabinClasses = (List<CabinClass>) query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchCabinClassException(AisMsg.NO_SUCH_CABIN_CLASS_ERROR);
        }
        return cabinClasses;
    }

    @Override
    public List<TicketFamily> getAircraftTicketFamilys(Long aircraftId) throws NoSuchTicketFamilyException {
        List<TicketFamily> ticketFamilys = new ArrayList<>();
        try {
            for (CabinClass cabinClass : getAircraftCabinClasses(aircraftId)) {
                if (cabinClass != null) {
                    for (TicketFamily ticketFamily : cabinClassSession.getCabinClassTicketFamilys(cabinClass.getType())) {
                        ticketFamilys.add(ticketFamily);
                    }
                }
            }
        } catch (NoSuchCabinClassException e) {
        }
        return ticketFamilys;
    }

    //Aircraft purchase/rental cost per round trip
    @Override
    public float calcAircraftCostPerRoundTrip(Long flightScheduleId) {
        try {
            FlightSchedule flgihtSchedule = flightScheduleSession.getFlightScheduleById(flightScheduleId);
            float weeklyFreq = flgihtSchedule.getFlight().getWeeklyFrequency();
            Aircraft aircraft = flightScheduleSession.getFlightScheduleAircraft(flightScheduleId);
            float cost = aircraft.getCost();
            float lifetime = aircraft.getLifetime();
            if (cost != 0 && lifetime != 0 && weeklyFreq != 0) {
                return cost / ((lifetime * (365 / 7)) * weeklyFreq);
            }
        } catch (NoSuchFlightSchedulException | NoSuchAircraftException e) {
        }
        return 0;
    }

    //Aircraft fuel cost per km
    @Override
    public float calcAircraftFuelCostPerKm(Long flightScheduleId) {
        try {
            Aircraft aircraft = flightScheduleSession.getFlightScheduleAircraft(flightScheduleId);
            AircraftType aircraftType = aircraft.getAircraftType();
            return aircraft.getAvgUnitOilUsage() * aircraftType.getFuelCostPerKm();
        } catch (NoSuchAircraftException e) {
        }
        return 0;
    }

    @Override
    public List<CabinClassTicketFamily> getAircraftCabinClassTicketFamilys(Long aircraftId) throws NoSuchCabinClassTicketFamilyException {
        Query query = entityManager.createQuery("SELECT ct FROM CabinClassTicketFamily ct "
                + "WHERE ct.aircraftCabinClass.aircraftId = :inAircraftId "
                + "AND ct.aircraftCabinClass.cabinClass.deleted = FALSE "
                + "AND ct.ticketFamily.deleted = FALSE "
                + "AND ct.deleted = FALSE");
        query.setParameter("inAircraftId", aircraftId);
        List<CabinClassTicketFamily> cabinClassTicketFamilys = null;
        try {
            cabinClassTicketFamilys = (List<CabinClassTicketFamily>) query.getResultList();
        } catch (NoResultException e) {
            throw new NoSuchCabinClassTicketFamilyException(AisMsg.NO_SUCH_CABINCLASS_TICKETFAMILY_ERROR);
        }
        return cabinClassTicketFamilys;
    }

    @Override
    public void verifyTicketFamilyExistence(Long aircraftId) throws NoSuchCabinClassTicketFamilyException {
        if (getAircraftCabinClassTicketFamilys(aircraftId).isEmpty()) {
            throw new NoSuchCabinClassTicketFamilyException(AisMsg.NO_SUCH_CABINCLASS_TICKETFAMILY_ERROR);
        }
    }
}
