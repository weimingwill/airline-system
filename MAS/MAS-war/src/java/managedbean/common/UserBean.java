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
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import mas.common.entity.SystemUser;
import mas.common.session.SystemUserSessionLocal;
import mas.common.util.exception.EmailDoesNotExistException;
import mas.common.util.exception.InvalidPasswordException;
import mas.common.util.exception.UserDoesNotExistException;
import mas.common.util.exception.UserExistException;
import mas.common.util.helper.UserMsg;
import util.security.CryptographicHelper;

/**
 *
 * @author winga_000
 */
@Named(value = "userBean")
@SessionScoped
public class UserBean implements Serializable {

    @Inject
    private NavigationBean navigationBean;
    @Inject
    private EmailBean emailBean;

    @EJB
    private SystemUserSessionLocal systemUserSession;
    private String username;
    private String password;
    private String email;
    private String loginMsg;
    private String msg;
    private String resetDigest;
    private String newPassword;
    private boolean loggedIn;

    public UserBean() {
    }

    public String doLogin() throws UserDoesNotExistException, InvalidPasswordException {
        try {
            systemUserSession.verifySystemUserPassword(username, password);
        } catch (UserDoesNotExistException | InvalidPasswordException ex) {
            setLoginMsg(ex.getMessage());
            //FacesMessage msg = new FacesMessage("Login error!", "ERROR MSG");
            //msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            //FacesContext.getCurrentInstance().addMessage(null, msg);         
            return navigationBean.toLogin();
        }
        loggedIn = true;
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        sessionMap.put("username", username);
        setLoginMsg(UserMsg.LOGIN_SUCCESS_MSG);
        return navigationBean.redirectToWorkplace();
    }

    public String doLogout() {
        loggedIn = false;
        //FacesMessage msg = new FacesMessage("Logout successfully!", "INFO MSG");
        //msg.setSeverity(FacesMessage.SEVERITY_INFO);
        //FacesContext.getCurrentInstance().addMessage(null, msg);
        return navigationBean.toLogin();
    }

    public String createUser() throws UserExistException, InvalidPasswordException, UserDoesNotExistException {
        try {
            CryptographicHelper cryptographicHelper = new CryptographicHelper();
            password = cryptographicHelper.doMD5Hashing(password);
            systemUserSession.createUser(username, password);
            return doLogin();
        } catch (UserExistException ex) {
            setMsg(ex.getMessage());
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
            return navigationBean.redirectToHome();
        } catch (EmailDoesNotExistException ex) {
            setMsg(ex.getMessage());
            return navigationBean.toPasswordResetSendEmail();
        }
    }

    public String resetPassword() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String,String> params = externalContext.getRequestParameterMap();
        String userEmail = params.get("email");
        systemUserSession.resetPassword(userEmail, newPassword);
        msg = "Password reset successfully, you can login with new password now";
        return navigationBean.toLogin() + "?faces-redirect=true";
    }

    public String createNewToken() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    public SystemUser getUserByEmail(String email){
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

    /**
     * @return the loginMsg
     */
    public String getLoginMsg() {
        return loginMsg;
    }

    /**
     * @param loginMsg the loginMsg to set
     */
    public void setLoginMsg(String loginMsg) {
        this.loginMsg = loginMsg;
    }

    public SystemUserSessionLocal getSystemUserSession() {
        return systemUserSession;
    }

    public void setSystemUserSession(SystemUserSessionLocal systemUserSession) {
        this.systemUserSession = systemUserSession;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public NavigationBean getNavigationBean() {
        return navigationBean;
    }

    public void setNavigationBean(NavigationBean navigationBean) {
        this.navigationBean = navigationBean;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
