/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.afos;

import ams.afos.entity.BiddingSession;
import ams.afos.entity.FlightCrew;
import ams.afos.entity.FlightDuty;
import ams.afos.entity.Pairing;
import ams.afos.session.FlightCrewMgmtSessionLocal;
import ams.afos.session.FlightCrewSessionLocal;
import ams.afos.util.exception.BiddingSessionConflictException;
import ams.afos.util.exception.FlightDutyConflictException;
import ams.afos.util.exception.PairingConflictException;
import ams.afos.util.helper.NoCrewFoundException;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import managedbean.application.MsgController;

/**
 *
 * @author Lewis
 */
@Named(value = "biddingSessionBacking")
@ViewScoped
public class BiddingSessionBacking implements Serializable {

    @EJB
    private FlightCrewSessionLocal flightCrewSession;

    @EJB
    private FlightCrewMgmtSessionLocal flightCrewMgmtSession;

    @Inject
    private MsgController msgController;

    @Inject
    private FlightCrewBacking flightCrewBacking;

    private List<FlightDuty> flightDuties;
    private FlightDuty selectedFlightDuty;

    private List<Pairing> pairings;
    private Pairing selectedPairing;

    private List<BiddingSession> biddingSessions;
    private BiddingSession selectedBiddingSession;

    /**
     * Creates a new instance of BiddingSessionBacking
     */
    public BiddingSessionBacking() {
    }

    @PostConstruct
    public void init() {
        getFutureFlightDuties();
        getFuturePairings();
        getAllBiddingSession();
    }

    public void generatePairings() {
        try {
            flightCrewMgmtSession.generatePairings();
            setPairings(flightCrewMgmtSession.getNextMonthPairings());
            msgController.addMessage("Flight pairings of next month generated succesfully");
        } catch (PairingConflictException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
    }

    public void generateBiddingSession(String target) {
        try {
            flightCrewMgmtSession.generateBiddingSession(target);
            setBiddingSessions(flightCrewMgmtSession.getAllBiddingSession());
            msgController.addMessage("Bidding seesion of next month for " + target + " generated succesfully");
        } catch (BiddingSessionConflictException | NoCrewFoundException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
    }

    public void generateFlightDuty() {
        System.out.println("FlightDutyBacking: generateFlightDuty");
        try {
            setFlightDuties(flightCrewMgmtSession.generateFlightDuties());
            msgController.addMessage("Flight duties of next month generated succesfully");
        } catch (FlightDutyConflictException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
    }

    public void closeBiddingSession() {
        flightCrewMgmtSession.closeBiddingSession(selectedBiddingSession);
    }

    private void getAllBiddingSession() {
        setBiddingSessions(flightCrewMgmtSession.getAllBiddingSession());
    }

    private void getFutureFlightDuties() {
        setFlightDuties(flightCrewMgmtSession.getNextMonthFlightDuties());
    }

    private void getFuturePairings() {
        setPairings(flightCrewMgmtSession.getNextMonthPairings());
    }

    //    public boolean getOnOffValue(BiddingSession session) {
//        switch (session.getStatus()) {
//            case BiddingSessionStatus.CLOSED:
//                return false;
//            case BiddingSessionStatus.RELEASED:
//                return true;
//            default:
//                return false;
//        }
//    }
    /**
     * @return the flightDuties
     */
    public List<FlightDuty> getFlightDuties() {
        return flightDuties;
    }

    /**
     * @param flightDuties the flightDuties to set
     */
    public void setFlightDuties(List<FlightDuty> flightDuties) {
        this.flightDuties = flightDuties;
    }

    /**
     * @return the selectedFlightDuty
     */
    public FlightDuty getSelectedFlightDuty() {
        return selectedFlightDuty;
    }

    /**
     * @param selectedFlightDuty the selectedFlightDuty to set
     */
    public void setSelectedFlightDuty(FlightDuty selectedFlightDuty) {
        this.selectedFlightDuty = selectedFlightDuty;
    }

    /**
     * @return the pairings
     */
    public List<Pairing> getPairings() {
        return pairings;
    }

    /**
     * @param pairings the pairings to set
     */
    public void setPairings(List<Pairing> pairings) {
        this.pairings = pairings;
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
     * @return the biddingSessions
     */
    public List<BiddingSession> getBiddingSessions() {
        return biddingSessions;
    }

    /**
     * @param biddingSessions the biddingSessions to set
     */
    public void setBiddingSessions(List<BiddingSession> biddingSessions) {
        this.biddingSessions = biddingSessions;
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

}
