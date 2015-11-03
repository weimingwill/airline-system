/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.dcs;

import ams.aps.entity.Flight;
import ams.aps.entity.FlightSchedule;
import ams.ars.entity.AirTicket;
import ams.ars.entity.BoardingPass;
import ams.ars.entity.Seat;
import ams.crm.entity.Customer;
import ams.dcs.util.helper.AirTicketDisplayHelper;
import ams.dcs.session.CheckInSessionLocal;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import managedbean.application.DcsNavController;
import managedbean.application.MsgController;

/**
 *
 * @author ChuningLiu
 */
@Named(value = "passengerManager")
@SessionScoped
public class PassengerManager implements Serializable {

    @Inject
    private MsgController msgController;

    @Inject
    private DcsNavController dcsNavController;

    @EJB
    private CheckInSessionLocal checkInSession;

    private String passportNo;
    private Customer passenger;

    private List<FlightSchedule> flightSchedules = new ArrayList<>();
    private List<Customer> passengerList = new ArrayList<>();
    private List<AirTicket> airtickets = new ArrayList<>();//available tickets
    private List<AirTicket> airticketsUpdated = new ArrayList<>();//within 48h tickets
    private List<AirTicket> airTicketsSelected = new ArrayList<>();

    /**
     * Creates a new instance of PassengerController
     */
    public PassengerManager() {
    }

    public String checkPassenger() {

        passenger = checkInSession.getCustomerByPassport(getPassportNo());
        if (passenger == null) {
            msgController.addErrorMessage("Passenger not found!");
            System.err.println("No Passenger Found");
            return "";
        } else {
            setAirtickets(checkInSession.getFSforCheckin(passportNo));
            if (!airtickets.isEmpty()) {

                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.HOUR_OF_DAY, 48); // adds 48 hour
                Date checkInAlloweddate = cal.getTime(); // returns new date object, one hour in the future
                Date currentDate = new Date();

                for (AirTicket a : airtickets) {
                    FlightSchedule fs = a.getFlightSchedBookingClass().getFlightSchedule();
                    if (fs.getDepartDate().before(checkInAlloweddate) && fs.getDepartDate().after(currentDate)) {
                        getAirticketsUpdated().add(a);
                    }
                }
                if (getAirticketsUpdated().isEmpty()) {
                    msgController.addErrorMessage("Passenger do not have trips available for check-in!");
                    return "";
                }
                return dcsNavController.toCheckInPassenger();
            } else {
                msgController.addErrorMessage("Passenger do not have trips available for check-in!");
                return "";
            }
        }
    }

    public void SelectSeat(AjaxBehaviorEvent event) {
        System.out.println("passport = " + passportNo);
    }

    public String checkInPassenger() {
        String toLuggageCheckIn = dcsNavController.toCheckInLuggage();
        
        if(airTicketsSelected.isEmpty()){
            msgController.addMessage("No airticket selected!");
            toLuggageCheckIn = "";
        }else{
            for (AirTicket a : airTicketsSelected) {
                boolean success = checkInSession.checkInPassenger(a);
                if (!success) {
                    msgController.addErrorMessage("Ticket " + a.getId() + " check-in error!");
                    airTicketsSelected.remove(a);
                }
            }
            if (airTicketsSelected.isEmpty()) {
                toLuggageCheckIn = "";
            }         
        }
        return toLuggageCheckIn;
    }

    public void onPassportChange(AjaxBehaviorEvent event) {
        System.out.println("passport = " + passportNo);
    }

    /**
     * @return the passportNo
     */
    public String getPassportNo() {
        return passportNo;
    }

    /**
     * @param passportNo the passportNo to set
     */
    public void setPassportNo(String passportNo) {
        this.passportNo = passportNo;
    }

    /**
     * @return the passenger
     */
    public Customer getPassenger() {
        return passenger;
    }

    /**
     * @param passenger the passenger to set
     */
    public void setPassenger(Customer passenger) {
        this.passenger = passenger;
    }

    /**
     * @return the passengerList
     */
    public List<Customer> getPassengerList() {
        return passengerList;
    }

    /**
     * @param passengerList the passengerList to set
     */
    public void setPassengerList(List<Customer> passengerList) {
        this.passengerList = passengerList;
    }

    /**
     * @return the flightSchedules
     */
    public List<FlightSchedule> getFlightSchedules() {
        return flightSchedules;
    }

    /**
     * @param flightSchedules the flightSchedules to set
     */
    public void setFlightSchedules(List<FlightSchedule> flightSchedules) {
        this.flightSchedules = flightSchedules;
    }

    /**
     * @return the airtickets
     */
    public List<AirTicket> getAirtickets() {
        return airtickets;
    }

    /**
     * @param airtickets the airtickets to set
     */
    public void setAirtickets(List<AirTicket> airtickets) {
        this.airtickets = airtickets;
    }
    
    /**
     * @return the airticketsUpdated
     */
    public List<AirTicket> getAirticketsUpdated() {
        return airticketsUpdated;
    }

    /**
     * @param airticketsUpdated the airticketsUpdated to set
     */
    public void setAirticketsUpdated(List<AirTicket> airticketsUpdated) {
        this.airticketsUpdated = airticketsUpdated;
    }

    /**
     * @return the airTicketsSelected
     */
    public List<AirTicket> getAirTicketsSelected() {
        return airTicketsSelected;
    }

    /**
     * @param airTicketsSelected the airTicketsSelected to set
     */
    public void setAirTicketsSelected(List<AirTicket> airTicketsSelected) {
        this.airTicketsSelected = airTicketsSelected;
    }

}
