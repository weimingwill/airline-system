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
import ams.afos.util.helper.BiddingSessionStatus;
import ams.aps.entity.AircraftCabinClass;
import ams.aps.entity.Airport;
import ams.aps.entity.FlightSchedule;
import ams.aps.session.RoutePlanningSessionLocal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Lewis
 */
@Stateless
public class FlightCrewMgmtSession implements FlightCrewMgmtSessionLocal {

    @EJB
    private RoutePlanningSessionLocal routePlanningSession;

    @PersistenceContext(unitName = "MAS-ejbPU")
    private EntityManager em;

    private final double msToHrCoe = 2.77778e-7;

    @Override
    public void generatePairings() throws PairingConflictException{
        List<FlightDuty> nextMonthFlightDuties = getNextMonthFlightDuties();
        ArrayList<Long> flightDutyIdList = new ArrayList();
        checkFlightPairingExist(nextMonthFlightDuties);

        for (FlightDuty thisDuty : nextMonthFlightDuties) {
            flightDutyIdList.add(thisDuty.getId());
        }

        for (FlightDuty thisDuty : nextMonthFlightDuties) {
            Airport reportLoc = getReportLoc(thisDuty);
            if (reportLoc.getIsHub()) {
                Pairing newPairing = new Pairing();
                setPairingDuties(thisDuty, flightDutyIdList, newPairing);
                em.persist(newPairing);
                em.flush();
            }
        }
        // for the remaining flight duty which does not have a pair flight duty create a pairing which contains only 1 flight duty.
        System.out.println("Before addSinglePairings(): " + flightDutyIdList);
        addSinglePairings(flightDutyIdList);
        System.out.println("After addSinglePairings(): " + flightDutyIdList);
    }

    private void addSinglePairings(List<Long> flightDutyIdList) {
        for (Long fdId : flightDutyIdList) {
            Pairing singlePairing = new Pairing();
            FlightDuty thisFlightDuty = em.find(FlightDuty.class, fdId);
            singlePairing.setFlightDuties(Arrays.asList(thisFlightDuty));
            singlePairing.setCabinCrewQuota(thisFlightDuty.getCabinCrewQuota());
            singlePairing.setCockpitCrewQuota(thisFlightDuty.getCockpitCrewQuota());
            singlePairing.setLayoverTime(0.0);
            singlePairing.setPairingCode(generatePairingCode(thisFlightDuty, null));
            printPairing(singlePairing);
            em.persist(singlePairing);
            em.flush();
        }
        flightDutyIdList.clear();
    }

    private void setPairingDuties(FlightDuty thisDuty, ArrayList<Long> flightDutyIdList, Pairing newPairing) {
//        printFlightDuty(thisDuty);
        final int minLayoverHrs = 8;
        Calendar dismissTime = Calendar.getInstance(), nextReportTime;

        Airport dismissLoc = getDismissLoc(thisDuty);

        dismissTime.setTime(thisDuty.getDismissTime());
        nextReportTime = dismissTime;
        // Flight crew must have at least 8 hours of layover time
        nextReportTime.add(Calendar.HOUR_OF_DAY, minLayoverHrs);

        // Query paired flight duty
        System.out.println("getPairingDuties(): \n\tReport location: " + dismissLoc.getAirportName() + "\n\tNext Report Time: " + nextReportTime.getTime());
        Query query = em.createQuery("SELECT fd FROM FlightDuty fd WHERE fd.reportTime >= :reportTime AND fd.reportLoc.airportName = :reportLoc AND fd.cabinCrewQuota = :cbcq AND fd.cockpitCrewQuota = :ckcq  ORDER BY fd.reportTime ASC");
        query.setParameter("reportTime", nextReportTime.getTime());
        query.setParameter("reportLoc", dismissLoc.getAirportName());
        query.setParameter("cbcq", thisDuty.getCabinCrewQuota());
        query.setParameter("ckcq", thisDuty.getCockpitCrewQuota());
        query.setMaxResults(1);

        List<FlightDuty> nextFlightDuty = (List<FlightDuty>) query.getResultList();

        if (!nextFlightDuty.isEmpty()) {
            // if pair is found, bind the two flight duties together and set it flight pairing
//            printFlightDuty(nextFlightDuty.get(0));
            flightDutyIdList.remove(thisDuty.getId());
            flightDutyIdList.remove(nextFlightDuty.get(0).getId());
            List<FlightDuty> flightDutyList = new ArrayList();
            flightDutyList.add(thisDuty);
            flightDutyList.add(nextFlightDuty.get(0));
            // set pairing information
            newPairing.setCabinCrewQuota(thisDuty.getCabinCrewQuota());
            newPairing.setCockpitCrewQuota(thisDuty.getCockpitCrewQuota());
            newPairing.setFlightDuties(flightDutyList);
            newPairing.setLayoverTime((nextFlightDuty.get(0).getReportTime().getTime() - thisDuty.getDismissTime().getTime()) * msToHrCoe);
            newPairing.setPairingCode(generatePairingCode(thisDuty, nextFlightDuty.get(0)));
            printPairing(newPairing);
        } else {
            // if pair is not found, display result not found error
            System.out.println("No result found for next flight duty");
        }
    }

