/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.afos;

import ams.afos.entity.Checklist;
import ams.afos.entity.ChecklistItem;
import ams.afos.session.FlightCrewMgmtSessionLocal;
import ams.afos.util.helper.ChecklistType;
import ams.aps.entity.Flight;
import ams.aps.entity.FlightSchedule;
import ams.aps.session.FlightSchedulingSessionLocal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import managedbean.application.AfosNavController;
import managedbean.application.MsgController;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author Lewis
 */
@Named(value = "flightDutyBacking")
@ViewScoped
public class FlightDutyBacking implements Serializable {

    @EJB
    private FlightCrewMgmtSessionLocal flightCrewMgmtSession;
    @EJB
    private FlightSchedulingSessionLocal flightSchedulingSession;

    @Inject
    private AfosNavController afosNavController;

    @Inject
    private MsgController msgController;

    private List<String> checklistTypes;
    private List<Checklist> templates;
    private Checklist selectedTemplate;
    private Checklist flightChecklist;
    private String selectedType;
    private List<Flight> scheduledFlights;
    private List<Flight> selectedFlights;
    private Flight selectedFlight;
    private List<FlightSchedule> futureFlightSchedules;
    private ChecklistItem selectedChecklistItem;
    private ChecklistItem newChecklistItem = new ChecklistItem();
    private boolean isTemplate = false;

    /**
     * Creates a new instance of FlightDutyBacking
     */
    public FlightDutyBacking() {
    }

