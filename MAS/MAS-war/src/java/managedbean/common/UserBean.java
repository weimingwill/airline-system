/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.common;

import managedbean.application.NavigationBean;
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
//import mas.common.session.TimerSession;
import mas.common.util.exception.NoSuchEmailException;
import mas.common.util.exception.InvalidPasswordException;
import mas.common.util.exception.NoSuchUsernameException;
import mas.common.util.exception.UserExistException;
import mas.common.util.helper.CreateToken;
import mas.common.util.helper.UserMsg;
import util.security.CryptographicHelper;

/**
 *
 * @author winga_000
 */
@Named(value = "userBean")
@SessionScoped
public class UserBean implements Serializable {

    private final String TIMER_NAME_RESET_PASSWORD = "RESET_PASSWORD_TIMER";

    @Inject
    private NavigationBean navigationBean;
    @Inject
    private EmailBean emailBean;

    @EJB
    private SystemUserSessionLocal systemUserSession;
    private String username;
    private String password;
    private String email;
    private String resetDigest;
    private String newUsername;
    private String newPassword;
    private boolean loggedIn;
    //@EJB
    //private TimerSession timerSession;

    public UserBean() {
    }

    public String doLogin() throws NoSuchUsernameException, InvalidPasswordException {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        CryptographicHelper cryptographicHelper = new CryptographicHelper();
        try {
            systemUserSession.verifySystemUserPassword(username, cryptographicHelper.doMD5Hashing(password));
        } catch (NoSuchUsernameException | InvalidPasswordException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage()));
            return navigationBean.toLogin();
        }
        loggedIn = true;
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        sessionMap.put("username", username);
        externalContext.getFlash().setKeepMessages(true);
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successful", UserMsg.LOGIN_SUCCESS_MSG));
        return navigationBean.redirectToWorkplace();
    }

    public String doLogout() {
        loggedIn = false;
        username = null;
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Successful", UserMsg.LOGIN_OUT_MSG));
        return navigationBean.toLogin();
    }

    public String createUser() throws UserExistException, InvalidPasswordException, NoSuchUsernameException {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            CryptographicHelper cryptographicHelper = new CryptographicHelper();
            newPassword = cryptographicHelper.doMD5Hashing(newPassword);
            systemUserSession.createUser(newUsername, newPassword);
            context.getExternalContext().getFlash().setKeepMessages(true);
            context.addMessage(null, new FacesMessage("Successful", "New user " + newUsername + " is created successfuly!"));
            newUsername = null;
            newPassword = null;
            return navigationBean.toCreateUser() + "?faces-redirect=true";
        } catch (UserExistException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage()));
            return navigationBean.toCreateUser();
        }
    }

    public String resetPasswordSendEmail() {

        try {
            systemUserSession.verifySystemUserEmail(email);
            String resetDigest = createNewToken();
            String subject = "Reset Password";
            String mailContent = navigationBean.toUnsecuredUsersFolder() + "resetPassword.xhtml?faces-redirect=true&resetDigest=" + resetDigest + "&email=" + email;
            String receiver = "Weiming<a0119405@u.nus.edu>";
            emailBean.sendEmail(subject, mailContent, receiver);
            systemUserSession.setResetDigest(email, resetDigest);
            email = null;
            //timerSession.createTimers(50000, 50000, TIMER_NAME_RESET_PASSWORD);
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            context.addMessage(null, new FacesMessage("Successful", "Please check your email to reset password"));
            return navigationBean.redirectToHome() + "?faces-redirect=true";
        } catch (NoSuchEmailException ex) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage()));
            return navigationBean.toPasswordResetSendEmail();
        }
    }

    public String resetPassword() throws NoSuchEmailException {
        CryptographicHelper cryptographicHelper = new CryptographicHelper();
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Map<String, String> params = externalContext.getRequestParameterMap();
        String userEmail = params.get("email");
        String resetDigest = params.get("resetDigest");
        SystemUser user = systemUserSession.getSystemUserByEmail(userEmail);
        if (userEmail == null || resetDigest == null || user == null || !user.getResetDigest().equals(resetDigest)) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "You are not authorised to reset password. Send reset email again"));
            return navigationBean.toPasswordResetSendEmail() + "?faces-redirect=true";
        }
        systemUserSession.resetPassword(userEmail, cryptographicHelper.doMD5Hashing(newPassword));
        newPassword = null;
        context.addMessage(null, new FacesMessage("Successful", "Password reset successfully, you can login with new password now"));
        return navigationBean.toLogin() + "?faces-redirect=true";
    }

    public String createNewToken() {
        return CreateToken.createNewToken();
    }

    public SystemUser getUserByEmail(String email) throws NoSuchEmailException {
        SystemUser user = systemUserSession.getSystemUserByEmail(email);
        if (user == null) {
            throw new NoSuchEmailException();
        } else {
            return systemUserSession.getSystemUserByEmail(email);
        }
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

    public NavigationBean getNavigationBean() {
        return navigationBean;
    }

    public void setNavigationBean(NavigationBean navigationBean) {
        this.navigationBean = navigationBean;
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

    public EmailBean getEmailBean() {
        return emailBean;
    }

    public void setEmailBean(EmailBean emailBean) {
        this.emailBean = emailBean;
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
}