    private String generatePairingCode(FlightDuty thisFlightDuty, FlightDuty nextFlightDuty) {
        Calendar firstRpTime = Calendar.getInstance();
//                ,lastDmTime = Calendar.getInstance();
        firstRpTime.setTime(thisFlightDuty.getReportTime());
//        lastDmTime.setTime(nextFlightDuty.getDismissTime());
        String firstFN = thisFlightDuty.getFlightSchedules().get(0).getFlight().getFlightNo(), lastFN, pairingCode;
        firstFN = firstFN.split("MA")[1];
        if (nextFlightDuty != null) {
            lastFN = nextFlightDuty.getFlightSchedules().get(0).getFlight().getFlightNo();
            lastFN = lastFN.split("MA")[1];
            pairingCode = "D";
        } else {
            lastFN = "";
            pairingCode = "S";
        }
        pairingCode += firstFN + lastFN + firstRpTime.get(Calendar.YEAR) + (firstRpTime.get(Calendar.MONTH) + 1) + firstRpTime.get(Calendar.DAY_OF_WEEK);
        return pairingCode;
    }

    private Airport getReportLoc(FlightDuty thisDuty) {
        for (FlightSchedule thisSchedule : thisDuty.getFlightSchedules()) {
            if (thisSchedule.getPreFlightSched() == null
                    || (thisSchedule.getPreFlightSched() != null && !thisSchedule.getFlight().getFlightNo().equals(thisSchedule.getPreFlightSched().getFlight().getFlightNo()))) {
                return thisSchedule.getLeg().getDepartAirport();
            }
        }
        return null;
    }

    private Airport getDismissLoc(FlightDuty thisDuty) {
        for (FlightSchedule thisSchedule : thisDuty.getFlightSchedules()) {
            if (thisSchedule.getNextFlightSched() == null
                    || (thisSchedule.getNextFlightSched() != null && !thisSchedule.getFlight().getFlightNo().equals(thisSchedule.getNextFlightSched().getFlight().getFlightNo()))) {
                return thisSchedule.getLeg().getArrivalAirport();
            }
        }
        return null;
    }

    @Override
    public List<FlightDuty> generateFlightDuties() throws FlightDutyConflictException {
        List<FlightSchedule> nextMonthFlights = getNextMonthFlightSchedule();
        checkFlightDutyExist(nextMonthFlights);
        List<FlightDuty> outputList = new ArrayList();
        for (FlightSchedule thisSchedule : nextMonthFlights) {
            System.out.print("generateFlightDuties(): Start create new FlightDuty\n");

            if (thisSchedule.getPreFlightSched() == null
                    || (thisSchedule.getPreFlightSched() != null && !thisSchedule.getFlight().getFlightNo().equals(thisSchedule.getPreFlightSched().getFlight().getFlightNo()))) {
                //if first flight segment of flight
                FlightDuty newFlightDuty = new FlightDuty();
                System.out.println("\tFound first flight segment of flight");
                newFlightDuty.setFlightSchedules(getFlightDutyFlights(thisSchedule));
                for (FlightSchedule sc : newFlightDuty.getFlightSchedules()) {
                    printFlightInfo(sc);
                }
                System.out.print("generateFlightDuties(): End of FlightDuty flight schedules\n\n\n");
                newFlightDuty.setReportLoc(newFlightDuty.getFlightSchedules().get(0).getLeg().getDepartAirport());
                newFlightDuty.setDismissLoc(newFlightDuty.getFlightSchedules().get(newFlightDuty.getFlightSchedules().size() - 1).getLeg().getArrivalAirport());
                setFlightDutyInfo(newFlightDuty);
                setCrewQuota(newFlightDuty, thisSchedule);
                em.persist(newFlightDuty);
                em.flush();
                outputList.add(newFlightDuty);
            }
        }
        return outputList;
    }

