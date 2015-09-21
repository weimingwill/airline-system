/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;


import ams.ais.entity.CabinClass;
import ams.ais.entity.TicketFamily;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
/**
 *
 * @author Bowen
 */
@Stateless
public class CabinClassSession implements CabinClassSessionLocal {
    
    @EJB
    private TicketFamilySessionLocal ticketFamilySession;
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    private EntityManager entityManager;
    
    
    @Override
    public void createCabinClass(String name, String type) {
        CabinClass cabinClass = new CabinClass();
        cabinClass.create(type,name);
        entityManager.persist(cabinClass);
    }

    @Override
    public List<CabinClass> getAllCabinClass() {
        Query query = entityManager.createQuery("SELECT c FROM CabinClass c");
        return query.getResultList();
        

    }

    @Override
    public void deleteCabinClass(String name) {
        
        CabinClass cabinclass = getCabinClassByName(name);
        cabinclass.setDeleted(true);
        entityManager.merge(cabinclass);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CabinClass getCabinClassByName(String name) {
        Query query = entityManager.createQuery("SELECT c FROM CabinClass c WHERE c.name = :inCabinClassName");
        query.setParameter("inCabinClassName", name);
        CabinClass cabinclass = null;
        try {
            cabinclass = (CabinClass) query.getSingleResult();
        } catch (NoResultException ex) {
            cabinclass = null;
        }
        return cabinclass;
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}

}