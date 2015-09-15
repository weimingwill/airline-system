/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.session;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mas.common.entity.Permission;
import mas.common.entity.SystemMsg;
import mas.common.entity.SystemRole;
import mas.common.entity.SystemUser;
import mas.common.util.exception.NoSuchEmailException;
import mas.common.util.helper.UserMsg;
import mas.common.util.exception.InvalidPasswordException;
import mas.common.util.exception.NoSuchMessageException;
import mas.common.util.exception.NoSuchUsernameException;
import mas.common.util.exception.ExistSuchUserException;
import mas.common.util.exception.NoSuchRoleException;
import mas.common.util.helper.CreateToken;
import mas.common.util.helper.RolePermission;
import mas.common.util.helper.UserRolePermission;

@Stateless
public class SystemUserSession implements SystemUserSessionLocal {

    @EJB
    private RoleSessionLocal roleSession;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    private EntityManager entityManager;

    private SystemMsg systemMsg;

    @Override
    public SystemUser getSystemUserByName(String username) {
        Query query = entityManager.createQuery("SELECT u FROM SystemUser u WHERE u.username = :inUserName");
        query.setParameter("inUserName", username);
        SystemUser user = null;
        try {
            user = (SystemUser) query.getSingleResult();
        } catch (NoResultException ex) {
            user = null;
        }
        return user;
    }

    @Override
    public SystemUser getSystemUserByEmail(String email) {
        Query query = entityManager.createQuery("SELECT u FROM SystemUser u WHERE u.email = :email");
        query.setParameter("email", email);
        SystemUser user = null;
        try {
            user = (SystemUser) query.getSingleResult();
        } catch (NoResultException ex) {
            ex.printStackTrace();
        }
        return user;
    }

    @Override
    public void verifySystemUserPassword(String username, String inputPassword) throws NoSuchUsernameException, InvalidPasswordException {
        SystemUser user = getSystemUserByName(username);
        if (user == null) {
            throw new NoSuchUsernameException(UserMsg.NO_SUCH_USERNAME_ERROR);
        } else {
            String userPassword = user.getPassword();
            if (!userPassword.equals(inputPassword)) {
                throw new InvalidPasswordException(UserMsg.WRONG_PASSWORD_ERROR);
            }
        }
    }

