/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.crm;

import ams.aps.session.RoutePlanningSessionLocal;
import ams.ars.entity.AirTicket;
import ams.ars.entity.Booking;
import ams.crm.entity.RegCust;
import ams.crm.session.BookingSessionLocal;
import ams.crm.session.CustomerExSessionLocal;
import ams.crm.util.exception.NoSuchBookingException;
import ams.crm.util.exception.NoSuchRegCustException;
import ams.crm.util.helper.CrmMsg;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.common.EmailController;

/**
 *
 * @author Bowen
 */
@Named(value = "viewBookingBacking")
@ViewScoped
public class ViewBookingBacking implements Serializable {

    /**
     * Creates a new instance of ViewBookingBacking
     */
    @Inject
    MsgController msgController;
    @Inject
    private CustomerController customerController;
    @Inject
    private EmailController emailController;

    @EJB
    private CustomerExSessionLocal customerExSession;
    @EJB
    private BookingSessionLocal bookingSession;
    @EJB
    private RoutePlanningSessionLocal routePlanningSession;

    private String email;
    private RegCust regCust;
    private Booking selectedBooking;
    private Double actualPointClaim;
    private Double actualDistance = 0.0;
    private String bookingReferenceNo;
    private List<AirTicket> airtickets;

    @PostConstruct
    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        this.email = (String) sessionMap.get("email");
        initializeCustomer();
    }

    public void initializeCustomer() {
        try {
            regCust = getRegCustByEmail();
        } catch (NoSuchRegCustException ex) {
            email = null;
        }
    }

    public Booking checkBookingReference() {

        try {
            selectedBooking = bookingSession.getUnClaimedBookingByBookingRef(bookingReferenceNo);
            actualDistance = customerExSession.calcCustValue(selectedBooking, regCust);
//            mileCalculation();
        } catch (NoSuchBookingException ex) {
            msgController.addErrorMessage("Please input a valid booking reference. Please check if you have claim the miles before");
            Logger.getLogger(ViewBookingBacking.class.getName()).log(Level.SEVERE, null, ex);
        }

        return selectedBooking;
    }

    public void claimMiles() throws NoSuchBookingException {

        try {
            customerController.claimMiles();
        } catch (NoSuchRegCustException ex) {
            Logger.getLogger(RedeemMilesBacking.class.getName()).log(Level.SEVERE, null, ex);
        }
        msgController.addMessage("Congratulations! You have successfully claim your points!");
        DecimalFormat fm = new DecimalFormat();
        fm.setMaximumFractionDigits(2);
        String subject = "[Important] Merlion Airline Receive your redemption Code";
        String mailContent = "Dear Customer:\n\n You have successfully received " + fm.format(actualPointClaim) + " points on your accounts. \n For more information, Please visit our website.\n\n Hope we can see you again! \n\n Best Regards,\n Merlion Airline";
        String receiver = customerController.getEmail();
        emailController.sendEmail(subject, mailContent, receiver);

    }

//    public void mileCalculation() {
//
////        actualDistance = routePlanningSession.distance(arrAirport, deptAirport);
//        airtickets = selectedBooking.getAirTickets();
//
//        if (airtickets == null) {
//            msgController.addErrorMessage("No Airtickets related to this booking");
//        } else {
//            for (AirTicket at : airtickets) {
//                actualDistance = actualDistance + routePlanningSession.distance(at.getFlightSchedBookingClass().getFlightSchedule().getLeg().getDepartAirport(), at.getFlightSchedBookingClass().getFlightSchedule().getLeg().getArrivalAirport());
//            }
//        }
//        actualDistance = actualDistance * 0.000621371;
//        actualPointClaim = actualDistance / 8;
//        if (regCust.getMembership().getName().equals("Elite Bronze")) {
//            actualPointClaim = actualPointClaim * 1;
//        } else if (regCust.getMembership().getName().equals("Elite Silver")) {
//            actualPointClaim = actualPointClaim * 1.1;
//        } else if (regCust.getMembership().getName().equals("Elite Gold")) {
//            actualPointClaim = actualPointClaim * 1.2;
//        }
//        System.out.println("calculated mile is" + actualPointClaim);
//    }

    public Double getActualDistance() {
        return actualDistance;
    }

    public void setActualDistance(Double actualDistance) {
        this.actualDistance = actualDistance;
    }

    public List<Booking> getBookingByEmail() {

        System.out.print("Booking Email is" + email);
        return bookingSession.getBookingsByEmail(email);
    }

    public List<Booking> getCurrentBookingByEmail() {
        System.out.print("Booking Email is" + email);
        return bookingSession.getCurrentBookingsByEmail(email);
    }

    public List<Booking> getUnClaimedBookingsByEmail() {
        return bookingSession.getUnClaimedBookingsByEmail(email);
    }

    public RegCust getRegCustByEmail() throws NoSuchRegCustException {
        return customerExSession.getRegCustByEmail(email);
    }

    public ViewBookingBacking() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RegCust getRegCust() {
        return regCust;
    }

    public void setRegCust(RegCust regCust) {
        this.regCust = regCust;
    }

    public Booking getSelectedBooking() {
        System.out.print("selectedbooking is" + selectedBooking);
        return selectedBooking;
    }

    public void setSelectedBooking(Booking selectedBooking) {
        this.selectedBooking = selectedBooking;
    }

    public Double getActualPointClaim() {
        return actualPointClaim;
    }

    public void setActualPointClaim(Double actualPointClaim) {
        this.actualPointClaim = actualPointClaim;
    }

    public String getBookingReferenceNo() {
        return bookingReferenceNo;
    }

    public void setBookingReferenceNo(String bookingReferenceNo) {
        this.bookingReferenceNo = bookingReferenceNo;
    }

    public List<AirTicket> getAirtickets() {
        return airtickets;
    }

    public void setAirtickets(List<AirTicket> airtickets) {
        this.airtickets = airtickets;
    }
}
