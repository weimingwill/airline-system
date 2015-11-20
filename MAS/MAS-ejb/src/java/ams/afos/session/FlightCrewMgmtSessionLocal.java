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
import ams.afos.util.exception.BiddingSessionConflictException;
import ams.afos.util.exception.FlightDutyConflictException;
import ams.afos.util.exception.PairingConflictException;
import ams.aps.entity.Flight;
import ams.aps.entity.FlightSchedule;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Lewis
 */
@Local
public interface FlightCrewMgmtSessionLocal {
    public List<FlightDuty> generateFlightDuties() throws FlightDutyConflictException;
    public List<FlightDuty> getNextMonthFlightDuties();
    public void generatePairings() throws PairingConflictException;
    public List<Pairing> getNextMonthPairings();
    public void generateBiddingSession(String target) throws BiddingSessionConflictException;

    
    public void startBiddingSession(BiddingSession session);
    public void suspendBiddingSession(BiddingSession session);
    public void closeBiddingSession(BiddingSession session);
    public List<BiddingSession> getAllBiddingSession();
    public void assignPairingsToCrew(String type, Pairing pairingWithBids, List<FlightCrew> orderedFlightCrew);
    public void createFlightDutyChecklist(Checklist checklist, Flight flight); // combine create and update together
    public void updateAttendance(List<FlightCrew> flightCrews,FlightSchedule flightSchedule);
    public List<FlightCrew> getOnDutyCrews(FlightSchedule flightSchedule);
    public Checklist getFlightChecklist(Flight thisFlight, String type);
    public List<Checklist> getChecklistTemplates(String type);
    public Checklist getPostFlightReport(FlightSchedule flightSchedule);
}