    @PostConstruct
    public void init() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String uri = request.getRequestURI();
        uri = uri.substring(uri.lastIndexOf("/") + 1, uri.indexOf('.', uri.lastIndexOf("/")));
        System.out.println("FlightDutyBacking: init() uri = " + uri);
        switch (uri) {
            case "selectChecklistType":
                setScheduledFlights(getFlightsWithFlightSchedules());
                setChecklistTypes((List<String>) new ArrayList());
                getChecklistTypes().add(ChecklistType.PRE_FLIGHT);
                getChecklistTypes().add(ChecklistType.POST_FLIGHT);
                break;
            case "createChecklist":
                Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
                setSelectedFlight((Flight) map.get("selectedFlight"));
                setSelectedType((String) map.get("selectedType"));
                setSelectedTemplate((Checklist) map.get("selectedTemplate"));
                setFlightChecklist(flightCrewMgmtSession.getFlightChecklist(selectedFlight, selectedType));
                if (flightChecklist == null) {
                    Checklist newflightChecklist = new Checklist();
                    if (selectedTemplate != null) {
                        newflightChecklist.setChecklistItems(selectedTemplate.getChecklistItems());
                    }
                    setFlightChecklist(newflightChecklist);
                }
        }

    }

    public String createNewChecklist() {
        flightChecklist.setIsTemplate(isTemplate);
        System.out.println("isTemplate = " + isTemplate);
        flightChecklist.setType(selectedType);
        flightChecklist.setDeleted(false);
        flightCrewMgmtSession.createFlightDutyChecklist(flightChecklist, selectedFlight);
        msgController.addMessage("Create " + selectedType + " Checklist for Flight" + selectedFlight.getFlightNo() + "/" + selectedFlight.getReturnedFlight().getFlightNo());
        return afosNavController.toSelectChecklist();
    }

    private List<Flight> getFlightsWithFlightSchedules() {
        return flightSchedulingSession.getScheduledFlights();
    }

    public void onChecklistTypeChange(AjaxBehaviorEvent event) {
        System.out.println("onChecklistTypeChange()" + selectedType);
        setTemplates(getChecklistTemplate());
    }

    private List<Checklist> getChecklistTemplate() {
        return flightCrewMgmtSession.getChecklistTemplates(selectedType);
    }

    public String proceedToCreateFlight() {
        Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        map.put("selectedFlight", selectedFlight);
        map.put("selectedType", selectedType);
        return afosNavController.toCreateChecklist();
    }

    public void onViewBtnClick() {
        System.out.println("selectedFlight: " + selectedFlight);
        if (selectedFlight != null) {
            setFutureFlightSchedules(flightSchedulingSession.getThisFlightFlightSchedules(selectedFlight));
            RequestContext context = RequestContext.getCurrentInstance();
            context.update("flightScheduleDlg");
            context.execute("PF('flightScheduleDlg').show();");
        }
    }

    public String onCreateBtnClick() {
        if (selectedType == null || selectedType.equals("")) {
            msgController.addErrorMessage("Please Select Checklist Type!");
            return "";
        } else if (selectedFlight == null) {
            msgController.addErrorMessage("Please Select Flights!");
            return "";
        } else if (templates == null || templates.isEmpty()) {
            return proceedToCreateFlight();
        } else {
            if (flightCrewMgmtSession.getFlightChecklist(selectedFlight, selectedType) == null) {
                // if there is no existing flight checklist for selected flight and checklist type
                RequestContext context = RequestContext.getCurrentInstance();
                context.execute("PF('templateDlg').show();");
                return "";
            } else {
                return proceedToCreateFlight();
            }
        }
    }

    public String onUseTemplateBtnClick() {
        Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        map.put("selectedTemplate", selectedTemplate);
        return proceedToCreateFlight();
    }

    public void onAddItemBtnClick() {
        List<ChecklistItem> checklistItems = flightChecklist.getChecklistItems();
        if (checklistItems == null) {
            checklistItems = new ArrayList();
            checklistItems.add(newChecklistItem);
            flightChecklist.setChecklistItems(checklistItems);
        } else {
            checklistItems.add(newChecklistItem);
        }
        System.out.println("New Item: \n\tTitle: " + newChecklistItem.getName() + "\n\tDescription: " + newChecklistItem.getDescription());
    }

    public void onDeleteBtnClick() {
        flightChecklist.getChecklistItems().remove(selectedChecklistItem);
    }

    public void setAsTemplate() {
        isTemplate = true;
        createNewChecklist();
    }

    /**
     * @return the selectedType
     */
    public String getSelectedType() {
        return selectedType;
    }

    /**
     * @param selectedType the selectedType to set
     */
    public void setSelectedType(String selectedType) {
        this.selectedType = selectedType;
    }

    /**
     * @return the templates
     */
    public List<Checklist> getTemplates() {
        return templates;
    }

    /**
     * @param templates the templates to set
     */
    public void setTemplates(List<Checklist> templates) {
        this.templates = templates;
    }

    /**
     * @return the selectedTemplate
     */
    public Checklist getSelectedTemplate() {
        return selectedTemplate;
    }

    /**
     * @param selectedTemplate the selectedTemplate to set
     */
    public void setSelectedTemplate(Checklist selectedTemplate) {
        this.selectedTemplate = selectedTemplate;
    }

    /**
     * @return the selectedFlights
     */
    public List<Flight> getSelectedFlights() {
        return selectedFlights;
    }

    /**
     * @param selectedFlights the selectedFlights to set
     */
    public void setSelectedFlights(List<Flight> selectedFlights) {
        this.selectedFlights = selectedFlights;
    }

    /**
     * @return the selectedFlight
     */
    public Flight getSelectedFlight() {
        return selectedFlight;
    }

    /**
     * @param selectedFlight the selectedFlight to set
     */
    public void setSelectedFlight(Flight selectedFlight) {
        this.selectedFlight = selectedFlight;
    }

    /**
     * @return the futureFlightSchedules
     */
    public List<FlightSchedule> getFutureFlightSchedules() {
        return futureFlightSchedules;
    }

    /**
     * @param futureFlightSchedules the futureFlightSchedules to set
     */
    public void setFutureFlightSchedules(List<FlightSchedule> futureFlightSchedules) {
        this.futureFlightSchedules = futureFlightSchedules;
    }

    /**
     * @return the scheduledFlights
     */
    public List<Flight> getScheduledFlights() {
        return scheduledFlights;
    }

    /**
     * @param scheduledFlights the scheduledFlights to set
     */
    public void setScheduledFlights(List<Flight> scheduledFlights) {
        this.scheduledFlights = scheduledFlights;
    }

    /**
     * @return the checklistTypes
     */
    public List<String> getChecklistTypes() {
        return checklistTypes;
    }

    /**
     * @param checklistTypes the checklistTypes to set
     */
    public void setChecklistTypes(List<String> checklistTypes) {
        this.checklistTypes = checklistTypes;
    }

    /**
     * @return the flightChecklist
     */
    public Checklist getFlightChecklist() {
        return flightChecklist;
    }

    /**
     * @param flightChecklist the flightChecklist to set
     */
    public void setFlightChecklist(Checklist flightChecklist) {
        this.flightChecklist = flightChecklist;
    }

    /**
     * @return the selectedChecklistItem
     */
    public ChecklistItem getSelectedChecklistItem() {
        return selectedChecklistItem;
    }

    /**
     * @param selectedChecklistItem the selectedChecklistItem to set
     */
    public void setSelectedChecklistItem(ChecklistItem selectedChecklistItem) {
        this.selectedChecklistItem = selectedChecklistItem;
    }

    /**
     * @return the newChecklistItem
     */
    public ChecklistItem getNewChecklistItem() {
        return newChecklistItem;
    }

    /**
     * @param newChecklistItem the newChecklistItem to set
     */
    public void setNewChecklistItem(ChecklistItem newChecklistItem) {
        this.newChecklistItem = newChecklistItem;
    }

}
