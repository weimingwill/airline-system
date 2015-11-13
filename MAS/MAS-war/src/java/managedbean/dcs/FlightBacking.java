/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.dcs;

import ams.aps.entity.FlightSchedule;
import ams.dcs.session.CheckInSessionLocal;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import managedbean.application.DcsNavController;
import managedbean.application.MsgController;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

/**
 *
 * @author ChuningLiu
 */
@Named(value = "flightBacking")
@ViewScoped
public class FlightBacking {

    @Inject
    private MsgController msgController;

    @Inject
    private DcsNavController dcsNavController;

    @EJB
    private CheckInSessionLocal checkInSession;

    /**
     * Creates a new instance of FlightBacking
     */
    private FlightSchedule fs = new FlightSchedule();
    private String flightNo;
    private Date currentDate = new Date();
    private Date selectedDate = new Date();

    private String boardingGate = "";
    private String flightStatus = "";

    private List<FlightSchedule> flightsDepart;
    private FlightSchedule departFlight;
    private List<FlightSchedule> flightsArrival;
    private FlightSchedule arrivalFlight;

    private FlightSchedule selectedFlight;
    private String arrivalGate;
    private String arrivalTerminal;
    private Date acctualArrivalDate;
    private Date acctualDepartureDate;
    private String departureGate;
    private String departureTerminal;
    
    @PostConstruct
    public void init() {
        setFlightsDepart(getDepFlightSchedules());
        setFlightsArrival(getArrFlightSchedules());
    }

    public FlightBacking() {
    }

    public String updateFlightStatus() {
        return "";
    }

    public String changeBoardingGate() {
        return "";
    }

    private FlightSchedule searchFlightSchedule(String flightNo, Date flightDate) {
        return new FlightSchedule();
    }

    public void onTabChange(TabChangeEvent event) {
        RequestContext context = RequestContext.getCurrentInstance();
        context.update(":myForm:flight1");
        context.update(":myForm:flight2");
    }

    public void onEditDFlightBtnClick() {
        if (selectedFlight != null) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.update("depFlightInfoDlg");
            context.execute("PF('depFlightInfoDlg').show();");
        }
    }
    
    public void onEditAFlightBtnClick() {
        if (selectedFlight != null) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.update("arrFlightInfoDlg");
            context.execute("PF('arrFlightInfoDlg').show();");
        }
    }

    private List<FlightSchedule> getDepFlightSchedules() {
        return checkInSession.getFlightSchedulesForDeparture();
    }

    private List<FlightSchedule> getArrFlightSchedules() {
        return checkInSession.getFlightSchedulesForArrival();
    }

    /**
     * @return the fs
     */
    public FlightSchedule getFs() {
        return fs;
    }

    /**
     * @param fs the fs to set
     */
    public void setFs(FlightSchedule fs) {
        this.fs = fs;
    }

    /**
     * @return the flightNo
     */
    public String getFlightNo() {
        return flightNo;
    }

    /**
     * @param flightNo the flightNo to set
     */
    public void setFlightNo(String flightNo) {
        this.flightNo = flightNo;
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
     * @return the selectedDate
     */
    public Date getSelectedDate() {
        return selectedDate;
    }

    /**
     * @param selectedDate the selectedDate to set
     */
    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }

    /**
     * @return the boardingGate
     */
    public String getBoardingGate() {
        return boardingGate;
    }

    /**
     * @param boardingGate the boardingGate to set
     */
    public void setBoardingGate(String boardingGate) {
        this.boardingGate = boardingGate;
    }

    /**
     * @return the flightStatus
     */
    public String getFlightStatus() {
        return flightStatus;
    }

    /**
     * @param flightStatus the flightStatus to set
     */
    public void setFlightStatus(String flightStatus) {
        this.flightStatus = flightStatus;
    }

    /**
     * @return the flightsDepart
     */
    public List<FlightSchedule> getFlightsDepart() {
        return flightsDepart;
    }

    /**
     * @param flightsDepart the flightsDepart to set
     */
    public void setFlightsDepart(List<FlightSchedule> flightsDepart) {
        this.flightsDepart = flightsDepart;
    }

    /**
     * @return the departFlight
     */
    public FlightSchedule getDepartFlight() {
        return departFlight;
    }

    /**
     * @param departFlight the departFlight to set
     */
    public void setDepartFlight(FlightSchedule departFlight) {
        this.departFlight = departFlight;
    }

    /**
     * @return the flightsArrival
     */
    public List<FlightSchedule> getFlightsArrival() {
        return flightsArrival;
    }

    /**
     * @param flightsArrival the flightsArrival to set
     */
    public void setFlightsArrival(List<FlightSchedule> flightsArrival) {
        this.flightsArrival = flightsArrival;
    }

    /**
     * @return the arrivalFlight
     */
    public FlightSchedule getArrivalFlight() {
        return arrivalFlight;
    }

    /**
     * @param arrivalFlight the arrivalFlight to set
     */
    public void setArrivalFlight(FlightSchedule arrivalFlight) {
        this.arrivalFlight = arrivalFlight;
    }

    /**
     * @return the selectedFlight
     */
    public FlightSchedule getSelectedFlight() {
        return selectedFlight;
    }

    /**
     * @param selectedFlight the selectedFlight to set
     */
    public void setSelectedFlight(FlightSchedule selectedFlight) {
        this.selectedFlight = selectedFlight;
    }

    /**
     * @return the arrivalGate
     */
    public String getArrivalGate() {
        return arrivalGate;
    }

    /**
     * @param arrivalGate the arrivalGate to set
     */
    public void setArrivalGate(String arrivalGate) {
        this.arrivalGate = arrivalGate;
    }

    /**
     * @return the arrivalTerminal
     */
    public String getArrivalTerminal() {
        return arrivalTerminal;
    }

    /**
     * @param arrivalTerminal the arrivalTerminal to set
     */
    public void setArrivalTerminal(String arrivalTerminal) {
        this.arrivalTerminal = arrivalTerminal;
    }

    /**
     * @return the departureGate
     */
    public String getDepartureGate() {
        return departureGate;
    }

    /**
     * @param departureGate the departureGate to set
     */
    public void setDepartureGate(String departureGate) {
        this.departureGate = departureGate;
    }

    /**
     * @return the departureTerminal
     */
    public String getDepartureTerminal() {
        return departureTerminal;
    }

    /**
     * @param departureTerminal the departureTerminal to set
     */
    public void setDepartureTerminal(String departureTerminal) {
        this.departureTerminal = departureTerminal;
    }

    /**
     * @return the acctualArrivalDate
     */
    public Date getAcctualArrivalDate() {
        return acctualArrivalDate;
    }

    /**
     * @param acctualArrivalDate the acctualArrivalDate to set
     */
    public void setAcctualArrivalDate(Date acctualArrivalDate) {
        this.acctualArrivalDate = acctualArrivalDate;
    }

    /**
     * @return the acctualDepartureDate
     */
    public Date getAcctualDepartureDate() {
        return acctualDepartureDate;
    }

    /**
     * @param acctualDepartureDate the acctualDepartureDate to set
     */
    public void setAcctualDepartureDate(Date acctualDepartureDate) {
        this.acctualDepartureDate = acctualDepartureDate;
    }

}
