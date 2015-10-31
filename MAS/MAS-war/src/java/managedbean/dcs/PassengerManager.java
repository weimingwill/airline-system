/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.dcs;

import ams.aps.entity.Flight;
import ams.aps.entity.FlightSchedule;
import ams.ars.entity.AirTicket;
import ams.crm.entity.Customer;
import ams.dcs.util.helper.AirTicketDisplayHelper;
import ams.dcs.util.helper.PassengerDisplayHelper;
import ams.dcs.session.CheckInSessionLocal;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
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
    private List<PassengerDisplayHelper> passengerShowList = new ArrayList<>();

    private List<FlightSchedule> flightSchedules = new ArrayList<>();
    private List<Customer> passengerList = new ArrayList<>();
    private List<AirTicketDisplayHelper> airTicketShowList = new ArrayList<>();

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
            setFlightSchedules(checkInSession.getFSforCheckinByPassport(passportNo));
            if (!flightSchedules.isEmpty()) {
                passengerList.add(passenger);
                for (FlightSchedule fs: flightSchedules){
                    AirTicketDisplayHelper ah = new AirTicketDisplayHelper();
                    ah.setFlightNo(fs.getFlight().getFlightNo());
                    ah.setOriITAT(fs.getLeg().getDepartAirport().getIataCode());
                    ah.setDestITAT(fs.getLeg().getArrivalAirport().getIataCode());
                    ah.setOriName(fs.getLeg().getDepartAirport().getAirportName());
                    ah.setDestName(fs.getLeg().getArrivalAirport().getAirportName());
                    fs.ge
                }

                return dcsNavController.toCheckInPassenger();

            } else {
                msgController.addErrorMessage("Passenger do not have trips available for check-in!");
                return "";
            }
        }
    }

    public List<List> organisePassengers(List<AirTicket> airTicketList) {

        List<AirTicketDisplayHelper> fsList = new ArrayList<>();

        return null; //change
    }

    public List<AirTicketDisplayHelper> organiseAnAirTicket(AirTicket airTicket) {
        AirTicketDisplayHelper atHelper = new AirTicketDisplayHelper();
        List<FlightSchedule> fsList = new ArrayList<>();

        fsList = airTicket.getBooking().getFlightSchedules();
        return null; //change
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
     * @return the passengerShowList
     */
    public List<PassengerDisplayHelper> getPassengerShowList() {
        return passengerShowList;
    }

    /**
     * @param passengerShowList the passengerShowList to set
     */
    public void setPassengerShowList(List<PassengerDisplayHelper> passengerShowList) {
        this.passengerShowList = passengerShowList;
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
     * @return the airTicketShowList
     */
    public List<AirTicketDisplayHelper> getAirTicketShowList() {
        return airTicketShowList;
    }

    /**
     * @param airTicketShowList the airTicketShowList to set
     */
    public void setAirTicketShowList(List<AirTicketDisplayHelper> airTicketShowList) {
        this.airTicketShowList = airTicketShowList;
    }

}
