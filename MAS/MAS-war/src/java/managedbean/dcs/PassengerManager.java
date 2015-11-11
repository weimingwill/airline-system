/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.dcs;

import ams.ais.entity.BookingClass;
import ams.ais.entity.CabinClass;
import ams.ais.entity.FlightScheduleBookingClass;
import ams.ais.entity.TicketFamily;
import ams.aps.entity.Airport;
import ams.aps.entity.Flight;
import ams.aps.entity.FlightSchedule;
import ams.aps.entity.Leg;
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
        
        passenger = new Customer();
        passenger.setFirstName("Chuning");
        passenger.setLastName("Liu");
        passportNo = "G11231123";
        passenger.setPassportNo("G11231123");
        
        Airport a1 = new Airport();
        a1.setAirportName("aaa111");
        a1.setIataCode("AAA");
        Airport a2 = new Airport();
        a2.setAirportName("aaa222");
        a2.setIataCode("AAB");
        Leg l = new Leg();
        l.setDepartAirport(a1);
        l.setArrivalAirport(a2);
        
        Flight f = new Flight();
        f.setFlightNo("MU545");
        FlightSchedule fs = new FlightSchedule();
        fs.setLeg(l);
        fs.setArrivalDate(new Date());
        fs.setDepartGate("28");
        fs.setDepartDate(new Date());
        fs.setFlight(f);
        
        CabinClass cc = new CabinClass();
        cc.setType("B");
        TicketFamily tf = new TicketFamily();
        tf.setCabinClass(cc);
        FlightScheduleBookingClass fabc = new FlightScheduleBookingClass();
        fabc.setFlightSchedule(fs);
        BookingClass bc = new BookingClass();
        bc.setTicketFamily(tf);
        fabc.setBookingClass(bc);
        
        Seat s = new Seat();
        s.setRowNo(11);
        s.setColNo("A");
        
        pass = new BoardingPass();
        airTicket = new AirTicket();
        airTicket.setSeat(s);
        airTicket.setCustomer(passenger);
        airTicket.setFlightSchedBookingClass(fabc);
        airTicket.setBoardingPass(pass);
        airTicketsSelected.add(airTicket);
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

    public void selectSeat(AjaxBehaviorEvent event) {
        System.out.println("passport = " + passportNo);
    }

    public void checkInPassenger() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("window.open('" + request.getContextPath() + dcsNavController.toBoardingPass() + "', '_blank')");
        if (airTicket == null) {
            msgController.addErrorMessage("No PNR selected!");
        } else if (!checkInSession.checkInPassenger(airTicket)) {
            msgController.addErrorMessage("Check-in error!");
        } else {
            msgController.addMessage("Check in completed!");
//            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
//            RequestContext context = RequestContext.getCurrentInstance();
            context.update(":myForm:ticket");
            context.execute("PF('luggageDlg').show()");
            context.execute("window.open('" + request.getContextPath() + dcsNavController.toBoardingPass() + "', '_blank')");
        }

    }

    public void proceedToLuggage() {
        Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        map.put("checkedTicket", airTicket);
        map.put("passenger", passenger);
        luggageManager.ticketReceived();
    }

    public String boardPassenger() {
        try {
            airTicket = checkInSession.getAirTicketByPassID(boardingPassNo);
            passenger = airTicket.getCustomer();
            pass = airTicket.getBoardingPass();
            return dcsNavController.toConfirmBoarding();
        } catch (NoSuchBoardingPassException e) {
            msgController.addErrorMessage("No such boarding pass found!");
            return "";
        }
    }

    public String confirmBoarding() {
        try {
            checkInSession.boardPassenger(airTicket);
            msgController.addMessage("Boarding successfully!");
            return dcsNavController.toBoardPassenger();
        } catch (Exception e) {
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
}
