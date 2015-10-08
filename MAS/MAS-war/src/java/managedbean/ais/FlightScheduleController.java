/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.ais;

import ams.ais.entity.BookingClass;
import ams.ais.entity.CabinClass;
import ams.ais.session.TicketFamilySessionLocal;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.helper.BookingClassHelper;
import ams.ais.util.helper.CabinClassTicketFamilyHelper;
import ams.ais.util.helper.FlightSchCabinClsTicFamBookingClsHelper;
import ams.ais.util.helper.FlightScheduleBookingClassHelper;
import ams.ais.util.helper.SeatClassHelper;
import ams.ais.util.helper.TicketFamilyBookingClassHelper;
import ams.aps.entity.FlightSchedule;
import ams.ais.session.FlightScheduleSessionLocal;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import ams.aps.util.helper.ApsMessage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;
import org.primefaces.context.RequestContext;

/**
 *
 * @author winga_000
 */
@Named(value = "flightScheduleController")
@ViewScoped
public class FlightScheduleController implements Serializable {

    @Inject
    private NavigationController navigationController;
    @Inject
    private MsgController msgController;

    @EJB
    private FlightScheduleSessionLocal flightScheduleSession;
    @EJB
    private TicketFamilySessionLocal ticketFamilySession;
    private Long flightScheduleId;
    private String cabinClassType;
    private String bookingClassName;
    private List<FlightScheduleBookingClassHelper> flightScheduleBookingClassHelpers;
    private List<SeatClassHelper> seatClassHelpers;
    private FlightSchedule selectedFlightSchedule;
    private List<TicketFamilyBookingClassHelper> ticketFamilyBookingClassHelpers;
    private List<CabinClassTicketFamilyHelper> cabinClassTicketFamilyHelpers;
    private List<FlightSchCabinClsTicFamBookingClsHelper> flightSchCabinClsTicFamBookingClsHelpers;
    private List<BookingClassHelper> bookingClassHelpers;

    public FlightScheduleController() {
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

    public List<FlightSchedule> getAllFlightSchedule() {
        List<FlightSchedule> flightSchedules = new ArrayList<>();
        try {
            flightSchedules = flightScheduleSession.getAllFilghtSchedules();
        } catch (NoSuchFlightSchedulException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return flightSchedules;
    }

    public String toAssignFlightScheduleBookingClass() {
        if (selectedFlightSchedule != null) {
            flightScheduleId = selectedFlightSchedule.getFlightScheduleId();
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            Map<String, Object> sessionMap = externalContext.getSessionMap();
            sessionMap.put("flightScheduleId", flightScheduleId);
            return navigationController.redirectToAssignFlightScheduleBookingClass();
        }
        msgController.addErrorMessage(ApsMessage.HAVE_NOT_SELECT_FLIGHTSCHEDULE_WARNING);
        return "";
    }

    public String toSeatAllocation() {
        if (selectedFlightSchedule != null) {
            flightScheduleId = selectedFlightSchedule.getFlightScheduleId();
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            Map<String, Object> sessionMap = externalContext.getSessionMap();
            sessionMap.put("flightScheduleId", flightScheduleId);
            return navigationController.redirectToSeatAllocation();
        }
        msgController.addErrorMessage(ApsMessage.HAVE_NOT_SELECT_FLIGHTSCHEDULE_WARNING);
        return "";
    }

    public String toPriceBookingClasses() {
        if (selectedFlightSchedule != null) {
            flightScheduleId = selectedFlightSchedule.getFlightScheduleId();
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            Map<String, Object> sessionMap = externalContext.getSessionMap();
            sessionMap.put("flightScheduleId", flightScheduleId);
            return navigationController.redirectToPriceBookingClasses();
        }
        msgController.addErrorMessage(ApsMessage.HAVE_NOT_SELECT_FLIGHTSCHEDULE_WARNING);
        return "";
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
        System.out.println("FlightScheduleId : " + selectedFlightSchedule);
        if (selectedFlightSchedule != null) {
            flightSchCabinClsTicFamBookingClsHelpers = flightScheduleSession.getFlightSchCabinClsTicFamBookingClsHelpers(selectedFlightSchedule.getFlightScheduleId());
//            RequestContext context = RequestContext.getCurrentInstance();
//            context.update(":viewBookingClassForm:viewBookingClass");
//            context.execute("PF('flightScheduleBookingClassDialog').show()");
        } else {
            msgController.addErrorMessage(ApsMessage.HAVE_NOT_SELECT_FLIGHTSCHEDULE_WARNING);
        }
    }

    public boolean haveBookingClass() {
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

    public List<CabinClassTicketFamilyHelper> getCabinClassTicketFamilyHelpers() {
        return cabinClassTicketFamilyHelpers;
    }

    public void setCabinClassTicketFamilyHelpers(List<CabinClassTicketFamilyHelper> cabinClassTicketFamilyHelpers) {
        this.cabinClassTicketFamilyHelpers = cabinClassTicketFamilyHelpers;
    }

    public List<FlightSchCabinClsTicFamBookingClsHelper> getFlightSchCabinClsTicFamBookingClsHelpers() {
        return flightSchCabinClsTicFamBookingClsHelpers;
    }

    public void setFlightSchCabinClsTicFamBookingClsHelpers(List<FlightSchCabinClsTicFamBookingClsHelper> flightSchCabinClsTicFamBookingClsHelpers) {
        this.flightSchCabinClsTicFamBookingClsHelpers = flightSchCabinClsTicFamBookingClsHelpers;
    }

    public List<BookingClassHelper> getBookingClassHelpers() {
        return bookingClassHelpers;
    }

    public void setBookingClassHelpers(List<BookingClassHelper> bookingClassHelpers) {
        this.bookingClassHelpers = bookingClassHelpers;
    }

}
