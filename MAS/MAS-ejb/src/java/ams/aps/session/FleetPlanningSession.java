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
import ams.aps.util.helper.AircraftModelFilterHelper;
import ams.aps.util.helper.AircraftStatus;
import ams.aps.util.helper.Message;
import ams.aps.util.helper.RetireAircraftFilterHelper;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mas.common.util.helper.MySQLConnection;

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
            query = entityManager.createQuery("SELECT a FROM Aircraft a WHERE a.status <> :inRetiredStatus AND a.status <> :inCrashedStatus");
            query.setParameter("inRetiredStatus", AircraftStatus.RETIRED);
            query.setParameter("inCrashedStatus", AircraftStatus.CRASHED);
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
    public boolean updateAircraftInfo(Aircraft updatedAircraft) {
        try {
            Aircraft thisAircraft = entityManager.find(Aircraft.class, updatedAircraft.getAircraftId());
            thisAircraft.setLifetime(updatedAircraft.getLifetime());
            thisAircraft.setCost(updatedAircraft.getCost());
            thisAircraft.setStatus(updatedAircraft.getStatus());
            thisAircraft.setAvgUnitOilUsage(updatedAircraft.getAvgUnitOilUsage());
            entityManager.merge(thisAircraft);
        } catch (IllegalArgumentException ex) {
            return false;
        }
        return true;
    }

    @Override
    public boolean addNewAircraft(Aircraft newAircraft, List<AircraftCabinClassHelper> newAircraftCabinClassHelpers) {
        AircraftType model = entityManager.find(AircraftType.class, newAircraft.getAircraftType().getId());

        Calendar cal = Calendar.getInstance();
        Date addOnDate = new Date(cal.getTimeInMillis());
        Float avgUnitOilUsage = (model.getMaxFuelCapacity() / model.getRangeInKm() * 100) / model.getTypicalSeating();
        String status = AircraftStatus.IDLE;

        newAircraft.setAddOnDate(addOnDate);
        newAircraft.setAvgUnitOilUsage(avgUnitOilUsage);
        newAircraft.setStatus(status);
        newAircraft.setAircraftType(model);
        newAircraft.setScheduled(Boolean.FALSE);

        entityManager.persist(newAircraft);
        entityManager.flush();
        // Add cabin class configuration for aircraft
        return addAircraftCabinClass(newAircraft, newAircraftCabinClassHelpers);
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

    @Override
    public boolean retireSelectedAircrafts(List<Aircraft> aircrafts) {
        try {
            for (Aircraft thisAircraft : aircrafts) {
                thisAircraft.setStatus(AircraftStatus.RETIRED);
                entityManager.merge(thisAircraft);
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    @Override
    public List<List<AircraftCabinClassHelper>> getCabinClassByAircraftType(String typeCode) {
        List<List<AircraftCabinClassHelper>> outputHelperList = new ArrayList();
        for (Long aircraftId : getNumberOfConfigurations(typeCode)) {
            Query query = entityManager.createQuery("SELECT a FROM AircraftCabinClass a WHERE a.aircraft.aircraftType.typeCode = :inTypeCode AND a.aircraftId=:inId");
            query.setParameter("inId", aircraftId);
            query.setParameter("inTypeCode", typeCode);
            List<AircraftCabinClass> thisAircraftCabinClasses;
            List<AircraftCabinClassHelper> outputHelpers = new ArrayList();
            try {
                thisAircraftCabinClasses = (List<AircraftCabinClass>) query.getResultList();
                for (AircraftCabinClass thisAircraftCabinClass : thisAircraftCabinClasses) {
                    outputHelpers.add(new AircraftCabinClassHelper(thisAircraftCabinClass.getCabinClassId(), thisAircraftCabinClass.getCabinClass().getType(), thisAircraftCabinClass.getCabinClass().getName(), thisAircraftCabinClass.getSeatQty()));
                }

            } catch (Exception e) {

            }
            outputHelperList.add(outputHelpers);
        }
        return outputHelperList;
    }

    private List<Long> getNumberOfConfigurations(String typeCode) {
        Query query = entityManager.createQuery("SELECT a.aircraftId FROM AircraftCabinClass a WHERE a.aircraft.aircraftType.typeCode = :inTypeCode GROUP BY a.aircraftId");
        query.setParameter("inTypeCode", typeCode);
        try {
            return (List<Long>) query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Aircraft> filterAircraftsForRetire(RetireAircraftFilterHelper filters) {
        // Maintenance and flight cycle filters will be implemented once Flight Scheduling and Maintenance Scheduling modules are developed
        Query query = entityManager.createQuery("SELECT a FROM Aircraft a "
                + "WHERE a.addOnDate >= :inAddOnDate "
                + "AND a.avgUnitOilUsage >= :inMinAvgUnitOilUsage "
                + "AND (a.lifetime BETWEEN :inMinLifetime AND :inMaxLifetime) "
                + "AND a.aircraftType.maxFuelCapacity >= :inMinFuelCapacity "
                + "AND a.status <> :inRetired AND a.status <> :inCrashed");
        query.setParameter("inAddOnDate", filters.getFromAddOnDate());
        query.setParameter("inMinAvgUnitOilUsage", filters.getMinAvgFuelUsage());
        query.setParameter("inMinLifetime", filters.getMinLifespan());
        query.setParameter("inMaxLifetime", filters.getMaxLifespan());
        query.setParameter("inMinFuelCapacity", filters.getMinFuelCapacity());
        query.setParameter("inRetired", AircraftStatus.RETIRED);
        query.setParameter("inCrashed", AircraftStatus.CRASHED);

        List<Aircraft> filteredAircrafts = new ArrayList();
        try {
            filteredAircrafts = (List<Aircraft>) query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("FleetPlanningSession: filterAircraftsForRetire()" + filteredAircrafts);
        return filteredAircrafts;
    }

    @Override
    public void initRetireAicraftFilter(RetireAircraftFilterHelper retireAircraftFilterHelper) {
        // TODO: Maintenance and Flight Cycle will be implemented
        Connection conn = MySQLConnection.establishConnection();
        String query = "SELECT MIN(A.ADDONDATE), MIN(A.AVGUNITOILUSAGE), MIN(T.MAXFUELCAPACITY), MIN(A.LIFETIME), MAX(A.LIFETIME) FROM `mas`.`AIRCRAFT` A, `mas`.`AIRCRAFTTYPE` T WHERE A.AIRCRAFTTYPE_ID = T.ID";
        Statement stmt = null;

        try {
            stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            while (resultSet.next()) {
                Date minAddOnDate = resultSet.getDate(1);
                double minAvgFuelUsage = Math.floor(resultSet.getFloat(2));
                double minFuelCapacity = Math.floor(resultSet.getFloat(3));
                double minLifespan = Math.floor(resultSet.getFloat(4));
                double maxLifespan = Math.floor(resultSet.getFloat(5));
                System.out.println(minAddOnDate + ", " + minAvgFuelUsage
                        + ", " + minFuelCapacity + ", " + minLifespan
                        + ", " + maxLifespan);

                retireAircraftFilterHelper.setFromAddOnDate(minAddOnDate);
                retireAircraftFilterHelper.setMinAvgFuelUsage((float) minAvgFuelUsage);
                retireAircraftFilterHelper.setMinFuelCapacity((float) minFuelCapacity);
                retireAircraftFilterHelper.setMinLifespan((float) minLifespan);
                retireAircraftFilterHelper.setMaxLifespan((float) maxLifespan);
            }

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(FleetPlanningSession.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean addNewAircraftModel(AircraftType newAircraftModel) {
        try {
            entityManager.persist(newAircraftModel);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean updateAircraftModel(AircraftType updatedAircraftModel) {
        try {
            entityManager.find(AircraftType.class, updatedAircraftModel.getId());
            entityManager.merge(updatedAircraftModel);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public List<AircraftType> filterAircraftModels(AircraftModelFilterHelper filters) {

        Query query = entityManager.createQuery("SELECT t FROM AircraftType t WHERE t.mfdBy IN :inManufacturer AND t.typeFamily IN :inTypeFamily "
                + "AND (t.maxSeating IS NULL OR t.maxSeating BETWEEN :inMinMaxSeating AND :inMaxMaxSeating) "
                + "AND (t.approxCost IS NULL OR t.approxCost BETWEEN :inMinApproxPrice AND :inMaxApproxPrice) "
                + "AND (t.fuelCostPerKm IS NULL OR t.fuelCostPerKm BETWEEN :inMinFuelCostPerKm AND :inMaxFuelCostPerKm) "
                + "AND (t.rangeInKm IS NULL OR t.rangeInKm BETWEEN :inMinRange AND :inMaxRange) "
                + "AND (t.maxPayload IS NULL OR t.maxPayload BETWEEN :inMinPayload AND :inMaxPayload) "
                + "AND (t.maxMachNo IS NULL OR t.maxMachNo BETWEEN :inMinMaxMachNum AND :inMaxMaxMachNum) "
                + "AND (t.maxFuelCapacity IS NULL OR t.maxFuelCapacity BETWEEN :inMinFuelCapacity AND :inMaxFuelCapacity)");
        
        query.setParameter("inManufacturer", filters.getManufacturers());
        query.setParameter("inTypeFamily", filters.getTypeFamilies());
        query.setParameter("inMinMaxSeating", filters.getMinMaxSeating());
        query.setParameter("inMaxMaxSeating", filters.getMaxMaxSeating() == 0 ? Math.pow(10, 10) : filters.getMaxMaxSeating());
        query.setParameter("inMinApproxPrice", filters.getMinApproxPrice());
        query.setParameter("inMaxApproxPrice", filters.getMaxApproxPrice() == 0 ? Math.pow(10, 10) : filters.getMaxApproxPrice());
        query.setParameter("inMinFuelCostPerKm", filters.getMinFuelCostPerKm());
        query.setParameter("inMaxFuelCostPerKm", filters.getMaxFuelCostPerKm() == 0 ? Math.pow(10, 10) : filters.getMaxFuelCostPerKm());
        query.setParameter("inMinRange", filters.getMinRange());
        query.setParameter("inMaxRange", filters.getMaxRange() == 0 ? Math.pow(10, 10) : filters.getMaxRange());
        query.setParameter("inMinPayload", filters.getMinPayload());
        query.setParameter("inMaxPayload", filters.getMaxPayload() == 0 ? Math.pow(10, 10) : filters.getMaxPayload());
        query.setParameter("inMinMaxMachNum", filters.getMinMaxMachNum()-0.001);
        query.setParameter("inMaxMaxMachNum", filters.getMaxMaxMachNum() == 0 ? 1 : filters.getMaxMaxMachNum());
        query.setParameter("inMinFuelCapacity", filters.getMinFuelCapacity());
        query.setParameter("inMaxFuelCapacity", filters.getMaxFuelCapacity() == 0 ? Math.pow(10, 10) : filters.getMaxFuelCapacity());

        List<AircraftType> filteredAircraftModels = new ArrayList();
        try {
            filteredAircraftModels = (List<AircraftType>) query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("FleetPlanningSession: filterAircraftModels()" + filteredAircraftModels);
        return filteredAircraftModels;
    }

    @Override
    public void initAircraftModelFilter(AircraftModelFilterHelper aircraftModelFilterHelper) {
        List<String> manufacturers = getAllManufacturer();
        List<String> typeFamilyList = getAllTypeFamily();
        aircraftModelFilterHelper.setManufacturers(manufacturers);
        aircraftModelFilterHelper.setTypeFamilies(typeFamilyList);
        System.out.println(manufacturers);
        System.out.println(typeFamilyList);
        Connection conn = MySQLConnection.establishConnection();
        String query = "SELECT FLOOR(MIN(T.RANGEINKM)), CEIL(MAX(T.RANGEINKM)), "
                + "FLOOR(MIN(T.MAXPAYLOAD)), CEIL(MAX(T.MAXPAYLOAD)), "
                + "ROUND(MIN(T.MAXMACHNO),2), ROUND(MAX(T.MAXMACHNO),2), "
                + "FLOOR(MIN(T.MAXFUELCAPACITY)), CEIL(MAX(T.MAXFUELCAPACITY)), "
                + "MIN(T.MAXSEATING), MAX(T.MAXSEATING), "
                + "FLOOR(MIN(T.APPROXCOST)), CEIL(MAX(T.APPROXCOST)), "
                + "FLOOR(MIN(T.FUELCOSTPERKM)), CEIL(MAX(T.FUELCOSTPERKM)) FROM AIRCRAFTTYPE AS T";
        Statement stmt = null;

        try {
            stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            while (resultSet.next()) {
                aircraftModelFilterHelper.setMinRange(resultSet.getFloat(1));
                aircraftModelFilterHelper.setMaxRange(resultSet.getFloat(2));
                aircraftModelFilterHelper.setMinPayload(resultSet.getFloat(3));
                aircraftModelFilterHelper.setMaxPayload(resultSet.getFloat(4));
                aircraftModelFilterHelper.setMinMaxMachNum(resultSet.getFloat(5));
                aircraftModelFilterHelper.setMaxMaxMachNum(resultSet.getFloat(6));
                aircraftModelFilterHelper.setMinFuelCapacity(resultSet.getFloat(7));
                aircraftModelFilterHelper.setMaxFuelCapacity(resultSet.getFloat(8));
                aircraftModelFilterHelper.setMinMaxSeating(resultSet.getInt(9));
                aircraftModelFilterHelper.setMaxMaxSeating(resultSet.getInt(10));
                aircraftModelFilterHelper.setMinApproxPrice(resultSet.getFloat(11));
                aircraftModelFilterHelper.setMaxApproxPrice(resultSet.getFloat(12));
                aircraftModelFilterHelper.setMinFuelCostPerKm(resultSet.getFloat(13));
                aircraftModelFilterHelper.setMaxFuelCostPerKm(resultSet.getFloat(14));
            }

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(FleetPlanningSession.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<String> getAllManufacturer() {
        return (List<String>) entityManager.createQuery("SELECT DISTINCT t.mfdBy FROM AircraftType t").getResultList();
    }

    @Override
    public List<String> getAllTypeFamily() {
        return (List<String>) entityManager.createQuery("SELECT DISTINCT t.typeFamily FROM AircraftType t").getResultList();
    }

}