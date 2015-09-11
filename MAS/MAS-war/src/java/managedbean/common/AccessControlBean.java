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
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import mas.common.session.AccessControlSessionLocal;
import mas.common.session.SystemUserSessionLocal;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import managedbean.application.NavigationBean;
import mas.common.entity.Permission;
import mas.common.entity.SystemRole;
import mas.common.util.exception.NoSuchPermissionException;
import mas.common.util.exception.NoSuchRoleException;
import mas.common.util.exception.PermissionExistException;
import mas.common.util.exception.RoleExistException;
import mas.common.util.helper.UserMsg;

@Named(value = "accessControlBean")
@RequestScoped
public class AccessControlBean implements Serializable {

    @Inject
    UserBean userBean;
    @Inject
    NavigationBean navigationBean;

    @EJB
    private SystemUserSessionLocal systemUserSession;
    @EJB
    private AccessControlSessionLocal accessControlSession;

    private String username;
    // For Role Management
    private String inputRoleName;
    private List<Permission> rolePermissions;

    // For User Management
//    private List<SelectItem> usernameList;
//    private List<SelectItem> roleNameList;
//    private List<SelectItem> permissionList;
    private List<String> roleNameList;
    private List<String> permissionList;
    
    private String selectedUser;
    private String[] selectedRoleNames;
    private String[] selectedPermissions;
//    private List<String> selectedRoleNames;
//    private List<String> selectedPermissions;

    /**
     * Creates a new instance of AccessControlManagedBean
     */
    public AccessControlBean() {
    }

    @PostConstruct
    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        this.username = (String) sessionMap.get("username");
        setRoleNameList();
        setPermissionList();
//        setUsernameList(prepareItems(systemUserSession.getSystemUsernameList()));
//        setRoleNameList(prepareItems(accessControlSession.getRolesNameList()));
    }

    public String createSystemRole(String roleName, String[] permissions) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            accessControlSession.createSystemRole(roleName, permissions);
            context.addMessage(null, new FacesMessage("Successful", "Create new role: " + roleName + " successfully!"));
        } catch (RoleExistException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", UserMsg.EXIST_ROLE_ERROR));
        }
        return navigationBean.redirectToCreateRole();
    }

    public List<SystemRole> getAllRoles() {
        return accessControlSession.getAllRoles();
    }

    public List<SystemRole> getUserRoles() {
        return accessControlSession.getUserRoles(username);
    }

    public List<Permission> getAllPermissions() {
        return accessControlSession.getAllPermissions();
    }
    
    public String createPermission(String module, String title) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            accessControlSession.createPermission(module, title);
            context.addMessage(null, new FacesMessage("Successful", "Create new permission: " + module + ":" + title + " successfully!"));
        } catch (PermissionExistException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", UserMsg.EXIST_PERMISSION_ERROR));
        }
        return navigationBean.redirectToCreatePermission();
    }

    public void updateRolePermission(String roleName, Map<String, ArrayList<String>> permissions) throws NoSuchRoleException {
        accessControlSession.assignRoleToPermissions(roleName, permissions);
    }

    public void updateUserRole(String username, ArrayList<String> roles) {
        accessControlSession.assignUserToRole(username, roles);
    }

    public void displayUserRoles(String username) {
        accessControlSession.getUserRoles(username);
    }
    
//
//    public void displayRolePermission() {
//        
//        try {
//            setRolePermissions(accessControlSession.getRolePermissions(inputRoleName));
//        } catch (NoSuchRoleException ex) {
//            
//        } catch (NoSuchPermissionException ex) {
//            setMsg(ex.getMessage());
//            setRolePermissions(null);
//        }
//    }

    private void setRoleNameList(){
        List<SystemRole> roles = getAllRoles();
        roleNameList = new ArrayList<String>();
        for (SystemRole role : roles) {
            roleNameList.add(role.getRoleName());
        }
    }
    
    private void setPermissionList(){
        List<Permission> permissions = getAllPermissions();
        permissionList = new ArrayList<String>();
        for (Permission permission : permissions) {
            String p = permission.getModule() + ":" + permission.getTitle();
            permissionList.add(p);
        }
    }
    
    private List<SelectItem> prepareItems(List<String> itemNames) {
        System.err.println("prepareItem(): before for loop");
        List<SelectItem> resultItems = new ArrayList<SelectItem>();
        for (String itemName : itemNames) {
            resultItems.add(new SelectItem(itemName, itemName));
        }
        System.err.println("prepareItem(): after for loop");
        return resultItems;
    }


    
    //Getter and Setter
    
    /**
     * @return the selectedUser
     */
    public String getSelectedUser() {
        return selectedUser;
    }

    /**
     * @param selectedUser the selectedUser to set
     */
    public void setSelectedUser(String selectedUser) {
        this.selectedUser = selectedUser;
    }
    
    /**
     * @return the inputRoleName
     */
    public String getInputRoleName() {
        return inputRoleName;
    }

    /**
     * @param inputRoleName the inputRoleName to set
     */
    public void setInputRoleName(String inputRoleName) {
        this.inputRoleName = inputRoleName;
    }

    /**
     * @return the rolePermissions
     */
    public List<Permission> getRolePermissions() {
        return rolePermissions;
    }

    /**
     * @param rolePermissions the rolePermissions to set
     */
    public void setRolePermissions(List<Permission> rolePermissions) {
        this.rolePermissions = rolePermissions;
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

    public List<String> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<String> permissionList) {
        this.permissionList = permissionList;
    }

    public String[] getSelectedRoleNames() {
        return selectedRoleNames;
    }

    public void setSelectedRoleNames(String[] selectedRoleNames) {
        this.selectedRoleNames = selectedRoleNames;
    }

    public String[] getSelectedPermissions() {
        return selectedPermissions;
    }

    public void setSelectedPermissions(String[] selectedPermissions) {
        this.selectedPermissions = selectedPermissions;
    }

    
    
}
