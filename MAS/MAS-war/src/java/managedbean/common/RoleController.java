/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import managedbean.application.NavigationController;
import mas.common.entity.Permission;
import mas.common.entity.SystemRole;
import mas.common.session.RoleSessionLocal;
import mas.common.session.SystemUserSessionLocal;
import mas.common.util.exception.NoSuchRoleException;
import mas.common.util.exception.ExistSuchRoleException;
import mas.common.util.helper.UserMsg;

/**
 *
 * @author winga_000
 */
@Named(value = "roleController")
@RequestScoped
public class RoleController implements Serializable {

    @Inject
    UserController userController;
    @Inject
    NavigationController navigationController;

    @EJB
    private SystemUserSessionLocal systemUserSession;
    @EJB
    private RoleSessionLocal roleSession;
    
    private String username;
    private List<String> roleNameList;
    private String[] selectedRoleNames;
    private List<Permission> rolePermissions;

    /**
     * Creates a new instance of roleController
     */
    public RoleController() {
    }

    @PostConstruct
    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        this.username = (String) sessionMap.get("username");
        setRoleNameList();
    }

    public String createSystemRole(String roleName, String[] permissions) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            roleSession.createSystemRole(roleName, permissions);
            context.addMessage(null, new FacesMessage("Successful", "Create new role: " + roleName + " successfully!"));
        } catch (ExistSuchRoleException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", UserMsg.EXIST_ROLE_ERROR));
        }
        return navigationController.redirectToCreateRole();
    }

    public List<SystemRole> getAllRoles() {
        return roleSession.getAllRoles();
    }

    public List<SystemRole> getUserRoles() {
        return roleSession.getUserRoles(username);
    }

    private void setRoleNameList() {
        List<SystemRole> roles = getAllRoles();
        roleNameList = new ArrayList<String>();
        for (SystemRole role : roles) {
            roleNameList.add(role.getRoleName());
        }
    }

    public void updateRolePermission(String roleName, Map<String, ArrayList<String>> permissions) throws NoSuchRoleException {
        roleSession.assignRoleToPermissions(roleName, permissions);
    }

    public void updateUserRole(String username, ArrayList<String> roles) {
        roleSession.assignUserToRole(username, roles);
    }

    public void displayUserRoles(String username) {
        roleSession.getUserRoles(username);
    }


    //Getter and Setter
    public UserController getUserController() {
        return userController;
    }    
    
    public void setUserController(UserController userController) {
        this.userController = userController;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoleNameList() {
        return roleNameList;
    }

    public void setRoleNameList(List<String> roleNameList) {
        this.roleNameList = roleNameList;
    }

    public String[] getSelectedRoleNames() {
        return selectedRoleNames;
    }

    public void setSelectedRoleNames(String[] selectedRoleNames) {
        this.selectedRoleNames = selectedRoleNames;
    }

    public List<Permission> getRolePermissions() {
        return rolePermissions;
    }

    public void setRolePermissions(List<Permission> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }
    

}
