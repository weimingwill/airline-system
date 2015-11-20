/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.afos;

import ams.afos.entity.Checklist;
import ams.afos.entity.ChecklistItem;
import ams.afos.entity.FlightCrew;
import ams.afos.session.FlightCrewMgmtSessionLocal;
import ams.afos.util.helper.ChecklistType;
import ams.aps.entity.Airport;
import ams.aps.entity.Flight;
import ams.aps.entity.FlightSchedule;
import ams.aps.session.FlightSchedulingSessionLocal;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import managedbean.application.AfosNavController;
import managedbean.application.MsgController;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;

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
    private String selectedTypeForChlt;
    private List<Flight> scheduledFlights;
    private List<Flight> selectedFlights;
    private Flight selectedFlight;
    private Flight selectedFlightForChlt;
    private List<FlightSchedule> futureFlightSchedules;
    private List<FlightSchedule> pastFlightSchedules;
    private FlightSchedule selectedFlightSchedule;
    private ChecklistItem selectedChecklistItem;
    private ChecklistItem newChecklistItem = new ChecklistItem();
    private boolean isTemplate = false;

    private List<FlightCrew> crewsForFlightSchedule;
    private FlightCrew selectedCrew;
    private Checklist postFlightReport;

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
        setScheduledFlights(getFlightsWithFlightSchedules());
        Map<String, Object> map;
        switch (uri) {
            case "selectChecklistType":
                setChecklistTypes((List<String>) new ArrayList());
                getChecklistTypes().add(ChecklistType.PRE_FLIGHT);
                getChecklistTypes().add(ChecklistType.POST_FLIGHT);
                break;
            case "createChecklist":
                map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
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
                removeSelectedFlightFromFlightList();
                break;
            case "crewReporting":
                setFutureFlightSchedules(flightSchedulingSession.getAllFutureFlightSchedules());
                setPastFlightSchedules(flightSchedulingSession.getAllPastFlightSchedules());
            case "updateCrewAttendance":
                map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
                setCrewsForFlightSchedule((List<FlightCrew>) map.get("flightCrews"));
                setSelectedFlightSchedule((FlightSchedule) map.get("selectedFlightSchedule"));
                break;
            case "viewDutyReport":
                map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
                setPostFlightReport((Checklist) map.get("postFlightReport"));
                setSelectedFlightSchedule((FlightSchedule) map.get("selectedFlightSchedule"));
                break;
            default:
        }
    }

    private void removeSelectedFlightFromFlightList() {
        scheduledFlights.remove(selectedFlight);
        scheduledFlights.remove(selectedFlight.getReturnedFlight());
    }

    public String applyTmpToOtherFlights() {
        for (Flight thisFlight : selectedFlights) {
            createNewChecklist(new Checklist(), thisFlight);
        }
        createNewChecklist(flightChecklist, selectedFlight);
        return afosNavController.toSelectChecklist();
    }

    public String createNewChecklist(Checklist checklist, Flight thisFlight) {
        flightChecklist.setIsTemplate(isTemplate);
        System.out.println("isTemplate = " + isTemplate);
        flightChecklist.setType(selectedType);
        flightChecklist.setDeleted(false);
        if (checklist.getChecklistItems() == null || checklist.getChecklistItems().isEmpty()) {
            // if checklist if a new checklist -> case: apply checklist to other flight
            checklist.setChecklistItems(flightChecklist.getChecklistItems());
            checklist.setDeleted(flightChecklist.getDeleted());
            checklist.setIsTemplate(false);
            checklist.setType(flightChecklist.getType());
        }
        flightCrewMgmtSession.createFlightDutyChecklist(checklist, thisFlight);
        msgController.addMessage("Create " + selectedType + " Checklist for Flight" + thisFlight.getFlightNo() + "/" + thisFlight.getReturnedFlight().getFlightNo());
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
        newChecklistItem.setCreatedTime(Calendar.getInstance().getTime());
        newChecklistItem.setLastUpdateTime(Calendar.getInstance().getTime());
        if (checklistItems == null) {
            checklistItems = new ArrayList();
            checklistItems.add(newChecklistItem);
            flightChecklist.setChecklistItems(checklistItems);
        } else {
            checklistItems.add(newChecklistItem);
        }
        System.out.println("New Item: \n\tTitle: " + newChecklistItem.getName() + "\n\tDescription: " + newChecklistItem.getDescription());
    }

    public void onCellEdit(CellEditEvent event) {
        int rowIndex = event.getRowIndex();
        ChecklistItem oldItem = flightChecklist.getChecklistItems().get(rowIndex);
        ChecklistItem newItem = new ChecklistItem();
        newItem.setCreatedTime(oldItem.getCreatedTime());
        newItem.setDescription(oldItem.getDescription());
        newItem.setLastUpdateTime(Calendar.getInstance().getTime());
        newItem.setName(oldItem.getName());
        flightChecklist.getChecklistItems().remove(oldItem);
        flightChecklist.getChecklistItems().add(rowIndex, newItem);
    }

    public void onDeleteBtnClick() {
        flightChecklist.getChecklistItems().remove(selectedChecklistItem);
    }

    public void setAsTemplate() {
        isTemplate = true;
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('applyTemplateDlg').show();");
    }

    public void onViewPreBtnClick() {
        setSelectedTypeForChlt(ChecklistType.PRE_FLIGHT);
        getFlightCheckist();
    }

    public void onViewPostBtnClick() {
        setSelectedTypeForChlt(ChecklistType.POST_FLIGHT);
        getFlightCheckist();
    }

    private void getFlightCheckist() {
        setFlightChecklist(flightCrewMgmtSession.getFlightChecklist(selectedFlightForChlt, selectedTypeForChlt));
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("checklistDlg");
        context.execute("PF('checklistDlg').show();");
    }

    public String onViewCrewsBtnClick() {
        setCrewsForFlightSchedule(flightCrewMgmtSession.getOnDutyCrews(selectedFlightSchedule));
        if (crewsForFlightSchedule == null || crewsForFlightSchedule.isEmpty()) {
            msgController.addErrorMessage("There is no crew assigned to this flight");
            return "";
        } else {
            Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            map.put("flightCrews", crewsForFlightSchedule);
            map.put("selectedFlightSchedule", selectedFlightSchedule);
            return afosNavController.toUpdateCrewAttendance();
        }
    }

    public String getDepartureDateTime() {
        SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy");
        return ft.format(selectedFlightSchedule.getDepartDate());
    }

    public String updateAttendance() {
        for (FlightCrew fc : crewsForFlightSchedule) {
            if (fc.getStatus() == null || fc.getStatus().equals("")) {
                msgController.addErrorMessage("Please update attendance for crew: " + fc.getName());
                return "";
            }
        }
        flightCrewMgmtSession.updateAttendance(crewsForFlightSchedule, selectedFlightSchedule);
        return "";
    }

    public String onSaveBtnClick() {
        updateAttendance();
        return afosNavController.toCrewReporting();
    }

    public String onViewReportBtnClick() {
        setPostFlightReport(flightCrewMgmtSession.getPostFlightReport(selectedFlightSchedule));
        Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        map.put("postFlightReport", getPostFlightReport());
        map.put("selectedFlightSchedule", selectedFlightSchedule);
        return afosNavController.toViewPostDutyReport();
    }

    public String getItemValue(double value) {
        if (value == 1) {
            return "Checked";
        } else if (value == 0) {
            return "Not Checked";
        } else {
            String pattern = "###,###.##";
            DecimalFormat decimalFormat = new DecimalFormat(pattern);
            return decimalFormat.format(value);
        }
    }

    public void preProcessPDF(Object document) throws IOException, BadElementException, DocumentException {
        Document pdf = (Document) document;
        pdf.open();
        pdf.setPageSize(PageSize.A4);
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String logo = servletContext.getRealPath("") + File.separator + "resources" + File.separator + "images" + File.separator + "logo.png";
        Image logoImg = Image.getInstance(logo);
        logoImg.scalePercent(20f);
        SimpleDateFormat sdf = new SimpleDateFormat("E, dd/MM/yyyy 'at' hh:mm:ss a");

        Font titleFont = new Font();
        Font subtitleFont = new Font();
        titleFont.setSize(16);
        titleFont.setStyle(Font.BOLD);
        subtitleFont.setSize(12);
        subtitleFont.setStyle(Font.BOLD);

        Paragraph title = new Paragraph("Post-Flight Duty Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(12f);

        Paragraph subTitle1 = new Paragraph("Flight Information:", subtitleFont);
        subTitle1.setSpacingAfter(8f);

        Paragraph subTitle2 = new Paragraph("Post-Flight Checklist:", subtitleFont);
        subTitle2.setSpacingBefore(12f);
        subTitle2.setSpacingAfter(12f);

        com.lowagie.text.List list = new com.lowagie.text.List(true, 15);
        list.setLettered(true);
        list.add("Flight: " + selectedFlightSchedule.getFlight().getFlightNo());
        list.add("Return Flight: " + selectedFlightSchedule.getFlight().getReturnedFlight().getFlightNo());
        Airport departure, arrival;
        departure = selectedFlightSchedule.getLeg().getDepartAirport();
        arrival = selectedFlightSchedule.getLeg().getArrivalAirport();
        sdf.setTimeZone(TimeZone.getTimeZone(getTimeZone(departure)));
        list.add("Departure Time: " + sdf.format(selectedFlightSchedule.getDepartDate()) + " (" + departure.getCity().getCityName() + " Time)");
        sdf.setTimeZone(TimeZone.getTimeZone(getTimeZone(arrival)));
        list.add("Arrival Time: " + sdf.format(selectedFlightSchedule.getArrivalDate())+" (" + arrival.getCity().getCityName() + " Time)");
        list.add("Origin: " + departure.getCity().getCityName() + " (" + departure.getAirportName() + ")");
        list.add("Destination: " + arrival.getCity().getCityName() + " (" + arrival.getAirportName() + ")");

        pdf.add(logoImg);
        pdf.add(new Paragraph(" "));
        pdf.add(title);
        pdf.add(subTitle1);
        pdf.add(list);
        pdf.add(subTitle2);
        pdf.setMargins(72, 72, 72, 72);
        pdf.addAuthor("Merlion Airline");
        pdf.addCreationDate();
        sdf.applyPattern("dd/MM/yyyy_hh:mm:ss");
        pdf.addTitle("Post-Flight Duty Report_" + selectedFlightSchedule.getFlight().getFlightNo() + "_" + sdf.format(selectedFlightSchedule.getDepartDate()));
    }

    private String getTimeZone(Airport airport) {
        DecimalFormat df = new DecimalFormat("#.#");
        float utc = airport.getCity().getUTC();
        if (utc > 0) {
            return "GMT+" + df.format(utc);
        } else {
            return "GMT" + df.format(utc);
        }
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

    /**
     * @return the selectedFlightForChlt
     */
    public Flight getSelectedFlightForChlt() {
        return selectedFlightForChlt;
    }

    /**
     * @param selectedFlightForChlt the selectedFlightForChlt to set
     */
    public void setSelectedFlightForChlt(Flight selectedFlightForChlt) {
        this.selectedFlightForChlt = selectedFlightForChlt;
    }

    /**
     * @return the selectedTypeForChlt
     */
    public String getSelectedTypeForChlt() {
        return selectedTypeForChlt;
    }

    /**
     * @param selectedTypeForChlt the selectedTypeForChlt to set
     */
    public void setSelectedTypeForChlt(String selectedTypeForChlt) {
        this.selectedTypeForChlt = selectedTypeForChlt;
    }

    /**
     * @return the pastFlightSchedules
     */
    public List<FlightSchedule> getPastFlightSchedules() {
        return pastFlightSchedules;
    }

    /**
     * @param pastFlightSchedules the pastFlightSchedules to set
     */
    public void setPastFlightSchedules(List<FlightSchedule> pastFlightSchedules) {
        this.pastFlightSchedules = pastFlightSchedules;
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
     * @return the crewsForFlightSchedule
     */
    public List<FlightCrew> getCrewsForFlightSchedule() {
        return crewsForFlightSchedule;
    }

    /**
     * @param crewsForFlightSchedule the crewsForFlightSchedule to set
     */
    public void setCrewsForFlightSchedule(List<FlightCrew> crewsForFlightSchedule) {
        this.crewsForFlightSchedule = crewsForFlightSchedule;
    }

    /**
     * @return the selectedCrew
     */
    public FlightCrew getSelectedCrew() {
        return selectedCrew;
    }

    /**
     * @param selectedCrew the selectedCrew to set
     */
    public void setSelectedCrew(FlightCrew selectedCrew) {
        this.selectedCrew = selectedCrew;
    }

    /**
     * @return the postFlightReport
     */
    public Checklist getPostFlightReport() {
        return postFlightReport;
    }

    /**
     * @param postFlightReport the postFlightReport to set
     */
    public void setPostFlightReport(Checklist postFlightReport) {
        this.postFlightReport = postFlightReport;
    }

}
