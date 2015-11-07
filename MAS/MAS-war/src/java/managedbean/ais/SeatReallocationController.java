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
import ams.ais.entity.PhaseDemand;
import ams.ais.entity.SeatAllocationHistory;
import ams.ais.entity.TicketFamily;
import ams.ais.session.BookingClassSessionLocal;
import ams.ais.session.CabinClassSessionLocal;
import ams.ais.session.FlightScheduleSessionLocal;
import ams.ais.session.SeatReallocationSessionLocal;
import ams.ais.session.TicketFamilySessionLocal;
import ams.ais.util.exception.ExistSuchCheckPointException;
import ams.ais.util.exception.NeedBookingClassException;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchPhaseDemandException;
import ams.ais.util.helper.BookingClassHelper;
import ams.ais.util.helper.CabinClassTicketFamilyHelper;
import ams.ais.util.helper.FlightSchCabinClsTicFamBookingClsHelper;
import ams.ais.util.helper.FlightScheduleBookingClassHelper;
import ams.ais.util.helper.SeatClassHelper;
import ams.ais.util.helper.TicketFamilyBookingClassHelper;
import ams.aps.util.exception.NoSuchFlightScheduleBookingClassException;
import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftCabinClass;
import ams.aps.entity.Airport;
import ams.aps.entity.City;
import ams.aps.entity.FlightSchedule;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import ams.aps.util.helper.ApsMessage;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Tongtong
 */
@Named(value = "seatReallocationController")
@SessionScoped
public class SeatReallocationController implements Serializable {

    @Inject
    private MsgController msgController;

    @Inject
    private NavigationController navigationController;

    @EJB
    private CabinClassSessionLocal cabinClassSession;

    @EJB
    private TicketFamilySessionLocal ticketFamilySession;

    @EJB
    private BookingClassSessionLocal bookingClassSession;

    @EJB
    private SeatReallocationSessionLocal seatReallocationSession;

    @EJB
    private FlightScheduleSessionLocal flightScheduleSession;

    private FlightSchedule flightSchedule;
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
    private Long flightScheduleId;
    private List<FlightSchCabinClsTicFamBookingClsHelper> flightSchCabinClsTicFamBookingClsHelpers;
    private float newDemandMean;
    private float newDemandDev;
    private String cabinClassType;
    private String bookingClassName;
    private List<FlightScheduleBookingClassHelper> flightScheduleBookingClassHelpers;
    private List<SeatClassHelper> seatClassHelpers;
    private FlightSchedule selectedFlightSchedule;
    private List<TicketFamilyBookingClassHelper> ticketFamilyBookingClassHelpers;
    private List<CabinClassTicketFamilyHelper> cabinClassTicketFamilyHelpers;
    private List<BookingClassHelper> bookingClassHelpers;
    private PhaseDemand phaseDemand;
    private float daysBeforeDeparture;
    private List<SeatAllocationHistory> allSeatReAllocationHistorys;
    private PhaseDemand selectedPhaseDemand;
    private List<PhaseDemand> phaseDemands;

    /**
     * Creates a new instance of SeatReallocationController
     */
    public SeatReallocationController() {
    }

    @PostConstruct
    public void init() {

    }

    public String toAddCheckPoint() throws NoSuchFlightScheduleBookingClassException {

        return navigationController.redirectToAddCheckPoint();

    }

    public String reallocateBookingClassSeats() {
        try {
            flightScheduleBookingClass = flightScheduleSession.getFlightScheduleBookingClass(flightScheduleId, bookingClass.getBookingClassId());
            seatReallocationSession.reallocateSeatsforBookingClass(flightScheduleBookingClass, newDemandMean, newDemandDev);
        } catch (NoSuchFlightScheduleBookingClassException e) {
            msgController.addErrorMessage(e.getMessage());
        }

        msgController.addMessage("Reallocate booking class seats succesffully!");
        return navigationController.redirectToViewFlightSchedule();
    }

