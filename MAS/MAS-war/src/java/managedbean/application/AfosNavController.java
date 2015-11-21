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

    public String toViewBidHist() {
        return AFOS_URL + "crew_schedule/viewBidHist.xhtml" + REDIRECT;
    }

    public String toSelectChecklist() {
        return AFOS_URL + "crew_regulation/selectChecklistType.xhtml" + REDIRECT;
    }

    public String toCreateChecklist() {
        return AFOS_URL + "crew_regulation/createChecklist.xhtml" + REDIRECT;
    }

    public String toCrewReporting() {
        return AFOS_URL + "crew_regulation/crewReporting.xhtml" + REDIRECT;
    }

    public String toUpdateCrewAttendance() {
        return AFOS_URL + "crew_regulation/updateCrewAttendance.xhtml" + REDIRECT;
    }

    public String toViewPostDutyReport() {
        return AFOS_URL + "crew_regulation/viewDutyReport.xhtml" + REDIRECT;
    }

    public String toReportPreFlightDuty() {
        return AFOS_URL + "crew_regulation/reportPreFlightDuty.xhtml" + REDIRECT;
    }

    public String toReportPostFlightDuty() {
        return AFOS_URL + "crew_regulation/reportPostFlightDuty.xhtml" + REDIRECT;
    }

    public String toUpdateChecklist() {
        return AFOS_URL + "crew_regulation/updateChecklist.xhtml" + REDIRECT;
    }

    public String toSwapWorkingSlots() {
        return AFOS_URL + "crew_schedule/swapWorkingSlots.xhtml" + REDIRECT;
    }
}
