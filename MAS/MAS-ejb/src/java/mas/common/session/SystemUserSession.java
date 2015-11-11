 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.session;

import java.util.ArrayList;
import java.util.List;
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
import mas.common.util.exception.ExistSuchUserEmailException;
import mas.common.util.exception.NoSuchEmailException;
import mas.common.util.helper.UserMsg;
import mas.common.util.exception.InvalidPasswordException;
import mas.common.util.exception.NoSuchMessageException;
import mas.common.util.exception.NoSuchUsernameException;
import mas.common.util.exception.ExistSuchUserException;
import mas.common.util.exception.NeedResetDigestException;
import mas.common.util.exception.NoSuchResetDigestException;
import mas.common.util.exception.NoSuchRoleException;
import mas.common.util.exception.UserInUseException;
import mas.common.util.helper.CreateToken;
import mas.common.util.helper.NavigationUrlHelper;
import mas.common.util.helper.PermissionHelper;
import mas.common.util.helper.PermissionNamesHelper;
import mas.common.util.helper.SystemMsgHelper;
import mas.common.util.helper.UserStatus;

@Stateless
public class SystemUserSession implements SystemUserSessionLocal {

    @EJB
    private RoleSessionLocal roleSession;
    @EJB
    private PermissionSessionLocal permissionSession;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    private EntityManager entityManager;

    //Get user
    @Override
    public SystemUser getSystemUserByName(String username) throws NoSuchUsernameException {
        Query query = entityManager.createQuery("SELECT u FROM SystemUser u WHERE u.username = :inUserName and u.deleted = FALSE");
        query.setParameter("inUserName", username);
        SystemUser user = null;
        try {
            user = (SystemUser) query.getSingleResult();
        } catch (NoResultException ex) {
            throw new NoSuchUsernameException(UserMsg.NO_SUCH_USERNAME_ERROR);
        }
        return user;
    }

    @Override
    public SystemUser getSystemUserByEmail(String email) throws NoSuchEmailException {
        Query query = entityManager.createQuery("SELECT u FROM SystemUser u WHERE u.email = :email and u.deleted = FALSE");
        query.setParameter("email", email);
        SystemUser user = null;
        try {
            user = (SystemUser) query.getSingleResult();
        } catch (NoResultException ex) {
            throw new NoSuchEmailException(UserMsg.NO_SUCH_EMAIL_ERROR);
        }
        return user;
    }

    @Override
    public List<SystemUser> getAllUsers() {
        Query query = entityManager.createQuery("SELECT u FROM SystemUser u WHERE u.deleted = FALSE");
        return query.getResultList();
    }

    @Override
    public List<SystemUser> getAllOtherUsers(String username) {
        Query query = entityManager.createQuery("SELECT u FROM SystemUser u where u.username <> :username  and u.deleted = FALSE");
        query.setParameter("username", username);
        return query.getResultList();
    }

    @Override
    public List<String> getAllOtherUsernames(String username) {
        List<SystemUser> users = getAllOtherUsers(username);
        List<String> usernames = new ArrayList<>();
        if (users != null) {
            for (SystemUser user : users) {
                usernames.add(user.getUsername());
            }
        }
        return usernames;
    }

    //User Messages
    @Override
    public List<SystemMsg> getUserMessages(String username) {
        List<SystemMsg> msgs = new ArrayList<>();
        try {
            SystemUser user = getSystemUserByName(username);
            for (SystemMsg msg : user.getSystemMsgs()) {
                String senderName = msg.getMessageFrom();
                SystemUser sender;
                try {
                    sender = getSystemUserByName(senderName);
                } catch (NoSuchUsernameException e) {
                    sender = null;
                }
                if (!msg.getDeleted() && sender != null && !sender.getDeleted()) {
                    msgs.add(msg);
                }
            }
            return msgs;
        } catch (NoSuchUsernameException e) {
            return null;
        }
    }

