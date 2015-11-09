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
import ams.aps.entity.FlightSchedule;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Lewis
 */
@Local
public interface FlightCrewMgmtSessionLocal {
    public List<FlightDuty> generateFlightDuties();
    
    public void startBiddingSession(BiddingSession session);
    public void suspendBiddingSession(BiddingSession session);
    public List<BiddingSession> getAllBiddingSession();
    public void assignPairingsToCrew(FlightCrew thisCrew);
    public void createFlightDutyChecklist(Checklist checklist);
    public void updateFlightDutyChecklist(Checklist checklist);
    public void updateAttendance(List<FlightCrew> flightCrews);
    public List<FlightCrew> getOnDutyCrews(FlightSchedule flightSchedule);
    public List<Checklist> getFlightDutyChecklist(FlightSchedule flightSchedule);
    public List<Checklist> getChecklistTemplates(String type);
    public List<Checklist> getPostFlightReport(FlightSchedule flightSchedule);
    
}
