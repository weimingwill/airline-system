/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.afos;

import ams.afos.entity.Checklist;
import ams.afos.entity.ChecklistItem;
import ams.afos.entity.MaintCheckType;
import ams.afos.entity.MaintCrew;
import ams.afos.entity.MaintSchedule;
import ams.afos.session.MaintMgmtSessionLocal;
import ams.afos.util.helper.ChecklistType;
import ams.afos.util.helper.MaintScheduleClashException;
import ams.aps.entity.Aircraft;
import ams.aps.entity.FlightSchedule;
import ams.aps.session.FleetPlanningSessionLocal;
import ams.aps.session.FlightSchedulingSessionLocal;
import ams.aps.util.helper.AircraftStatus;
import ams.aps.util.helper.FlightSchedMethod;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import managedbean.application.AfosNavController;
import managedbean.application.MsgController;
import managedbean.aps.FlightScheduleManager;
import mas.util.helper.DateHelper;
import org.primefaces.event.CellEditEvent;
import org.primefaces.extensions.model.timeline.TimelineEvent;
import org.primefaces.extensions.model.timeline.TimelineGroup;
import org.primefaces.extensions.model.timeline.TimelineModel;

/**
 *
 * @author Lewis
 */
@Named(value = "maintTaskManager")
@SessionScoped
public class MaintTaskManager implements Serializable {
    @EJB
    private FleetPlanningSessionLocal fleetPlanningSession;
    @EJB
    private FlightSchedulingSessionLocal flightSchedulingSession;

    @EJB
    private MaintMgmtSessionLocal maintMgmtSession;

    @Inject
    private FlightScheduleManager flightScheduleManager;

    @Inject
    private AfosNavController afosNavController;

    @Inject
    private MsgController msgController;

    private Date timelineStartDate;
    private Date timelineEndDate;
    private Date currentDate = Calendar.getInstance().getTime();
    private Date endTimeMin;
    private TimelineModel model;
    private MaintSchedule newMaintSchedule = new MaintSchedule();
    private List<MaintCrew> availableCrews;
    private List<MaintCrew> selectedCrews;
    private List<MaintCheckType> checkTypes;
    private MaintCheckType selectedCheckType;
    private Aircraft selectedAircraft;
    private Checklist newMaintChecklist;
    private ChecklistItem selectedChecklistItem;
    private ChecklistItem newChecklistItem = new ChecklistItem();

    /**
     * Creates a new instance of MaintTaskManager
     */
    public MaintTaskManager() {
    }

    @PostConstruct
    public void init() {
        initTimeline();
        initSession();
    }

    private void initSession() {
        setCheckTypes(maintMgmtSession.getMaintCheckTypes());
        setAvailableCrews(maintMgmtSession.getMaintCrews());
    }

    private void initTimeline() {
        Calendar temp = Calendar.getInstance();
        temp.add(Calendar.HOUR_OF_DAY, -2);
        setTimelineStartDate(temp.getTime());
        temp.add(Calendar.DAY_OF_MONTH, 7);
        setTimelineEndDate(temp.getTime());
        model = new TimelineModel();
        setTimelineGroup();
        setMainScheduleToTimeline();
    }

    public void setTimelineGroup() {
        model = new TimelineModel();
        List<Aircraft> availableAircrafts = fleetPlanningSession.getFleet(AircraftStatus.IDLE);
        List<FlightSchedule> flightSchedules;
        for (Aircraft aircraft : availableAircrafts) {
            TimelineGroup timelineGroup = new TimelineGroup(aircraft.getTailNo(), aircraft);
            model.addGroup(timelineGroup);

            flightSchedules = flightSchedulingSession.getFlightSchedulesByTailNoAndTime(aircraft.getTailNo(), DateHelper.getCurrYearFirstDay(), DateHelper.getNextYearLastDay(), FlightSchedMethod.DISPLAY);
            if (flightSchedules.isEmpty()) {
                model.add(new TimelineEvent(null, new Date(), new Date(), true, aircraft.getTailNo()));
            }
            for (FlightSchedule flightSchedule : flightSchedules) {
                if (flightSchedule.getPreFlightSched() == null) {
                    flightSchedulingSession.setRouteFlightSchedule(flightSchedule);
                    model.add(new TimelineEvent(getFlightScheduleDisplayString(flightSchedule), flightSchedule.getDepartDate(), flightSchedule.getArrivalDate(), true, aircraft.getTailNo(), "schedule"));
                }
            }
        }
    }