    @Override
    public List<SystemMsg> getUserMessages(String username) {
        SystemUser user = getSystemUserByName(username);
        try {
            return user.getSystemMsgs();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<SystemMsg> getUserUnreadMessages(String username) throws NoSuchUsernameException {
        SystemUser user = getSystemUserByName(username);
        if (user == null) {
            throw new NoSuchUsernameException(UserMsg.NO_SUCH_USERNAME_ERROR);
        } else {
            List<SystemMsg> unreadMsg = new ArrayList<SystemMsg>();
            try {
                for (SystemMsg msg : user.getSystemMsgs()) {
                    if (!msg.isReaded()) {
                        unreadMsg.add(msg);
                    }
                }
                return unreadMsg;
            } catch (Exception e) {
                return null;
            }
        }
    }

    @Override
    public void readUnreadMessages(String username) throws NoSuchMessageException {
        List<SystemMsg> unreadMsgs = getUserMessages(username);
        if (unreadMsgs == null) {
            throw new NoSuchMessageException(UserMsg.NO_MESSAGE_ERROR);
        } else {
            for (SystemMsg msg : unreadMsgs) {
                msg.setReaded(true);
                entityManager.merge(msg);
            }
        }
    }

    @Override
    public List<SystemUser> getAllUsers() {
        Query query = entityManager.createQuery("SELECT u FROM SystemUser u");
        return query.getResultList();
    }

    @Override
    public List<SystemUser> getAllOtherUsers(String username) {
        Query query = entityManager.createQuery("SELECT u FROM SystemUser u where u.username <> :username");
        query.setParameter("username", username);
        return query.getResultList();
    }

    @Override
    public void createUser(String username, String password, String email, List<SystemRole> roles) throws ExistSuchUserException, NoSuchRoleException {
        SystemUser user = getSystemUserByName(username);
        if (user != null) {
            throw new ExistSuchUserException(UserMsg.EXIST_USERNAME_ERROR);
        } else {
            SystemUser systemUser = new SystemUser();
            systemUser.create(username, password, email);
            for (SystemRole role : roles) {
                SystemRole r = roleSession.getSystemRoleByName(role.getRoleName());
                systemUser.getSystemRoles().add(r);
                r.getSystemUsers().add(systemUser);
            }
            entityManager.persist(systemUser);
        }
    }

    @Override
    public void assignUserToRole(List<SystemUser> users, List<SystemRole> roles) {
        for (SystemUser user : users) {
            SystemUser systemUser = entityManager.find(SystemUser.class, user.getSystemUserId());
            for (SystemRole role : roles) {
                SystemRole systemRole = entityManager.find(SystemRole.class, role.getSystemRoleId());
                systemUser.getSystemRoles().add(systemRole);
                systemRole.getSystemUsers().add(systemUser);
            }
            entityManager.merge(systemUser);
        }
    }

    @Override
    public void assignUserToRole(SystemUser user, List<SystemRole> roles) {
        SystemUser systemUser = entityManager.find(SystemUser.class, user.getSystemUserId());
        systemUser.setSystemRoles(roles);
        entityManager.merge(systemUser);
    }

    @Override
    public List<SystemRole> getUserRoles(String username) throws NoSuchUsernameException {
        SystemUser user = getSystemUserByName(username);
        if (user == null) {
            throw new NoSuchUsernameException(UserMsg.NO_SUCH_USERNAME_ERROR);
        } else {
            return user.getSystemRoles();
        }
    }

    @Override
    public List<String> getSystemUsernameList() {
        Query query = entityManager.createQuery("SELECT u.username FROM SystemUser u");
        return (List<String>) query.getResultList();
    }

    @Override
    public void verifySystemUserEmail(String email) throws NoSuchEmailException {
        SystemUser user = getSystemUserByEmail(email);
        if (user == null) {
            throw new NoSuchEmailException(UserMsg.NO_SUCH_EMAIL_ERROR);
        }
    }

    @Override
    public void resetPassword(String email, String password) throws NoSuchEmailException {
        SystemUser user = getSystemUserByEmail(email);
        verifySystemUserEmail(email);
        user.setPassword(password);
        entityManager.merge(user);
    }

    @Override
    public void setResetDigest(String email, String resetDigest) {
        SystemUser user = getSystemUserByEmail(email);
        user.setResetDigest(resetDigest);
        entityManager.merge(user);
    }

    @Override
    public void expireResetPassword(String email) {
        SystemUser user = getSystemUserByEmail(email);
        String resetDigest = CreateToken.createNewToken();
        user.setResetDigest(resetDigest);
        entityManager.merge(user);
    }

    @Override
    public void lockUser(String username) {
        SystemUser user = getSystemUserByName(username);
        user.setLocked(true);
        entityManager.merge(user);
    }

    @Override
    public void unlockUser(String username) {
        SystemUser user = getSystemUserByName(username);
        user.setLocked(false);
        entityManager.merge(user);
    }

    @Override
    public List<UserRolePermission> getAllUsersRolesPermissions() {
        List<UserRolePermission> userRolePermissionList = new ArrayList<UserRolePermission>();
        for (SystemUser user : getAllUsers()) {
            for (SystemRole role : user.getSystemRoles()) {
                for (Permission permission : role.getPermissions()) {
                    UserRolePermission userRolePermission
                            = new UserRolePermission(user.getUsername(), role.getRoleName(), permission.getPermissionName());
                    userRolePermissionList.add(userRolePermission);
                }
            }
        }
        return userRolePermissionList;
    }

    @Override
    public List<RolePermission> getUserRolesPermissions(String username) throws NoSuchUsernameException {
        SystemUser user = getSystemUserByName(username);
        List<RolePermission> rolePermissionList = new ArrayList<RolePermission>();
        if (user == null) {
            throw new NoSuchUsernameException(UserMsg.NO_SUCH_USERNAME_ERROR);
        } else {
            for (SystemRole role : user.getSystemRoles()) {
                for (Permission permission : role.getPermissions()) {
                    RolePermission rolePermission
                            = new RolePermission(role.getRoleName(), permission.getPermissionName());
                    rolePermissionList.add(rolePermission);
                }
            }
        }
        return rolePermissionList;
    }

    @Override
    public boolean hasRole(String username, String roleName) {
        SystemRole role = roleSession.getSystemRoleByName(roleName);
        List<SystemRole> roles;
        try {
            roles = getUserRoles(username);
        } catch (NoSuchUsernameException ex) {
            return false;
        }
        if(role == null || roles == null){
            return false;
        }
        return roles.contains(role);
    }

    @Override
    public boolean isAdmin(String username) {
        SystemUser user = getSystemUserByName(username);
        if(user == null) {
            return false;
        } else {
            return true;
        }
    }

}
