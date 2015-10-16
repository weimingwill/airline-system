/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.ais;

import ams.ais.entity.BookingClass;
import ams.ais.entity.TicketFamily;
import ams.ais.session.BookingClassSessionLocal;
import ams.ais.util.exception.ExistSuchBookingClassNameException;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.helper.FlightSchCabinClsTicFamBookingClsHelper;
import ams.ais.session.FlightScheduleSessionLocal;
import ams.ais.session.SeatReallocationSessionLocal;
import ams.ais.session.TicketFamilySessionLocal;
import ams.ais.util.exception.DuplicatePriceException;
import ams.ais.util.exception.NeedBookingClassException;
import ams.ais.util.exception.WrongSumOfBookingClassSeatQtyException;
import ams.ais.util.exception.WrongSumOfTicketFamilySeatQtyException;
import ams.ais.util.helper.BookingClassHelper;
import ams.ais.util.helper.TicketFamilyBookingClassHelper;
import ams.aps.util.exception.NoSuchAircraftCabinClassException;
import ams.aps.util.exception.NoSuchAircraftException;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import ams.aps.util.exception.NoSuchFlightScheduleBookingClassException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.inject.Named;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;

@Named(value = "bookingClassController")
@ViewScoped
public class BookingClassController implements Serializable {

    @Inject
    private MsgController msgController;
    @Inject
    private NavigationController navigationController;
    @Inject
    private SeatReallocationController seatReallocationController;

    @EJB
    private BookingClassSessionLocal bookingClassSession;
    @EJB
    private FlightScheduleSessionLocal flightScheduleSession;
    @EJB
    private SeatReallocationSessionLocal seatReallocationSession;
    @EJB
    private TicketFamilySessionLocal ticketFamilySession;

