/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.dcs;

import ams.aps.entity.FlightSchedule;
import java.util.Date;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author ChuningLiu
 */
@Named(value = "flightBacking")
@ViewScoped
public class FlightBacking {

    /**
     * Creates a new instance of FlightBacking
     */
    
    private FlightSchedule fs = new FlightSchedule();
    private String flightNo;
    private Date currentDate = new Date();
    private Date selectedDate = new Date();
    
    private String boardingGate = "";
    private String flightStatus = "";
    
    public String updateFlightStatus(){
        return "";
    }
    
    public String changeBoardingGate(){
        return "";
    }
    
    private FlightSchedule searchFlightSchedule(String flightNo, Date flightDate){
        return new FlightSchedule();
    }
    
    public FlightBacking() {
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
    
    
    
}
