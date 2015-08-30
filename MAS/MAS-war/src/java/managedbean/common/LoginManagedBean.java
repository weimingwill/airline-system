/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.common;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import mas.common.session.SystemUserSessionLocal;

/**
 *
 * @author winga_000
 */
@ManagedBean
@RequestScoped
public class LoginManagedBean {
    @EJB
    private SystemUserSessionLocal systemUserSession;
    private String username;
    private String password;
    
    
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
    
    public void varifyUserLogin(String userName, String password){
        systemUserSession.verifySystemUserPassword(userName, password);
    }
    
}
