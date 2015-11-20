/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.crm.entity.Membership;
import ams.crm.entity.Privilege;
import ams.crm.entity.PrivilegeValue;
import ams.crm.util.exception.NoSuchPrivilegeValueException;
import ams.crm.util.helper.CrmMsg;
import java.util.List;
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
public class PrivilegeSession implements PrivilegeSessionLocal {
    @PersistenceContext
    private EntityManager em;
    @Override
    public List<Privilege> getAllPrivileges() {
         Query query = em.createQuery("SELECT p FROM Privilege p");
        return query.getResultList();
    }
    
    @Override
    public List<Membership> getAllMemberships(){
        Query query = em.createQuery("SELECT m FROM Membership m");
        return query.getResultList();
    }
    
    @Override
    public PrivilegeValue getPVById(Long pId,Long mid) throws NoSuchPrivilegeValueException {
       Query query = em.createQuery("SELECT u FROM PrivilegeValue u WHERE u.privilegeValueId.privilegeId = :inpId AND u.membership.id = :inmId");
        query.setParameter("inpId", pId);
        query.setParameter("inmId", mid);
        PrivilegeValue pv;
        try {
           pv = (PrivilegeValue) query.getSingleResult();
        } catch (NoResultException ex) {
            throw new NoSuchPrivilegeValueException(CrmMsg.No_SUCH_PRIVILEGE_VALUE_ERROR);
        }
        return pv;
    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
