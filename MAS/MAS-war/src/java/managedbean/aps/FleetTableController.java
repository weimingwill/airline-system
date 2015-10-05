/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftType;
import ams.aps.session.FleetPlanningSessionLocal;
import ams.aps.util.helper.AircraftStatus;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;

/**
 *
 * @author Lewis
 */
@Named(value = "fleetTableController")
@ViewScoped
public class FleetTableController implements Serializable {

    @EJB
    private FleetPlanningSessionLocal fleetPlanningSession;

    @Inject
    FleetController fleetController;

    @Inject
    MsgController msgController;

    @Inject
    NavigationController navigationController;

    @Inject
    FleetFilterController fleetFilterController;

    private List<Aircraft> fleetList = new ArrayList<>();
    private List<AircraftType> aircraftModelList = new ArrayList();
    private List<Aircraft> selectedAircrafts;
    private Aircraft selectedAircraft;
    private AircraftType selectedAircraftType;
    private AircraftType newAircraftType = new AircraftType();
    private DashboardModel model;
    private List<String> aircraftStatusList = new ArrayList<String>(Arrays.asList(AircraftStatus.IDLE, AircraftStatus.IN_MAINT, AircraftStatus.IN_USE));
    private String selectedAircraftStatus;

    private String[] status = new String[2];

    /**
     * Creates a new instance of FleetTableController
     */
    public FleetTableController() {
    }

    @PostConstruct
    public void init() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String uri = request.getRequestURI();
        uri = uri.substring(uri.lastIndexOf("/") + 1, uri.indexOf('.', uri.lastIndexOf("/")));

