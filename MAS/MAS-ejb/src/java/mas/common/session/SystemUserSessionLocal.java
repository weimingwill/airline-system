/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.session;

import java.util.List;
import javax.ejb.Local;
import mas.common.entity.SystemMsg;
import mas.common.entity.SystemUser;
import mas.common.util.exception.InvalidPasswordException;
import mas.common.util.exception.UserDoesNotExistException;

/**
 *
 * @author Lewis
 */
@Local
public interface SystemUserSessionLocal {
    public void verifySystemUserPassword(String username, String inputPassword) throws UserDoesNotExistException, InvalidPasswordException;
    public List<SystemMsg> getUserMessages(String username);
    public List<SystemMsg> getUserUnreadMessages(String username);
    public SystemUser getSystemUserByName(String username);
    public void readUnreadMessages(String username);
    public List<SystemUser> getAllUsers();
    public List<SystemUser> getAllOtherUsers(String username);
}
