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
import ams.afos.entity.helper.PairingCrewId;
import ams.afos.util.helper.BiddingSessionStatus;
import ams.afos.util.helper.PairingCrewStatus;
import ams.afos.util.helper.SwappingReqStataus;
import ams.aps.util.exception.EmptyTableException;
import com.fasterxml.classmate.TypeBindings;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Lewis
 */
@Stateless
public class FlightCrewSession implements FlightCrewSessionLocal {

    @PersistenceContext(unitName = "MAS-ejbPU")
    private EntityManager em;

    @Override
    public List<FlightCrew> getAllFlightCrew() throws EmptyTableException {
        Query query = em.createQuery("SELECT f FROM FlightCrew f WHERE f.deleted = FALSE");
        List<FlightCrew> flightCrews;
        try {
            flightCrews = (List<FlightCrew>) query.getResultList();
            return flightCrews;
        } catch (Exception e) {
            throw new EmptyTableException("No Flight Crew Found in Database!");
        }
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public void persist(Object object) {
        em.persist(object);
    }

    @Override
    public void updateFlightChecklist(Checklist checklist) {
        em.merge(checklist);
    }

    @Override
    public void placeBidForPairings(List<Pairing> pairings, FlightCrew flightCrew) {
        List<PairingFlightCrew> pairingFlightCrews = new ArrayList();
        for (Pairing thisPairing : pairings) {
            System.out.println(flightCrew);
            System.out.println(thisPairing);
            pairingFlightCrews.add(associatePairingCrew(thisPairing, flightCrew));
        }
        setPairingFlightCrewsToPairing(pairings, pairingFlightCrews);

    }

    private PairingFlightCrew associatePairingCrew(Pairing thisPairing, FlightCrew flightCrew) {
        Calendar cal = Calendar.getInstance();
        PairingFlightCrew pairingFlightCrew = new PairingFlightCrew();
        pairingFlightCrew.setLastUpdateTime(cal.getTime());
        pairingFlightCrew.setStatus(PairingCrewStatus.PENDING);
        PairingCrewId pairingCrewId = new PairingCrewId();
        pairingCrewId.setPairingId(thisPairing.getPairingId());
        pairingCrewId.setSystemUserId(flightCrew.getSystemUserId());
        pairingFlightCrew.setPairingCrewId(pairingCrewId);
        pairingFlightCrew.setFlightCrew(em.find(FlightCrew.class, flightCrew.getSystemUserId()));
        pairingFlightCrew.setPairing(em.find(Pairing.class, thisPairing.getPairingId()));
        em.persist(pairingFlightCrew);
        return pairingFlightCrew;
    }

    private void setPairingFlightCrewsToPairing(List<Pairing> pairings, List<PairingFlightCrew> pairingFlightCrews) {
        for (Pairing pairing : pairings) {
            pairing.setPairingFlightCrews(pairingFlightCrews);
            em.merge(pairing);
        }
        em.flush();
    }

    @Override
    public void updateBids(List<PairingFlightCrew> selectedPairings, FlightCrew flightCrew) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<PairingFlightCrew> getFlightCrewBiddingHistory(FlightCrew flightCrew) {
        Date currMonthFirstDay = getCurrMonthFirstDay();
        Date currMonthLastDay = getCurrMonthLastDay();
        Query query = em.createQuery("SELECT pfc FROM PairingFlightCrew pfc WHERE pfc.flightCrew.systemUserId = :id ");
        query.setParameter("id", flightCrew.getSystemUserId());
//        query.setParameter("currMonthFirstDay", currMonthFirstDay);
//        query.setParameter("currMonthLastDay", currMonthLastDay);
        return (List<PairingFlightCrew>) query.getResultList();
    }

    private Date getCurrMonthFirstDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
        return calendar.getTime();
    }

