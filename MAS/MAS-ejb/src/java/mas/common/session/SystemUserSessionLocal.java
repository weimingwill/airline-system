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
    public void verifySystemUserPassword(String username, String inputPassword) throws NoSuchUsernameException, InvalidPasswordException;
    public List<SystemMsg> getUserMessages(String username);
    public List<SystemMsg> getUserUnreadMessages(String username) throws NoSuchUsernameException;
    public SystemUser getSystemUserByName(String username);
    public void readUnreadMessages(String username)  throws NoSuchMessageException;
    public List<SystemUser> getAllUsers();
    public List<SystemUser> getAllOtherUsers(String uresetPasswordsername);
    public void createUser(String username, String password, String email, List<SystemRole> roles) throws ExistSuchUserException, NoSuchRoleException;
//    public void createUser(String username, String password, String[] roleNames) throws ExistSuchUserException, NoSuchRoleException;
    public List<String> getSystemUsernameList();
    public void verifySystemUserEmail(String email) throws NoSuchEmailException;
    public SystemUser getSystemUserByEmail(String email);
    public void resetPassword(String email, String password) throws NoSuchEmailException ;
    public void setResetDigest(String email, String resetDigest);
    public void expireResetPassword(String email);
    public void lockUser(String username);
    public void unlockUser(String username);
    public void assignUserToRole(List<SystemUser> users, List<SystemRole> roles);
    public void assignUserToRole(SystemUser user, List<SystemRole> roles);
    public List<UserRolePermission> getAllUsersRolesPermissions();
    public List<RolePermission> getUserRolesPermissions(String username) throws NoSuchUsernameException;
    public List<SystemRole> getUserRoles(String username) throws NoSuchUsernameException;
    public boolean hasRole(String username, String roleName) throws NoSuchUsernameException, NoSuchRoleException;
    public boolean isAdmin(String username);
}
