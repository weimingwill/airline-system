/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.common;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import mas.common.session.SystemUserSessionLocal;
import mas.common.util.exception.*;
import mas.common.util.helper.UserMsg;

/**
 *
 * @author winga_000
 */
@Named(value = "loginManagedBean")
@RequestScoped
public class LoginManagedBean {
    @EJB
    private SystemUserSessionLocal systemUserSession;
    private String username;
    private String password;
    private String loginMsg;
    
    /**
     * Creates a new instance of LoginManagedBean
     */
    public LoginManagedBean() {
    }

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
    
    public String varifyUserLogin() throws UserDoesNotExistException, InvalidPasswordException{
        try{
            systemUserSession.verifySystemUserPassword(username, password);
        } catch (UserDoesNotExistException | InvalidPasswordException ex){
            setLoginMsg(ex.getMessage());
            return "";
        }
        setLoginMsg(UserMsg.LOGIN_SUCCESS_MSG);
        return "views/common/systemAdminWorkspace.xhtml";
    }
    
}
