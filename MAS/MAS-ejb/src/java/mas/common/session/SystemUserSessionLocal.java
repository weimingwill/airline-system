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
import mas.common.util.exception.NoSuchEmailException;
import mas.common.util.exception.InvalidPasswordException;
import mas.common.util.exception.NoMessageException;
import mas.common.util.exception.NoSuchUsernameException;
import mas.common.util.exception.ExistSuchUserException;

/**
 *
 * @author Lewis
 */
@Local
public interface SystemUserSessionLocal {
    public void verifySystemUserPassword(String username, String inputPassword) throws NoSuchUsernameException, InvalidPasswordException;
    public List<SystemMsg> getUserMessages(String username);
    public List<SystemMsg> getUserUnreadMessages(String username) throws NoSuchUsernameException;
    public SystemUser getSystemUserByName(String username);
    public void readUnreadMessages(String username)  throws NoMessageException;
    public List<SystemUser> getAllUsers();
    public List<SystemUser> getAllOtherUsers(String uresetPasswordsername);
    public void createUser(String username, String password) throws ExistSuchUserException;
    public List<String> getSystemUsernameList();
    public void verifySystemUserEmail(String email) throws NoSuchEmailException;
    public SystemUser getSystemUserByEmail(String email);
    public void resetPassword(String email, String password) throws NoSuchEmailException ;
    public void setResetDigest(String email, String resetDigest);
    public void expireResetPassword(String email);
    public void lockUser(String username);
    public void unlockUser(String username);
}
