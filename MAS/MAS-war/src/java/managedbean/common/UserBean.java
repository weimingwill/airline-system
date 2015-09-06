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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import mas.common.session.SystemUserSessionLocal;
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
    
    @EJB
    private SystemUserSessionLocal systemUserSession;
    private String username;
    private String password;
    private String loginMsg;
    private String msg;
    private boolean loggedIn;
    
    public UserBean() {
    }
    
    public String doLogin() throws UserDoesNotExistException, InvalidPasswordException{
        try{
            systemUserSession.verifySystemUserPassword(username, password);
        } catch (UserDoesNotExistException | InvalidPasswordException ex){
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

    public String doLogout(){
        loggedIn = false;
        //FacesMessage msg = new FacesMessage("Logout successfully!", "INFO MSG");
        //msg.setSeverity(FacesMessage.SEVERITY_INFO);
        //FacesContext.getCurrentInstance().addMessage(null, msg);
        return navigationBean.toLogin();
    }
    
    public String createUser() throws UserExistException, InvalidPasswordException, UserDoesNotExistException{
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
    
    //Getter and Setter
    
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
}
