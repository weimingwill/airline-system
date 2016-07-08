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
import ams.aps.util.exception.ObjectExistsException;
import ams.aps.util.helper.AircraftCabinClassHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;

/**
 *
 * @author Lewis
 */
@Named(value = "fleetController")
@ViewScoped
public class FleetController implements Serializable {

    @Inject
    MsgController msgController;

    @Inject
    NavigationController navigationController;

    @EJB
    private FleetPlanningSessionLocal fleetPlanningSession;
    private List<CabinClass> cabinClasses;
    private List<AircraftType> aircraftModels;
    private AircraftType aircraftModel;
    private String msg = "";
    private int[] seatQtyList = new int[10];
    private String seatConfigString = null;
    private List<AircraftCabinClassHelper> aircraftCabinClassHelpers = new ArrayList<>();
    private List<AircraftCabinClassHelper> suggestedCabinClass;
    private List<List<AircraftCabinClassHelper>> selectedModelCabinClassesSet;
    private List<AircraftCabinClassHelper> selectedModelCabinClassList;
    private double approxCost;

    /**
     * Creates a new instance of FleetController
     */
    public FleetController() {
    }

    @PostConstruct
    public void init() {
        System.out.println("FleetController: init()");
        getAvailableAircraftModels();
        getAvailableCabinClasses();
    }

    public List<AircraftType> getAvailableAircraftModels() {
        System.out.println("FleetController: getAvailableAircraftModels()");
        try {
            setAircraftModels(fleetPlanningSession.getAircraftModels());
        } catch (EmptyTableException ex) {
            setMsg(ex.getMessage());
        }
        return aircraftModels;
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
//        setSelectedModelCabinClassesSet(fleetPlanningSession.getCabinClassByAircraftType(typeCode));
        setSuggestedCabinCalss(fleetPlanningSession.getCabinClassByAircaftType(typeCode));
    }

    public int calculateTotalSeat(Aircraft aircraft) {
        int totalSeat = 0;
        for (AircraftCabinClass cabinClass : aircraft.getAircraftCabinClasses()) {
            totalSeat += cabinClass.getSeatQty();
        }
        return totalSeat;
    }

    public String addNewAircraft(String tailNo, String lifespan, String source, String cost, String seatConfig) {
        int totalQty = getTotalSeatQty();
        if (totalQty > aircraftModel.getMaxSeating()) {
            msgController.addErrorMessage("Total seat quantity (" + totalQty + ") must be lower than maximum quantity (" + aircraftModel.getMaxSeating() + ")");
            return "";
        } else {
            try {
                fleetPlanningSession.checkTailNoExist(tailNo);
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
                    return navigationController.redirectToCurrentPage();
                } else {
                    msgController.addErrorMessage("Add New Aircraft");
                    return "";
                }
            } catch (ObjectExistsException ex) {
                msgController.addErrorMessage(ex.getMessage());
                return "";
            }
        }
    }

    public void onSeatQtyChange(AircraftCabinClassHelper thisHelper) {
        System.out.println("this aircraftCabinClass: "+thisHelper.getSeatQty());
        int totalQty = getTotalSeatQty();
        setRowCols(thisHelper);
        
        if (totalQty > aircraftModel.getMaxSeating()) {
            msgController.addErrorMessage("Total seat quantity (" + totalQty + ") must be lower than maximum quantity (" + aircraftModel.getMaxSeating() + ")");
        } else if (totalQty > aircraftModel.getTypicalSeating()) {
            msgController.warn("Total seat quantity (" + totalQty + ") is suggested to be lower or equal to typical seating quantity (" + aircraftModel.getTypicalSeating() + ")");
        }
    }
    
    public void onRowColChange(AircraftCabinClassHelper thisHelper){
        thisHelper.setSeatQty(thisHelper.getNumOfCol()*thisHelper.getNumOfRow());
    }
    
    private void setRowCols(AircraftCabinClassHelper thisHelper){
        int numOfCols = 1, numOfRows = 1;
        
        switch(thisHelper.getType()){
            case "F": 
                numOfCols = 2;break;
            case "B":
                numOfCols = 4;break;
            case "P":
                numOfCols = 6;break;
            case "E":
                numOfCols = 8;break;
                
        }
        numOfRows = thisHelper.getSeatQty()/numOfCols;
        thisHelper.setNumOfRow(numOfRows);
        thisHelper.setNumOfCol(numOfCols);
        thisHelper.setSeatQty(numOfRows*numOfCols);
    }

    private int getTotalSeatQty() {
        int totalQty = 0;
        for (AircraftCabinClassHelper thisHelper : aircraftCabinClassHelpers) {
            totalQty += thisHelper.getSeatQty();
        }
        return totalQty;
    }

    public void onModelSelectChange() {
        getCabinClassByAircraftModel(aircraftModel.getTypeCode());
        setApproxCost(aircraftModel.getApproxCost());
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

    /**
     * @return the suggestedCabinClass
     */
    public List<AircraftCabinClassHelper> getSuggestedCabinCalss() {
        return suggestedCabinClass;
    }

    /**
     * @param suggestedCabinClass the suggestedCabinClass to set
     */
    public void setSuggestedCabinCalss(List<AircraftCabinClassHelper> suggestedCabinClass) {
        this.suggestedCabinClass = suggestedCabinClass;
    }

    /**
     * @return the approxCost
     */
    public double getApproxCost() {
        return approxCost;
    }

    /**
     * @param approxCost the approxCost to set
     */
    public void setApproxCost(double approxCost) {
        this.approxCost = approxCost;
    }

}
