/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import ams.ais.entity.BookingClass;
import ams.ais.entity.CabinClass;
import ams.ais.entity.TicketFamily;
import ams.ais.session.CabinClassSessionLocal;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.ais.util.helper.FlightSchCabinClsTicFamBookingClsHelper;
import ams.ais.util.helper.FlightScheduleBookingClassHelper;
import ams.ais.util.helper.SeatClassHelper;
import ams.ais.util.helper.TicketFamilyBookingClassHelper;
import ams.aps.entity.Aircraft;
import ams.aps.entity.FlightSchedule;
import ams.aps.session.FlightScheduleSessionLocal;
import ams.aps.util.exception.NoSuchAircraftCabinClassException;
import ams.aps.util.exception.NoSuchAircraftException;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;
import mas.util.helper.SafeHelper;
import org.primefaces.context.RequestContext;

/**
 *
 * @author winga_000
 */
@Named(value = "flightScheduleController")
@SessionScoped
public class FlightScheduleController implements Serializable {

    @Inject
    private NavigationController navigationController;
    @Inject
    private MsgController msgController;

    @EJB
    private FlightScheduleSessionLocal flightScheduleSession;
    @EJB
    private CabinClassSessionLocal cabinClassSession;
    private Long flightScheduleId;
    private String cabinClassType;
    private String bookingClassName;
    private List<FlightScheduleBookingClassHelper> flightScheduleBookingClassHelpers;
    private List<SeatClassHelper> seatClassHelpers;
    private FlightSchedule selectedFlightSchedule;
    private List<TicketFamilyBookingClassHelper> ticketFamilyBookingClassHelpers;
    
    public FlightScheduleController() {
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

    public String toAddFlightScheduleBookingClass() {
        if (selectedFlightSchedule != null) {
            flightScheduleId = selectedFlightSchedule.getFlightScheduleId();
            int i = 1;
            flightScheduleBookingClassHelpers = new ArrayList<>();
            seatClassHelpers = new ArrayList<>();
            Aircraft aircraft;
            try {
                aircraft = flightScheduleSession.getFlightScheduleAircraft(flightScheduleId);
                for (CabinClass cabinClass : SafeHelper.emptyIfNull(getFlightScheduleCabinClasses())) {

                    FlightScheduleBookingClassHelper flightScheduleBookingClassHelper;
                    if (flightScheduleSession.haveBookingClass(flightScheduleId)) {
                        //initialize flightScheduleBookingClassHelpers if fligtSchedule have booking classes
                        List<TicketFamily> ticketFamilys;
                        try {
                            ticketFamilys = cabinClassSession.getCabinClassTicketFamilyFromCTJoinTable(aircraft.getAircraftId(), cabinClass.getCabinClassId());
                        } catch (NoSuchTicketFamilyException e) {
                            ticketFamilys = null;
                        }
                        flightScheduleBookingClassHelper = new FlightScheduleBookingClassHelper(i, cabinClass, ticketFamilys);

                        //initialize seatClassHelpers
                        for (TicketFamily ticketFamily : ticketFamilys) {
                            List<BookingClass> bookingClasses = new ArrayList<>();
                            try {
                                bookingClasses = flightScheduleSession.getFlightScheduleBookingClassesOfTicketFamily(flightScheduleId, ticketFamily.getTicketFamilyId());
                                System.out.println("BookingClass: " + bookingClasses);
                            } catch (NoSuchBookingClassException e) {
                                bookingClasses = null;
                            }
                            SeatClassHelper seatClassHelper = new SeatClassHelper(cabinClass.getName(), ticketFamily.getName(), bookingClasses);
                            seatClassHelpers.add(seatClassHelper);
                            System.out.println("SeatClassHelper: " + seatClassHelper.getCabinClassName() + ":" + seatClassHelper.getTicketFamilyName()
                            + ":" + seatClassHelper.getBookingClasses());
                        }
                        System.out.println("SeatClassHelpers: " + seatClassHelpers);
                    } else {
                        flightScheduleBookingClassHelper = new FlightScheduleBookingClassHelper(i, cabinClass);
                    }
                    try {
                        if (cabinClassSession.getCabinClassTicketFamilys(cabinClass.getType()).isEmpty()) {
                            flightScheduleBookingClassHelper.setHaveTicketFamily(true);
                        }
                    } catch (NoSuchTicketFamilyException e) {
                        flightScheduleBookingClassHelper.setHaveTicketFamily(false);
                    }
                    i++;
                    flightScheduleBookingClassHelpers.add(flightScheduleBookingClassHelper);
                }
                return navigationController.redirectToAddFlightScheduleBookingClass();
            } catch (NoSuchAircraftException ex) {
                msgController.addErrorMessage(ex.getMessage());
                return "";
            }
        }
        msgController.addErrorMessage("Please select a flight schedule");
        return "";
    }

    public void onTicketFamilyChange() {
        System.out.println("On Ticket Family Change");
        seatClassHelpers = new ArrayList<>();
        for (FlightScheduleBookingClassHelper fsbc : SafeHelper.emptyIfNull(flightScheduleBookingClassHelpers)) {
            for (TicketFamily ticketFamily : SafeHelper.emptyIfNull(fsbc.getTicketFamilys())) {
                List<BookingClass> bookingClasses = new ArrayList<>();
//                System.out.println("CabinClass: " + fsbc.getCabinClass().getName() + ". TicketFamily: " + ticketFamily.getName());
                SeatClassHelper seatClassHelper = new SeatClassHelper(fsbc.getCabinClass().getName(), ticketFamily.getName(), bookingClasses);
                System.out.println("CabinClass: " + seatClassHelper.getCabinClassName() + ". TicketFamily: " + seatClassHelper.getTicketFamilyName());
                seatClassHelpers.add(seatClassHelper);
            }
        }
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

    public String addBookingClass(String method) {
        try {
            flightScheduleSession.addBookingClass(flightScheduleId, flightScheduleBookingClassHelpers, seatClassHelpers, method);
            msgController.addMessage("Add booking class succesffully!");
            return navigationController.redirectToViewFlightSchedule();
        } catch (NoSuchAircraftCabinClassException | NoSuchTicketFamilyException | NoSuchAircraftException 
                | NoSuchCabinClassException | NoSuchFlightSchedulException ex) {
            msgController.addErrorMessage(ex.getMessage());
            return navigationController.redirectToAddFlightScheduleBookingClass();
        }
    }
    
    public boolean haveBookingClass(){
        return flightScheduleSession.haveBookingClass(flightScheduleId);
    }
    
    //Getter and setter
    public Long getFlightScheduleId() {
        return flightScheduleId;
    }

    public void setFlightScheduleId(Long flightScheduleId) {
        this.flightScheduleId = flightScheduleId;
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


}
