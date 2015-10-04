/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import ams.aps.entity.Aircraft;
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
import org.primefaces.component.datatable.DataTable;
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
    FleetFilterController fleetFilterController;

    private List<Aircraft> fleetList = new ArrayList<>();
    private List<Aircraft> selectedAircrafts;
    private Aircraft selectedAircraft;
    private DashboardModel model;
    private List<String> aircraftStatusList = new ArrayList<String>(Arrays.asList(AircraftStatus.IDLE, AircraftStatus.IN_MAINT, AircraftStatus.IN_USE));
    private String selectedAircraftStatus;
    private DataTable theDataTable;
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
                status[1] = AircraftStatus.IN_MAINT;
                break;
            case "viewFleet":
                status = null;
                break;
            case "viewRetiredFleet":
                status[0] = AircraftStatus.RETIRED;
                status[1] = AircraftStatus.CRASHED;
        }
        getFleet(status);
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
        if (!(selectedAircraft == null)) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.update(dialogId);
            context.execute("PF(\'" + dialogId + "\').show();");
        }
    }

    public void onEditButtonClick() {
        onOpenDialogBtnClick("editAircraftDlg");
    }

    public void onViewMaintRcdBtnClick() {
        System.out.println("FleetTableController: onViewMaintRcdBtnClick()");
        onOpenDialogBtnClick("aircraftMaintRcdDlg");
    }

    public void onViewFlightHistBtnClick() {
        onOpenDialogBtnClick("aircraftFlightHistDlg");
    }

    public void applyRetireAicraftFilters() {
        setFleetList(fleetFilterController.getFilteredAircrafts());
    }

    public void resetRetireAicraftFilters() {
        fleetFilterController.setInitialValue();

        status[0] = AircraftStatus.IDLE;
        status[1] = AircraftStatus.IN_MAINT;
        getFleet(status);
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
            msgController.addMessage("Update aircraft " + selectedAircraft.getTailNo() + " information");
        }
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
     * @return the theDataTable
     */
    public DataTable getTheDataTable() {
        return theDataTable;
    }

    /**
     * @param theDataTable the theDataTable to set
     */
    public void setTheDataTable(DataTable theDataTable) {
        this.theDataTable = theDataTable;
    }
}
