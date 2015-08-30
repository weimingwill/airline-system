/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.session;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import ams.aps.entity.Country;
import javax.persistence.NoResultException;
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
    public Country getCountryByCode(String code){
        Query query = entityManager.createQuery("SELECT c FROM Country c WHERE c.contryCode = :inCode");
        query.setParameter("inCode", code);
        Country country = null;
        try{
            country = (Country) query.getSingleResult();
        } catch (NoResultException e){
            e.printStackTrace();
        }
        return country;
    }
}
