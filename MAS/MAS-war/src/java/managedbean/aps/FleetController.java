/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import ams.ais.entity.CabinClass;
import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftCabinClass;
import ams.aps.entity.AircraftType;
import ams.aps.session.FleetPlanningSessionLocal;
import ams.aps.util.exception.EmptyTableException;
import ams.aps.util.helper.AircraftCabinClassHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.inject.Inject;
import managedbean.application.MsgController;

/**
 *
 * @author Lewis
 */
@Named(value = "fleetController")
@RequestScoped
public class FleetController implements Serializable {

    @Inject
    MsgController msgController;

    @EJB
    private FleetPlanningSessionLocal fleetPlanningSession;

    private List<CabinClass> cabinClasses;
    private List<AircraftType> aircraftModels;
    private List<AircraftType> filteredAircraftModels;
    private AircraftType aircraftModel;
    private String msg = "";
    private int[] seatQtyList = new int[10];
    private String seatConfigString = null;
    private List<AircraftCabinClassHelper> aircraftCabinClassHelpers = new ArrayList<>();
    private List<List<AircraftCabinClassHelper>> selectedModelCabinClassesSet;
    private List<AircraftCabinClassHelper> selectedModelCabinClassList;

    /**
     * Creates a new instance of FleetController
     */
    public FleetController() {
    }

    @PostConstruct
    public void init() {
        getAvailableAircraftModels();
        getAvailableCabinClasses();
    }

    public void getAvailableAircraftModels() {
        try {
            setAircraftModels(fleetPlanningSession.getAircraftModels());
        } catch (EmptyTableException ex) {
            setMsg(ex.getMessage());
        }
    }

    public void getAvailableCabinClasses() {
        try {
            setCabinClasses(fleetPlanningSession.getAllCabinClasses());
            for (CabinClass cabinClass : cabinClasses) {
                aircraftCabinClassHelpers.add(new AircraftCabinClassHelper(cabinClass.getCabinClassId(), cabinClass.getType(), cabinClass.getName(), 0));
            }
        } catch (EmptyTableException ex) {
            setMsg(ex.getMessage());
        }
    }

    private void getCabinClassByAircraftModel(String typeCode) {
        setSelectedModelCabinClassesSet(fleetPlanningSession.getCabinClassByAircraftType(typeCode));
    }

    public int calculateTotalSeat(Aircraft aircraft) {
        int totalSeat = 0;
        for (AircraftCabinClass cabinClass : aircraft.getAircraftCabinClasses()) {
            totalSeat += cabinClass.getSeatQty();
        }
        return totalSeat;
    }

    public void addNewAircraft(String tailNo, String lifespan, String source, String cost, String seatConfig) {
        System.out.println("FleetController: addNewAircraft():");
        Aircraft newAircraft = new Aircraft();
        newAircraft.setTailNo(tailNo.toUpperCase());
        newAircraft.setLifetime(Float.parseFloat(lifespan));
        newAircraft.setSource(source);
        newAircraft.setCost(Float.parseFloat(cost));
        newAircraft.setAircraftType(aircraftModel);
        newAircraft.setAircraftCabinClasses(null);
        List<AircraftCabinClassHelper> cabinClassHelpers = new ArrayList();
        for (AircraftCabinClassHelper thisHelper : aircraftCabinClassHelpers) {
            if (thisHelper.getSeatQty() != 0) {
                cabinClassHelpers.add(thisHelper);
            }
        }
        if (fleetPlanningSession.addNewAircraft(newAircraft, cabinClassHelpers)) {
            msgController.addMessage("Add New Aircraft");
        } else {
            msgController.addErrorMessage("Add New Aircraft");
        }
    }

    public void onModelSelectChange() {
        getCabinClassByAircraftModel(aircraftModel.getTypeCode());
    }

    public List<Aircraft> getFleet(String status) {
        return fleetPlanningSession.getFleet(status);
    }

    public AircraftType getAircraftModelById(Long id) {
        return fleetPlanningSession.getAircraftTypeById(id);
    }

    /**
     * @return the aircraftModels
     */
    public List<AircraftType> getAircraftModels() {
        return aircraftModels;
    }

    /**
     * @param aircraftModels the aircraftModels to set
     */
    public void setAircraftModels(List<AircraftType> aircraftModels) {
        this.aircraftModels = aircraftModels;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * @return the aircraftModel
     */
    public AircraftType getAircraftModel() {
        return aircraftModel;
    }

    /**
     * @param aircraftModel the aircraftModel to set
     */
    public void setAircraftModel(AircraftType aircraftModel) {
        this.aircraftModel = aircraftModel;
    }

    /**
     * @return the cabinClasses
     */
    public List<CabinClass> getCabinClasses() {
        return cabinClasses;
    }

    /**
     * @param cabinClasses the cabinClasses to set
     */
    public void setCabinClasses(List<CabinClass> cabinClasses) {
        this.cabinClasses = cabinClasses;
    }

    /**
     * @return the seatConfigString
     */
    public String getSeatConfigString() {
        return seatConfigString;
    }

    /**
     * @param seatConfigString the seatConfigString to set
     */
    public void setSeatConfigString(String seatConfigString) {
        this.seatConfigString = seatConfigString;
    }

    /**
     * @return the seatQtyList
     */
    public int[] getSeatQtyList() {
        return seatQtyList;
    }

    /**
     * @param seatQtyList the seatQtyList to set
     */
    public void setSeatQtyList(int[] seatQtyList) {
        this.seatQtyList = seatQtyList;
    }

    /**
     * @return the aircraftCabinClassHelpers
     */
    public List<AircraftCabinClassHelper> getAircraftCabinClassHelpers() {
        return aircraftCabinClassHelpers;
    }

    /**
     * @param aircraftCabinClassHelpers the aircraftCabinClassHelpers to set
     */
    public void setAircraftCabinClassHelpers(List<AircraftCabinClassHelper> aircraftCabinClassHelpers) {
        this.aircraftCabinClassHelpers = aircraftCabinClassHelpers;
    }

    /**
     * @return the filteredAircraftModels
     */
    public List<AircraftType> getFilteredAircraftModels() {
        return filteredAircraftModels;
    }

    /**
     * @param filteredAircraftModels the filteredAircraftModels to set
     */
    public void setFilteredAircraftModels(List<AircraftType> filteredAircraftModels) {
        this.filteredAircraftModels = filteredAircraftModels;
    }

    /**
     * @return the selectedModelCabinClassesSet
     */
    public List<List<AircraftCabinClassHelper>> getSelectedModelCabinClassesSet() {
        return selectedModelCabinClassesSet;
    }

    /**
     * @param selectedModelCabinClassesSet the selectedModelCabinClassesSet to
     * set
     */
    public void setSelectedModelCabinClassesSet(List<List<AircraftCabinClassHelper>> selectedModelCabinClassesSet) {
        this.selectedModelCabinClassesSet = selectedModelCabinClassesSet;
    }

    /**
     * @return the selectedModelCabinClassList
     */
    public List<AircraftCabinClassHelper> getSelectedModelCabinClassList() {
        return selectedModelCabinClassList;
    }

    /**
     * @param selectedModelCabinClassList the selectedModelCabinClassList to set
     */
    public void setSelectedModelCabinClassList(List<AircraftCabinClassHelper> selectedModelCabinClassList) {
        this.selectedModelCabinClassList = selectedModelCabinClassList;
    }

}