    private void checkFlightDutyExist(List<FlightSchedule> nextMonthFlights) throws FlightDutyConflictException {
        Query q;
        for (FlightSchedule fs : nextMonthFlights) {
            q = em.createQuery("SELECT fd FROM FlightDuty fd WHERE :fs MEMBER OF (fd.flightSchedules)");
            q.setParameter("fs", fs);
            if (!q.getResultList().isEmpty()) {
                System.out.println("Flight duty has already bean generated");
                throw new FlightDutyConflictException("Next month flight duties have already bean generated");
            }
        }
    }
    
    private void checkFlightPairingExist(List<FlightDuty> nextMonthFlightDutys) throws PairingConflictException {
        Query q;
        for (FlightDuty fd : nextMonthFlightDutys) {
            q = em.createQuery("SELECT p FROM Pairing p WHERE :fd MEMBER OF (p.flightDuties)");
            q.setParameter("fd", fd);
            if (!q.getResultList().isEmpty()) {
                System.out.println("Flight duty has already bean generated");
                throw new PairingConflictException("Next month parings have already bean generated");
            }
        }
    }
    
    private void checkBiddingSessionExist(List<Pairing> pairings, FlightCrew crew) throws BiddingSessionConflictException {
        Query q;
        for (Pairing p: pairings) {
            q = em.createQuery("SELECT bs FROM BiddingSession bs WHERE (:p MEMBER OF (bs.pairings)) AND (:c MEMBER OF (bs.flightCrews))");
            q.setParameter("p", p);
            q.setParameter("c", crew);
            if (!q.getResultList().isEmpty()) {
                System.out.println("Bidding session for "+crew.getPosition().getType()+" has already bean generated");
                throw new BiddingSessionConflictException("Bidding session for "+crew.getPosition().getType()+" has already bean generated");
            }
        }
    }

    private void setFlightDutyInfo(FlightDuty newFlightDuty) {
        Double totalFlyingTime = 0.0;
        Double totalFlyingDist = 0.0;
        Double totalSitTime = 0.0;
        List<FlightSchedule> flightSchedules = newFlightDuty.getFlightSchedules();
        for (int i = 0; i < flightSchedules.size(); i++) {
            if (i != flightSchedules.size() - 1) {
                totalSitTime += flightSchedules.get(i).getTurnoverTime();
            }
            totalFlyingDist += routePlanningSession.distance(flightSchedules.get(i).getLeg().getDepartAirport(), flightSchedules.get(i).getLeg().getArrivalAirport());
            totalFlyingTime += flightSchedules.get(i).getArrivalDate().getTime() - flightSchedules.get(i).getDepartDate().getTime();
        }
        totalFlyingTime *= msToHrCoe;
        totalFlyingDist /= 1000;
        newFlightDuty.setFlyingDistInKm(totalFlyingDist);
        newFlightDuty.setFlyingTimeInHrs(totalFlyingTime);
        newFlightDuty.setSitTimeInHrs(totalSitTime);
        System.out.println("setFlightDutyInfo(): \n\tTotal Flying Dist = " + totalFlyingDist + "\n\tTotal Flying Hours = " + totalFlyingTime + "\n\tTotal Sit Time = " + totalSitTime);
        setFlightDutyTime(newFlightDuty);
    }

    private void setFlightDutyTime(FlightDuty newFlightDuty) {
        Calendar reportTime = Calendar.getInstance();
        Calendar dismissTime = Calendar.getInstance();
        reportTime.setTime(newFlightDuty.getFlightSchedules().get(0).getDepartDate());
        dismissTime.setTime(newFlightDuty.getFlightSchedules().get(newFlightDuty.getFlightSchedules().size() - 1).getArrivalDate());
        reportTime.add(Calendar.HOUR_OF_DAY, -1);
        dismissTime.add(Calendar.MINUTE, 20);
        newFlightDuty.setReportTime(reportTime.getTime());
        newFlightDuty.setDismissTime(dismissTime.getTime());
        newFlightDuty.setAppliedPeriod(getNextMonthFirstDay());
    }

    private List<FlightSchedule> getFlightDutyFlights(FlightSchedule thisSchedule) {
        FlightSchedule currSchedule = thisSchedule;
        FlightSchedule nextSchedule = thisSchedule.getNextFlightSched();
        List<FlightSchedule> flightDutyFlightSchedules = new ArrayList();

        flightDutyFlightSchedules.add(currSchedule);
        while (nextSchedule != null && nextSchedule.getFlight().getFlightNo().equals(currSchedule.getFlight().getFlightNo())) {
            flightDutyFlightSchedules.add(nextSchedule);
            currSchedule = nextSchedule;
            nextSchedule = nextSchedule.getNextFlightSched();
        }
        return flightDutyFlightSchedules;

    }

