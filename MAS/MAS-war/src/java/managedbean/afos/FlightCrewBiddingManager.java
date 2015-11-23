/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.afos;

import ams.afos.entity.BiddingSession;
import ams.afos.entity.FlightCrew;
import ams.afos.entity.Pairing;
import ams.afos.session.FlightCrewSessionLocal;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import managedbean.application.AfosNavController;
import managedbean.application.MsgController;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Lewis
 */
@Named(value = "flightCrewBiddingManager")
@SessionScoped
public class FlightCrewBiddingManager implements Serializable {
    
    @EJB
    private FlightCrewSessionLocal flightCrewSession;
    
    @Inject
    private FlightCrewBacking flightCrewBacking;
    
    @Inject
    private BiddingSessionBacking biddingSessionBacking;
    
    @Inject
    private AfosNavController afosNavController;
    
    @Inject
    private MsgController msgController;
    
    private List<Pairing> availablePairings;
    private List<Pairing> pairingsWithSameCode;
    private List<Pairing> pairingsToBid;
    private Pairing selectedPairing;
    private FlightCrew currentCrew;
    
    private List<BiddingSession> crewBiddingSessions;
    private BiddingSession selectedBiddingSession;

    /**
     * Creates a new instance of FlightCrewBiddingManager
     */
    public FlightCrewBiddingManager() {
    }
    
    @PostConstruct
    public void init() {
        setCurrentCrew(flightCrewBacking.getCurrFlightCrew());
        System.out.println("Current Crew: " + currentCrew);
        
        if (currentCrew != null) {
            getCrewBiddingSessions(currentCrew);
        }
    }
    
    public void placeBidForPairings() {
        if (pairingsToBid == null || pairingsToBid.isEmpty()) {
            msgController.addErrorMessage("Pairing is not select!");
        } else {
            flightCrewSession.placeBidForPairings(pairingsToBid, currentCrew);
            setAvailablePairings(flightCrewSession.getAllEligiblePairings(selectedBiddingSession, currentCrew));
            msgController.addMessage("Bids placed!");
        }
    }
    
    public void getCrewBiddingSessions(FlightCrew flightCrew) {
        setCrewBiddingSessions(flightCrewSession.getEligibleBiddingSessions(flightCrew));
    }
    
    public void getSessionAvailablePairings() {
        System.out.println("selectedBiddingSession:" + selectedBiddingSession);
        System.out.println("parings: " + selectedBiddingSession.getPairings());
        setAvailablePairings(flightCrewSession.getAllEligiblePairings(selectedBiddingSession, currentCrew));
    }
    
    public String onContinueToBiddingBtnClick() {
        if (selectedBiddingSession != null) {
            getSessionAvailablePairings();
            RequestContext context = RequestContext.getCurrentInstance();
            context.update("availablePairingTable");
            return afosNavController.toBidPairings();
        } else {
            if (crewBiddingSessions.isEmpty()) {
                msgController.addErrorMessage("There is no open bidding session currently.");
            } else {
                msgController.addErrorMessage("Please select bidding session to proceed.");
            }
            return "";
        }
    }
    
    public void onViewPairingBtnClick() {
        System.out.println("selectedPairing: " + selectedPairing);
    }
    
    public void onBidBtnClick() {
        System.out.println("selectedPairing: " + selectedPairing);
        setPairingWithSamePairingCode(selectedPairing);
    }
    
    private void setPairingWithSamePairingCode(Pairing selectedPairing) {
        List<Pairing> outputList = new ArrayList();
        for (Pairing thisPairing : availablePairings) {
            if (selectedPairing.getPairingCode().equals(thisPairing.getPairingCode())) {
                outputList.add(thisPairing);
            }
        }
        setPairingsWithSameCode(outputList);
    }
    
    public String convertDateFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd");        
        return sdf.format(date);
    }

    /**
     * @return the pairingsWithSameCode
     */
    public List<Pairing> getPairingsWithSameCode() {
        return pairingsWithSameCode;
    }

    /**
     * @param pairingsWithSameCode the pairingsWithSameCode to set
     */
    public void setPairingsWithSameCode(List<Pairing> pairingsWithSameCode) {
        this.pairingsWithSameCode = pairingsWithSameCode;
    }

    /**
     * @param availablePairings the availablePairings to set
     */
    public void setAvailablePairings(List<Pairing> availablePairings) {
        this.availablePairings = availablePairings;
    }

    /**
     * @return the availablePairings
     */
    public List<Pairing> getAvailablePairings() {
        return availablePairings;
    }

    /**
     * @return the currentCrew
     */
    public FlightCrew getCurrentCrew() {
        return currentCrew;
    }

    /**
     * @param currentCrew the currentCrew to set
     */
    public void setCurrentCrew(FlightCrew currentCrew) {
        this.currentCrew = currentCrew;
    }

    /**
     * @return the crewBiddingSessions
     */
    public List<BiddingSession> getCrewBiddingSessions() {
        return crewBiddingSessions;
    }

    /**
     * @param crewBiddingSessions the crewBiddingSessions to set
     */
    public void setCrewBiddingSessions(List<BiddingSession> crewBiddingSessions) {
        this.crewBiddingSessions = crewBiddingSessions;
    }

    /**
     * @return the selectedBiddingSession
     */
    public BiddingSession getSelectedBiddingSession() {
        return selectedBiddingSession;
    }

    /**
     * @param selectedBiddingSession the selectedBiddingSession to set
     */
    public void setSelectedBiddingSession(BiddingSession selectedBiddingSession) {
        this.selectedBiddingSession = selectedBiddingSession;
    }

    /**
     * @return the selectedPairing
     */
    public Pairing getSelectedPairing() {
        return selectedPairing;
    }

    /**
     * @param selectedPairing the selectedPairing to set
     */
    public void setSelectedPairing(Pairing selectedPairing) {
        this.selectedPairing = selectedPairing;
    }

    /**
     * @return the pairingsToBid
     */
    public List<Pairing> getPairingsToBid() {
        return pairingsToBid;
    }

    /**
     * @param pairingsToBid the pairingsToBid to set
     */
    public void setPairingsToBid(List<Pairing> pairingsToBid) {
        this.pairingsToBid = pairingsToBid;
    }
}