    public List<SeatAllocationHistory> getAllSeatAllocationHistroy() throws NoSuchFlightSchedulException, NoSuchFlightScheduleBookingClassException, NeedBookingClassException {
        allSeatReAllocationHistorys = seatReallocationSession.getBookingClassSeatAllocationHistory(flightScheduleBookingClass);

        return allSeatReAllocationHistorys;
    }

    public List<PhaseDemand> getAllPhaseDemands() throws NoSuchFlightSchedulException, NoSuchFlightScheduleBookingClassException, NeedBookingClassException {
        return seatReallocationSession.getPhaseDemands(flightScheduleId, flightSchCabinClsTicFamBookingClsHelpers);

    }

    public String addPhaseDemand() throws ExistSuchCheckPointException, NoSuchFlightScheduleBookingClassException {
        try {
            seatReallocationSession.addPhaseDemand(flightScheduleBookingClass, daysBeforeDeparture, newDemandMean, newDemandDev);
            msgController.addMessage("Yield management model is updated successfully!");

        } catch (ExistSuchCheckPointException e) {
            msgController.addErrorMessage(e.getMessage());
        }

        return navigationController.redirectToYieldManagement();
    }

    public PhaseDemand getPhaseDemandbyId(long id) {
        return seatReallocationSession.getPhaseDemandbyId(id);
    }

    public String deletePhaseDemand() throws NoSuchPhaseDemandException, NoSuchFlightScheduleBookingClassException {
        System.out.println("selected phase demand is:" + selectedPhaseDemand.getDaysBeforeDeparture());
        seatReallocationSession.deletePhaseDemand(flightScheduleBookingClass, selectedPhaseDemand);
        msgController.addMessage("Yield management model is updated successfully");

        return navigationController.redirectToYieldManagement();

    }

    public List<FlightSchedule> getAllFlightSchedule() {
        List<FlightSchedule> flightSchedules = new ArrayList<>();
        try {
            flightSchedules = flightScheduleSession.getAllFilghtSchedules();
        } catch (NoSuchFlightSchedulException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return flightSchedules;
    }

    public List<BookingClass> getTicketFamilyBookingClasses(String cabinClassName, String ticketFamilyName) {
        List<BookingClass> bookingClasses;
        try {
            bookingClasses = ticketFamilySession.getTicketFamilyBookingClasses(cabinClassName, ticketFamilyName);
        } catch (NoSuchBookingClassException e) {
            bookingClasses = new ArrayList<>();
        }
        return bookingClasses;
    }

    public List<CabinClass> getFlightScheduleCabinClasses() {
        List<CabinClass> cabinClasses;
        try {
            cabinClasses = flightScheduleSession.getFlightScheduleCabinCalsses(flightScheduleId);
        } catch (NoSuchCabinClassException e) {
            return null;
        }
        return cabinClasses;
    }

    public void onViewBookingClassClick() {
        if (selectedFlightSchedule != null) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.update(":form:viewBookingClass");
            context.execute("PF('flightScheduleBookingClassDialog').show()");
        }
    }

    public boolean haveBookingClass() {
        return flightScheduleSession.haveBookingClass(flightScheduleId);
    }

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

    public NavigationController getNavigationController() {
        return navigationController;
    }

    public void setNavigationController(NavigationController navigationController) {
        this.navigationController = navigationController;
    }

    public FlightScheduleSessionLocal getFlightScheduleSession() {
        return flightScheduleSession;
    }

    public void setFlightScheduleSession(FlightScheduleSessionLocal flightScheduleSession) {
        this.flightScheduleSession = flightScheduleSession;
    }

    public Long getFlightScheduleId() {
        return flightScheduleId;
    }

    public void setFlightScheduleId(Long flightScheduleId) {
        this.flightScheduleId = flightScheduleId;
    }

    public List<FlightSchCabinClsTicFamBookingClsHelper> getFlightSchCabinClsTicFamBookingClsHelpers() {
        return flightSchCabinClsTicFamBookingClsHelpers;
    }

    public void setFlightSchCabinClsTicFamBookingClsHelpers(List<FlightSchCabinClsTicFamBookingClsHelper> flightSchCabinClsTicFamBookingClsHelpers) {
        this.flightSchCabinClsTicFamBookingClsHelpers = flightSchCabinClsTicFamBookingClsHelpers;
    }

