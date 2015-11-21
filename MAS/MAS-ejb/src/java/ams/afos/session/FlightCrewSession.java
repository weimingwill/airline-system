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
import ams.aps.util.exception.EmptyTableException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mas.common.entity.SystemUser;

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
        Calendar cal = Calendar.getInstance();
        List<PairingFlightCrew> pairingFlightCrews = new ArrayList();
        for (Pairing thisPairing : pairings) {
            System.out.println(flightCrew);
            System.out.println(thisPairing);
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
            pairingFlightCrews.add(pairingFlightCrew);
        }
        setPairingFlightCrewsToPairing(pairings, pairingFlightCrews);

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
    public List<SwappingRequest> getMatchedSwappingReqs(SwappingRequest newRequest) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void matchSelectedSwappingReq(SwappingRequest thisRequest, SwappingRequest selectedRequest) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendSwappingRequest(SwappingRequest newRequest) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
}
