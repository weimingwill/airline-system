/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.common;

import managedbean.application.NavigationController;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import mas.common.entity.SystemUser;
import mas.common.session.SystemUserSessionLocal;
import mas.common.util.exception.NoSuchEmailException;
import mas.common.util.exception.NoSuchUsernameException;
import mas.common.util.exception.ExistSuchUserException;
import mas.common.util.helper.CreateToken;
import util.helper.CountdownHelper;
import util.security.CryptographicHelper;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import managedbean.application.MsgController;
import mas.common.entity.Permission;
import mas.common.entity.SystemRole;
import mas.common.session.RoleSessionLocal;
import mas.common.util.exception.ExistSuchUserEmailException;
import mas.common.util.exception.NeedResetDigestException;
import mas.common.util.exception.NoSuchResetDigestException;
import mas.common.util.exception.NoSuchRoleException;
import mas.common.util.exception.UserInUseException;
import mas.common.util.helper.PermissionHelper;
import mas.common.util.helper.RolePermission;
import mas.common.util.helper.UserRolePermission;

/**
 *
 * @author winga_000
 */
@Named(value = "userController")
@RequestScoped
public class UserController implements Serializable {

    private final String TIMER_NAME_RESET_PASSWORD = "RESET_PASSWORD_TIMER";

    @Inject
    private NavigationController navigationController;
    @Inject
    private EmailController emailController;
    @Inject
    private MsgController msgController;

    @EJB
    private SystemUserSessionLocal systemUserSession;
    @EJB
    private RoleSessionLocal roleSession;
    private String username;
    private String password;
    private String email;
    private String resetDigest;
    private String newUsername;
    private String newPassword;
    private List<SystemUser> userList;
    private SystemUser selectedUser;
    private List<SystemRole> roleList;
    private List<UserRolePermission> userRolePermissionList;
    private List<RolePermission> rolePermissionList;
    private List<String> otherUsernames;
    private String name;
    private String address;
    private String department;
    private String phone;

    public UserController() {
    }

