/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.session;

import java.util.List;
import javax.ejb.Local;
import mas.common.entity.SystemMsg;
import mas.common.entity.SystemRole;
import mas.common.entity.SystemUser;
import mas.common.util.exception.ExistSuchUserEmailException;
import mas.common.util.exception.NoSuchEmailException;
import mas.common.util.exception.InvalidPasswordException;
import mas.common.util.exception.NoSuchMessageException;
import mas.common.util.exception.NoSuchUsernameException;
import mas.common.util.exception.ExistSuchUserException;
import mas.common.util.exception.NoSuchRoleException;
import mas.common.util.helper.RolePermission;
import mas.common.util.helper.UserRolePermission;

/**
 *
 * @author Lewis
 */
@Local
public interface SystemUserSessionLocal {

    public SystemUser getSystemUserByName(String username) throws NoSuchUsernameException;

    public SystemUser getSystemUserByEmail(String email) throws NoSuchEmailException;
    
    //Messages
    public List<SystemMsg> getUserMessages(String username);

    public List<SystemMsg> getUserUnreadMessages(String username);

    public void readUnreadMessages(String username) throws NoSuchMessageException;

    public void flagMessage(String username, String message) throws NoSuchMessageException;
    
    public void unFlagMessage(String username, String message) throws NoSuchMessageException;
    
    public String deleteMessage(String username, String message) throws NoSuchMessageException;
    
    public void verifySystemUserPassword(String username, String inputPassword) throws NoSuchUsernameException, InvalidPasswordException;

    public void verifySystemUserEmail(String email) throws NoSuchEmailException;

    public void verifySystemUserExistence(String useranme, String email) throws ExistSuchUserException;
    
    public void verifyUserEmailExistence(String email) throws ExistSuchUserEmailException;
    
    public List<SystemUser> getAllUsers();

    public List<SystemUser> getAllOtherUsers(String username);

    public List<String> getAllOtherUsernames(String username);
    
    public void createUser(String username, String password, String email, List<SystemRole> roles) throws ExistSuchUserException, NoSuchRoleException;
//    public void createUser(String username, String password, String[] roleNames) throws ExistSuchUserException, NoSuchRoleException;

    public void updateUserProfile(String username, String email) throws NoSuchUsernameException, ExistSuchUserEmailException;
    
    public void changePassword(String username, String password) throws NoSuchUsernameException;
    
    public List<String> getSystemUsernameList();

    public void resetPassword(String email, String password) throws NoSuchEmailException;

    public void setResetDigest(String email, String resetDigest) throws NoSuchEmailException;

    public void expireResetPassword(String email) throws NoSuchEmailException;

    public void lockUser(String username) throws NoSuchUsernameException;

    public void unlockUser(String username) throws NoSuchUsernameException;

    public void assignUserToRole(List<SystemUser> users, List<SystemRole> roles);

    public void assignUserToRole(SystemUser user, List<SystemRole> roles);

    public List<UserRolePermission> getAllUsersRolesPermissions();

    public List<RolePermission> getUserRolesPermissions(String username);

    public List<SystemRole> getUserRoles(String username);

    public boolean hasRole(String username, String roleName);

    public boolean isAdmin(String username);
}