    @Override
    public List<SystemMsg> getUserUnreadMessages(String username) {
        try {
            SystemUser user = getSystemUserByName(username);
            List<SystemMsg> unreadMsg = new ArrayList<>();
            for (SystemMsg msg : user.getSystemMsgs()) {
                if (!msg.getReaded()) {
                    unreadMsg.add(msg);
                }
            }
            return unreadMsg;
        } catch (NoSuchUsernameException ex) {
            return null;
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
    public void flagMessage(String username, Long messageId) throws NoSuchMessageException {
        List<SystemMsg> msgs = getUserMessages(username);
        if (msgs == null) {
            throw new NoSuchMessageException(UserMsg.NO_MESSAGE_ERROR);
        } else {
            for (SystemMsg msg : msgs) {
                if (msg.getSystemMsgId().equals(messageId)) {
                    msg.setFlaged(true);
                    entityManager.merge(msg);
                }
            }
        }
    }

    @Override
    public void unFlagMessage(String username, Long messageId) throws NoSuchMessageException {
        List<SystemMsg> msgs = getUserMessages(username);
        if (msgs == null) {
            throw new NoSuchMessageException(UserMsg.NO_MESSAGE_ERROR);
        } else {
            for (SystemMsg msg : msgs) {
                if (msg.getSystemMsgId().equals(messageId)) {
                    msg.setFlaged(false);
                    entityManager.merge(msg);
                }
            }
        }
    }

    @Override
    public Long deleteMessage(String username, Long messageId) throws NoSuchMessageException {
        List<SystemMsg> msgs = getUserMessages(username);
        if (msgs == null) {
            throw new NoSuchMessageException(UserMsg.NO_MESSAGE_ERROR);
        } else {
            for (SystemMsg msg : msgs) {
                if (msg.getSystemMsgId().equals(messageId)) {
                    msg.setDeleted(true);
                    entityManager.merge(msg);
                }
            }
        }
        return messageId;
    }

    @Override
    public List<SystemMsgHelper> getSystemMsgHelpers(String username) {
        List<SystemMsgHelper> systemMsgHelpers = new ArrayList<>();
        List<SystemMsg> msgs = getUserMessages(username);
        if (msgs != null) {
            for (String sender : getSystemMsgSenders(username)) {
                SystemMsgHelper systemMsgHelper = new SystemMsgHelper();
                systemMsgHelper.setSender(sender);
                systemMsgHelper.setMsgs(getSenderMsgs(username, sender));
                systemMsgHelpers.add(systemMsgHelper);
            }
        }
        return systemMsgHelpers;
    }

    @Override
    public List<String> getSystemMsgSenders(String username) {
        List<SystemMsg> msgs = getUserMessages(username);
        List<String> senders = new ArrayList<>();
        if (msgs != null) {
            for (SystemMsg msg : msgs) {
                if (!senders.contains(msg.getMessageFrom())) {
                    senders.add(msg.getMessageFrom());
                }
            }
        }
        return senders;
    }

    @Override
    public List<SystemMsg> getSenderMsgs(String username, String sender) {
        List<SystemMsg> msgs = getUserMessages(username);
        List<SystemMsg> senderMsgs = new ArrayList<>();
        if (msgs != null) {
            for (SystemMsg msg : msgs) {
                if (msg.getMessageFrom().equals(sender)) {
                    senderMsgs.add(msg);
                }
            }
        }
        return senderMsgs;
    }

    @Override
    public void createUser(String username, String password, String name, String email, String phone,
            String address, String department, List<SystemRole> roles) throws ExistSuchUserException, NoSuchRoleException {
        verifySystemUserExistence(username, email);
        SystemUser systemUser = new SystemUser();
        systemUser.create(username, password, name, email, phone, address, department);
        for (SystemRole role : roles) {
            SystemRole r = roleSession.getSystemRoleByName(role.getRoleName());
            systemUser.getSystemRoles().add(r);
            r.getSystemUsers().add(systemUser);
        }
        entityManager.persist(systemUser);
    }

    @Override
    public void updateUserProfile(String username, String name, String email, String phone,
            String address, String department) throws NoSuchUsernameException, ExistSuchUserEmailException {
        List<SystemUser> systemUsers = getAllOtherUsers(username);
        SystemUser user = getSystemUserByName(username);
        if (systemUsers != null) {
            for (SystemUser systemUser : systemUsers) {
                if (email.equals(systemUser.getEmail())) {
                    throw new ExistSuchUserEmailException(UserMsg.EXIST_USER_EMAIL_ERROR);
                }
            }
        }
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);
        user.setDepartment(department);
        entityManager.merge(user);
    }

    @Override
    public void changePassword(String username, String password) throws NoSuchUsernameException {
        SystemUser user = getSystemUserByName(username);
        user.setPassword(password);
        entityManager.merge(user);
    }

    @Override
    public void verifySystemUserPassword(String username, String inputPassword) throws InvalidPasswordException, NoSuchUsernameException {
        try {
            SystemUser user = getSystemUserByName(username);
            String userPassword = user.getPassword();
            if (!userPassword.equals(inputPassword)) {
                throw new InvalidPasswordException(UserMsg.WRONG_PASSWORD_ERROR);
            }
        } catch (NoSuchUsernameException ex) {
            throw new NoSuchUsernameException(UserMsg.NO_SUCH_USERNAME_ERROR);
        }
    }

    @Override
    public void verifySystemUserEmail(String email) throws NoSuchEmailException {
        SystemUser user = getSystemUserByEmail(email);
    }

    @Override
    public void verifyUserEmailExistence(String email) throws ExistSuchUserEmailException {
        List<SystemUser> users = getAllUsers();
        if (users != null) {
            for (SystemUser user : users) {
                if (user.getEmail().equals(email)) {
                    throw new ExistSuchUserEmailException(UserMsg.EXIST_USER_EMAIL_ERROR);
                }
            }
        }
    }

    @Override
    public void verifySystemUserExistence(String useranme, String email) throws ExistSuchUserException {
        List<SystemUser> users = getAllUsers();
        if (users != null) {
            for (SystemUser user : users) {
                if (user.getUsername().equals(useranme) || user.getEmail().equals(email)) {
                    throw new ExistSuchUserException(UserMsg.EXIST_USER_ERROR);
                }
            }
        }
    }

    @Override
    public void verifyResetPassword(String email, String resetDigest)
            throws NoSuchEmailException, NeedResetDigestException, NoSuchResetDigestException {
        SystemUser user = getSystemUserByEmail(email);
        if (resetDigest == null) {
            throw new NeedResetDigestException(UserMsg.NEED_RESET_DIGEST_ERROR);
        } else if (!user.getResetDigest().equals(resetDigest)) {
            throw new NoSuchResetDigestException(UserMsg.NO_SUCH_RESET_DIGEST_ERROR);
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
    public List<SystemRole> getUserRoles(String username) {
        Query query = entityManager.createQuery("SELECT r FROM SystemUser u JOIN u.systemRoles ur, SystemRole r "
                + "WHERE ur.systemRoleId = r.systemRoleId and u.username = :inUsername and u.deleted = FALSE and r.deleted = FALSE");
        query.setParameter("inUsername", username);
        List<SystemRole> roles;
        try {
            roles = (List<SystemRole>) query.getResultList();
            return roles;
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public List<Permission> getUserPermissions(String username) {
        List<SystemRole> roles = getUserRoles(username);
        if (roles != null) {
            List<Permission> permissionList = new ArrayList<>();
            for (SystemRole role : roles) {
                try {
                    List<Permission> permissions = roleSession.getRolePermissions(role.getRoleName());
                    for (Permission permission : permissions) {
                        permissionList.add(permission);
                    }
                } catch (NoSuchRoleException ex) {
                }
            }
            return permissionList;
        }
        return null;
    }

    @Override
    public List<String> getUserPermissionSystems(String username) {
        List<String> permissionSystems = new ArrayList<>();
        List<Permission> permissions = getUserPermissions(username);
        if (permissions != null) {
            for (Permission permission : permissions) {
                if (!permissionSystems.contains(permission.getSystem())) {
                    permissionSystems.add(permission.getSystem());
                }
            }
            return permissionSystems;
        }
        return null;
    }

    @Override
    public List<PermissionHelper> getPermissionHelpers(String username) {
        List<PermissionHelper> permissionHelpers = new ArrayList<>();
        for (String system : getUserPermissionSystems(username)) {
            PermissionHelper permissionHelper = new PermissionHelper();
            permissionHelper.setName(system);
            setSystemUrl(system, permissionHelper);

            List<PermissionHelper> modules = new ArrayList<>();
            List<String> systemModules = getUserPermissionModules(username, system);
            for (String module : systemModules) {
                PermissionHelper m = new PermissionHelper();
                m.setName(module);
                setModuleUrl(module, m);
                modules.add(m);
            }
            permissionHelper.setPermissions(modules);
            permissionHelpers.add(permissionHelper);
        }
        return permissionHelpers;
    }

    private void setSystemUrl(String system, PermissionHelper permissionHelper) {
        switch (system) {
            case PermissionNamesHelper.APS:
                permissionHelper.setUrl(NavigationUrlHelper.APS_URL);
                break;
            case PermissionNamesHelper.AIS:
                permissionHelper.setUrl(NavigationUrlHelper.AIS_URL);
                break;
            case PermissionNamesHelper.AFOS:
                permissionHelper.setUrl(NavigationUrlHelper.AFOS_URL);
                break;
        }
    }

    private void setModuleUrl(String system, PermissionHelper permissionHelper) {
        switch (system) {
            case PermissionNamesHelper.FLEET_PLANNING:
                permissionHelper.setUrl(NavigationUrlHelper.APS_FP_URL);
                break;
            case PermissionNamesHelper.FLIGHT_SCHEDULING:
                permissionHelper.setUrl(NavigationUrlHelper.APS_FS_URL);
                break;
            case PermissionNamesHelper.ROUTE_PLANNING:
                permissionHelper.setUrl(NavigationUrlHelper.APS_RP_URL);
                break;
            case PermissionNamesHelper.PRODUCT_DESIGNE:
                permissionHelper.setUrl(NavigationUrlHelper.AIS_PD_URL);
                break;
            case PermissionNamesHelper.REVENUE_MANAGMENT:
                permissionHelper.setUrl(NavigationUrlHelper.AIS_RM_URL);
                break;
            default:
                permissionHelper.setUrl(NavigationUrlHelper.AFOS_URL);
                break;
        }
    }

    @Override
    public List<String> getSystemUsernameList() {
        Query query = entityManager.createQuery("SELECT u.username FROM SystemUser u");
        return (List<String>) query.getResultList();
    }

    @Override
    public void resetPassword(String email, String resetDiget, String password)
            throws NoSuchEmailException, NeedResetDigestException, NoSuchResetDigestException {
        verifyResetPassword(email, resetDiget);
        SystemUser user = getSystemUserByEmail(email);
        verifySystemUserEmail(email);
        user.setPassword(password);
        user.setActivated(true);
        entityManager.merge(user);
        expireResetPassword(email);
    }

    @Override
    public void setResetDigest(String email, String resetDigest) throws NoSuchEmailException {
        SystemUser user = getSystemUserByEmail(email);
        user.setResetDigest(resetDigest);
        entityManager.merge(user);
    }

    @Override
    public void expireResetPassword(String email) throws NoSuchEmailException {
        SystemUser user = getSystemUserByEmail(email);
        String resetDigest = CreateToken.createNewToken();
        user.setResetDigest(resetDigest);
        entityManager.merge(user);
    }

    @Override
    public void lockUser(String username) throws NoSuchUsernameException {
        SystemUser user = getSystemUserByName(username);
        user.setLocked(true);
        entityManager.merge(user);
    }

    @Override
    public void unlockUser(String username) throws NoSuchUsernameException {
        SystemUser user = getSystemUserByName(username);
        user.setLocked(false);
        entityManager.merge(user);
    }

    @Override
    public boolean hasRole(String username, String roleName) {
        try {
            SystemUser user = getSystemUserByName(username);
            SystemRole role = roleSession.getSystemRoleByName(roleName);
            List<SystemRole> roles = getUserRoles(username);
            if (roles == null) {
                return false;
            }
            return roles.contains(role);
        } catch (NoSuchRoleException | NoSuchUsernameException ex) {
            return false;
        }
    }

    @Override
    public boolean isAdmin(String username) {
        try {
            SystemUser user = getSystemUserByName(username);
            return username.equals("admin");
        } catch (NoSuchUsernameException ex) {
            return false;
        }
    }

    @Override
    public String deleteUser(String username) throws NoSuchUsernameException, UserInUseException {
        SystemUser user = getSystemUserByName(username);
        if (user == null) {
            throw new NoSuchUsernameException(UserMsg.NO_SUCH_USERNAME_ERROR);
        } else if (!user.getStatus().equals(UserStatus.IDLE)) {
            throw new UserInUseException(UserMsg.USER_IN_USER_ERROR);
        } else {
            user.setDeleted(true);
        }
        return username;
    }

    @Override
    public void doLogin(String username, String inputPassword) throws NoSuchUsernameException, InvalidPasswordException {
        verifySystemUserPassword(username, inputPassword);
        SystemUser user = getSystemUserByName(username);
        user.setStatus(UserStatus.LOGGEDIN);
        entityManager.merge(user);
    }

    @Override
    public void doLogout(String username) throws NoSuchUsernameException {
        SystemUser user = getSystemUserByName(username);
        user.setStatus(UserStatus.IDLE);
        entityManager.merge(user);
    }

    @Override
    public List<String> getUserPermissionModules(String username, String systemName) {
        Query q = entityManager.createQuery("SELECT DISTINCT(p.systemModule) FROM Permission p, SystemRole r, SystemUser u WHERE u.username = :inUsername AND r MEMBER OF u.systemRoles AND p MEMBER OF r.permissions AND p.system = :inSystemName");
        q.setParameter("inUsername", username);
        q.setParameter("inSystemName", systemName);
        try {
            List<String> modules;
            modules = (List<String>) q.getResultList();
            return modules;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
