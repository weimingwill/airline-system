/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.application;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 *
 * @author winga_000
 */
@Named(value = "navigationBean")
@SessionScoped
public class NavigationBean implements Serializable {

    /**
     * Creates a new instance of NavigationBean
     */
    public NavigationBean() {
    }
    
    public String toLogin(){
        return "/views/secured/users/login.xhtml";
    }
    
    public String redirectToHome(){
        return "/views/index.xhtml";
    }
    
    public String toSendMessage(){
        return "/views/secured/common/sendMessage.xhtml";
    }
    
    public String redirectToWorkplace(){
        return "/views/secured/users/userWorkplace.xhtml?faces-redirect=true";
    }
    
    public String toViewMessages(){
        return "/views/secured/common/viewMessages.xhtml";
    }
    
    public String toCreateUser(){
        return "/views/secured/users/createUser.xhtml";
    }

    public String toAssignUserRole(){
        return "/views/secured/access_control/assignUserRole.xhtml";
    }
    
    public String redirectToAdminWorkplace(){
        return "/views/secured/users/systemAdminWorkspace.xhtml?faces-redirect=true";
    }
    
}