    private void setCrewQuota(FlightDuty dp, FlightSchedule schedule) {
        double totalFlyingTime = dp.getFlyingTimeInHrs();
        if (totalFlyingTime > 0) {
            int numCabinCrew = 0;
            int numCockpitCrew = 2;
            int totalPassenger = 0;
            for (AircraftCabinClass thisACC : schedule.getAircraft().getAircraftCabinClasses()) {
                totalPassenger += thisACC.getSeatQty();
            }

            if (totalPassenger <= 50) {
                numCabinCrew = 1;
            } else if (totalPassenger > 50 && totalPassenger < 100) {
                numCabinCrew = 2;
            } else {
                numCabinCrew = 2 + totalPassenger / 50;
            }

            if (totalFlyingTime > 8 && totalFlyingTime <= 12) {
                numCabinCrew = (int) Math.floor(numCabinCrew * 1.5);
                numCockpitCrew += 1;
            } else if (totalFlyingTime > 12) {
                numCabinCrew = numCabinCrew * 2;
                numCockpitCrew += 2;
            }
            dp.setCabinCrewQuota(numCabinCrew);
            dp.setCockpitCrewQuota(numCockpitCrew);
            System.out.println("setCrewQuota(): \n\tNumber of Passengers = " + totalPassenger + "\n\tNumber of Cabin Crew = " + numCabinCrew + "\n\tNumber of Cockpit Crew = " + numCockpitCrew);
        }

    }

    private List<FlightSchedule> getNextMonthFlightSchedule() {
        Date nextMonthFirstDay = getNextMonthFirstDay();
        Date nextMonthLastDay = getNextMonthLastDay();
        System.out.println("FlightCrewMgmtSession: nextMonthFirstDay = " + nextMonthFirstDay);
        System.out.println("FlightCrewMgmtSession: nextMonthLastDay = " + nextMonthLastDay);

        Query query = em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.departDate BETWEEN :nextMonthFirstDay AND :nextMonthLastDay AND fs.deleted = FALSE ORDER BY fs.departDate ASC, fs.flight.flightNo ASC");
        query.setParameter("nextMonthFirstDay", nextMonthFirstDay);
        query.setParameter("nextMonthLastDay", nextMonthLastDay);

        try {
            List<FlightSchedule> nextMonthFlights;
            nextMonthFlights = (List<FlightSchedule>) query.getResultList();
            System.out.println("nextMonthFlights = " + nextMonthFlights);
            return nextMonthFlights;
        } catch (Exception ex) {
            return new ArrayList();
        }
    }

