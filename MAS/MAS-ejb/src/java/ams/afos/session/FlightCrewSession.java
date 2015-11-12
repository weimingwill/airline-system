/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.session;

import ams.afos.entity.BiddingSession;
import ams.afos.entity.Checklist;
import ams.afos.entity.FlightCrew;
import ams.afos.entity.Pairing;
import ams.afos.entity.PairingFlightCrew;
import ams.afos.entity.SwappingRequest;
import ams.afos.util.helper.BiddingSessionStatus;
import ams.aps.util.exception.EmptyTableException;
import java.util.List;
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
    public void updatePreFlightChecklist(Checklist checklist) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updatePostFlightChecklist(Checklist checklist) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void placeBidForPairings(List<Pairing> pairings, FlightCrew flightCrew) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateBids(List<PairingFlightCrew> selectedPairings, FlightCrew flightCrew) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<PairingFlightCrew> getFlightCrewBiddingHistory(FlightCrew flightCrew) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    //    @Override
//    public List<Pairing> getAllEligiblePairings(FlightCrew flightCrew) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
}
