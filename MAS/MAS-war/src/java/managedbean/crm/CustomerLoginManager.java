/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.crm;

import ams.crm.entity.RegCust;
import ams.crm.session.CustomerSessionLocal;
import ams.crm.util.exception.InvalidPasswordException;
import ams.crm.util.exception.NoSuchRegCustException;
import ams.crm.util.helper.CrmMsg;
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
import util.helper.CountdownHelper;
import util.security.CryptographicHelper;

/**
 *
 * @author Bowen
 */
@Named(value = "loginManager")
@SessionScoped
public class CustomerLoginManager implements Serializable {
    @Inject
    private NavigationController navigationController;
    @Inject
    private MsgController msgController;

    @EJB
    private CustomerSessionLocal customerSession;
    
    
    
    private String email;
    private String password;
    private boolean loggedIn;
//    private int countTrial = 0;
//    private JsfCaptcha captcha;
//    private String captchaCode;
    /**
     * Creates a new instance of LoginManager
     */
    public CustomerLoginManager() {
    }
    
    public String doLogin() throws NoSuchRegCustException, InvalidPasswordException, InterruptedException {
//        CountdownHelper countdownHelper = new CountdownHelper(registrationSession);
//     
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext(); 
        try {
           
            if (!customerSession.getRegCustByEmail(email).getActivated()) {
                msgController.addErrorMessage(CrmMsg.NEED_ACTIVATION_ERROR);
                return navigationController.redirectToLogin();
            }
        } catch (NoSuchRegCustException ex) {
            msgController.addErrorMessage(ex.getMessage());
            return navigationController.redirectToLogin();
        }

//       
//        CryptographicHelper cryptographicHelper = new CryptographicHelper();
        try {
            customerSession.doLogin(email, password);
        } catch (NoSuchRegCustException | InvalidPasswordException ex) {
//            msgController.addErrorMessage(ex.getMessage());
//            countTrial++;
//            if (countTrial > 2) {
//                if (!isAdmin()) {
//                    systemUserSession.lockUser(username);
//                    System.out.println("locked user " + username);
//                }1
//
//                countdownHelper.unlockUserCountDown(1800 * 1000, username); //Unlock user after 30mins
//                countTrial = 0;
//            }

            
            return navigationController.redirectToLogin();
        }
        loggedIn = true;
       
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        sessionMap.put("username", username);
        externalContext.getFlash().setKeepMessages(true);
        msgController.addMessage(UserMsg.LOGIN_SUCCESS_MSG);
//        captchaCode = null;
        return navigationController.redirectToWorkspace();
    }
    
}
