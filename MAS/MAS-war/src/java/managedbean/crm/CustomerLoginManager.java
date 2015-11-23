/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.crm;

import ams.crm.session.CustomerExSessionLocal;
import ams.crm.util.exception.InvalidPasswordException;
import ams.crm.util.exception.NoSuchRegCustException;
import ams.crm.util.helper.CrmMsg;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import managedbean.application.CrmExNavController;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;
import mas.common.util.helper.UserMsg;

/**
 *
 * @author Bowen
 */
@Named(value = "customerLoginManager")
@SessionScoped
public class CustomerLoginManager implements Serializable {

    @Inject
    private NavigationController navigationController;
    @Inject
    private MsgController msgController;
    @Inject
    private CrmExNavController crmExNavController;

    @EJB
    private CustomerExSessionLocal customerSession;

    private String email;
    private String password;
    private boolean loggedIn = false;

    /**
     * Creates a new instance of LoginManager
     */
    public CustomerLoginManager() {
    }

    public String doLogout() {
        loggedIn = false;
        email = null;
        msgController.addMessage(UserMsg.LOGIN_OUT_MSG);
        return crmExNavController.redirectToCrmEx();
    }

    public String doLogin() throws NoSuchRegCustException, InvalidPasswordException, InterruptedException {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        try {
            customerSession.doLogin(email, password);
            loggedIn = true;
            msgController.addMessage(CrmMsg.LOGIN_SUCCESS_MSG);

        } catch (NoSuchRegCustException | InvalidPasswordException ex) {
            msgController.addErrorMessage(ex.getMessage());
            return crmExNavController.redirectToAccountSummary();
        }

        Map<String, Object> sessionMap = externalContext.getSessionMap();
        sessionMap.put("email", email);
        externalContext.getFlash().setKeepMessages(true);
        return crmExNavController.redirectToAccountSummary();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

}