    private Date getCurrMonthLastDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
        return calendar.getTime();
    }

    @Override
    public List<SwappingRequest> getMatchedSwappingReqs(Pairing chosenPairing, Pairing targetPairing, List<Pairing> existingPairings) {
        Query q = em.createQuery("SELECT s FROM SwappingRequest s WHERE s.chosenPairing.pairingId = :targetPairingId AND s.targetPairing.pairingId = :chosenPairingId AND s.status = :status");
        q.setParameter("targetPairingId", targetPairing.getPairingId());
        q.setParameter("chosenPairingId", chosenPairing.getPairingId());
        q.setParameter("status", SwappingReqStataus.PENDING);
        List<SwappingRequest> outputList = (List<SwappingRequest>) q.getResultList();
        removeClashedReqs(outputList, existingPairings);
        return outputList;
    }

    @Override
    public List<SwappingRequest> getAlternativeSwappingReqs(Pairing chosenPairing, List<Pairing> existingPairings) {
        Query q = em.createQuery("SELECT s FROM SwappingRequest s WHERE s.targetPairing.pairingId = :chosenPairingId AND s.status = :status");
        q.setParameter("chosenPairingId", chosenPairing.getPairingId());
        q.setParameter("status", SwappingReqStataus.PENDING);
        List<SwappingRequest> outputList = (List<SwappingRequest>) q.getResultList();
        System.out.println("alternative reqs: " + outputList);
        removeClashedReqs(outputList, existingPairings);
        System.out.println("alternative reqs (After remove clash): " + outputList);
        return outputList;
    }

    private void removeClashedReqs(List<SwappingRequest> requests, List<Pairing> existingPairings) {
        if (requests != null && !requests.isEmpty()) {
            List<SwappingRequest> clashedRequests = new ArrayList();
            for (SwappingRequest request : requests) {
                for (Pairing pairing : existingPairings) {
                    if (!Objects.equals(pairing.getPairingId(), request.getChosenPairing().getPairingId())) {
                        if (clashPairing(request.getChosenPairing(), pairing)) {
                            clashedRequests.add(request);
                        }
                    }
                }

            }
            requests.removeAll(clashedRequests);
        }
    }

    private boolean clashPairing(Pairing p1, Pairing p2) {
        Date p1StartTime = getPairingStartDate(p1),
                p1EndTime = getPairingEndDate(p1),
                p2StartTime = getPairingStartDate(p2),
                p2EndTime = getPairingEndDate(p2);
        if ((p1StartTime.getTime() >= p2StartTime.getTime() && p1StartTime.getTime() <= p2EndTime.getTime())
                || (p1EndTime.getTime() >= p2StartTime.getTime() && p1EndTime.getTime() <= p2EndTime.getTime())
                || (p1StartTime.getTime() <= p2StartTime.getTime() && p1EndTime.getTime() >= p2EndTime.getTime())) {
            return true;
        } else {
            return false;
        }

    }

    private Date getPairingStartDate(Pairing pairing) {
        if (pairing != null) {
            return pairing.getFlightDuties().get(0).getReportTime();
        } else {
            return null;
        }
    }

    private Date getPairingEndDate(Pairing pairing) {
        if (pairing != null) {
            return pairing.getFlightDuties().get(pairing.getFlightDuties().size() - 1).getDismissTime();
        } else {
            return null;
        }
    }

    @Override
    public void matchSelectedSwappingReq(SwappingRequest selectedRequest) {
        FlightCrew sender = em.find(FlightCrew.class, selectedRequest.getSender().getSystemUserId());
        FlightCrew receiver = em.find(FlightCrew.class, selectedRequest.getReceiver().getSystemUserId());
        Pairing senderPairing = em.find(Pairing.class, selectedRequest.getChosenPairing().getPairingId());
        Pairing receiverPairing = em.find(Pairing.class, selectedRequest.getTargetPairing().getPairingId());
        PairingFlightCrew senderPFC = em.find(PairingFlightCrew.class, findPairingCrew(sender, senderPairing).getPairingCrewId());
        PairingFlightCrew receiverPFC = em.find(PairingFlightCrew.class, findPairingCrew(receiver, receiverPairing).getPairingCrewId());
        SwappingRequest swappingRequest = em.find(SwappingRequest.class, selectedRequest.getId());

        // check request status
        swappingRequest.setReceiver(receiver);
        swappingRequest.setStatus(selectedRequest.getStatus());
        em.merge(swappingRequest);

        // delete old association
        senderPFC.setStatus(PairingCrewStatus.SWAPPED);
        receiverPFC.setStatus(PairingCrewStatus.SWAPPED);

        // establish new association
        PairingFlightCrew newSenderPFC = associatePairingCrew(receiverPairing, sender);
        PairingFlightCrew newReceiverPFC = associatePairingCrew(senderPairing, receiver);

        newSenderPFC.setStatus(PairingCrewStatus.SUCCESS);
        newReceiverPFC.setStatus(PairingCrewStatus.SUCCESS);

        em.merge(newSenderPFC);
        em.merge(newReceiverPFC);
        em.merge(senderPFC);
        em.merge(receiverPFC);
        em.flush();
    }

    private PairingFlightCrew findPairingCrew(FlightCrew flightCrew, Pairing pairing) {
        Query q = em.createQuery("SELECT pfc FROM PairingFlightCrew pfc WHERE pfc.flightCrew.systemUserId = :crewId AND pfc.pairing.pairingId = :pairingId AND pfc.status = :status");
        q.setParameter("crewId", flightCrew.getSystemUserId());
        q.setParameter("pairingId", pairing.getPairingId());
        q.setParameter("status", PairingCrewStatus.SUCCESS);
        return (PairingFlightCrew) q.getSingleResult();
    }

    @Override
    public void sendSwappingRequest(SwappingRequest newRequest) {
        newRequest.setTargetPairing(em.find(Pairing.class, newRequest.getTargetPairing().getPairingId()));
        newRequest.setChosenPairing(em.find(Pairing.class, newRequest.getChosenPairing().getPairingId()));
        newRequest.setSender(em.find(FlightCrew.class, newRequest.getSender().getSystemUserId()));
        em.persist(newRequest);
    }

    @Override
    public void cancelSwappingRequest(SwappingRequest thisRequest) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FlightCrew getFlightCrewByUsername(String username) {
        Query query = em.createQuery("SELECT fc FROM FlightCrew fc WHERE fc.username =:username");
        query.setParameter("username", username);
        try {
            return (FlightCrew) query.getSingleResult();
        } catch (Exception ex) {
            System.out.println("No such user exits");
        }
        return null;
    }

    @Override
    public List<BiddingSession> getEligibleBiddingSessions(FlightCrew flightCrew) {

        Query query = em.createQuery("SELECT bs FROM BiddingSession bs WHERE bs.status = :status AND :crew MEMBER OF bs.flightCrews ORDER BY bs.startTime ASC");
        query.setParameter("status", BiddingSessionStatus.CREATED);
        query.setParameter("crew", flightCrew);
        return (List<BiddingSession>) query.getResultList();
    }

    @Override
    public List<Pairing> getAllEligiblePairings(BiddingSession session, FlightCrew flightCrew) {
        Query query = em.createQuery("SELECT p FROM Pairing p, IN(p.biddingSessions) AS bs WHERE (:session MEMBER OF p.biddingSessions) AND (:crew MEMBER OF bs.flightCrews) AND p NOT IN(SELECT pfc.pairing FROM PairingFlightCrew pfc WHERE pfc.flightCrew.systemUserId = :crewId)");
        query.setParameter("session", session);
        query.setParameter("crew", flightCrew);
        query.setParameter("crewId", flightCrew.getSystemUserId());
        return (List<Pairing>) query.getResultList();
    }

    @Override
    public Pairing getPairingById(Long id) {
        return em.find(Pairing.class, id);
    }

    @Override
    public List<FlightDuty> getCrewCurrMonthDuties(FlightCrew thisCrew) {
        Calendar temp = Calendar.getInstance();
        Query q = em.createQuery("SELECT DISTINCT fd FROM PairingFlightCrew pfc, IN(pfc.pairing.flightDuties) fd, IN(fd.flightSchedules) fs WHERE (fs.departDate BETWEEN :nextMonthFirstDay AND :nextMonthLastDay) AND pfc.flightCrew.systemUserId = :crewId AND pfc.status = :status ORDER BY fs.departDate");
        temp.setTime(getNextMonthFirstDay());
        temp.add(Calendar.MONTH, -1);
        q.setParameter("nextMonthFirstDay", temp.getTime());
        temp.setTime(getNextMonthLastDay());
        temp.add(Calendar.MONTH, -1);
        q.setParameter("nextMonthLastDay", temp.getTime());
        q.setParameter("crewId", thisCrew.getSystemUserId());
        q.setParameter("status", PairingCrewStatus.SUCCESS);
        return q.getResultList();
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

    @Override
    public List<Pairing> getCrewNextMonthPairings(FlightCrew flightCrew) {
        Query q = em.createQuery("SELECT DISTINCT pfc.pairing FROM PairingFlightCrew pfc, IN(pfc.pairing.flightDuties) fd, IN(fd.flightSchedules) fs WHERE pfc.flightCrew.systemUserId = :crewId AND pfc.status = :status AND fs.departDate BETWEEN :nextMonthFirstDay AND :nextMonthlastDay ORDER BY fs.departDate");
        q.setParameter("crewId", flightCrew.getSystemUserId());
        q.setParameter("status", PairingCrewStatus.SUCCESS);
        q.setParameter("nextMonthFirstDay", getNextMonthFirstDay());
        q.setParameter("nextMonthlastDay", getNextMonthLastDay());
        return (List<Pairing>) q.getResultList();
    }

    @Override
    public List<Pairing> getAllNextMonthPairings(FlightCrew flightCrew) {
        Query q = em.createQuery("SELECT p FROM BiddingSession b, IN(b.pairings) p WHERE b.createdTime BETWEEN :currentMonthFirstDay AND :currentMonthLastDay AND :flightCrew MEMBER OF b.flightCrews");
        q.setParameter("currentMonthFirstDay", getCurrMonthFirstDay());
        q.setParameter("currentMonthLastDay", getCurrMonthLastDay());
        q.setParameter("flightCrew", flightCrew);
        return (List<Pairing>) q.getResultList();
    }

}
