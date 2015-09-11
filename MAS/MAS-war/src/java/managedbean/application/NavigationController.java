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
@Named(value = "navigationController")
@SessionScoped
public class NavigationController implements Serializable {

    private final String REDIRECT = "?faces-redirect=true";

    public NavigationController() {
    }

    public String toLogin() {
        return "/views/secured/common/users/login.xhtml";
    }

    public String redirectToHome() {
        return "/index.xhtml";
    }

    //Message
    public String toSendMessage() {
        return "/views/secured/common/messages/sendMessage.xhtml";
    }

    public String toViewMessages() {
        return "/views/secured/common/messages/viewMessages.xhtml";
    }

    public String redirectToSendMessages() {
        return "/views/secured/common/messages/sendMessage.xhtml" + REDIRECT;
    }

    public String redirectToViewMessages() {
        return "/views/secured/common/messages/viewMessages.xhtml" + REDIRECT;
    }

    //Password reset

    public String toPasswordReset() {
        return "/views/unsecured/common/users/resetPassword.xhtml";
    }

    public String toPasswordResetSendEmail() {
        return "/views/unsecured/common/users/resetPasswordSendEmail.xhtml";
    }

    public String toUnsecuredUsersFolder() {
        return "http://localhost:8080/MAS-war/views/unsecured/common/users/";
    }

    //Access control
    public String toAssignUserRole() {
        return "/views/secured/common/access_control/assignUserRole.xhtml";
    }

    public String redirectToWorkspace() {
        return "/views/secured/common/users/userWorkspace.xhtml" + REDIRECT;
    }

    public String redirectToAdminWorkspace() {
        return "/views/secured/common/users/adminWorkspace.xhtml" + REDIRECT;
    }

    //User
    public String toCreateUser() {
        return "/views/secured/common/users/createUser.xhtml";
    }

    public String redirectToCreateUser() {
        return "/views/secured/common/users/createUser.xhtml" + REDIRECT;
    }

    //Role
    public String toCreateRole() {
        return "/views/secured/common/roles/createRole.xhtml";
    }

    public String redirectToCreateRole() {
        return "/views/secured/common/roles/createRole.xhtml" + REDIRECT;
    }

    //Permission
    public String toCreatePermission() {
        return "/views/secured/common/permissions/createPermission.xhtml";
    }

    public String redirectToCreatePermission() {
        return "/views/secured/common/permissions/createPermission.xhtml?" + REDIRECT;
    }
}
