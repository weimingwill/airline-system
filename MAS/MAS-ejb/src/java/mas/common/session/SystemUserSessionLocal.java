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
import mas.common.util.exception.EmailDoesNotExistException;
import mas.common.util.exception.InvalidPasswordException;
import mas.common.util.exception.UserDoesNotExistException;
import mas.common.util.exception.UserExistException;

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
    public List<SystemUser> getAllOtherUsers(String uresetPasswordsername);
    public void createUser(String username, String password) throws UserExistException;
    public List<String> getSystemUsernameList();
    public void verifySystemUserEmail(String email) throws EmailDoesNotExistException;
    public SystemUser getSystemUserByEmail(String email);
    public void resetPassword(String email, String password);
    public void setResetDigest(String email, String resetDigest);
}
