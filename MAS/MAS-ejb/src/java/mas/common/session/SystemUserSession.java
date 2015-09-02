/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.session;

import javax.ejb.EJB;
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
    @EJB
    private AccessControlSessionLocal accessControlSession;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public SystemUser getSystemUser(String userName) {
        return accessControlSession.getSystemUserByName(userName);
    }
    
    @Override
    public Boolean verifySystemUserPassword(String userName, String inputPassword){
        SystemUser user = getSystemUser(userName);
        String userPassword = user.getPassword();
        if(userPassword.equals(inputPassword)){
            return true;
        } else {
            return false;
        }
    }
}
