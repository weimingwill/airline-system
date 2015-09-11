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

    public UserController() {
    }

    public String doLogin() throws NoSuchUsernameException, InvalidPasswordException, InterruptedException {
        CountdownHelper countdownHelper = new CountdownHelper(systemUserSession);
//        boolean isHuman = captcha.validate(captchaCode);
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        if (systemUserSession.getSystemUserByName(username).isLocked()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Your account has been locked, please reset password or wait 30mins to try again"));
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
            String receiver = "Weimin g<a0119405@u.nus.edu>";
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

    public String resetPassword() throws NoSuchEmailException {
        CryptographicHelper cryptographicHelper = new CryptographicHelper();
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Map<String, String> params = externalContext.getRequestParameterMap();
        String userEmail = params.get("email");
        String resetDigest = params.get("resetDigest");
        SystemUser user = systemUserSession.getSystemUserByEmail(userEmail);
        if (userEmail == null || resetDigest == null || user == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "You are not authorised to reset password. Send reset email again"));
            return navigationController.toPasswordResetSendEmail() + "?faces-redirect=true";
        } else if (!user.getResetDigest().equals(resetDigest)) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Password reset expired. Send reset email again"));
            return navigationController.toPasswordResetSendEmail() + "?faces-redirect=true";
        }
        systemUserSession.resetPassword(userEmail, cryptographicHelper.doMD5Hashing(newPassword));
        systemUserSession.expireResetPassword(email);//expire reset digest if it has been used;
        newPassword = null;
        context.addMessage(null, new FacesMessage("Successful", "Password reset successfully, you can login with new password now"));
        return navigationController.toLogin() + "?faces-redirect=true";
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

    public String createUser() throws ExistSuchUserException, InvalidPasswordException, NoSuchUsernameException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            CryptographicHelper cryptographicHelper = new CryptographicHelper();
            newPassword = cryptographicHelper.doMD5Hashing(newPassword);
            systemUserSession.createUser(newUsername, newPassword);
            context.getExternalContext().getFlash().setKeepMessages(true);
            context.addMessage(null, new FacesMessage("Successful", "New user " + newUsername + " is created successfuly!"));
            newUsername = null;
            newPassword = null;
            return navigationController.redirectToCreateUser();
        } catch (ExistSuchUserException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage()));
            return navigationController.toCreateUser();
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


}