    private Date getNextMonthFirstDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
        return calendar.getTime();
    }

    private Date getNextMonthLastDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
        return calendar.getTime();
    }

    @Override
    public void generateBiddingSession(String target) throws BiddingSessionConflictException{
        List<Pairing> nextMonthPairings = getNextMonthPairings();
        List<FlightCrew> flightCrews = getCrews(target);
        checkBiddingSessionExist(nextMonthPairings, flightCrews.get(0));
        BiddingSession newBiddingSession = new BiddingSession();
        Calendar createTime, startTime;
        createTime = Calendar.getInstance();
        startTime = Calendar.getInstance(); // should change to the first of last week of each month.
        Double remainingHrs = 7 * 24.0;
        
        
        newBiddingSession.setStartTime(startTime.getTime());
        newBiddingSession.setCreatedTime(createTime.getTime());
        newBiddingSession.setRemainingHrs(remainingHrs);
        
        newBiddingSession.setStatus(BiddingSessionStatus.CREATED);
        printBiddingSession(newBiddingSession);
        em.persist(newBiddingSession);
        for(Pairing pairing: nextMonthPairings){
            List<BiddingSession> biddingSessions = pairing.getBiddingSessions();
            biddingSessions.add(newBiddingSession);
            pairing.setBiddingSessions(biddingSessions);
            em.merge(pairing);
        }
        for(FlightCrew crew: flightCrews){
            List<BiddingSession> biddingSessions = crew.getBiddingSessions();
            biddingSessions.add(newBiddingSession);
            crew.setBiddingSessions(biddingSessions);
            em.merge(crew);
        }
        newBiddingSession.setPairings(nextMonthPairings);
        newBiddingSession.setFlightCrews(flightCrews);
        em.merge(newBiddingSession);
    }

    @Override
    public void startBiddingSession(BiddingSession session) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void suspendBiddingSession(BiddingSession session) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void assignPairingsToCrew(FlightCrew thisCrew) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void createFlightDutyChecklist(Checklist checklist) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateFlightDutyChecklist(Checklist checklist) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateAttendance(List<FlightCrew> flightCrews) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<FlightCrew> getOnDutyCrews(FlightSchedule flightSchedule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Checklist> getFlightDutyChecklist(FlightSchedule flightSchedule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Checklist> getChecklistTemplates(String type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Checklist> getPostFlightReport(FlightSchedule flightSchedule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<BiddingSession> getAllBiddingSession() {
        Query query = em.createQuery("SELECT bs FROM BiddingSession bs ORDER BY bs.startTime ASC");
        return (List<BiddingSession>) query.getResultList();
    }

    @Override
    public List<FlightDuty> getNextMonthFlightDuties() {
        Date nextMonthFirstDay = getNextMonthFirstDay();
        Date nextMonthLastDay = getNextMonthLastDay();
        Query query = em.createQuery("SELECT fd FROM FlightDuty fd WHERE fd.appliedPeriod BETWEEN :nextMonthFirstDay AND :nextMonthLastDay ORDER BY fd.reportTime ASC");
        query.setParameter("nextMonthFirstDay", nextMonthFirstDay);
        query.setParameter("nextMonthLastDay", nextMonthLastDay);
        return (List<FlightDuty>) query.getResultList();
    }

    @Override
    public List<Pairing> getNextMonthPairings() {
        Date nextMonthFirstDay = getNextMonthFirstDay();
        Calendar temp = Calendar.getInstance();
        temp.setTime(nextMonthFirstDay);
        String pattern = "" + temp.get(Calendar.YEAR) + (temp.get(Calendar.MONTH) + 1);
        Query query = em.createQuery("SELECT p FROM Pairing p WHERE p.pairingCode LIKE :pattern ORDER BY p.pairingCode");
        query.setParameter("pattern", "%" + pattern + "%");
        return (List<Pairing>) query.getResultList();
    }

    private List<FlightCrew> getCrews(String target) {
        Query query = em.createQuery("SELECT fc FROM FlightCrew fc WHERE fc.deleted = FALSE AND fc.position.type = :target ORDER BY fc.dateJoined ASC");
        query.setParameter("target", target);
        return (List<FlightCrew>) query.getResultList();
    }

    // for debug purposes
    private void printFlightDuty(FlightDuty duty) {
        System.out.println("printFlightDuty(): "
                + "\n\tID: " + duty.getId()
                + "\n\tReport Time: " + duty.getReportTime()
                + "\n\tReport Location: " + duty.getReportLoc().getAirportName()
                + "\n\tDismiss Time: " + duty.getDismissTime()
                + "\n\tDismiss Location: " + duty.getDismissLoc().getAirportName()
                + "\n\tCabin Crew Quota: " + duty.getCabinCrewQuota()
                + "\n\tCockpit Crew Quota: " + duty.getCockpitCrewQuota()
                + "\n\tFlight Number: " + duty.getFlightSchedules().get(0).getFlight().getFlightNo()
        );
    }

    private void printFlightInfo(FlightSchedule thisSchedule) {
        System.out.println("thisSchedule: \n\tFlight No: " + thisSchedule.getFlight().getFlightNo()
                + "\n\tFrom: " + thisSchedule.getLeg().getDepartAirport().getAirportName()
                + "\n\tTo: " + thisSchedule.getLeg().getArrivalAirport().getAirportName()
                + "\n\tDeparture Time: " + thisSchedule.getDepartDate()
                + "\n\tArrival Time: " + thisSchedule.getArrivalDate() + "\n\n");
    }

    private void printPairing(Pairing pairing) {
        System.out.println("printPairing(): "
                + "\n\tPairing Code: " + pairing.getPairingCode()
                + "\n\tLayover Time: " + pairing.getLayoverTime()
                + "\n\tCabin Crew Quota: " + pairing.getCabinCrewQuota()
                + "\n\tCockpit Crew Quota: " + pairing.getLayoverTime()
        );
    }

    private void printBiddingSession(BiddingSession session) {
        System.out.println("printBiddingSession(): "
                + "\n\tStatus: " + session.getStatus()
                + "\n\tCreated Time: " + session.getCreatedTime()
                + "\n\tStart Time: " + session.getStartTime()
                + "\n\tRemaining Hours: " + session.getRemainingHrs()
                + "\n\tPairings: " + session.getPairings()
                + "\n\tCrews: " + session.getFlightCrews()
        );
    }

}
