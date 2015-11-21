/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.ars_crm;

import ams.ars.entity.Booking;
import ams.crm.session.BookingSessionLocal;
import ams.crm.util.exception.NoSuchBookingException;
import ams.crm.util.helper.CustomerHelper;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.inject.Inject;
import managedbean.application.CrmExNavController;
import managedbean.application.MsgController;

/**
 *
 * @author weiming
 */
@Named(value = "manageBookingManager")
@SessionScoped
public class ManageBookingManager implements Serializable {

    @EJB
    private BookingSessionLocal bookingSession;

    @Inject
    CrmExNavController crmExNavController;
    @Inject
    MsgController msgController;
    @Inject
    BookingManager bookingManager;

    //Search booking
    private boolean selectBookingRef = true;
    private String searchBy;
    private String bookingRef;
    private String ticketNo;
    private String lastName;

    private Booking booking;
    private List<CustomerHelper> custHelpers;
    private double farePrice;
    private Booking selectThisBooking;

    /**
     * Creates a new instance of ManageBookingManager
     */
    public ManageBookingManager() {
    }

    @PostConstruct
    public void init() {
        searchBy = "bookingRef";
    }

    public void onSearchBySelected() {
        if ("bookingRef".equals(searchBy)) {
            selectBookingRef = true;
        } else {
            selectBookingRef = false;
        }
    }

    public String searchForBooking() {
        try {
            if (selectBookingRef) {
                booking = bookingSession.getBookingByBookingRef(bookingRef);
            } else {
                booking = bookingSession.getBookingByETicketNo(ticketNo);
            }
            custHelpers = bookingManager.setCustomerHelpers(booking);
            farePrice = bookingManager.setFarePrice(booking);
            return crmExNavController.redirectToMyBooking();
        } catch (NoSuchBookingException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return "";
    }

    public String searchForThisBooking() {
        try {
            System.out.print("This select booking is   "+selectThisBooking);
            booking = bookingSession.getBookingByBookingRef(selectThisBooking.getReferenceNo());
            custHelpers = bookingManager.setCustomerHelpers(booking);
            farePrice = bookingManager.setFarePrice(booking);
            return crmExNavController.redirectToMyBooking();
        } catch (NoSuchBookingException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return "";
    }

    //
    //Getter and Setter
    //

    public String getSearchBy() {
        return searchBy;
    }

    public void setSearchBy(String searchBy) {
        this.searchBy = searchBy;
    }

    public String getBookingRef() {
        return bookingRef;
    }

    public void setBookingRef(String bookingRef) {
        this.bookingRef = bookingRef;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isSelectBookingRef() {
        return selectBookingRef;
    }

    public void setSelectBookingRef(boolean selectBookingRef) {
        this.selectBookingRef = selectBookingRef;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public List<CustomerHelper> getCustHelpers() {
        return custHelpers;
    }

    public void setCustHelpers(List<CustomerHelper> custHelpers) {
        this.custHelpers = custHelpers;
    }

    public double getFarePrice() {
        return farePrice;
    }

    public void setFarePrice(double farePrice) {
        this.farePrice = farePrice;
    }

    public Booking getSelectThisBooking() {
        return selectThisBooking;
    }

    public void setSelectThisBooking(Booking selectThisBooking) {
        this.selectThisBooking = selectThisBooking;
    }

}
