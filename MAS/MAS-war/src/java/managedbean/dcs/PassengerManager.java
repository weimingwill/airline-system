/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.dcs;

import ams.aps.entity.FlightSchedule;
import ams.ars.entity.AirTicket;
import ams.ars.entity.BoardingPass;
import ams.ars.entity.Seat;
import ams.crm.entity.Customer;
import ams.dcs.session.CheckInSessionLocal;
import ams.dcs.util.exception.NoSuchBoardingPassException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import managedbean.application.DcsNavController;
import managedbean.application.MsgController;
import org.primefaces.context.RequestContext;

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

    @Inject
    private LuggageManager luggageManager;

    private String passportNo;
    private Customer passenger;

    private long boardingPassNo;
    private BoardingPass pass;

    private Seat seat;
    private List<Seat> seats;

    private AirTicket selectedTicket;
    private AirTicket airTicket;
    private Date boardingDate;
    private List<FlightSchedule> flightSchedules = new ArrayList<>();
    private List<AirTicket> airtickets = new ArrayList<>();//available tickets
    private List<AirTicket> airticketsUpdated = new ArrayList<>();//within 48h tickets
    private List<AirTicket> airTicketsSelected = new ArrayList<>();

    /**
     * Creates a new instance of PassengerController
     */
    public PassengerManager() {
    }

    @PostConstruct
    public void init() {
        cleanSession();
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
                    msgController.addErrorMessage("Passenger do not have PNR available for check-in!");
                    return "";
                }
                return dcsNavController.toCheckInPassenger();
            } else {
                msgController.addErrorMessage("Passenger do not have PNR available for check-in!");
                return "";
            }
        }
    }

    public String getAvailableSeats() {
        List<Seat> ss = checkInSession.getSeatsByTicket(selectedTicket);
        if (ss != null) {
            setSeats(ss);
            return dcsNavController.toSelectSeat();
        } else {
            msgController.addErrorMessage("No seats available");
            return "";
        }
    }

    public void onRowChange() {
    }

    public void selectSeat() {
        System.out.println("passport = " + passportNo);
        if (seat == null) {
            msgController.addErrorMessage("Seat not selected!");
        } else {
            if (checkInSession.selectSeat(airTicket.getId(), seat)) {
                msgController.addMessage("Select a seat successfully!");
                seat = null;
                airTicket = null;
            } else {
                msgController.addErrorMessage("Failed to select seat!");
            }
        }
    }

    public void checkInPassenger() {
//        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
//        RequestContext context = RequestContext.getCurrentInstance();
//        context.execute("window.open('" + request.getContextPath() + dcsNavController.toBoardingPass() + "', '_blank')");
        if (airTicket == null) {
            msgController.addErrorMessage("No PNR selected!");
        } else {
            pass = checkInSession.checkInPassenger(airTicket);
            if (pass == null) {
                msgController.addErrorMessage("Check-in error!");
            } else {
                msgController.addMessage("Check in completed!");
                boardingDate = pass.getBoardingTime();
                HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                RequestContext context = RequestContext.getCurrentInstance();
                context.update(":myForm:ticket");
                context.execute("PF('luggageDlg').show()");
                context.execute("window.open('" + request.getContextPath() + dcsNavController.toBoardingPass() + "', '_blank')");
            }
        }
    }

    public void proceedToLuggage() {
        Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        map.put("checkedTicket", airTicket);
        map.put("passenger", passenger);
        cleanSession();
        luggageManager.ticketReceived();
    }

    public String boardPassenger() {
        try {
            airTicket = checkInSession.getAirTicketByPassID(boardingPassNo);
            passenger = airTicket.getCustomer();
            pass = airTicket.getBoardingPass();
            return dcsNavController.toConfirmBoarding();
        } catch (NoSuchBoardingPassException e) {
            cleanSession();
            msgController.addErrorMessage("No such boarding pass found!");
            return "";
        }
    }

    public String confirmBoarding() {
        try {
            checkInSession.boardPassenger(airTicket);
            msgController.addMessage("Boarding successfully!");
            cleanSession();
            return dcsNavController.toBoardPassenger();
        } catch (Exception e) {
            cleanSession();
            msgController.addErrorMessage("Boarsing Error happened!");
            return dcsNavController.toBoardPassenger();
        }
    }

    private void cleanSession() {

        setAirTicketsSelected(new ArrayList());
        setAirtickets(new ArrayList());
        setAirticketsUpdated(new ArrayList());
        setFlightSchedules(new ArrayList());
        setPassenger(null);
        setPassportNo("");
        setAirTicket(new AirTicket());
        setBoardingDate(new Date());
    }

    public Seat getSeatbyID(long id) {
        Seat temp = checkInSession.getSeatByID(id);
        if (temp == null) {
            System.out.println("Seat Not Found: " + id);
            msgController.addErrorMessage("Seat Not Found!");
            return null;
        } else {
            return temp;
        }
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

    /**
     * @return the boardingPassNo
     */
    public long getBoardingPassNo() {
        return boardingPassNo;
    }

    /**
     * @param boardingPassNo the boardingPassNo to set
     */
    public void setBoardingPassNo(long boardingPassNo) {
        this.boardingPassNo = boardingPassNo;
    }

    /**
     * @return the pass
     */
    public BoardingPass getPass() {
        return pass;
    }

    /**
     * @param pass the pass to set
     */
    public void setPass(BoardingPass pass) {
        this.pass = pass;
    }

    /**
     * @return the airTicket
     */
    public AirTicket getAirTicket() {
        return airTicket;
    }

    /**
     * @param airTicket the airTicket to set
     */
    public void setAirTicket(AirTicket airTicket) {
        this.airTicket = airTicket;
    }

    /**
     * @return the boardingDate
     */
    public Date getBoardingDate() {
        return boardingDate;
    }

    /**
     * @param boardingDate the boardingDate to set
     */
    public void setBoardingDate(Date boardingDate) {
        this.boardingDate = boardingDate;
    }

    /**
     * @return the seats
     */
    public List<Seat> getSeats() {
        return seats;
    }

    /**
     * @param seats the seats to set
     */
    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    /**
     * @return the selectedTicket
     */
    public AirTicket getSelectedTicket() {
        return selectedTicket;
    }

    /**
     * @param selectedTicket the selectedTicket to set
     */
    public void setSelectedTicket(AirTicket selectedTicket) {
        this.selectedTicket = selectedTicket;
    }

    /**
     * @return the seat
     */
    public Seat getSeat() {
        return seat;
    }

    /**
     * @param seat the seat to set
     */
    public void setSeat(Seat seat) {
        this.seat = seat;
    }

}
