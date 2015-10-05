/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.ais;

import ams.ais.entity.BookingClass;
import ams.ais.entity.CabinClass;
import ams.ais.entity.CabinClassTicketFamily;
import ams.ais.entity.FlightScheduleBookingClass;
import ams.ais.entity.TicketFamily;
import ams.ais.session.BookingClassSessionLocal;
import ams.ais.session.CabinClassSessionLocal;
import ams.ais.session.SeatReallocationSessionLocal;
import ams.ais.session.TicketFamilySessionLocal;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.exception.NoSuchFlightScheduleBookingClassException;
import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftCabinClass;
import ams.aps.entity.FlightSchedule;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import managedbean.application.MsgController;

/**
 *
 * @author Tongtong
 */
@Named(value = "seatReallocationController")
@SessionScoped
public class SeatReallocationController implements Serializable {

    @Inject
    private MsgController msgController;

    @EJB
    private CabinClassSessionLocal cabinClassSession;

    @EJB
    private TicketFamilySessionLocal ticketFamilySession;

    @EJB
    private BookingClassSessionLocal bookingClassSession;

    @EJB
    private SeatReallocationSessionLocal seatReallocationSession;

    private FlightSchedule flightSchedule;
    private Aircraft aircraft;
    private CabinClass cabinClass;
    private List<CabinClass> cabinClasses;
    private List<AircraftCabinClass> aircraftCabinClasses;
    private AircraftCabinClass aircraftCabinClass;
    private List<CabinClassTicketFamily> cabinClassTicketFamily;
    private List<TicketFamily> ticketFamilys;
    private List<FlightScheduleBookingClass> flightScheduleBookingClasses;
    private TicketFamily ticketFamily;
    private FlightScheduleBookingClass flightScheduleBookingClass;
    private BookingClass bookingClass;
    private List<BookingClass> bookingClasses;

    /**
     * Creates a new instance of SeatReallocationController
     */
    public SeatReallocationController() {
    }

    @PostConstruct
    public void init() {
        
        flightSchedule = 

        aircraft = flightSchedule.getAircraft();
        aircraftCabinClasses = aircraft.getAircraftCabinClasses();

//        countries = (List<Country>) routePlanningSession.getCountryList();
//        hubs = (List<Airport>) routePlanningSession.getHubs();
//        viewRoutes();
//        viewObsoleteRoutes();
    }

    public CabinClass getCabinClassbyName(String cabinClassName) {
        return cabinClassSession.getCabinClassByName(cabinClassName);

    }

    public TicketFamily getTicketFamilybyName(String ticketFamilyName) {
        return ticketFamilySession.getTicketFamilyByName(ticketFamilyName);
    }

    public FlightScheduleBookingClass getFlightScheduleBookingClassbyIDs(Long flightScheduleID, Long bookingClassIS) {
        return seatReallocationSession.getFlightScheduleBookingClassbyFlightScheduleIDandBookingClassID(flightScheduleID, bookingClassIS);
    }

    public BookingClass getBookingClassbyName(String bookingClassName) throws NoSuchBookingClassException {
        return bookingClassSession.search(bookingClassName);
    }

    public void reallocateBookingClassSeats(FlightScheduleBookingClass fsbc, float newDemandMean, float newDemandDev) {
        System.out.println("SeatReallocationController:reallocateBookingClassSeats() ");
        if (seatReallocationSession.reallocateBookingClassSeats(fsbc, newDemandMean, newDemandDev)) {
            msgController.addMessage("Add a hub successfully!");
//            cleanGlobalVariable();
        } else {
            msgController.addErrorMessage("Failed to add hub!");
//            cleanGlobalVariable();
        }
    }

    public void onCabinClassChange() {
        setTicketFamilys(cabinClass.getTicketFamilys());
        ticketFamily = null;
        flightScheduleBookingClass = null;
    }

