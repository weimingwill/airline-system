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
import java.util.logging.Level;
import java.util.logging.Logger;
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
import mas.common.util.exception.NoSuchPermissionException;
import mas.common.util.helper.RolePermission;
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

    private List<SystemRole> roleList;
    private List<SystemRole> roles;
    private List<String> roleNameList;
//    private List<Permission> rolePermissions;
    private List<Permission> permissionList;
    private List<RolePermission> rolePermissionList;
    private SystemRole selectedRole;

    /**
     * Creates a new instance of roleController
     */
    public RoleController() {
    }

    @PostConstruct
    public void init() {
        setRoleNameList();
        roles = getAllRoles();
    }

    public String createSystemRole(String roleName, String[] permissions) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            roleSession.createSystemRole(roleName, permissions);
            context.addMessage(null, new FacesMessage("Successful", "Create new role: " + roleName + " successfully!"));
        } catch (ExistSuchRoleException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", UserMsg.EXIST_ROLE_ERROR));
        } catch (NoSuchPermissionException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", UserMsg.NO_PERMISSION_ERROR));
        }
        return navigationController.redirectToCreateRole();
    }

    public List<SystemRole> getAllRoles() {
        return roleSession.getAllRoles();
    }

    private void setRoleNameList() {
        List<SystemRole> roles = getAllRoles();
        roleNameList = new ArrayList<>();
        for (SystemRole role : roles) {
            roleNameList.add(role.getRoleName());
        }
    }

    public void onRoleSelected() throws NoSuchRoleException {
        permissionList = roleSession.getRolePermissions(selectedRole.getRoleName());
    }

    public String grantRolePermissions() {
        roleSession.grantRolePermissions(selectedRole, permissionList);
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        context.addMessage(null, new FacesMessage("Successful", "Grant role: " + selectedRole.getRoleName() + " with permissions"));
        return navigationController.redirectToGrantRolePermissions();
    }
    
    public List<Permission> getRolePermissionsByRoleName(String roleName) throws NoSuchRoleException{
        return roleSession.getRolePermissions(roleName);
    }
    
    //Getter and Setter
    public UserController getUserController() {
        return userController;
    }

    public void setUserController(UserController userController) {
        this.userController = userController;
    }

    public List<String> getRoleNameList() {
        return roleNameList;
    }

    public void setRoleNameList(List<String> roleNameList) {
        this.roleNameList = roleNameList;
    }

    public List<SystemRole> getRoles() {
        return roles;
    }

    public void setRoles(List<SystemRole> roles) {
        this.roles = roles;
    }

    public List<SystemRole> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<SystemRole> roleList) {
        this.roleList = roleList;
    }

    public List<RolePermission> getRolePermissionList() {
        return rolePermissionList;
    }

    public void setRolePermissionList(List<RolePermission> rolePermissionList) {
        this.rolePermissionList = rolePermissionList;
    }

    public SystemRole getSelectedRole() {
        return selectedRole;
    }

    public void setSelectedRole(SystemRole selectedRole) {
        this.selectedRole = selectedRole;
    }

    public List<Permission> getPermissionList() {
        return permissionList;
    }
    
    public void setPermissionList(List<Permission> permissionList) {
        this.permissionList = permissionList;
    }

}
