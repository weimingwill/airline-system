/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.application;

import java.io.Serializable;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import managedbean.dcs.LuggageManager;
import managedbean.dcs.PassengerManager;

/**
 *
 * @author ChuningLiu
 */
@Named(value = "dcsNavController")
@RequestScoped
public class DcsNavController implements Serializable {

    private final String REDIRECT = "?faces-redirect=true";
    private final String DCS_URL = "/views/internal/secured/dcs/";
    private final String CRM_URL = "/views/external/unsecured/crm_web/";

    @Inject
    private PassengerManager passengerManager;

    @Inject
    private LuggageManager luggageManager;

    /**
     * Creates a new instance of DcsNavController
     */
    public DcsNavController() {
    }

    public String toSearchPassenger() {
        passengerManager.init();
        return DCS_URL + "searchPassenger.xhtml" + REDIRECT;
    }

    public String toCheckInPassenger() {
        String returnString;
        if (getCurrURI().contains(CRM_URL)) {
            returnString = CRM_URL + "online_checkin/checkinOnline.xhtml" + REDIRECT;
        } else {
            returnString = DCS_URL + "checkInPassenger.xhtml" + REDIRECT;
        }
        return returnString;
    }

    public String toSelectSeat() {
        String returnString;
        if (getCurrURI().contains(CRM_URL)) {
            returnString = CRM_URL + "online_checkin/selectSeat.xhtml" + REDIRECT;
        } else {
            returnString = DCS_URL + "selectSeat.xhtml" + REDIRECT;
        }
        return returnString;
    }

    public String toCheckInLuggage() {
        return DCS_URL + "checkInLuggage.xhtml" + REDIRECT;
    }

    public String toPayLuggage() {
        return DCS_URL + "payLuggage.xhtml" + REDIRECT;
    }

    public String toAddLuggage() {
        return DCS_URL + "addLuggage.xhtml" + REDIRECT;
    }

    public String toBoardPassenger() {
        return DCS_URL + "boardPassenger.xhtml" + REDIRECT;
    }

    public String toConfirmBoarding() {
        return DCS_URL + "confirmBoarding.xhtml" + REDIRECT;
    }

    public String toBoardingPass() {
        return DCS_URL + "boardingPass.xhtml";
    }

    public String toSearchBPForLuggage() {
        luggageManager.cleanVariables();
        return DCS_URL + "searchBPForLuggage.xhtml" + REDIRECT;
    }

    public String toViewFlightInfo() {
        return DCS_URL + "viewFlightInfo.xhtml" + REDIRECT;
    }

    public String toViewLuggage() {
        return DCS_URL + "viewLuggage.xhtml" + REDIRECT;
    }

    public String toSearchTicket() {
        luggageManager.cleanVariables();
        return DCS_URL + "searchTicket.xhtml" + REDIRECT;
    }

    public String toSelfSearchPassenger() {
        passengerManager.init();
        return DCS_URL + "searchPassenger.xhtml" + REDIRECT;
    }

    public String getCurrURI() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String uri = request.getRequestURI();
        return uri;
    }
}
