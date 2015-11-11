/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.session;

import ams.ais.entity.CabinClass;
import ams.ais.entity.CabinClassTicketFamily;
import ams.ais.entity.TicketFamily;
import ams.ais.entity.helper.CabinClassTicketFamilyId;
import ams.ais.session.ProductDesignSessionLocal;
import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftCabinClass;
import ams.aps.entity.AircraftType;
import ams.aps.entity.helper.AircraftCabinClassId;
import ams.aps.util.exception.EmptyTableException;
import ams.aps.util.exception.NoSuchAircraftCabinClassException;
import ams.aps.util.exception.ObjectExistsException;
import ams.aps.util.helper.AircraftCabinClassHelper;
import ams.aps.util.helper.AircraftModelFilterHelper;
import ams.aps.util.helper.AircraftStatus;
import ams.aps.util.helper.Message;
import ams.aps.util.helper.RetireAircraftFilterHelper;
import ams.ars.entity.Seat;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
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
    
    @PersistenceContext
    private EntityManager entityManager;

    @EJB
    private ProductDesignSessionLocal productDesignSession;

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

        // add aircrafts for model
        List<Aircraft> aircraftsForModel = (List<Aircraft>) model.getAircrafts();
        if (aircraftsForModel == null) {
            aircraftsForModel = new ArrayList();
        }
        aircraftsForModel.add(newAircraft);
        model.setAircrafts(aircraftsForModel);
        entityManager.merge(model);

        // Add cabin class configuration for aircraft
        return addAircraftCabinClass(newAircraft, newAircraftCabinClassHelpers);
    }

    @Override
    public AircraftType getAircraftTypeById(Long id) {
        return entityManager.find(AircraftType.class, id);
    }

    @Override
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
            thisAircraftCabinClass.setNumRows(aircraftCabinClassHelper.getNumOfRow());
            thisAircraftCabinClass.setNumCols(aircraftCabinClassHelper.getNumOfCol());
            entityManager.persist(thisAircraftCabinClass);
            entityManager.flush();
            addCabinClassTickeFamily(thisAircraftCabinClass);
            addAircraftCabinClassSeat(thisAircraftCabinClass);
            if (!aircraftCabinClassList.add(thisAircraftCabinClass)) {
                return false;
            }
        }
        newAircraft.setAircraftCabinClasses(aircraftCabinClassList);
        entityManager.merge(newAircraft);
        entityManager.flush();
        return true;
    }
    
    private void addAircraftCabinClassSeat(AircraftCabinClass aircraftCabinClass){
        List<String> columns = new ArrayList(Arrays.asList("A","B","C","D","E","F","G","H"));
        List<Seat> seats = new ArrayList();
        int row = aircraftCabinClass.getNumRows();
        int col = aircraftCabinClass.getNumCols();
        for(int i = 0; i < row; i ++){
            for(int j = 0; j < col; j++){
                Seat newSeat = new Seat();
                newSeat.setReserved(Boolean.FALSE);
                newSeat.setRowNo(i+1);
                newSeat.setColNo(columns.get(j));
                entityManager.persist(newSeat);
                entityManager.flush();
                seats.add(newSeat);
            }
        }
        aircraftCabinClass.setSeats(seats);
        entityManager.merge(aircraftCabinClass);
        entityManager.flush();
    }
    
    //Set all ticket familys under cabin class to aircraft as default.
    private boolean addCabinClassTickeFamily(AircraftCabinClass aircraftCabinClass) {
        List<CabinClassTicketFamily> cabinClassTixFams = new ArrayList<>();
        try { 
            AircraftCabinClass aircraftCabinCls = productDesignSession.getAircraftCabinClassById(aircraftCabinClass.getAircraftId(), aircraftCabinClass.getCabinClassId());
            AircraftCabinClassId aircraftCabinClassId = new AircraftCabinClassId(aircraftCabinCls.getAircraftId(), aircraftCabinCls.getCabinClassId());
            List<TicketFamily> ticketFamilys = getCabinClassTicketFamily(aircraftCabinCls.getCabinClassId());
            int i = 0;
            for (TicketFamily ticketFamily : ticketFamilys) {
                TicketFamily tixFam = entityManager.find(TicketFamily.class, ticketFamily.getTicketFamilyId());
                CabinClassTicketFamilyId cabinClassTicketFamilyId = new CabinClassTicketFamilyId(aircraftCabinClassId, tixFam.getTicketFamilyId());
                CabinClassTicketFamily cabinClassTicketFamily = new CabinClassTicketFamily();
                cabinClassTicketFamily.setAircraftCabinClass(aircraftCabinCls);
                cabinClassTicketFamily.setCabinClassTicketFamilyId(cabinClassTicketFamilyId);
                cabinClassTicketFamily.setTicketFamily(tixFam);
                cabinClassTicketFamily.setSeatQty(calcSeatQty(i, ticketFamilys.size(), aircraftCabinCls.getSeatQty()));
                cabinClassTicketFamily.setPrice((float) 0);
                cabinClassTicketFamily.setDeleted(false);
                entityManager.persist(cabinClassTicketFamily);
                entityManager.flush();
                cabinClassTixFams.add(cabinClassTicketFamily);
                i++;
            }
            aircraftCabinCls.setCabinClassTicketFamilys(cabinClassTixFams);
            entityManager.merge(aircraftCabinCls);
            entityManager.flush();
        } catch (NoSuchAircraftCabinClassException ex) {
            return false;
        }
        return true;
    }
    
    private int calcSeatQty(int index, int numOfTixFam, int totalSeatQty) {
        if (index == numOfTixFam - 1) {
            return totalSeatQty - totalSeatQty/numOfTixFam * (numOfTixFam - 1);
        } else {
            return totalSeatQty/numOfTixFam;
        }
        
//        System.out.println("index: " + index + " .numOfRemainedTixFam: " + numOfRemainedTixFam + " .totalSeatQty" + totalSeatQty);
//        if (index <= 0) {
//            return totalSeatQty/numOfRemainedTixFam;
//        } else {
//            int num = numOfRemainedTixFam;
//            return (totalSeatQty - calcSeatQty(--index, ++numOfRemainedTixFam, totalSeatQty))/num;
//        }
    }

    private List<TicketFamily> getCabinClassTicketFamily(Long cabinClassId) {
        Query query = entityManager.createQuery("SELECT t FROM TicketFamily t WHERE t.cabinClass.cabinClassId = :inId AND t.deleted = FALSE");
        query.setParameter("inId", cabinClassId);
        List<TicketFamily> ticketFamilys = new ArrayList<>();
        try {
            ticketFamilys = query.getResultList();
        } catch (NoResultException e) {
        }
        return ticketFamilys;
    }

    @Override
    public boolean retireSelectedAircrafts(List<Aircraft> aircrafts) {
        try {
            for (Aircraft thisAircraft : aircrafts) {
                thisAircraft.setStatus(AircraftStatus.RETIRED);
                entityManager.merge(thisAircraft);
                AircraftType model = entityManager.find(AircraftType.class, thisAircraft.getAircraftType().getId());
                List<Aircraft> aircraftsForModel = (List<Aircraft>) model.getAircrafts();
                if (aircraftsForModel != null) {
                    aircraftsForModel.remove(thisAircraft);
                }
                model.setAircrafts(aircraftsForModel);
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    @Override
    public List<AircraftCabinClassHelper> getCabinClassByAircaftType(String typeCode) {
        List<AircraftCabinClassHelper> outputHelperList = new ArrayList();
        AircraftCabinClassHelper thisHelper = new AircraftCabinClassHelper();

        Connection conn = MySQLConnection.establishConnection();
        String query = "SELECT C.CABINCLASSID, C.TYPE, C.NAME, CEIL(AVG(AC.SEATQTY)) FROM CABINCLASS C, AIRCRAFT_CABINCLASS AC, AIRCRAFTTYPE T, AIRCRAFT A WHERE AC.AIRCRAFTID = A.AIRCRAFTID AND A.AIRCRAFTTYPE_ID = T.ID AND T.TYPECODE=\'" + typeCode + "\' AND C.CABINCLASSID=AC.CABINCLASSID GROUP BY AC.CABINCLASSID";
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            while (resultSet.next()) {
                thisHelper = new AircraftCabinClassHelper();
                thisHelper.setId(resultSet.getLong(1));
                thisHelper.setType(resultSet.getString(2));
                thisHelper.setName(resultSet.getString(3));
                thisHelper.setSeatQty(resultSet.getInt(4));
                System.out.println(thisHelper.getId() + ", " + thisHelper.getType()
                        + ", " + thisHelper.getName() + ", " + thisHelper.getSeatQty());

                outputHelperList.add(thisHelper);
            }

            stmt.close();
            System.out.println("outputHelperList = " + outputHelperList);
        } catch (SQLException ex) {
            Logger.getLogger(FleetPlanningSession.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return outputHelperList;
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
        query.setParameter("inMinMaxMachNum", filters.getMinMaxMachNum() - 0.001);
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

    @Override
    public void checkTailNoExist(String tailNo) throws ObjectExistsException {
        Query query = entityManager.createQuery("SELECT a FROM Aircraft a WHERE a.tailNo = :inTailNo");
        query.setParameter("inTailNo", tailNo);

        try {
            query.getSingleResult();
            throw new ObjectExistsException("Aicraft with tail number " + tailNo + " already exists!");
        } catch (NoResultException ex) {

        }
    }

}
