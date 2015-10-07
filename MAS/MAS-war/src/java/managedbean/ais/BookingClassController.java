/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.ais;

import ams.ais.session.BookingClassSessionLocal;
import ams.ais.util.exception.ExistSuchBookingClassNameException;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.helper.FlightSchCabinClsTicFamBookingClsHelper;
import ams.ais.session.FlightScheduleSessionLocal;
import ams.ais.util.helper.BookingClassHelper;
import ams.ais.util.helper.TicketFamilyBookingClassHelper;
import ams.aps.util.exception.NoSuchAircraftCabinClassException;
import ams.aps.util.exception.NoSuchAircraftException;
import ams.aps.util.exception.NoSuchFlightScheduleBookingClassException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;

/**
 *
 * @author Tongtong
 */
@Named(value = "bookingClassController")
@RequestScoped
public class BookingClassController implements Serializable {

    @Inject
    private MsgController msgController;
    @Inject
    private NavigationController navigationController;

    @EJB
    private BookingClassSessionLocal bookingClassSession;
    @EJB
    private FlightScheduleSessionLocal flightScheduleSession;

    private String bookingClassName;
    private Long flightScheduleId;
    private List<FlightSchCabinClsTicFamBookingClsHelper> flightSchCabinClsTicFamBookingClsHelpers;
    private float basicPrice;

    /**
     * Creates a new instance of BookingClassController
     */
    @PostConstruct
    public void Init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        this.flightScheduleId = (Long) sessionMap.get("flightScheduleId");
        System.out.println("Initialize Booking Class Controler: ");
        System.out.println("FlightScheduleId: " + flightScheduleId);
        initialHelper();
        initialBasicPrice();
        System.out.println("Helper: " + flightSchCabinClsTicFamBookingClsHelpers);
    }

    public BookingClassController() {
    }

    public String createBookingClass() {
        try {
            bookingClassSession.createBookingClass(bookingClassName);
            msgController.addMessage("Create booking class successfully!");
        } catch (ExistSuchBookingClassNameException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToCreateBookingClass();
    }

    public String deleteBookingClass() {
        try {
            bookingClassSession.deleteBookingClass(bookingClassName);
            msgController.addMessage("Booking class is deleted successfully!");
        } catch (NoSuchBookingClassException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToDeleteBookingClass();
    }

    public String allocateSeats() {
        try {
            bookingClassSession.allocateSeats(flightScheduleId, flightSchCabinClsTicFamBookingClsHelpers);
            msgController.addMessage("Allocate seats succesfully!");
        } catch (NoSuchAircraftCabinClassException | NoSuchAircraftException | NoSuchFlightScheduleBookingClassException ex) {
            msgController.addErrorMessage(ex.getMessage());
            return "";
        }
        return navigationController.redirectToViewFlightSchedule();
    }

    public String priceBookingClasses() {
        try {
            bookingClassSession.priceBookingClasses(flightScheduleId, flightSchCabinClsTicFamBookingClsHelpers);
            msgController.addMessage("Price booking class succesfully!");
        } catch (NoSuchFlightScheduleBookingClassException ex) {
            msgController.addErrorMessage(ex.getMessage());
            return "";
        }
        return navigationController.redirectToViewFlightSchedule();
    }

    public float initialBasicPrice() {
        if (!flightSchCabinClsTicFamBookingClsHelpers.isEmpty()) {
            for (FlightSchCabinClsTicFamBookingClsHelper helper : flightSchCabinClsTicFamBookingClsHelpers) {
                for (TicketFamilyBookingClassHelper tfbcHelper : helper.getTicketFamilyBookingClassHelpers()) {
                    for (BookingClassHelper bcHelper : tfbcHelper.getBookingClassHelpers()) {
                        basicPrice = bcHelper.getBasicPrice();
                        break;
//                        if (bcHelper.getPrice() == 0 && bcHelper.getPriceCoefficient() == 0) {
//                            basicPrice = 0;
//                        } else {
//                            basicPrice = bcHelper.getPrice() / bcHelper.getPriceCoefficient();
//                        }
//                        break;
                    }
                }
            }
        }
        return basicPrice;
    }

    private void initialHelper() {
        flightSchCabinClsTicFamBookingClsHelpers = flightScheduleSession.getFlightSchCabinClsTicFamBookingClsHelpers(flightScheduleId);
    }

    public float getCalculatedPrice(float priceCoefficient) {
        System.out.println("Price: " + priceCoefficient * basicPrice);
        return priceCoefficient * basicPrice;
    }

    //Getter and Setter
    public String getBookingClassName() {
        return bookingClassName;
    }

    public void setBookingClassName(String bookingClassName) {
        this.bookingClassName = bookingClassName;
    }

    public List<FlightSchCabinClsTicFamBookingClsHelper> getFlightSchCabinClsTicFamBookingClsHelpers() {
        return flightSchCabinClsTicFamBookingClsHelpers;
    }

    public void setFlightSchCabinClsTicFamBookingClsHelpers(List<FlightSchCabinClsTicFamBookingClsHelper> flightSchCabinClsTicFamBookingClsHelpers) {
        this.flightSchCabinClsTicFamBookingClsHelpers = flightSchCabinClsTicFamBookingClsHelpers;
    }

    public Long getFlightScheduleId() {
        return flightScheduleId;
    }

    public void setFlightScheduleId(Long flightScheduleId) {
        this.flightScheduleId = flightScheduleId;
    }

    public float getBasicPrice() {
        return basicPrice;
    }

    public void setBasicPrice(float basicPrice) {
        this.basicPrice = basicPrice;
    }

}