    public void onTicketFamilyChange() {
        try {
            setFlightScheduleBookingClasses(ticketFamilySession.getFlightScheduleBookingClassesbyTicketFamily(ticketFamily.getName()));
        } catch (NoSuchFlightScheduleBookingClassException ex) {
            Logger.getLogger(SeatReallocationController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

//    public void onCabinClassChange() {
//        setTicketFamilies((List<TicketFamily>) ticketFamilySession.getTicketFamilyByCabinClass());
//        city = null;
//        airport = null;
//        airportsNotHub = new ArrayList<Airport>();
//    }
//
//    public void onCityChange() {
//        setAirportsNotHub((List<Airport>) routePlanningSession.getNonHubAirportListByCity(city.getId()));
//    }
//    private void cleanGlobalVariable() {
//        country = null;
//        cities = null;
//        city = null;
//        airports = null;
//        airport = null;
//        hub = null;
//        airportsNotHub = null;
//        route = null;
//        routeList = null;
//        setStopover(null);
//        setDestination(null);
//        hubs = (List<Airport>) routePlanningSession.getHubs();
//    }
    public MsgController getMsgController() {
        return msgController;
    }

    public void setMsgController(MsgController msgController) {
        this.msgController = msgController;
    }

    public CabinClassSessionLocal getCabinClassSession() {
        return cabinClassSession;
    }

    public void setCabinClassSession(CabinClassSessionLocal cabinClassSession) {
        this.cabinClassSession = cabinClassSession;
    }

    public TicketFamilySessionLocal getTicketFamilySession() {
        return ticketFamilySession;
    }

    public void setTicketFamilySession(TicketFamilySessionLocal ticketFamilySession) {
        this.ticketFamilySession = ticketFamilySession;
    }

    public BookingClassSessionLocal getBookingClassSession() {
        return bookingClassSession;
    }

    public void setBookingClassSession(BookingClassSessionLocal bookingClassSession) {
        this.bookingClassSession = bookingClassSession;
    }

    public SeatReallocationSessionLocal getSeatReallocationSession() {
        return seatReallocationSession;
    }

    public void setSeatReallocationSession(SeatReallocationSessionLocal seatReallocationSession) {
        this.seatReallocationSession = seatReallocationSession;
    }

    public FlightSchedule getFlightSchedule() {
        return flightSchedule;
    }

    public void setFlightSchedule(FlightSchedule flightSchedule) {
        this.flightSchedule = flightSchedule;
    }

    public Aircraft getAircraft() {
        return aircraft;
    }

    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }

    public List<CabinClass> getCabinClasses() {
        return cabinClasses;
    }

    public void setCabinClasses(List<CabinClass> cabinClasses) {
        this.cabinClasses = cabinClasses;
    }

    public List<AircraftCabinClass> getAircraftCabinClasses() {
        return aircraftCabinClasses;
    }

    public void setAircraftCabinClasses(List<AircraftCabinClass> aircraftCabinClasses) {
        this.aircraftCabinClasses = aircraftCabinClasses;
    }

    public AircraftCabinClass getAircraftCabinClass() {
        return aircraftCabinClass;
    }

    public void setAircraftCabinClass(AircraftCabinClass aircraftCabinClass) {
        this.aircraftCabinClass = aircraftCabinClass;
    }

    public List<CabinClassTicketFamily> getCabinClassTicketFamily() {
        return cabinClassTicketFamily;
    }

    public void setCabinClassTicketFamily(List<CabinClassTicketFamily> cabinClassTicketFamily) {
        this.cabinClassTicketFamily = cabinClassTicketFamily;
    }

    public List<TicketFamily> getTicketFamilys() {
        return ticketFamilys;
    }

    public void setTicketFamilys(List<TicketFamily> ticketFamilys) {
        this.ticketFamilys = ticketFamilys;
    }

    public CabinClass getCabinClass() {
        return cabinClass;
    }

    public void setCabinClass(CabinClass cabinClass) {
        this.cabinClass = cabinClass;
    }

    public List<FlightScheduleBookingClass> getFlightScheduleBookingClasses() {
        return flightScheduleBookingClasses;
    }

    public void setFlightScheduleBookingClasses(List<FlightScheduleBookingClass> flightScheduleBookingClasses) {
        this.flightScheduleBookingClasses = flightScheduleBookingClasses;
    }

    public TicketFamily getTicketFamily() {
        return ticketFamily;
    }

    public void setTicketFamily(TicketFamily ticketFamily) {
        this.ticketFamily = ticketFamily;
    }

    public FlightScheduleBookingClass getFlightScheduleBookingClass() {
        return flightScheduleBookingClass;
    }

    public void setFlightScheduleBookingClass(FlightScheduleBookingClass flightScheduleBookingClass) {
        this.flightScheduleBookingClass = flightScheduleBookingClass;
    }

    public BookingClass getBookingClass() {
        return bookingClass;
    }

    public void setBookingClass(BookingClass bookingClass) {
        this.bookingClass = bookingClass;
    }

    public List<BookingClass> getBookingClasses() {
        return bookingClasses;
    }

    public void setBookingClasses(List<BookingClass> bookingClasses) {
        this.bookingClasses = bookingClasses;
    }
    
    
    
    



}
