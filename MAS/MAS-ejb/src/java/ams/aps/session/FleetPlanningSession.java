/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.session;

import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftType;
import ams.aps.util.exception.EmptyTableException;
import ams.aps.util.helper.Message;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author ChuningLiu
 */
@Stateless
public class FleetPlanningSession implements FleetPlanningSessionLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<AircraftType> getAircraftModels() throws EmptyTableException{
        Query query = entityManager.createQuery("SELECT a FROM AircraftType a");
        List<AircraftType> aircraftModels = null;
        try {
            aircraftModels = (List<AircraftType>) query.getResultList();
        } catch (NoResultException ex) {
            throw new EmptyTableException(Message.EMPTY_TABLE);
        }
        return aircraftModels;
    }

    @Override
    public List<Aircraft> getOwnedAircrafts() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Aircraft> getObsoleteAircrafts() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateAircraftInfo(Aircraft updatedAircraft) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addNewAircraft(Aircraft newAircraft) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void markAircraftAsRetired(String tailNo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AircraftType getAircraftTypeById(Long id) {
        return entityManager.find(AircraftType.class, id);
    }    
}
