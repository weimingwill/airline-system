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
import ams.ais.util.exception.NeedBookingClassException;
import ams.ais.util.helper.BookingClassHelper;
import ams.ais.util.helper.CabinClassTicketFamilyHelper;
import ams.ais.util.helper.FlightSchCabinClsTicFamBookingClsHelper;
import ams.ais.util.helper.FlightScheduleBookingClassHelper;
import ams.ais.util.helper.SeatClassHelper;
import ams.ais.util.helper.TicketFamilyBookingClassHelper;
import ams.aps.entity.AircraftCabinClass;
import ams.aps.entity.FlightSchedule;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import ams.aps.util.exception.NoSuchFlightScheduleBookingClassException;
import ams.aps.util.helper.ApsMessage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;

/**
 *
 * @author Tongtong
 */
@Named(value = "yieldManagementController")
@ViewScoped
public class YieldManagementController implements Serializable{

    @Inject
    private MsgController msgController;

    @Inject
    private NavigationController navigationController;
    
    @Inject 
    private SeatReallocationController seatReallocationController;

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
     * Creates a new instance of YieldManagementController
     */
    public YieldManagementController() {
    }

    @PostConstruct
    public void init() {

        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        this.flightScheduleId = (Long) sessionMap.get("flightScheduleId");
        System.out.println("Initialize Seat Reallocation Controller: ");
        System.out.println("FlightScheduleId: " + flightScheduleId);
        initialHelper();
        System.out.println("Helper: " + flightSchCabinClsTicFamBookingClsHelpers);
        List<CabinClass> ccs = new ArrayList<>();
        for (FlightSchCabinClsTicFamBookingClsHelper fscctfbch : flightSchCabinClsTicFamBookingClsHelpers) {
            ccs.add(fscctfbch.getCabinClass());
        }
        cabinClasses = ccs;

    }

    private void initialHelper() {
        flightSchCabinClsTicFamBookingClsHelpers = flightScheduleSession.getFlightSchCabinClsTicFamBookingClsHelpers(flightScheduleId);
    }

    public String toReallocateBookingClassSeats() {
        seatReallocationController.setFlightScheduleId(flightScheduleId);
        seatReallocationController.setBookingClass(bookingClass);
        flightSchCabinClsTicFamBookingClsHelpers = flightScheduleSession.getFlightSchCabinClsTicFamBookingClsHelpers(flightScheduleId);
        if (flightSchCabinClsTicFamBookingClsHelpers != null) {
            return navigationController.redirectToReallocationBookingClassSeats();
        }
        msgController.addErrorMessage(ApsMessage.HAVE_NOT_SELECT_FLIGHTSCHEDULE_WARNING);
        return "";
    }

    public String toViewSeatReallocationHistory() throws NoSuchFlightSchedulException, NoSuchFlightScheduleBookingClassException, NeedBookingClassException {
        flightSchCabinClsTicFamBookingClsHelpers = flightScheduleSession.getFlightSchCabinClsTicFamBookingClsHelpers(flightScheduleId);
        flightScheduleBookingClass = flightScheduleSession.getFlightScheduleBookingClass(flightScheduleId, bookingClass.getBookingClassId());
        if (flightSchCabinClsTicFamBookingClsHelpers != null && flightScheduleBookingClass != null) {
            allSeatReAllocationHistorys = seatReallocationSession.getBookingClassSeatAllocationHistory(flightScheduleBookingClass);
            seatReallocationController.setAllSeatReAllocationHistorys(allSeatReAllocationHistorys);
            return navigationController.redirectToViewSeatsReallocationHistroy();
        }

        msgController.addErrorMessage(ApsMessage.HAVE_NOT_SELECT_FLIGHTSCHEDULE_WARNING);
        return "";
    }

