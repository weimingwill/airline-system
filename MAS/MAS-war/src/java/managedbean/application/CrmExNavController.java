/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.application;

import java.io.Serializable;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author weiming
 */
@Named(value = "crmExNavController")
@RequestScoped
public class CrmExNavController implements Serializable {

    private final String REDIRECT = "?faces-redirect=true";
    private final String CRM_URL = "/views/external/unsecured/crm_web/";
    private final String CRM_EX_UNSECURED_URL = "/views/external/unsecured/crm_web/customer_registration/";
    private final String CRM_EX_SECURED_URL = "/views/external/secured/crm_web/customer_management/";

    /**
     * Creates a new instance of ArsNavController
     */
    public CrmExNavController() {
    }

    public String redirectToCrmEx() {
        return CRM_URL + "index.xhtml" + REDIRECT;
    }

    //Flight Booking
    public String redirectToSearchFlightResult() {
        return CRM_URL + "flight_booking/searchResults.xhtml" + REDIRECT;
    }

    public String redirectToLoginBooking() {
        return CRM_URL + "flight_booking/loginBooking.xhtml" + REDIRECT;
    }

    public String redirectToPassengerInfo() {
        return CRM_URL + "flight_booking/passengerInfo.xhtml" + REDIRECT;
    }

    public String redirectToAddOnServices() {
        return CRM_URL + "flight_booking/addOnServices.xhtml" + REDIRECT;
    }

    public String redirectToBookingSummary() {
        return CRM_URL + "flight_booking/bookingSummary.xhtml" + REDIRECT;
    }

    public String redirectToItinerary() {
        return CRM_URL + "flight_booking/itinerary.xhtml" + REDIRECT;
    }

    //Flight status
    public String redirectToSearchFlightStatus() {
        return CRM_URL + "flight_status/searchResults.xhtml" + REDIRECT;
    }

    //Manage booking
    public String redirectToMyBooking() {
        return CRM_URL + "manage_booking/myBooking.xhtml" + REDIRECT;
    }

    // External CRM
    public String redirectToCustomerLogin() {
        return CRM_EX_SECURED_URL + "customerLogin.xhtml" + REDIRECT;
    }

    public String redirectToProfileManagement() {
        return CRM_EX_SECURED_URL + "profileManagement.xhtml" + REDIRECT;
    }

    public String redirectToMilesManagement() {
        return CRM_EX_SECURED_URL + "milesManagement.xhtml" + REDIRECT;
    }

    public String redirectToBookingManagement() {
        return CRM_EX_SECURED_URL + "bookingManagement.xhtml" + REDIRECT + "i=3";
    }

    public String redirectToAccountSummary() {
        return CRM_EX_SECURED_URL + "accountSummary.xhtml" + REDIRECT;
    }

    public String redirectToFeedBack() {
        return CRM_EX_SECURED_URL + "feedback.xhtml" + REDIRECT;
    }

    public String redirectToMainPage() {
        return CRM_EX_SECURED_URL + "customerMainPage.xhtml" + REDIRECT;
    }

    public String redirectToGeneralInformation() {
        return CRM_EX_UNSECURED_URL + "generalInformation.xhtml" + REDIRECT;
    }

    public String redirectToCustomerRegistration() {
        return CRM_EX_UNSECURED_URL + "registration.xhtml" + REDIRECT;
    }

}
