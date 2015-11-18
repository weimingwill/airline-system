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
import ams.afos.entity.helper.PairingCrewId;
import ams.afos.util.exception.BiddingSessionConflictException;
import ams.afos.util.exception.FlightDutyConflictException;
import ams.afos.util.exception.PairingConflictException;
import ams.afos.util.helper.BiddingSessionStatus;
import ams.afos.util.helper.CrewType;
import ams.afos.util.helper.PairingCrewStatus;
import ams.aps.entity.AircraftCabinClass;
import ams.aps.entity.Airport;
import ams.aps.entity.FlightSchedule;
import ams.aps.session.RoutePlanningSessionLocal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
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
    public void generatePairings() throws PairingConflictException {
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
        firstRpTime.setTime(thisFlightDuty.getReportTime());
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
        for (Pairing p : pairings) {
            q = em.createQuery("SELECT bs FROM BiddingSession bs WHERE (:p MEMBER OF (bs.pairings)) AND (:c MEMBER OF (bs.flightCrews))");
            q.setParameter("p", p);
            q.setParameter("c", crew);
            if (!q.getResultList().isEmpty()) {
                System.out.println("Bidding session for " + crew.getPosition().getType() + " has already bean generated");
                throw new BiddingSessionConflictException("Bidding session for " + crew.getPosition().getType() + " has already bean generated");
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

    @Override
    public void generateBiddingSession(String target) throws BiddingSessionConflictException {
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
        for (Pairing pairing : nextMonthPairings) {
            List<BiddingSession> biddingSessions = pairing.getBiddingSessions();
            biddingSessions.add(newBiddingSession);
            pairing.setBiddingSessions(biddingSessions);
            em.merge(pairing);
        }
        for (FlightCrew crew : flightCrews) {
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
    public void closeBiddingSession(BiddingSession session) {
        Query query = em.createQuery("SELECT DISTINCT(p) FROM PairingFlightCrew pfc, Pairing p WHERE (:session MEMBER OF p.biddingSessions) AND pfc.pairing.pairingId = P.pairingId AND pfc.status = :status");
        query.setParameter("session", session);
        query.setParameter("status", PairingCrewStatus.PENDING);
        List<Pairing> pairings = query.getResultList();
        List<FlightCrew> orderedCabinCrews, orderedCockpitCrews;
        for (Pairing thisPairing : pairings) {
            System.out.print("closeBiddingSession(): thisPairing");
            printPairing(thisPairing);
            orderedCabinCrews = getOrderedFlightCrews(CrewType.FLIGHT_ATTENDENT, thisPairing);
            orderedCockpitCrews = getOrderedFlightCrews(CrewType.PILOT, thisPairing);
            assignPairingsToCrew(CrewType.FLIGHT_ATTENDENT, thisPairing, orderedCabinCrews);
            assignPairingsToCrew(CrewType.PILOT, thisPairing, orderedCockpitCrews);
        }
        assignRemainingPairingsToCrew(CrewType.FLIGHT_ATTENDENT, session);
        assignRemainingPairingsToCrew(CrewType.PILOT, session);
    }

    @Override
    public void assignPairingsToCrew(String type, Pairing pairingWithBids, List<FlightCrew> orderedFlightCrew) {
        List<PairingFlightCrew> pairingFlightCrews;
        pairingFlightCrews = pairingWithBids.getPairingFlightCrews();
        int quota = 0;
        quota = getPairngCrewQuota(type, pairingWithBids);
        int remainingQuota = quota;
        if (!orderedFlightCrew.isEmpty()) {
            // Based on crew type and crew quota to assign pairing to crew
            PairingFlightCrew thisPairingFlightCrew;
            String status;
            System.out.println("orderedFlightCrew.size() " + orderedFlightCrew.size());
            for (int i = 0; i < orderedFlightCrew.size(); i++) {
                printFlightCrew(orderedFlightCrew.get(i));
                thisPairingFlightCrew = getPairingFlightCrewByCrew(pairingFlightCrews, orderedFlightCrew.get(i));
                System.out.println("thisPairingFlightCrew: " + thisPairingFlightCrew);
                if (thisPairingFlightCrew != null) {
                    if (i < quota) {
                        status = PairingCrewStatus.SUCCESS;
                        remainingQuota--;
                    } else {
                        status = PairingCrewStatus.FAIL;
                    }
                    thisPairingFlightCrew.setStatus(status);
                    em.merge(thisPairingFlightCrew);

                    em.flush();
                }
            }
        }
        setPairingCrewQuota(type, pairingWithBids, remainingQuota);
        em.merge(pairingWithBids);
    }

    // NOT VERY IDEAL
    // TODO: 
    // Directly assign crew pairings based on crew's maximum flying time
    // Prioritize to assign crew to pairings with minimum filled in crews
//    private List<Pairing> getFailedPairings() {
//        Date nextMonthFirstDay = getNextMonthFirstDay();
//        Date nextMonthLastDay = getNextMonthLastDay();
//        Calendar temp = Calendar.getInstance();
//
//        Query query = em.createQuery("SELECT pfc.pairing FROM PairingFlightCrew pfc WHERE pfc.status = :status AND pfc.lastUpdateTime BETWEEN :thisMonthFirstDay AND :thisMonthLastDay");
//        query.setParameter("status", PairingCrewStatus.FAIL);
//        temp.setTime(nextMonthFirstDay);
//        temp.add(Calendar.MONTH, -1);
//        query.setParameter("thisMonthFirstDay", temp.getTime());
//        temp.setTime(nextMonthLastDay);
//        temp.add(Calendar.MONTH, -1);
//        query.setParameter("thisMonthLastDay", temp.getTime());
//        return (List<Pairing>) query.getResultList();
//    }
    // TODO: get pairings with min crews
    private List<Pairing> getPairingsWithMinCrews(String type, BiddingSession session) {
        Query query;
        switch (type) {
            case CrewType.FLIGHT_ATTENDENT:
                query = em.createQuery("SELECT p FROM Pairing p, IN(p.biddingSessions) bs, IN(bs.flightCrews) fc WHERE p.cabinCrewQuota > 0 AND (:session MEMBER OF p.biddingSessions) AND fc.position.type = :type ORDER BY p.cabinCrewQuota DESC");
                query.setParameter("session", session);
                query.setParameter("type", type);
                return (List<Pairing>) query.getResultList();
            case CrewType.PILOT:
                query = em.createQuery("SELECT p FROM Pairing p, IN(p.biddingSessions) bs, IN(bs.flightCrews) fc WHERE p.cockpitCrewQuota > 0 AND (:session MEMBER OF p.biddingSessions) AND fc.position.type = :type ORDER BY p.cockpitCrewQuota DESC");
                query.setParameter("session", session);
                query.setParameter("type", type);
                return (List<Pairing>) query.getResultList();
            default:
                return null;
        }
    }

    // TODO: get crews with min pairings and order by 
    // 1. crew position ranking low - high (ASC) and 
    // 2. crew seniority low - high (DSC)
    private List<FlightCrew> getCrewWithMinPairings(String type, BiddingSession session) {
        List<FlightCrew> outputList;
        // Get crew with no pairings at all
        Query q1 = em.createQuery("SELECT fc FROM FlightCrew fc WHERE fc.position.type = :type AND fc NOT IN(SELECT DISTINCT pfc.flightCrew FROM PairingFlightCrew pfc, pfc.pairing p WHERE pfc.status = :status AND (:session MEMBER OF p.biddingSessions) AND pfc.flightCrew.position.type = :type) ORDER BY fc.position.rank ASC, fc.dateJoined DESC");
        q1.setParameter("status", PairingCrewStatus.SUCCESS);
        q1.setParameter("session", session);
        q1.setParameter("type", type);
        outputList = (List<FlightCrew>) q1.getResultList();

        // Get crew with certain success pairings
        Query q2 = em.createQuery("SELECT DISTINCT pfc.flightCrew, COUNT(pfc.flightCrew) FROM PairingFlightCrew pfc, pfc.pairing p WHERE pfc.status = :status AND (:session MEMBER OF p.biddingSessions) AND pfc.flightCrew.position.type = :type GROUP BY pfc.flightCrew ORDER BY COUNT(pfc.flightCrew) ASC, pfc.flightCrew.position.rank ASC, pfc.flightCrew.dateJoined DESC");
        q2.setParameter("status", PairingCrewStatus.SUCCESS);
        q2.setParameter("session", session);
        q2.setParameter("type", type);
        List<Object[]> crewsWithCertainAmtPairings = q2.getResultList();
        for (Object[] result : crewsWithCertainAmtPairings) {
            outputList.add((FlightCrew) result[0]);
        }
        return outputList;
    }

    // TODO: assign remaining pairings to crew
    // 1. check crew flying requirement
    // 2. check pairing remaining quota
    // 3. check crew pairing clashes (i.e. if the crew has been assigned the same pairing)
    // 4. assign pairings to crew
    private void assignRemainingPairingsToCrew(String type, BiddingSession session) {
        List<Pairing> remainingPairings = getPairingsWithMinCrews(type, session);
        List<FlightCrew> crewWithMinPairings = getCrewWithMinPairings(type, session);
        /* for debugging */
//        System.out.println("crew with min pairings");
//        for (FlightCrew fc : crewWithMinPairings) {
//            printFlightCrew(fc);
//        }
        /* for debugging */
        double minDutyTime = getMinDutyTimeFromPairings(remainingPairings);
        int quota, remainingQuota;
        PairingFlightCrew pairingFlightCrew;
        List<FlightCrew> crewsReachMaxRqmt;
        
        for (Iterator<Pairing> pairingIter = remainingPairings.iterator(); pairingIter.hasNext() && !crewWithMinPairings.isEmpty();) {
            Pairing thisPairing = pairingIter.next();
            quota = getPairngCrewQuota(type, thisPairing);
            remainingQuota = quota;

            crewsReachMaxRqmt = new ArrayList();
            for (Iterator<FlightCrew> crewIter = crewWithMinPairings.iterator(); crewIter.hasNext();) {
                FlightCrew thisCrew = crewIter.next();
                /* for debugging */
//                System.out.println("start assigning remaining pairing to crew");
//                printPairing(thisPairing);
//                printFlightCrew(thisCrew); 
                /* for debugging */
                // If crew reaches its max requirement, add crew to list for removal
                if (reachMaxCrewRequirement(minDutyTime, thisCrew, session)) {
                    System.err.println("Crew " + thisCrew.getUsername()+ " reaches his max requirement");
                    crewsReachMaxRqmt.add(thisCrew);
                } else if (pairingWithinCrewRequirement(thisPairing, thisCrew, session) && !crewPairingClash(thisPairing, thisCrew) && remainingQuota > 0) {
                    pairingFlightCrew = new PairingFlightCrew();
                    pairingFlightCrew.setPairing(thisPairing);
                    pairingFlightCrew.setFlightCrew(thisCrew);
                    pairingFlightCrew.setStatus(PairingCrewStatus.SUCCESS);
                    pairingFlightCrew.setLastUpdateTime(Calendar.getInstance().getTime());
                    PairingCrewId pairingCrewId = new PairingCrewId();
                    pairingCrewId.setPairingId(thisPairing.getPairingId());
                    pairingCrewId.setSystemUserId(thisCrew.getSystemUserId());
                    pairingFlightCrew.setPairingCrewId(pairingCrewId);
                    em.persist(pairingFlightCrew);
                    remainingQuota--;
                }
            }
            // remove crews who reach max requirement from the list
            crewWithMinPairings.removeAll(crewsReachMaxRqmt);
            
            setPairingCrewQuota(type, thisPairing, remainingQuota);
            // if there is no remaining quota, remove the pairing from list
            if (remainingQuota == 0) {
                System.err.println("Pairing: " + thisPairing.getPairingId() + " is full");
                pairingIter.remove();
            }
        }
    }

    private int getPairngCrewQuota(String type, Pairing pairing) {
        switch (type) {
            case CrewType.FLIGHT_ATTENDENT:
                return pairing.getCabinCrewQuota();
            case CrewType.PILOT:
                return pairing.getCockpitCrewQuota();
            default:
                return 0;
        }
    }

    private void setPairingCrewQuota(String type, Pairing pairing, int remainingQuota) {
        switch (type) {
            case CrewType.FLIGHT_ATTENDENT:
                pairing.setCabinCrewQuota(remainingQuota);
                break;
            case CrewType.PILOT:
                pairing.setCockpitCrewQuota(remainingQuota);
                break;
        }
    }

    private double getMinDutyTimeFromPairings(List<Pairing> pairings) {
        if (!pairings.isEmpty()) {
            double minDutyTime = getPairingTotalDutyTime(pairings.get(0));
            for (Pairing p : pairings) {
                minDutyTime = Math.min(minDutyTime, getPairingTotalDutyTime(p));
            }
            return minDutyTime;
        } else {
            return 0;
        }

    }

    // check if the crew has already been assigned to the pairing
    private boolean crewPairingClash(Pairing pairing, FlightCrew flightCrew) {
        Query q = em.createQuery("SELECT pfc from PairingFlightCrew pfc WHERE pfc.flightCrew.systemUserId = :thisCrewId AND pfc.pairing.pairingId = :thisPairngId AND pfc.status = :status");
        q.setParameter("thisCrewId", flightCrew.getSystemUserId());
        q.setParameter("thisPairngId", pairing.getPairingId());
        q.setParameter("status", PairingCrewStatus.SUCCESS);

        try {
            q.getSingleResult();
            System.err.println("Crew Pairing Clashes");
            return true;
        } catch (Exception e) {
            System.err.println("Crew Pairing Do Not clash");
            return false;
        }
    }

    // check if the new pairing is within crew duty requirement if added to crew's next month line
    private boolean pairingWithinCrewRequirement(Pairing thisPairing, FlightCrew thisCrew, BiddingSession session) {
        double thisPairingDuration = getPairingTotalDutyTime(thisPairing);
        double existingDutyDuration = getCrewExistingDutyDuration(session, thisCrew);
        System.out.println("pairingWithinCrewRequirement(): \n\tthisPairingDuration: " + thisPairingDuration + "hrs\n\texistingDutyDuration: " + existingDutyDuration + "hrs");
        if (thisPairingDuration + existingDutyDuration > thisCrew.getPosition().getMaxMonthlyFlyingHrs()) {
            System.out.println("Pairing exceeds crew requirement");
            return false;
        } else {
            System.out.println("Pairing within crew requirement");
            return true;
        }
    }

    private boolean reachMaxCrewRequirement(double minDutyTime, FlightCrew fc, BiddingSession session) {
        return minDutyTime + getCrewExistingDutyDuration(session, fc) > fc.getPosition().getMaxMonthlyFlyingHrs();
    }

    // helper function to calculate total duty time of a specific pairing
    private double getPairingTotalDutyTime(Pairing pairing) {
        double totalTime = 0;
        for (FlightDuty duty : pairing.getFlightDuties()) {
            totalTime += (duty.getDismissTime().getTime() - duty.getReportTime().getTime()) * msToHrCoe;
        }
        return totalTime;
    }

    // calculate crew exiting next month's duty total duration
    private double getCrewExistingDutyDuration(BiddingSession session, FlightCrew crew) {
        Query q = em.createQuery("SELECT fd FROM PairingFlightCrew pfc, IN(pfc.pairing.flightDuties) fd, pfc.pairing p WHERE pfc.flightCrew.systemUserId = :crewId AND pfc.status = :status AND (:session MEMBER OF p.biddingSessions) ");
        q.setParameter("crewId", crew.getSystemUserId());
        q.setParameter("status", PairingCrewStatus.SUCCESS);
        q.setParameter("session", session);
        List<FlightDuty> existingDuties = (List<FlightDuty>) q.getResultList();

        double totalDutyTime = 0;
        for (FlightDuty fd : existingDuties) {
            totalDutyTime += (fd.getDismissTime().getTime() - fd.getReportTime().getTime()) * msToHrCoe;
        }
        return totalDutyTime;
    }

    // Find target flight crew from a list of PairingFlightCrews
    private PairingFlightCrew getPairingFlightCrewByCrew(List<PairingFlightCrew> pairingFlightCrews, FlightCrew flightCrew) {
        for (PairingFlightCrew pfc : pairingFlightCrews) {
            if (pfc.getFlightCrew().getSystemUserId().equals(flightCrew.getSystemUserId())) {
                return pfc;
            }
        }
        return null;
    }

    // Get flight crews who bid for thisPairing in order
    // 1. FCFS -> lastUpdateTime ASC
    // 3. Position high to low -> crew positionRank DSC
    // 2. Seniority high to low -> crew joinedDate ASC
    private List<FlightCrew> getOrderedFlightCrews(String type, Pairing thisPairing) {
        Query query = em.createQuery("SELECT pfc.flightCrew FROM PairingFlightCrew pfc WHERE pfc.pairing.pairingId = :inPairingId AND pfc.status = :status AND pfc.flightCrew.position.type = :type ORDER BY pfc.lastUpdateTime ASC, pfc.flightCrew.position.rank DESC, pfc.flightCrew.dateJoined ASC");
        query.setParameter("inPairingId", thisPairing.getPairingId());
        query.setParameter("status", PairingCrewStatus.PENDING);
        query.setParameter("type", type);
        return (List<FlightCrew>) query.getResultList();
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

    // helper function
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
                + "\n\tPairing ID" + pairing.getPairingId()
                + "\n\tPairing Code: " + pairing.getPairingCode()
                + "\n\tLayover Time: " + pairing.getLayoverTime()
                + "\n\tCabin Crew Quota: " + pairing.getCabinCrewQuota()
                + "\n\tCockpit Crew Quota: " + pairing.getCockpitCrewQuota()
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

    private void printFlightCrew(FlightCrew fc) {
        System.out.println("printFlightCrew(): "
                + "\n\tFlight Crew ID: " + fc.getFlightCrewID()
                + "\n\tFlight Crew Name: " + fc.getName()
                + "\n\tFlight Crew Position Type: " + fc.getPosition().getType()
                + "\n\tFlight Crew Position Name: " + fc.getPosition().getName()
                + "\n\tFlight Crew Position Rank: " + fc.getPosition().getRank()
                + "\n\tFlight Crew Joined Date: " + fc.getDateJoined()
        );
    }

}
