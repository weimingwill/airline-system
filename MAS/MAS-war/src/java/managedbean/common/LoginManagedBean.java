/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.common;

import java.util.Map;
import javax.ejb.EJB;
import javax.faces.bean.RequestScoped;
import javax.inject.Named;
import mas.common.session.SystemUserSessionLocal;

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
    
    public String varifyUserLogin(){
        Map<Boolean, String> result = systemUserSession.verifySystemUserPassword(username, password);
        if(result.containsKey(Boolean.TRUE)){
            setLoginMsg(result.get(Boolean.TRUE));
            return "views/common/systemAdminWorkspace.xhtml";
        } else {
            setLoginMsg(result.get(Boolean.FALSE));
            return "";
        }
    }
    
}