    private String getFlightScheduleDisplayString(FlightSchedule flightSchedule){
        String str = "";
        str += flightSchedule.getFlight().getFlightNo() + ": ";
        str += flightSchedule.getLeg().getDepartAirport().getIataCode() + " - ";
        str += flightSchedule.getLeg().getArrivalAirport().getIataCode();
        return str;
    }
        
    private void setMainScheduleToTimeline() {
        List<MaintSchedule> allMaintSchedules = maintMgmtSession.getAllMaintTasks();
        System.out.println("maintSchedule: " + allMaintSchedules);
        for (MaintSchedule maintSchedule : allMaintSchedules) {
            model.add(new TimelineEvent(maintSchedule.getCheckType().getCheckType() + " Type Check", maintSchedule.getStartTime(), maintSchedule.getEndTime(), true, maintSchedule.getAircraft().getTailNo(),"maintenance"));
        }
    }

    public void setEndDateMin() {
        System.out.println("startTime" + newMaintSchedule.getStartTime());
        setEndTimeMin(newMaintSchedule.getStartTime());
    }

    private void addMaintTask(Checklist checklist) {
        try {
            newMaintSchedule.setAircraft(checklist.getAircraft());
            newMaintSchedule.setCheckType(selectedCheckType);
            newMaintSchedule.setDeleted(false);
            newMaintSchedule.setMaintCrews(selectedCrews);
            maintMgmtSession.addMaintTask(newMaintSchedule);
            maintMgmtSession.createMaintChecklist(checklist);
            msgController.addMessage("Create Maintenance Task for Flight " + selectedAircraft.getTailNo() + "/" + selectedAircraft.getAircraftType().getTypeCode());
        } catch (MaintScheduleClashException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
    }

    public String onCreateBtnClick() {
        if (newMaintSchedule.getStartTime() == null || newMaintSchedule.getEndTime() == null) {
            msgController.addErrorMessage("Please select maintenance dates");
            return "";
        } else if (selectedCheckType == null) {
            msgController.addErrorMessage("Please select maintenance check type");
            return "";
        } else if (selectedCrews == null || selectedCrews.isEmpty()) {
            msgController.addErrorMessage("Please select maintenance crews");
            return "";
        } else if (selectedAircraft == null) {
            msgController.addErrorMessage("Please select aircraft for maintenance");
            return "";
        } else {
            setNewMaintChecklist(maintMgmtSession.getAircraftMaintChecklistByType(selectedAircraft, getMaintChecklistType()));
            if (newMaintChecklist == null) {
                setNewMaintChecklist(new Checklist());
            }
            return afosNavController.toCreateMaintChecklist();
        }
    }

    public void onCellEdit(CellEditEvent event) {
        int rowIndex = event.getRowIndex();
        ChecklistItem oldItem = newMaintChecklist.getChecklistItems().get(rowIndex);
        ChecklistItem newItem = new ChecklistItem();
        newItem.setCreatedTime(oldItem.getCreatedTime());
        newItem.setDescription(oldItem.getDescription());
        newItem.setLastUpdateTime(Calendar.getInstance().getTime());
        newItem.setName(oldItem.getName());
        newMaintChecklist.getChecklistItems().remove(oldItem);
        newMaintChecklist.getChecklistItems().add(rowIndex, newItem);
    }

    public void onDeleteBtnClick() {
        newMaintChecklist.getChecklistItems().remove(selectedChecklistItem);
    }

    public void onAddItemBtnClick() {
        List<ChecklistItem> checklistItems = newMaintChecklist.getChecklistItems();
        ChecklistItem newItem = new ChecklistItem();
        newItem.setName(newChecklistItem.getName());
        newItem.setDescription(newChecklistItem.getDescription());
        newItem.setCreatedTime(Calendar.getInstance().getTime());
        newItem.setLastUpdateTime(Calendar.getInstance().getTime());

        if (checklistItems == null) {
            checklistItems = new ArrayList();
            checklistItems.add(newItem);
            newMaintChecklist.setChecklistItems(checklistItems);
        } else {
            checklistItems.add(newItem);
        }
        System.out.println("New Item: \n\tTitle: " + newChecklistItem.getName() + "\n\tDescription: " + newChecklistItem.getDescription());
        newChecklistItem.setDescription("");
        newChecklistItem.setName("");
    }

    public String createNewChecklist(Checklist checklist, Aircraft thisAircraft) {
        String checklistType = getMaintChecklistType();
        newMaintChecklist.setIsTemplate(false);
        newMaintChecklist.setType(checklistType);
        newMaintChecklist.setDeleted(false);
        newMaintChecklist.setAircraft(thisAircraft);
        if (checklist.getChecklistItems() == null || checklist.getChecklistItems().isEmpty()) {
            // if checklist if a new checklist -> case: apply checklist to other flight
            checklist.setChecklistItems(newMaintChecklist.getChecklistItems());
            checklist.setDeleted(newMaintChecklist.getDeleted());
            checklist.setIsTemplate(false);
            checklist.setType(newMaintChecklist.getType());
            checklist.setAircraft(newMaintChecklist.getAircraft());
        }
        addMaintTask(checklist);
        return afosNavController.toViewMaintSchedules();
    }

    private String getMaintChecklistType() {
        String checklistType = "";
        switch (selectedCheckType.getCheckType()) {
            case "A":
                checklistType = ChecklistType.MAINTENANCE_A;
                break;
            case "B":
                checklistType = ChecklistType.MAINTENANCE_B;
                break;
            case "C":
                checklistType = ChecklistType.MAINTENANCE_C;
                break;
            case "D":
                checklistType = ChecklistType.MAINTENANCE_D;
                break;
        }
        return checklistType;
    }

    public MaintCrew getCrewById(Long Id) {
        return maintMgmtSession.getCrewById(Id);
    }

    public MaintCheckType getCheckTypeById(Long id) {
        return maintMgmtSession.getCheckTypeById(id);
    }

    /**
     * @return the timelineStartDate
     */
    public Date getTimelineStartDate() {
        return timelineStartDate;
    }

    /**
     * @param timelineStartDate the timelineStartDate to set
     */
    public void setTimelineStartDate(Date timelineStartDate) {
        this.timelineStartDate = timelineStartDate;
    }

    /**
     * @return the timelineEndDate
     */
    public Date getTimelineEndDate() {
        return timelineEndDate;
    }

    /**
     * @param timelineEndDate the timelineEndDate to set
     */
    public void setTimelineEndDate(Date timelineEndDate) {
        this.timelineEndDate = timelineEndDate;
    }

    public TimelineModel getModel() {
        return model;
    }

    public void setModel(TimelineModel model) {
        this.model = model;
    }

    public MaintSchedule getNewMaintSchedule() {
        return newMaintSchedule;
    }

    public void setNewMaintSchedule(MaintSchedule newMaintSchedule) {
        this.newMaintSchedule = newMaintSchedule;
    }

    /**
     * @return the currentDate
     */
    public Date getCurrentDate() {
        return currentDate;
    }

    /**
     * @param currentDate the currentDate to set
     */
    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    /**
     * @return the endTimeMin
     */
    public Date getEndTimeMin() {
        return endTimeMin;
    }

    /**
     * @param endTimeMin the endTimeMin to set
     */
    public void setEndTimeMin(Date endTimeMin) {
        this.endTimeMin = endTimeMin;
    }

    /**
     * @return the availableCrews
     */
    public List<MaintCrew> getAvailableCrews() {
        return availableCrews;
    }

    /**
     * @param availableCrews the availableCrews to set
     */
    public void setAvailableCrews(List<MaintCrew> availableCrews) {
        this.availableCrews = availableCrews;
    }

    /**
     * @return the selectedCrews
     */
    public List<MaintCrew> getSelectedCrews() {
        return selectedCrews;
    }

    /**
     * @param selectedCrews the selectedCrews to set
     */
    public void setSelectedCrews(List<MaintCrew> selectedCrews) {
        this.selectedCrews = selectedCrews;
    }

    /**
     * @return the checkTypes
     */
    public List<MaintCheckType> getCheckTypes() {
        return checkTypes;
    }

    /**
     * @param checkTypes the checkTypes to set
     */
    public void setCheckTypes(List<MaintCheckType> checkTypes) {
        this.checkTypes = checkTypes;
    }

    /**
     * @return the selectedCheckType
     */
    public MaintCheckType getSelectedCheckType() {
        return selectedCheckType;
    }

    /**
     * @param selectedCheckType the selectedCheckType to set
     */
    public void setSelectedCheckType(MaintCheckType selectedCheckType) {
        this.selectedCheckType = selectedCheckType;
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
     * @return the newMaintChecklist
     */
    public Checklist getNewMaintChecklist() {
        return newMaintChecklist;
    }

    /**
     * @param newMaintChecklist the newMaintChecklist to set
     */
    public void setNewMaintChecklist(Checklist newMaintChecklist) {
        this.newMaintChecklist = newMaintChecklist;
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