        switch (uri) {
            case "retireAircraft":
                status[0] = AircraftStatus.IDLE;
                status[1] = AircraftStatus.IN_MAINT;getFleet(status);
                break;
            case "viewFleet":
                status = null;getFleet(status);
                break;
            case "viewRetiredFleet":
                status[0] = AircraftStatus.RETIRED;
                status[1] = AircraftStatus.CRASHED;
                getFleet(status);break;
            case "viewAircraftModel":
                setAircraftModelList(fleetController.getAvailableAircraftModels());
                break;
        }
        
    }

    public void getFleet(String[] status) {
        if (status != null) {
            for (String thisStatus : status) {
                fleetList.addAll(fleetController.getFleet(thisStatus));
            }
        } else {
            setFleetList(fleetPlanningSession.getFleet(null));
        }
    }

    public void onMinusBtnClick(ActionEvent event) {
        if (!selectedAircrafts.isEmpty()) {
            System.err.println("RetireAircrafts is not empty");
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('retireFleetDialog').show();");
        } else {
            System.err.println("RetireAircrafts is empty");
        }
    }

    public String retireAircrafts(ActionEvent event) {
        System.out.println("FleetTableController: retireAircrafts()");
        if (fleetPlanningSession.retireSelectedAircrafts(selectedAircrafts)) {
            msgController.addMessage("Retire Aircrafts");
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('retireFleetDialog').hide();");
        } else {
            msgController.addErrorMessage("Retire Aircrafts");
        }
        return "";
    }

    public void viewAircraftDetail(SelectEvent event) {
        System.out.println("viewAircraftDetail: " + selectedAircraft);
        if (!(selectedAircraft == null)) {
            createDashboardModel();
            RequestContext context = RequestContext.getCurrentInstance();
            context.update("aircraftDetailsDlg");
            context.execute("PF('aircraftDetailsDlg').show();");
        }
    }

    private void onOpenDialogBtnClick(String dialogId) {
        System.out.println("onOpenDialogBtnClick(): DialogId = " + dialogId);
        System.out.println("onOpenDialogBtnClick(): SelectedAircraft = " + selectedAircraft);
        System.out.println("onOpenDialogBtnClick(): SelectedAircraftModel = " + selectedAircraftType);
        if (!(selectedAircraft == null) || !(selectedAircraftType == null)) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.update(dialogId);
            context.execute("PF(\'" + dialogId + "\').show();");
        }
    }

    public void onEditAircraftBtnClick() {
        onOpenDialogBtnClick("editAircraftDlg");
    }

    public void onEditModelBtnClick() {
        onOpenDialogBtnClick("editAircraftModelDlg");
    }

    public void onViewMaintRcdBtnClick() {
        System.out.println("FleetTableController: onViewMaintRcdBtnClick()");
        onOpenDialogBtnClick("aircraftMaintRcdDlg");
    }

    public void onViewFlightHistBtnClick() {
        onOpenDialogBtnClick("aircraftFlightHistDlg");
    }

    public void applyFilters(String target) {
        System.out.println("FleetTableController: applyFilters(): target = " + target);
        switch (target) {
            case "Retire":
                setFleetList(fleetFilterController.getFilteredAircrafts());
                break;
            case "Purchase":
                setAircraftModelList(fleetFilterController.getFilteredAircraftModels());
                System.out.println("FleetTableController: applyFilters(): afterSetFilteredAircraftModels");
                break;
        }
    }

    public void resetFilters(String target) {
        System.out.println("FleetTableController: resetFilters() target = " + target);
        switch (target) {
            case "Retire":
                fleetFilterController.setInitialValue(target);
                status[0] = AircraftStatus.IDLE;
                status[1] = AircraftStatus.IN_MAINT;
                setFleetList(new ArrayList());
                getFleet(status);
                break;
            case "Purchase":
                fleetFilterController.setInitialValue(target);
                setAircraftModelList(fleetController.getAvailableAircraftModels());
                break;
        }
    }

    public void updateAircraftInfo(String newLifespan, String newCost, String newOilUsage) {
        System.out.println("new lifespan = " + newLifespan);
        System.out.println("new Cost = " + newCost);
        System.out.println("new Oil Usage = " + newOilUsage);
        selectedAircraft.setLifetime(Float.parseFloat(newLifespan));
        selectedAircraft.setCost(Float.parseFloat(newCost));
        selectedAircraft.setAvgUnitOilUsage(Float.parseFloat(newOilUsage));
        selectedAircraft.setStatus(selectedAircraftStatus);
        if (fleetPlanningSession.updateAircraftInfo(selectedAircraft)) {
            msgController.addMessage("Update aircraft " + selectedAircraft.getTailNo() + " information");
        } else {
            msgController.addErrorMessage("Update aircraft " + selectedAircraft.getTailNo() + " information");
        }
    }

    public String addNewAircraftModel() {
        System.out.println("FleetTableController: addNewAircraftModel()");
        if (fleetPlanningSession.addNewAircraftModel(newAircraftType)) {
            msgController.addMessage("Add new aircraft model " + newAircraftType.getTypeCode());
        } else {
            msgController.addErrorMessage("Add new aircraft model " + newAircraftType.getTypeCode());
        }
        return navigationController.redirectToCurrentPage();
    }

    public String updateAircraftModel() {
        System.out.println("FleetTableController: updateAircraftModel()");
        if (fleetPlanningSession.updateAircraftModel(selectedAircraftType)) {
            msgController.addMessage("Update aircraft model " + selectedAircraftType.getTypeCode());
        } else {
            msgController.addErrorMessage("Update aircraft model " + selectedAircraftType.getTypeCode());
        }
        return navigationController.redirectToCurrentPage();
    }

    public void createDashboardModel() {
        model = new DefaultDashboardModel();
        DashboardColumn column1 = new DefaultDashboardColumn();
        DashboardColumn column2 = new DefaultDashboardColumn();

//        column1.addWidget("testTile");
//        column2.addWidget("testTile2");
        column1.addWidget("cost");
        column1.addWidget("dimension");
        column2.addWidget("performance");
        column2.addWidget("capacity");

        model.addColumn(column1);
        model.addColumn(column2);

    }

    public boolean filterMin(Object value, Object filter, Locale locale) {

        String filterText = (filter == null) ? null : filter.toString().trim();
        if (filterText == null || filterText.equals("")) {
            return true;
        }

        if (value == null) {
            return false;
        }

        return ((Comparable) value).compareTo(Integer.valueOf(filterText)) > 0;
    }

    /**
     * @return the fleetList
     */
    public List<Aircraft> getFleetList() {
        return fleetList;
    }

    /**
     * @param fleetList the fleetList to set
     */
    public void setFleetList(List<Aircraft> fleetList) {
        this.fleetList = fleetList;
    }

    /**
     * @return the selectedAircrafts
     */
    public List<Aircraft> getSelectedAircrafts() {
        return selectedAircrafts;
    }

    /**
     * @param selectedAircrafts the selectedAircrafts to set
     */
    public void setSelectedAircrafts(List<Aircraft> selectedAircrafts) {
        this.selectedAircrafts = selectedAircrafts;
    }

    /**
     * @return the selectedAircraft
     */
    public Aircraft getSelectedAircraft() {
        return selectedAircraft;
    }

    /**
     * @param selectedAircraft the selectedAircraft to set
     */
    public void setSelectedAircraft(Aircraft selectedAircraft) {
        this.selectedAircraft = selectedAircraft;
    }

    /**
     * @return the model
     */
    public DashboardModel getModel() {
        return model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(DashboardModel model) {
        this.model = model;
    }

    /**
     * @return the aircraftStatusList
     */
    public List<String> getAircraftStatusList() {
        return aircraftStatusList;
    }

    /**
     * @param aircraftStatusList the aircraftStatusList to set
     */
    public void setAircraftStatusList(List<String> aircraftStatusList) {
        this.aircraftStatusList = aircraftStatusList;
    }

    /**
     * @return the selectedAircraftStatus
     */
    public String getSelectedAircraftStatus() {
        return selectedAircraftStatus;
    }

    /**
     * @param selectedAircraftStatus the selectedAircraftStatus to set
     */
    public void setSelectedAircraftStatus(String selectedAircraftStatus) {
        this.selectedAircraftStatus = selectedAircraftStatus;
    }


    /**
     * @return the selectedAircraftType
     */
    public AircraftType getSelectedAircraftType() {
        return selectedAircraftType;
    }

    /**
     * @param selectedAircraftType the selectedAircraftType to set
     */
    public void setSelectedAircraftType(AircraftType selectedAircraftType) {
        this.selectedAircraftType = selectedAircraftType;
    }

    /**
     * @return the newAircraftType
     */
    public AircraftType getNewAircraftType() {
        return newAircraftType;
    }

    /**
     * @param newAircraftType the newAircraftType to set
     */
    public void setNewAircraftType(AircraftType newAircraftType) {
        this.newAircraftType = newAircraftType;
    }

    /**
     * @return the aircraftModelList
     */
    public List<AircraftType> getAircraftModelList() {
        return aircraftModelList;
    }

    /**
     * @param aircraftModelList the aircraftModelList to set
     */
    public void setAircraftModelList(List<AircraftType> aircraftModelList) {
        this.aircraftModelList = aircraftModelList;
    }



}
