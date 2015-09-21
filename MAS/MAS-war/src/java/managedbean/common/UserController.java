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
import mas.common.entity.SystemRole;
import mas.common.session.RoleSessionLocal;
import mas.common.util.exception.ExistSuchUserEmailException;
import mas.common.util.exception.NoSuchRoleException;
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

    public UserController() {
    }

    @PostConstruct
    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        this.username = (String) sessionMap.get("username");
        try {
            email = getUserByUsername().getEmail();
        } catch (NoSuchUsernameException ex) {
            email = null;
        }
    }

    public String resetPasswordSendEmail() throws InterruptedException {
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

            countdownHelper.expireResetPasswoordCountDown(1800 * 1000, email);//Auto expire password after 30mins

            msgController.addMessage("Please check your email to reset password");
            return navigationController.redirectToHome() + "?faces-redirect=true";
        } catch (NoSuchEmailException ex) {
            msgController.addErrorMessage(ex.getMessage());
            return navigationController.toPasswordResetSendEmail();
        }
    }

    public String resetPassword() {
        CryptographicHelper cryptographicHelper = new CryptographicHelper();
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.getFlash().setKeepMessages(true);
        Map<String, String> params = externalContext.getRequestParameterMap();
        String userEmail = params.get("email");
        String resetDigest = params.get("resetDigest");
        try {
            systemUserSession.verifySystemUserEmail(userEmail);
            SystemUser user = systemUserSession.getSystemUserByEmail(userEmail);
            if (resetDigest == null) {
                msgController.addErrorMessage("You are not authorised to reset password. Send reset email again");
            } else if (!user.getResetDigest().equals(resetDigest)) {
                msgController.addErrorMessage("Password reset expired. Send reset email again");
            }
            systemUserSession.resetPassword(userEmail, cryptographicHelper.doMD5Hashing(newPassword));
            systemUserSession.expireResetPassword(email);//expire reset digest if it has been used;
            newPassword = null;
            msgController.addMessage("Password reset successfully, you can login with new password now");
        } catch (NoSuchEmailException e) {
            msgController.addErrorMessage("You are not authorised to reset password. Send reset email again");
        }
        return navigationController.redirectToPasswordResetSendEmail();
    }

    public String createNewToken() {
        return CreateToken.createNewToken();
    }

    public String createUser(String username, String password, String email) {
        try {
            CryptographicHelper cryptographicHelper = new CryptographicHelper();
            password = cryptographicHelper.doMD5Hashing(password);
            systemUserSession.createUser(username, password, email, roleList);
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
            System.out.println("Begin update user.");
            systemUserSession.updateUserProfile(username, email);
            msgController.addMessage("Update user profile successfully!");
        } catch (NoSuchUsernameException | ExistSuchUserEmailException ex) {
            msgController.addErrorMessage(ex.getMessage());
            return navigationController.toEditUserProfile();
        }
        return navigationController.redirectToViewUserProfile();
    }

    public String changePassword(String newPassword, String newConfirmPassword) {
        try {
            if (newPassword.equals(newConfirmPassword)) {
                CryptographicHelper cryptographicHelper = new CryptographicHelper();
                newPassword = cryptographicHelper.doMD5Hashing(newPassword);
                systemUserSession.changePassword(username, newPassword);
                msgController.addMessage("Change password successfully!");
                return navigationController.redirectToWorkspace();
            } else {
                msgController.addErrorMessage("The password entered are different.");
            }
        } catch (NoSuchUsernameException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.toChangePassword();
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

}
