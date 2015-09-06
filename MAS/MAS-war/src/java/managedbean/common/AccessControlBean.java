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
import javax.inject.Named;
import mas.common.session.AccessControlSessionLocal;
import mas.common.session.SystemUserSessionLocal;
import javax.faces.model.SelectItem;
import mas.common.entity.Permission;
import mas.common.util.exception.PermissionDoesNotExistException;
import mas.common.util.exception.RoleDoesNotExistException;
import mas.common.util.helper.UserMsg;

/**
 *
 * @author Lewis
 */
@Named(value = "accessControlBean")
@RequestScoped
public class AccessControlBean implements Serializable{
    @EJB
    private SystemUserSessionLocal systemUserSession;
    @EJB
    private AccessControlSessionLocal accessControlSession;
    
    private String msg;
    // For Role Management
    private String inputRoleName;
    private List<Permission> rolePermissions;
    
    // For User Management
    private List<SelectItem> usernameList;
    private List<SelectItem> roleNameList;
    private List<SelectItem> permissionList;
    
    private String selectedUser;
    private List<String> selectedRoleNames;
    private List<String> selectedPermissions;
    
    /**
     * Creates a new instance of AccessControlManagedBean
     */
    public AccessControlBean() {
    }
    
    @PostConstruct
    public void init(){
        setUsernameList(prepareItems(systemUserSession.getSystemUsernameList()));
        setRoleNameList(prepareItems(accessControlSession.getRolesNameList()));
    }
    
    public void createSystemRole(String roleName){
        accessControlSession.addNewSystemRole(roleName);
    }
    
    public void createPermission(String module, String title){
        accessControlSession.addNewPermission(module, title);
    }
    
    public void updateRolePermission(String roleName, Map<String, ArrayList<String>> permissions) throws RoleDoesNotExistException{
        accessControlSession.assignRoleToPermissions(roleName, permissions);
    }
    
    public void updateUserRole(String username, ArrayList<String> roles){
        accessControlSession.assignUserToRole(username, roles);
    }
    
    public void displayUserRoles(String username){
        accessControlSession.getUserRoles(username);
    }
    
    public void displayRolePermission() 
            throws RoleDoesNotExistException, PermissionDoesNotExistException{
        try {
            setRolePermissions(accessControlSession.getRolePermissions(inputRoleName));
        } catch (RoleDoesNotExistException ex){
            setMsg(ex.getMessage());
        } catch (PermissionDoesNotExistException ex){
            setMsg(ex.getMessage());
            setRolePermissions(null);
        }
    }
    
    private List<SelectItem> prepareItems(List<String> itemNames){
        System.err.println("prepareItem(): before for loop");
        List<SelectItem> resultItems = new ArrayList<SelectItem>();
        for (String itemName : itemNames) {
            resultItems.add(new SelectItem(itemName,itemName));
        }
        System.err.println("prepareItem(): after for loop");
        return resultItems;
    }

    /**
     * @return the usernameList
     */
    public List<SelectItem> getUsernameList() {
        return usernameList;
    }

    /**
     * @param usernameList the usernameList to set
     */
    public void setUsernameList(List<SelectItem> usernameList) {
        this.usernameList = usernameList;
    }

    /**
     * @return the roleNameList
     */
    public List<SelectItem> getRoleNameList() {
        return roleNameList;
    }

    /**
     * @param roleNameList the roleNameList to set
     */
    public void setRoleNameList(List<SelectItem> roleNameList) {
        this.roleNameList = roleNameList;
    }

    /**
     * @return the permissionList
     */
    public List<SelectItem> getPermissionList() {
        return permissionList;
    }

    /**
     * @param permissionList the permissionList to set
     */
    public void setPermissionList(List<SelectItem> permissionList) {
        this.permissionList = permissionList;
    }

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
     * @return the selectedRoleNames
     */
    public List<String> getSelectedRoleNames() {
        return selectedRoleNames;
    }

    /**
     * @param selectedRoleNames the selectedRoleNames to set
     */
    public void setSelectedRoleNames(List<String> selectedRoleNames) {
        this.selectedRoleNames = selectedRoleNames;
    }

    /**
     * @return the selectedPermissions
     */
    public List<String> getSelectedPermissions() {
        return selectedPermissions;
    }

    /**
     * @param selectedPermissions the selectedPermissions to set
     */
    public void setSelectedPermissions(List<String> selectedPermissions) {
        this.selectedPermissions = selectedPermissions;
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

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
