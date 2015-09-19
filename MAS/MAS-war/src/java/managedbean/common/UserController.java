/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.common;

import managedbean.application.NavigationController;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import mas.common.entity.SystemUser;
import mas.common.session.SystemUserSessionLocal;
import mas.common.util.exception.NoSuchEmailException;
import mas.common.util.exception.InvalidPasswordException;
import mas.common.util.exception.NoSuchUsernameException;
import mas.common.util.exception.ExistSuchUserException;
import mas.common.util.helper.CreateToken;
import mas.common.util.helper.UserMsg;
import util.helper.CountdownHelper;
import util.security.CryptographicHelper;
import botdetect.web.jsf.JsfCaptcha;
import java.util.List;
import mas.common.entity.SystemRole;
import mas.common.session.RoleSessionLocal;
import mas.common.util.exception.NoSuchRoleException;
import mas.common.util.helper.RolePermission;
import mas.common.util.helper.UserRolePermission;

/**
 *
 * @author winga_000
 */
@Named(value = "userController")
@SessionScoped
public class UserController implements Serializable {

    private final String TIMER_NAME_RESET_PASSWORD = "RESET_PASSWORD_TIMER";

    @Inject
    private NavigationController navigationController;
    @Inject
    private EmailController emailController;

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
    private boolean loggedIn;
    private JsfCaptcha captcha;
    private String captchaCode;
    private int countTrial = 0;
    private List<SystemUser> userList;
    private SystemUser selectedUser;
    private List<SystemRole> roleList;
    private List<UserRolePermission> userRolePermissionList;
    private List<RolePermission> rolePermissionList;

    public UserController() {
    }

    public String doLogin() throws NoSuchUsernameException, InvalidPasswordException, InterruptedException {
        CountdownHelper countdownHelper = new CountdownHelper(systemUserSession);
//        boolean isHuman = captcha.validate(captchaCode);
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        try {
            if (systemUserSession.getSystemUserByName(username).isLocked()) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Your account has been locked, please reset password or wait 30mins to try again"));
                return navigationController.toLogin();
            }
        } catch (NoSuchUsernameException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage()));
            return navigationController.toLogin();
        }

//        if (!isHuman) {
//            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Captcha code entered is incorrect"));
//            return navigationController.toLogin();
//        }
        CryptographicHelper cryptographicHelper = new CryptographicHelper();
        try {
            systemUserSession.verifySystemUserPassword(username, cryptographicHelper.doMD5Hashing(password));
        } catch (NoSuchUsernameException | InvalidPasswordException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage()));
            countTrial++;
            if (countTrial > 2) {
                systemUserSession.lockUser(username);
                System.out.println("locked user " + username);

                countdownHelper.unlockUserCountDown(1800 * 1000, username); //Unlock user after 30mins
                countTrial = 0;
            }

            System.out.println("Count Trial: " + countTrial);
            return navigationController.toLogin();
        }
        loggedIn = true;
        countTrial = 0;
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        sessionMap.put("username", username);
        externalContext.getFlash().setKeepMessages(true);
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successful", UserMsg.LOGIN_SUCCESS_MSG));
        captchaCode = null;
        return navigationController.redirectToWorkspace();
    }

    public String doLogout() {
        loggedIn = false;
        username = null;
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Successful", UserMsg.LOGIN_OUT_MSG));
        return navigationController.redirectToWorkspace();
    }

    public String resetPasswordSendEmail() throws InterruptedException {
        CountdownHelper countdownHelper = new CountdownHelper(systemUserSession);
        try {
            systemUserSession.verifySystemUserEmail(email);
            String resetDigest = createNewToken();
            String subject = "Reset Password";
            String mailContent = navigationController.toUnsecuredUsersFolder() + "resetPassword.xhtml?faces-redirect=true&resetDigest=" + resetDigest + "&email=" + email;
            //String receiver = "Weiming<a0119405@u.nus.edu>";
            String receiver = email;
            emailController.sendEmail(subject, mailContent, receiver);
            systemUserSession.setResetDigest(email, resetDigest);

            countdownHelper.expireResetPasswoordCountDown(1800 * 1000, email);//Auto expire password after 30mins

            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            context.addMessage(null, new FacesMessage("Successful", "Please check your email to reset password"));
            return navigationController.redirectToHome() + "?faces-redirect=true";
        } catch (NoSuchEmailException ex) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage()));
            return navigationController.toPasswordResetSendEmail();
        }
    }

    public String resetPassword() {
        CryptographicHelper cryptographicHelper = new CryptographicHelper();
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Map<String, String> params = externalContext.getRequestParameterMap();
        String userEmail = params.get("email");
        String resetDigest = params.get("resetDigest");
        try {
            systemUserSession.verifySystemUserEmail(userEmail);
            SystemUser user = systemUserSession.getSystemUserByEmail(userEmail);
            if (resetDigest == null) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "You are not authorised to reset password. Send reset email again"));
            } else if (!user.getResetDigest().equals(resetDigest)) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Password reset expired. Send reset email again"));
            }
            systemUserSession.resetPassword(userEmail, cryptographicHelper.doMD5Hashing(newPassword));
            systemUserSession.expireResetPassword(email);//expire reset digest if it has been used;
            newPassword = null;
            context.addMessage(null, new FacesMessage("Successful",
                    "Password reset successfully, you can login with new password now"));
        } catch (NoSuchEmailException e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error", "You are not authorised to reset password. Send reset email again"));
        }
        return navigationController.redirectToPasswordResetSendEmail();
    }

    public String createNewToken() {
        return CreateToken.createNewToken();
    }

