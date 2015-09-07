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
        return "/views/secured/common/users/login.xhtml";
    }
    
    public String redirectToHome(){
        return "/views/index.xhtml";
    }
    
    public String redirectToWorkplace(){
        return "/views/secured/common/users/userWorkplace.xhtml?faces-redirect=true";
    }
    
    public String toCreateUser(){
        return "/views/secured/common/users/createUser.xhtml";
    }    
    
    //Message
    public String toSendMessage(){
        return "/views/secured/common/message/sendMessage.xhtml";
    }

    public String toViewMessages(){
        return "/views/secured/message/viewMessages.xhtml";
    }    
    
    //Password reset
    public String toPasswordReset(){
        return "/views/secured/common/users/resetPassword.xhtml";
    }

    public String toPasswordResetSendEmail(){
        return "/views/secured/common/users/resetPasswordSendEmail.xhtml";
    }    
    
    public String toUnsecuredUsersFolder(){
        return "http://localhost:8080/MAS-war/views/unsecured/common/users/";
    }
    
    //Access control
    public String toAssignUserRole(){
        return "/views/secured/common/access_control/assignUserRole.xhtml";
    }
    
    public String redirectToAdminWorkplace(){
        return "/views/secured/common/users/systemAdminWorkspace.xhtml?faces-redirect=true";
    }
    
}
