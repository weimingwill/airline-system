/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.session;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mas.common.entity.SystemUser;

/**
 *
 * @author Lewis
 */
@Stateless
public class SystemUserSession implements SystemUserSessionLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public SystemUser getSystemUser(String userName) {
        Query query = entityManager.createQuery("SELECT u FROM SystemUser u WHERE u.username = :inUserName");
        query.setParameter("inUserName", userName);
        SystemUser systemUser = null;
        try {
            systemUser = (SystemUser) query.getSingleResult();
        } catch (NoResultException ex) {
            ex.printStackTrace();
        }
        return systemUser;
    }
}