//    public String createUser(String username, String password, String[] roles) throws ExistSuchUserException, InvalidPasswordException, NoSuchUsernameException, NoSuchRoleException {
//    public String createUser(String username, String password, List<SystemRole> roles) throws ExistSuchUserException, InvalidPasswordException, NoSuchUsernameException, NoSuchRoleException {
//        FacesContext context = FacesContext.getCurrentInstance();
//        context.getExternalContext().getFlash().setKeepMessages(true);
//        try {
//            CryptographicHelper cryptographicHelper = new CryptographicHelper();
//            password = cryptographicHelper.doMD5Hashing(password);
//            systemUserSession.createUser(username, password, roles);
//            context.getExternalContext().getFlash().setKeepMessages(true);
//            context.addMessage(null, new FacesMessage("Successful", "New user " + username + " is created successfuly!"));
//            return navigationController.redirectToCreateUser();
//        } catch (ExistSuchUserException ex) {
//            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage()));
//            return navigationController.toCreateUser();
//        }
//    }
    public String createUser(String username, String password, String email) throws ExistSuchUserException, InvalidPasswordException, NoSuchUsernameException, NoSuchRoleException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            CryptographicHelper cryptographicHelper = new CryptographicHelper();
            password = cryptographicHelper.doMD5Hashing(password);
            systemUserSession.createUser(username, password, email, roleList);
            context.getExternalContext().getFlash().setKeepMessages(true);
            context.addMessage(null, new FacesMessage("Successful", "New user " + username + " is created successfuly!"));
            return navigationController.redirectToCreateUser();
        } catch (ExistSuchUserException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage()));
            return navigationController.toCreateUser();
        }
    }

    public List<SystemUser> getAllUsers() {
        return systemUserSession.getAllUsers();
    }

    public String assignUserRoles() {
        systemUserSession.assignUserToRole(selectedUser, roleList);
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Successful", "Assign user:" + selectedUser.getUsername() + " with roles successuflly"));
        return navigationController.redirectToAssignUserRoles();
    }

    public List<SystemRole> getUserRoles() throws NoSuchUsernameException {
        return systemUserSession.getUserRoles(username);
    }

    public List<SystemRole> getUserRolesByUsername(String username) throws NoSuchUsernameException {
        return systemUserSession.getUserRoles(username);
    }

    public void onUserSelected() throws NoSuchUsernameException {
        rolePermissionList = systemUserSession.getUserRolesPermissions(selectedUser.getUsername());
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

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public JsfCaptcha getCaptcha() {
        return captcha;
    }

    public void setCaptcha(JsfCaptcha captcha) {
        this.captcha = captcha;
    }

    public String getCaptchaCode() {
        return captchaCode;
    }

    public void setCaptchaCode(String captchaCode) {
        this.captchaCode = captchaCode;
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
}