    private String bookingClassName;
    private Long flightScheduleId;
    private List<FlightSchCabinClsTicFamBookingClsHelper> flightSchCabinClsTicFamBookingClsHelpers;
    private float basicPrice;
    private BookingClass selectedBookingClass;
    private TicketFamily selectedTicketFamily;
    private Map<Long, Float> priceMap = new HashMap<>();
    private Map<Long, Float> priceCoefficientMap = new HashMap<>();
    private String useless = null;

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
        initialPrice();
        System.out.println("Helper: " + flightSchCabinClsTicFamBookingClsHelpers);
    }

    public BookingClassController() {
    }

    public List<TicketFamily> getAllTicketFamily() {
        return ticketFamilySession.getAllTicketFamily();

    }

    public List<BookingClass> getAllBookingClasses() {
        return bookingClassSession.getAllBookingClasses();
    }

    public String createBookingClass() {
        try {
            bookingClassSession.createBookingClass(bookingClassName, selectedTicketFamily);
            msgController.addMessage("Create booking class successfully!");
        } catch (ExistSuchBookingClassNameException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }

        return navigationController.redirectToCreateBookingClass();
    }

    public String deleteBookingClass() {
        try {
            System.out.printf("selected booking class is: " + selectedBookingClass);
            bookingClassSession.deleteBookingClass(selectedBookingClass.getName());
            msgController.addMessage("Booking class is deleted successfully!");
        } catch (NoSuchBookingClassException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToDeleteBookingClass();
    }

    public String assignFlightScheduleBookingClass() {
        try {
            flightScheduleSession.assignFlightScheduleBookingClass(flightScheduleId, flightSchCabinClsTicFamBookingClsHelpers);
            msgController.addMessage("assign flight schedule booking class succesffully!");

            return navigationController.redirectToViewFlightSchedule();
        } catch (NoSuchFlightSchedulException | NoSuchFlightScheduleBookingClassException | NeedBookingClassException ex) {
            msgController.addErrorMessage(ex.getMessage());
            return "";
        }
    }

    public String allocateSeats() {
        try {
            bookingClassSession.allocateSeats(flightScheduleId, flightSchCabinClsTicFamBookingClsHelpers);
            msgController.addMessage("Allocate seats succesfully!");
        } catch (NoSuchAircraftCabinClassException | NoSuchAircraftException | NoSuchFlightScheduleBookingClassException | WrongSumOfBookingClassSeatQtyException | WrongSumOfTicketFamilySeatQtyException ex) {
            msgController.addErrorMessage(ex.getMessage());
            return "";
        }
        return navigationController.redirectToViewFlightSchedule();
    }

    public String priceBookingClasses() {
        try {
            bookingClassSession.priceBookingClasses(flightScheduleId, flightSchCabinClsTicFamBookingClsHelpers, priceMap);
            List<Date> checkPoints = new ArrayList<>();

            checkPoints = seatReallocationSession.yieldManagement(flightScheduleId);
            sendYieldMgtReminder(checkPoints);

            msgController.addMessage("Price booking class succesfully!");
        } catch (NoSuchFlightScheduleBookingClassException | DuplicatePriceException ex) {
            msgController.addErrorMessage(ex.getMessage());
            return "";
        }

        return navigationController.redirectToViewFlightSchedule();
    }

    private void sendYieldMgtReminder(List<Date> checkDates) {
        Timer[] timers = new Timer[checkDates.size()];
        int count = 0;
        for (Date date : checkDates) {
            timers[count] = new Timer();
            timers[count].schedule(new YieldMgtTask(), date);
            count++;

        }

    }

    class YieldMgtTask extends TimerTask {

        @Override
        public void run() {
            msgController.sendReminder("It's time to perform yield management for flight schedule " + flightScheduleId);
        }

    }

    public void onPriceCoefficientChange(AjaxBehaviorEvent event) {
        String id = (String) ((UIComponent) event.getComponent()).getId();
        float priceCoefficient = (float) ((UIComponent) event.getComponent()).getAttributes().get("value");
        Long bookingClassId = Long.parseLong(id.split("priceCoefficient")[1]);
        priceMap.put(bookingClassId, getCalculatedPrice(priceCoefficient));
    }

    public void initialPrice() {
        if (!flightSchCabinClsTicFamBookingClsHelpers.isEmpty()) {
            for (FlightSchCabinClsTicFamBookingClsHelper helper : flightSchCabinClsTicFamBookingClsHelpers) {
                for (TicketFamilyBookingClassHelper tfbcHelper : helper.getTicketFamilyBookingClassHelpers()) {
                    for (BookingClassHelper bcHelper : tfbcHelper.getBookingClassHelpers()) {
                        if (bcHelper != null) {
                            priceMap.put(bcHelper.getBookingClass().getBookingClassId(), bcHelper.getPrice());
                            priceCoefficientMap.put(bcHelper.getBookingClass().getBookingClassId(), bcHelper.getPriceCoefficient());
                            basicPrice = bcHelper.getBasicPrice();
                        }
                    }
                }
            }
        }
    }

    private void initialHelper() {
        flightSchCabinClsTicFamBookingClsHelpers = flightScheduleSession.getFlightSchCabinClsTicFamBookingClsHelpers(flightScheduleId);
    }

    public float getCalculatedPrice(float priceCoefficient) {
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

    public BookingClass getSelectedBookingClass() {
        return selectedBookingClass;
    }

    public void setSelectedBookingClass(BookingClass selectedBookingClass) {
        this.selectedBookingClass = selectedBookingClass;
    }

    public TicketFamily getSelectedTicketFamily() {
        return selectedTicketFamily;
    }

    public void setSelectedTicketFamily(TicketFamily selectedTicketFamily) {
        this.selectedTicketFamily = selectedTicketFamily;
    }

    public Map<Long, Float> getPriceMap() {
        return priceMap;
    }

    public void setPriceMap(Map<Long, Float> priceMap) {
        this.priceMap = priceMap;
    }

    public Map<Long, Float> getPriceCoefficientMap() {
        return priceCoefficientMap;
    }

    public void setPriceCoefficientMap(Map<Long, Float> priceCoefficientMap) {
        this.priceCoefficientMap = priceCoefficientMap;
    }

    public String getUseless() {
        return useless;
    }

    public void setUseless(String useless) {
        this.useless = useless;
    }
}
