/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.common;

import botdetect.web.jsf.JsfCaptcha;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;
import mas.common.session.SystemUserSessionLocal;
import mas.common.util.exception.InvalidPasswordException;
import mas.common.util.exception.NoSuchUsernameException;
import mas.common.util.helper.UserMsg;
import util.helper.CountdownHelper;
import util.security.CryptographicHelper;

/**
 *
 * @author winga_000
 */
@Named(value = "loginController")
@SessionScoped
public class LoginController implements Serializable {

    @Inject
    private NavigationController navigationController;
    @Inject
    private MsgController msgController;

    @EJB
    private SystemUserSessionLocal systemUserSession;

    private String username;
    private String password;
    private boolean loggedIn;
    private int countTrial = 0;
    private JsfCaptcha captcha;
    private String captchaCode;

    /**
     * Creates a new instance of LoginController
     */
    public LoginController() {
    }

    public String doLogin() throws NoSuchUsernameException, InvalidPasswordException, InterruptedException {
        CountdownHelper countdownHelper = new CountdownHelper(systemUserSession);
        boolean isHuman = captcha.validate(captchaCode);
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        try {
            if (systemUserSession.getSystemUserByName(username).getLocked()) {
                msgController.addErrorMessage("Your account has been locked, please reset password or wait 30mins to try again or contact systme admin");
                return navigationController.redirectToLogin();
            }
            if (!systemUserSession.getSystemUserByName(username).getActivated()) {
                msgController.addErrorMessage(UserMsg.NEED_ACTIVATION_ERROR);
                return navigationController.redirectToLogin();
            }
        } catch (NoSuchUsernameException ex) {
            msgController.addErrorMessage(ex.getMessage());
            return navigationController.redirectToLogin();
        }

        if (!isHuman) {
            msgController.addErrorMessage("Captcha code entered is incorrect");
            return navigationController.redirectToLogin();
        }
        CryptographicHelper cryptographicHelper = new CryptographicHelper();
        try {
            systemUserSession.doLogin(username, cryptographicHelper.doMD5Hashing(password));
        } catch (NoSuchUsernameException | InvalidPasswordException ex) {
            msgController.addErrorMessage(ex.getMessage());
            countTrial++;
            if (countTrial > 2) {
                if (!isAdmin()) {
                     systemUserSession.lockUser(username);
                    System.out.println("locked user " + username);
                }

                countdownHelper.unlockUserCountDown(1800 * 1000, username); //Unlock user after 30mins
                countTrial = 0;
            }

            System.out.println("Count Trial: " + countTrial);
            return navigationController.redirectToLogin();
        }
        loggedIn = true;
        countTrial = 0;
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        sessionMap.put("username", username);
        externalContext.getFlash().setKeepMessages(true);
        msgController.addMessage(UserMsg.LOGIN_SUCCESS_MSG);
        captchaCode = null;
        return navigationController.redirectToWorkspace();
    }

    public String doLogout() {
        loggedIn = false;
        try {
            systemUserSession.doLogout(username);
        } catch (NoSuchUsernameException ex) {
            msgController.addErrorMessage("Logout failed: " + ex.getMessage());
            return navigationController.redirectToWorkspace();
        }
        username = null;
        msgController.addMessage(UserMsg.LOGIN_OUT_MSG);
        return navigationController.redirectToWorkspace();
    }

    public boolean isAdmin() {
        return systemUserSession.isAdmin(username);
    }

    //Getter and Setter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public int getCountTrial() {
        return countTrial;
    }

    public void setCountTrial(int countTrial) {
        this.countTrial = countTrial;
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