    @PostConstruct
    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        this.username = (String) sessionMap.get("username");
        initializeUser();
    }

    public String resetPasswordSendEmail(String email) throws InterruptedException {
        CountdownHelper countdownHelper = new CountdownHelper(systemUserSession);
        try {
            systemUserSession.verifySystemUserEmail(email);
            String resetDigest = createNewToken();
            String subject = "Reset Password";
            String mailContent = navigationController.toUnsecuredUsersFolder()
                    + "resetPassword.xhtml?faces-redirect=true&resetDigest=" + resetDigest + "&email=" + email;
            String receiver = email;
            emailController.sendEmail(subject, mailContent, receiver);
            systemUserSession.setResetDigest(email, resetDigest);

            countdownHelper.expireResetPasswoordCountDown(60 * 1000, email);//Auto expire password after 30mins

            msgController.addMessage("Please check your email to reset password");
            return navigationController.redirectToHome() + "?faces-redirect=true";
        } catch (NoSuchEmailException ex) {
            msgController.addErrorMessage(ex.getMessage());
            return navigationController.toPasswordResetSendEmail();
        }
    }

    public String resetPassword() {
//        if (!newPassword.equals(newConfirmPassword)) {
//            msgController.addErrorMessage("The two passwords entered are different.");
//            return "";
//        }
        CryptographicHelper cryptographicHelper = new CryptographicHelper();
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.getFlash().setKeepMessages(true);
        Map<String, String> params = externalContext.getRequestParameterMap();
        String userEmail = params.get("email");
        String resetDigest = params.get("resetDigest");
        try {
            systemUserSession.resetPassword(userEmail, resetDigest, cryptographicHelper.doMD5Hashing(newPassword));
            msgController.addMessage("Password reset successfully, you can login with new password now");
            return navigationController.redirectToLogin();
        } catch (NoSuchEmailException | NeedResetDigestException | NoSuchResetDigestException ex) {
            msgController.addErrorMessage(ex.getMessage());
            return navigationController.redirectToPasswordResetSendEmail();
        }
    }

    public String changePassword(String oldPassword) {
        try {
            CryptographicHelper cryptographicHelper = new CryptographicHelper();
            if (!cryptographicHelper.doMD5Hashing(oldPassword).equals(password)) {
                msgController.addErrorMessage("Old password entered is incorrect.");
                return "";
            }
            newPassword = cryptographicHelper.doMD5Hashing(newPassword);
            systemUserSession.changePassword(username, newPassword);
            msgController.addMessage("Change password successfully!");
            return navigationController.redirectToWorkspace();
        } catch (NoSuchUsernameException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.toChangePassword();
    }

    public String createNewToken() {
        return CreateToken.createNewToken();
    }

    public String createUser(String username, String name, String email,
            String phone, String address, String department) {
        try {
            String newPassword = createNewToken();
            systemUserSession.createUser(username, newPassword, name, email, phone, address, department, roleList);
            String resetDigest = createNewToken();
            String subject = "Reset Password";
            String mailContent = "Please reset password first using the following link: \n" + navigationController.toUnsecuredUsersFolder() + "resetPassword.xhtml?faces-redirect=true&resetDigest=" + resetDigest + "&email=" + email;
            String receiver = email;
            emailController.sendEmail(subject, mailContent, receiver);
            systemUserSession.setResetDigest(email, resetDigest);
            msgController.addMessage("New user " + username + " is created successfuly!");
            cleanAttributeValue();
            return navigationController.redirectToCreateUser();
        } catch (ExistSuchUserException | NoSuchEmailException | NoSuchRoleException ex) {
            msgController.addErrorMessage(ex.getMessage());
            return navigationController.toCreateUser();
        }
    }

    public List<SystemUser> getAllUsers() {
        return systemUserSession.getAllUsers();
    }

    public List<String> getAllOtherUsernames() {
        otherUsernames = systemUserSession.getAllOtherUsernames(username);
        return otherUsernames;
    }

    public String assignUserRoles() {
        systemUserSession.assignUserToRole(selectedUser, roleList);
        msgController.addMessage("Assign user:" + selectedUser.getUsername() + " with roles successuflly");
        cleanAttributeValue();
        return navigationController.redirectToAssignUserRoles();
    }

    public List<SystemRole> getUserRoles() throws NoSuchUsernameException {
        return systemUserSession.getUserRoles(username);
    }

    public List<SystemRole> getUserRolesByUsername(String username) throws NoSuchUsernameException {
        return systemUserSession.getUserRoles(username);
    }

    public void onUserSelected() throws NoSuchUsernameException {
//        rolePermissionList = systemUserSession.getUserRolesPermissions(selectedUser.getUsername());
        roleList = systemUserSession.getUserRoles(selectedUser.getUsername());
    }

    public boolean hasRole(String roleName) {
        return systemUserSession.hasRole(username, roleName);
    }

    public boolean isAdmin() {
        return systemUserSession.isAdmin(username);
    }

    public SystemUser getUserByEmail(String email) throws NoSuchEmailException {
        return systemUserSession.getSystemUserByEmail(email);
    }

    public SystemUser getUserByUsername() throws NoSuchUsernameException {
        return systemUserSession.getSystemUserByName(username);
    }

    public void cleanAttributeValue() {
        selectedUser = null;
        roleList = null;
        userRolePermissionList = null;
        rolePermissionList = null;
    }

    public String updateUserProfile() {
        try {
            systemUserSession.updateUserProfile(username, name, email, phone, address, department);
            msgController.addMessage("Update user profile successfully!");
        } catch (NoSuchUsernameException | ExistSuchUserEmailException ex) {
            msgController.addErrorMessage(ex.getMessage());
            return navigationController.toEditUserProfile();
        }
        return navigationController.redirectToViewUserProfile();
    }

    public List<Permission> getUserPermissions() {
        return systemUserSession.getUserPermissions(username);
    }

    public List<Permission> getUserPermissions(String username) {
        return systemUserSession.getUserPermissions(username);
    }

    public List<String> getUserPermissionSystems() {
        return systemUserSession.getUserPermissionSystems(username);
    }

    public List<PermissionHelper> getPermissionHelpers(){
        return systemUserSession.getPermissionHelpers(username);
    }
    
    public boolean canAccessAMS() {
        return !systemUserSession.getPermissionHelpers(username).isEmpty();
    }
    
    public void initializeUser() {
        try {
            SystemUser user = getUserByUsername();
            email = user.getEmail();
            name = user.getName();
            password = user.getPassword();
            address = user.getAddress();
            department = user.getDepartment();
            phone = user.getPhone();
        } catch (NoSuchUsernameException ex) {
            email = null;
            name = null;
            password = "";
            address = null;
            department = null;
            phone = null;
        }
    }

    public String deleteUser(String username) {
        try {
            systemUserSession.deleteUser(username);
            msgController.addMessage("Delete user:" + username + " successfully!");
        } catch (NoSuchUsernameException | UserInUseException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToViewAllUsers();
    }

    public String lockUser(String username) {
        try {
            systemUserSession.lockUser(username);
            msgController.addMessage("Lock user:" + username + " successfully!");
        } catch (NoSuchUsernameException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToViewAllUsers();
    }

    public String unlockUser(String username) {
        try {
            systemUserSession.unlockUser(username);
            msgController.addMessage("unlock user:" + username + " successfully!");
        } catch (NoSuchUsernameException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToViewAllUsers();
    }

//
//Getter and Setter
//
    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public SystemUserSessionLocal getSystemUserSession() {
        return systemUserSession;
    }

    public void setSystemUserSession(SystemUserSessionLocal systemUserSession) {
        this.systemUserSession = systemUserSession;
    }

    public NavigationController getNavigationController() {
        return navigationController;
    }

    public void setNavigationController(NavigationController navigationController) {
        this.navigationController = navigationController;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getResetDigest() {
        return resetDigest;
    }

    public void setResetDigest(String resetDigest) {
        this.resetDigest = resetDigest;
    }

    public EmailController getEmailController() {
        return emailController;
    }

    public void setEmailController(EmailController emailController) {
        this.emailController = emailController;
    }

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public List<SystemRole> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<SystemRole> roleList) {
        this.roleList = roleList;
    }

    public List<SystemUser> getUserList() {
        return userList;
    }

    public void setUserList(List<SystemUser> userList) {
        this.userList = userList;
    }

    public SystemUser getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(SystemUser selectedUser) {
        this.selectedUser = selectedUser;
    }

    public List<UserRolePermission> getUserRolePermissionList() {
        return userRolePermissionList;
    }

    public void setUserRolePermissionList(List<UserRolePermission> userRolePermissionList) {
        this.userRolePermissionList = userRolePermissionList;
    }

    public List<RolePermission> getRolePermissionList() {
        return rolePermissionList;
    }

    public void setRolePermissionList(List<RolePermission> rolePermissionList) {
        this.rolePermissionList = rolePermissionList;
    }

    public List<String> getOtherUsernames() {
        return otherUsernames;
    }

    public void setOtherUsernames(List<String> otherUsernames) {
        this.otherUsernames = otherUsernames;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
