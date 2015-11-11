/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.session;

import ams.afos.entity.FlightCrew;
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
    public List<FlightCrew> getAllFlightCrew() throws EmptyTableException{
        Query query = em.createQuery("SELECT f FROM FlightCrew f WHERE f.deleted = FALSE");
        List<FlightCrew> flightCrews;
        try{
            flightCrews = (List<FlightCrew>) query.getResultList();
            return flightCrews;
        }catch(Exception e){
            throw new EmptyTableException("No Flight Crew Found in Database!");
        }
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    public void persist(Object object) {
        em.persist(object);
    }
}
