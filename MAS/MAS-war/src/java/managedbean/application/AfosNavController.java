/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.application;

import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import managedbean.afos.FlightCrewBiddingManager;

/**
 *
 * @author Lewis
 */
@Named(value = "afosNavController")
@RequestScoped
public class AfosNavController {

    @Inject
    FlightCrewBiddingManager biddingManager;

    private final String REDIRECT = "?faces-redirect=true";
    private final String AFOS_URL = "/views/internal/secured/afos/";

    /**
     * Creates a new instance of AfosNavController
     */
    public AfosNavController() {
    }

    // AFOS
    public String toViewCrewProfile() {
        return AFOS_URL + "crew_regulation/viewFlightCrewProfile.xhtml" + REDIRECT;
    }

    public String toManageBidding() {
        return AFOS_URL + "crew_schedule/manageBidding.xhtml" + REDIRECT;
    }

    public String toSelectBiddingSession() {
        biddingManager.init();
        return AFOS_URL + "crew_schedule/selectBiddingSession.xhtml" + REDIRECT;
    }

    public String toBidPairings() {
        return AFOS_URL + "crew_schedule/bidPairings.xhtml" + REDIRECT;
    }
}
