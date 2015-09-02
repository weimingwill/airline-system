/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.session;

import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import mas.common.entity.SystemUser;
import mas.common.util.helper.UserMsg;

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
    public Map<Boolean,String> verifySystemUserPassword(String userName, String inputPassword) {
        SystemUser user = getSystemUser(userName);
        Map<Boolean, String> resultMap = new HashMap<>();
        if (user == null) {
            resultMap.put(Boolean.FALSE, UserMsg.WRONG_USERNAME_ERROR);
        } else {
            String userPassword = user.getPassword();
            if (userPassword.equals(inputPassword)) {
                resultMap.put(Boolean.TRUE, UserMsg.LOGIN_SUCCESS_MSG);
            } else {
                resultMap.put(Boolean.FALSE, UserMsg.WRONG_PASSWORD_ERROR);
            }
        }
        return resultMap;
    }
}
