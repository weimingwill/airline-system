/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.session;

import ams.afos.entity.BiddingSession;
import ams.afos.entity.Checklist;
import ams.afos.entity.FlightCrew;
import ams.afos.entity.FlightDuty;
import ams.afos.entity.Pairing;
import ams.afos.entity.PairingFlightCrew;
import ams.afos.entity.SwappingRequest;
import ams.aps.util.exception.EmptyTableException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Lewis
 */
@Local
public interface FlightCrewSessionLocal {
    public List<FlightCrew> getAllFlightCrew() throws EmptyTableException;
    public FlightCrew getFlightCrewByUsername(String username);
    public List<BiddingSession> getEligibleBiddingSessions(FlightCrew flightCrew);
    public Pairing getPairingById(Long id);
    
    // Flight Crew Duty Report
    public List<FlightDuty> getCrewCurrMonthFutureDuties(FlightCrew thisCrew);
    public List<FlightDuty> getCrewCurrMonthPastDuties(FlightCrew thisCrew);
    public void updateFlightChecklist(Checklist checklist);

    // Flight Crew Bidding
    public List<Pairing> getAllEligiblePairings(BiddingSession session, FlightCrew flightCrew);
    public void placeBidForPairings(List<Pairing> pairings, FlightCrew flightCrew);
    public void updateBids(List<PairingFlightCrew> selectedPairings, FlightCrew flightCrew);
    public List<PairingFlightCrew> getFlightCrewBiddingHistory(FlightCrew flightCrew);
    public List<SwappingRequest> getMatchedSwappingReqs(Pairing chosenPairing, Pairing targetPairing, List<Pairing> existingPairings);
    public List<SwappingRequest> getAlternativeSwappingReqs(Pairing chosenPairing, List<Pairing> existingPairings);
    public void matchSelectedSwappingReq(SwappingRequest selectedRequest);
    public void sendSwappingRequest(SwappingRequest newRequest);
    public void cancelSwappingRequest(SwappingRequest thisRequest);   
    public List<Pairing> getCrewNextMonthPairings(FlightCrew flightCrew);
    public List<Pairing> getAllNextMonthPairings(FlightCrew flightCrew);
    public List<SwappingRequest> getAllCrewSwappingRequests(FlightCrew flightCrew);
}
