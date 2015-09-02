/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.session;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import mas.common.entity.SystemUser;
import mas.common.util.helper.UserMsg;
import mas.common.util.exception.InvalidPasswordException;
import mas.common.util.exception.UserDoesNotExistException;

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
    public void verifySystemUserPassword(String userName, String inputPassword) throws UserDoesNotExistException, InvalidPasswordException {
        SystemUser user = getSystemUser(userName);
        if (user == null) {
            throw new UserDoesNotExistException(UserMsg.WRONG_USERNAME_ERROR);
        } else {
            String userPassword = user.getPassword();
            if (!userPassword.equals(inputPassword)) {
                throw new InvalidPasswordException(UserMsg.WRONG_PASSWORD_ERROR);
            }
        }
    }
}
