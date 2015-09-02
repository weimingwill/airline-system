/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.session;

import java.util.Map;
import javax.ejb.Local;
import mas.common.entity.SystemUser;
import mas.common.util.exception.InvalidPasswordException;
import mas.common.util.exception.UserDoesNotExistException;

/**
 *
 * @author Lewis
 */
@Local
public interface SystemUserSessionLocal {
    public SystemUser getSystemUser(String userName);
    public void verifySystemUserPassword(String userName, String inputPassword) throws UserDoesNotExistException, InvalidPasswordException;
    
}
