/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.afos;

import ams.afos.entity.Checklist;
import ams.afos.entity.ChecklistItem;
import ams.afos.entity.FlightCrew;
import ams.afos.entity.FlightDuty;
import ams.afos.session.FlightCrewSessionLocal;
import ams.afos.util.helper.ChecklistType;
import ams.aps.entity.FlightSchedule;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import managedbean.application.AfosNavController;
import managedbean.application.MsgController;

/**
 *
 * @author Lewis
 */
@Named(value = "flightCrewReportingBacking")
@ViewScoped
public class FlightCrewReportingBacking implements Serializable {

    @EJB
    private FlightCrewSessionLocal flightCrewSession;

    @Inject
    private FlightCrewBacking flightCrewBacking;

    @Inject
    private AfosNavController afosNavController;

    @Inject
    private FlightDutyBacking flightDutyBacking;

    @Inject
    private MsgController msgController;

    private FlightCrew currCrew;
    private List<FlightDuty> crewDuties;
    private FlightDuty selectedDuty;
    private FlightSchedule selectedFlightSchedule;
    private Checklist selectedChecklist;
    private List<ChecklistItem> checklistItems;

    /**
     * Creates a new instance of FlightCrewReportingBacking
     */
    public FlightCrewReportingBacking() {
    }

    @PostConstruct
    public void init() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String uri = request.getRequestURI();
        uri = uri.substring(uri.lastIndexOf("/") + 1, uri.indexOf('.', uri.lastIndexOf("/")));
        System.out.println("FlightDutyBacking: init() uri = " + uri);
        Map<String, Object> map;
        setCurrCrew(flightCrewBacking.getCurrFlightCrew());
        if (currCrew != null) {
            switch (uri) {
                case "reportPostFlightDuty":
                    setCrewDuties(flightCrewSession.getCrewCurrMonthPastDuties(currCrew));
                    break;
                case "reportPreFlightDuty":
                    setCrewDuties(flightCrewSession.getCrewCurrMonthFutureDuties(currCrew));
                    break;
                case "updateChecklist":
                    map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
                    String type = (String) map.get("checklistType");
                    setSelectedFlightSchedule((FlightSchedule) map.get("selectedFlightSchedule"));
                    setCurrSelectedChecklist(type);
                    setOldChecklistItems();
                    break;
                default:
            }
        }
    }

    private void setOldChecklistItems() {
        checklistItems = new ArrayList();
        ChecklistItem temp; 
        for(ChecklistItem item: selectedChecklist.getChecklistItems()){
            temp = new ChecklistItem();
            temp.setItemValue(item.getItemValue());
            temp.setRemark(item.getRemark());
            temp.setName(item.getName());
            temp.setDescription(item.getDescription());
            checklistItems.add(temp);
        }
    }

    private void setCurrSelectedChecklist(String type) {
        System.out.println("type: " + type);
        for (Checklist cl : selectedFlightSchedule.getChecklists()) {
            if (cl.getType().equals(type)) {
                setSelectedChecklist(cl);
                break;
            }
        }
    }

    public void updateChecklist() {
        checkChanges();
        flightCrewSession.updateFlightChecklist(selectedChecklist);
        msgController.addMessage("Update " + selectedChecklist.getType() + " Checklist for Flight " + selectedFlightSchedule.getFlight().getFlightNo());
    }
    
    private void checkChanges(){
        for(ChecklistItem item: selectedChecklist.getChecklistItems()){
            for(ChecklistItem oldItem: checklistItems){
                if(oldItem.getName().equals(item.getName()) && oldItem.getDescription().equals(oldItem.getDescription())
                        && (!Objects.equals(oldItem.getItemValue(), item.getItemValue()) || !oldItem.getRemark().equals(item.getRemark()))){
                    item.setLastUpdateTime(Calendar.getInstance().getTime());
                    item.setCheckedBy(currCrew.getFlightCrewID());
                    break;
                }
            }
        }
    }

    public String onSaveBtnClick() {
        switch (selectedChecklist.getType()) {
            case ChecklistType.POST_FLIGHT:
                return afosNavController.toReportPostFlightDuty();
            case ChecklistType.PRE_FLIGHT:
                return afosNavController.toReportPreFlightDuty();
            default:
                return "";
        }
    }

    public String onViewChecklistBtnClick(String type) {
        Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        map.put("selectedFlightSchedule", selectedFlightSchedule);
        map.put("checklistType", type);
        return afosNavController.toUpdateChecklist();
    }

    /**
     * @return the currCrew
     */
    public FlightCrew getCurrCrew() {
        return currCrew;
    }

    /**
     * @param currCrew the currCrew to set
     */
    public void setCurrCrew(FlightCrew currCrew) {
        this.currCrew = currCrew;
    }

    /**
     * @return the crewDuties
     */
    public List<FlightDuty> getCrewDuties() {
        return crewDuties;
    }

    /**
     * @param crewDuties the crewDuties to set
     */
    public void setCrewDuties(List<FlightDuty> crewDuties) {
        this.crewDuties = crewDuties;
    }

    /**
     * @return the selectedDuty
     */
    public FlightDuty getSelectedDuty() {
        return selectedDuty;
    }

    /**
     * @param selectedDuty the selectedDuty to set
     */
    public void setSelectedDuty(FlightDuty selectedDuty) {
        this.selectedDuty = selectedDuty;
    }

    /**
     * @return the selectedFlightSchedule
     */
    public FlightSchedule getSelectedFlightSchedule() {
        return selectedFlightSchedule;
    }

    /**
     * @param selectedFlightSchedule the selectedFlightSchedule to set
     */
    public void setSelectedFlightSchedule(FlightSchedule selectedFlightSchedule) {
        this.selectedFlightSchedule = selectedFlightSchedule;
    }

    /**
     * @return the selectedChecklist
     */
    public Checklist getSelectedChecklist() {
        return selectedChecklist;
    }

    /**
     * @param selectedChecklist the selectedChecklist to set
     */
    public void setSelectedChecklist(Checklist selectedChecklist) {
        this.selectedChecklist = selectedChecklist;
    }

    /**
     * @return the checklistItems
     */
    public List<ChecklistItem> getChecklistItems() {
        return checklistItems;
    }

    /**
     * @param checklistItems the checklistItems to set
     */
    public void setChecklistItems(List<ChecklistItem> checklistItems) {
        this.checklistItems = checklistItems;
    }

}