    public float getNewDemandMean() {
        return newDemandMean;
    }

    public void setNewDemandMean(float newDemandMean) {
        this.newDemandMean = newDemandMean;
    }

    public float getNewDemandDev() {
        return newDemandDev;
    }

    public void setNewDemandDev(float newDemandDev) {
        this.newDemandDev = newDemandDev;
    }

    public String getCabinClassType() {
        return cabinClassType;
    }

    public void setCabinClassType(String cabinClassType) {
        this.cabinClassType = cabinClassType;
    }

    public String getBookingClassName() {
        return bookingClassName;
    }

    public void setBookingClassName(String bookingClassName) {
        this.bookingClassName = bookingClassName;
    }

    public List<FlightScheduleBookingClassHelper> getFlightScheduleBookingClassHelpers() {
        return flightScheduleBookingClassHelpers;
    }

    public void setFlightScheduleBookingClassHelpers(List<FlightScheduleBookingClassHelper> flightScheduleBookingClassHelpers) {
        this.flightScheduleBookingClassHelpers = flightScheduleBookingClassHelpers;
    }

    public List<SeatClassHelper> getSeatClassHelpers() {
        return seatClassHelpers;
    }

    public void setSeatClassHelpers(List<SeatClassHelper> seatClassHelpers) {
        this.seatClassHelpers = seatClassHelpers;
    }

    public FlightSchedule getSelectedFlightSchedule() {
        return selectedFlightSchedule;
    }

    public void setSelectedFlightSchedule(FlightSchedule selectedFlightSchedule) {
        this.selectedFlightSchedule = selectedFlightSchedule;
    }

    public List<TicketFamilyBookingClassHelper> getTicketFamilyBookingClassHelpers() {
        return ticketFamilyBookingClassHelpers;
    }

    public void setTicketFamilyBookingClassHelpers(List<TicketFamilyBookingClassHelper> ticketFamilyBookingClassHelpers) {
        this.ticketFamilyBookingClassHelpers = ticketFamilyBookingClassHelpers;
    }

    public List<CabinClassTicketFamilyHelper> getCabinClassTicketFamilyHelpers() {
        return cabinClassTicketFamilyHelpers;
    }

    public void setCabinClassTicketFamilyHelpers(List<CabinClassTicketFamilyHelper> cabinClassTicketFamilyHelpers) {
        this.cabinClassTicketFamilyHelpers = cabinClassTicketFamilyHelpers;
    }

    public List<BookingClassHelper> getBookingClassHelpers() {
        return bookingClassHelpers;
    }

    public void setBookingClassHelpers(List<BookingClassHelper> bookingClassHelpers) {
        this.bookingClassHelpers = bookingClassHelpers;
    }

    public PhaseDemand getPhaseDemand() {
        return phaseDemand;
    }

    public void setPhaseDemand(PhaseDemand phaseDemand) {
        this.phaseDemand = phaseDemand;
    }

    public List<SeatAllocationHistory> getAllSeatReAllocationHistorys() {
        return allSeatReAllocationHistorys;
    }

    public void setAllSeatReAllocationHistorys(List<SeatAllocationHistory> allSeatReAllocationHistorys) {
        this.allSeatReAllocationHistorys = allSeatReAllocationHistorys;
    }

    public PhaseDemand getSelectedPhaseDemand() {
        return selectedPhaseDemand;
    }

    public void setSelectedPhaseDemand(PhaseDemand selectedPhaseDemand) {
        this.selectedPhaseDemand = selectedPhaseDemand;
    }

    public List<PhaseDemand> getPhaseDemands() {
        return phaseDemands;
    }

    public void setPhaseDemands(List<PhaseDemand> phaseDemands) {
        this.phaseDemands = phaseDemands;
    }

    public float getDaysBeforeDeparture() {
        return daysBeforeDeparture;
    }

    public void setDaysBeforeDeparture(float daysBeforeDeparture) {
        this.daysBeforeDeparture = daysBeforeDeparture;
    }

}
