/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.session;

import java.util.List;
import javax.ejb.Local;
import mas.common.entity.Permission;
import mas.common.entity.SystemMsg;
import mas.common.entity.SystemRole;
import mas.common.entity.SystemUser;
import mas.common.util.exception.ExistSuchUserEmailException;
import mas.common.util.exception.NoSuchEmailException;
import mas.common.util.exception.InvalidPasswordException;
import mas.common.util.exception.NoSuchMessageException;
import mas.common.util.exception.NoSuchUsernameException;
import mas.common.util.exception.ExistSuchUserException;
import mas.common.util.exception.NeedResetDigestException;
import mas.common.util.exception.NoSuchResetDigestException;
import mas.common.util.exception.NoSuchRoleException;
import mas.common.util.helper.SystemMsgHelper;

/**
 *
 * @author Lewis
 */
@Local
public interface SystemUserSessionLocal {

    //Users
    public SystemUser getSystemUserByName(String username) throws NoSuchUsernameException;

    public SystemUser getSystemUserByEmail(String email) throws NoSuchEmailException;

    public List<SystemUser> getAllUsers();

    public List<SystemUser> getAllOtherUsers(String username);

    public List<String> getAllOtherUsernames(String username);

    public List<String> getSystemUsernameList();

    public void createUser(String username, String password, String name, String email, String phone,
            String address, String department, List<SystemRole> roles) throws ExistSuchUserException, NoSuchRoleException;

    public void updateUserProfile(String username, String name, String email, String phone,
            String address, String department) throws NoSuchUsernameException, ExistSuchUserEmailException;

    public void changePassword(String username, String password) throws NoSuchUsernameException;

    public void resetPassword(String email, String resetDiget, String password) 
            throws NoSuchEmailException, NoSuchResetDigestException, NeedResetDigestException;

    public void setResetDigest(String email, String resetDigest) throws NoSuchEmailException;

    public void expireResetPassword(String email) throws NoSuchEmailException;

    public void lockUser(String username) throws NoSuchUsernameException;

    public void unlockUser(String username) throws NoSuchUsernameException;
    
    //Messages

    public List<SystemMsg> getUserMessages(String username);

    public List<SystemMsg> getUserUnreadMessages(String username);

    public void readUnreadMessages(String username) throws NoSuchMessageException;

    public void flagMessage(String username, Long messageId) throws NoSuchMessageException;

    public void unFlagMessage(String username, Long messageId) throws NoSuchMessageException;

    public Long deleteMessage(String username, Long messageId) throws NoSuchMessageException;

    public List<SystemMsgHelper> getSystemMsgHelpers(String username);
    
    public List<String> getSystemMsgSenders(String username);
    
    public List<SystemMsg> getSenderMsgs(String username, String sender);
    
    //Verification
    public void verifySystemUserPassword(String username, String inputPassword) throws NoSuchUsernameException, InvalidPasswordException;

    public void verifySystemUserEmail(String email) throws NoSuchEmailException;

    public void verifySystemUserExistence(String useranme, String email) throws ExistSuchUserException;

    public void verifyUserEmailExistence(String email) throws ExistSuchUserEmailException;

    public void verifyResetPassword(String email, String resetDigest) throws NoSuchEmailException, NeedResetDigestException, NoSuchResetDigestException;
    
    public void assignUserToRole(List<SystemUser> users, List<SystemRole> roles);

    public void assignUserToRole(SystemUser user, List<SystemRole> roles);
            
            //Access Control
    public List<SystemRole> getUserRoles(String username);

    public List<Permission> getUserPermissions(String username);
    
    public List<String> getUserPermissionSystems(String username);

    public boolean hasRole(String username, String roleName);

    public boolean isAdmin(String username);
    
    public String deleteUser(String username) throws NoSuchUsernameException;

}
