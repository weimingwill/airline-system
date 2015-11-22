/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.common;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;
import mas.common.entity.SystemUser;
import mas.common.session.SystemUserSessionLocal;
import mas.common.util.exception.NoSuchUsernameException;

/**
 *
 * @author Bowen
 */
@Named(value = "userBacking")
@ViewScoped
public class UserBacking implements Serializable {

    @Inject
    private NavigationController navigationController;

    @Inject
    private MsgController msgController;

    @EJB
    private SystemUserSessionLocal systemUserSession;

    private SystemUser selectedUser;

    public String updateUserPayroll() throws NoSuchUsernameException {
        System.out.println("controller selecteduser is" +selectedUser);
        try {
            systemUserSession.updateUserPayroll(selectedUser.getUsername(), selectedUser.getSalary());
            msgController.addMessage("Update user payroll successfully!");
        } catch (NoSuchUsernameException ex) {
            msgController.addErrorMessage(ex.getMessage());
            return navigationController.redirectToCurrentPage();
        }
        return navigationController.redirectToCurrentPage();
    }
    
    public List<SystemUser> getAllUsers() {
        return systemUserSession.getAllUsers();
    }

    public SystemUser getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(SystemUser selectedUser) {
        this.selectedUser = selectedUser;
    }

    /**
     * Creates a new instance of UserBacking
     */
    public UserBacking() {
    }

}