    public String toUpdateYieldManagementModel() throws NoSuchFlightScheduleBookingClassException {
        flightSchCabinClsTicFamBookingClsHelpers = flightScheduleSession.getFlightSchCabinClsTicFamBookingClsHelpers(flightScheduleId);
        flightScheduleBookingClass = flightScheduleSession.getFlightScheduleBookingClass(flightScheduleId, bookingClass.getBookingClassId());
        phaseDemands = flightScheduleBookingClass.getPhaseDemands();
        
        seatReallocationController.setPhaseDemands(phaseDemands);
        seatReallocationController.setFlightScheduleBookingClass(flightScheduleBookingClass);

        if (flightSchCabinClsTicFamBookingClsHelpers != null) {
            return navigationController.redirectToUpdateYieldManagementModel();
        }
        msgController.addErrorMessage(ApsMessage.HAVE_NOT_SELECT_FLIGHTSCHEDULE_WARNING);
        return "";
    }

    public void onCabinClassChange() {
        for (FlightSchCabinClsTicFamBookingClsHelper fscctfbch : flightSchCabinClsTicFamBookingClsHelpers) {
            if (fscctfbch.getCabinClass().equals(cabinClass)) {
                List<TicketFamilyBookingClassHelper> tfbchs = new ArrayList<>();
                tfbchs = fscctfbch.getTicketFamilyBookingClassHelpers();
                List<TicketFamily> tfs = new ArrayList<>();
                for (TicketFamilyBookingClassHelper tfbch : tfbchs) {
                    tfs.add(tfbch.getTicketFamily());
                }

                ticketFamilys = tfs;
                ticketFamilyBookingClassHelpers = tfbchs;

            }
        }

    }

    public void onTicketFamilyChange() throws NoSuchFlightScheduleBookingClassException {
        for (TicketFamilyBookingClassHelper tfbch : ticketFamilyBookingClassHelpers) {
            if (tfbch.getTicketFamily().equals(ticketFamily)) {
                List<BookingClassHelper> bchs = new ArrayList<>();
                bchs = tfbch.getBookingClassHelpers();
                List<BookingClass> bcs = new ArrayList<>();
                for (BookingClassHelper bch : bchs) {
                    bcs.add(bch.getBookingClass());
                }
                bookingClasses = bcs;
                System.out.println("bookingClasses size" + bookingClasses.size());

            }
        }

    }

    public void onBookingClassChange() throws NoSuchFlightScheduleBookingClassException {

        flightScheduleBookingClass = flightScheduleSession.getFlightScheduleBookingClass(flightScheduleId, bookingClass.getBookingClassId());

        System.out.println("flight schedule booking class id:" + flightScheduleBookingClass.getFlightScheduleBookingClassId().getBookingClassId());
        System.out.println("flight schedule booking class booking class id" + flightScheduleBookingClass.getFlightScheduleBookingClassId().getBookingClassId());
    }

    public MsgController getMsgController() {
        return msgController;
    }

    public void setMsgController(MsgController msgController) {
        this.msgController = msgController;
    }

    public NavigationController getNavigationController() {
        return navigationController;
    }

    public void setNavigationController(NavigationController navigationController) {
        this.navigationController = navigationController;
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

    public FlightScheduleSessionLocal getFlightScheduleSession() {
        return flightScheduleSession;
    }

    public void setFlightScheduleSession(FlightScheduleSessionLocal flightScheduleSession) {
        this.flightScheduleSession = flightScheduleSession;
    }

    public FlightSchedule getFlightSchedule() {
        return flightSchedule;
    }

    public void setFlightSchedule(FlightSchedule flightSchedule) {
        this.flightSchedule = flightSchedule;
    }

    public CabinClass getCabinClass() {
        return cabinClass;
    }

    public void setCabinClass(CabinClass cabinClass) {
        this.cabinClass = cabinClass;
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

    public float getDaysBeforeDeparture() {
        return daysBeforeDeparture;
    }

    public void setDaysBeforeDeparture(float daysBeforeDeparture) {
        this.daysBeforeDeparture = daysBeforeDeparture;
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
    
    

}
