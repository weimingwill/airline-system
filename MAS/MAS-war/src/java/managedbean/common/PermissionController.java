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
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import mas.common.session.PermissionSessionLocal;
import mas.common.session.SystemUserSessionLocal;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;
import mas.common.entity.Permission;
import mas.common.util.exception.ExistSuchPermissionException;
import mas.common.util.exception.NoSuchPermissionException;
import mas.common.util.helper.UserMsg;

@Named(value = "permissionController")
@RequestScoped
public class PermissionController implements Serializable {

    @Inject
    NavigationController navigationController;
    @Inject
    MsgController msgController;
    
    @EJB
    private SystemUserSessionLocal systemUserSession;
    @EJB
    private PermissionSessionLocal permissionSession;

    private List<String> permissionList;
    private String[] selectedPermissions;
    
//    private List<SelectItem> roleNameList;
//    private List<SelectItem> permissionList;
    

//    private List<String> selectedRoleNames;
//    private List<String> selectedPermissions;

    /**
     * Creates a new instance of AccessControlManagedBean
     */
    public PermissionController() {
    }

    @PostConstruct
    public void init() {
        setPermissionList();
    }

    public List<Permission> getAllPermissions() {
        return permissionSession.getAllPermissions();
    }
    
    public String createPermission(String system, String systemModule) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            permissionSession.createPermission(system, systemModule);
            context.addMessage(null, new FacesMessage("Successful", "Create new permission: " + system + ":" + systemModule + " successfully!"));
        } catch (ExistSuchPermissionException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", UserMsg.EXIST_PERMISSION_ERROR));
        }
        return navigationController.redirectToCreatePermission();
    }

    private void setPermissionList(){
        List<Permission> permissions = getAllPermissions();
        permissionList = new ArrayList<String>();
        for (Permission permission : permissions) {
            String p = permission.getPermissionName();
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

    public List<String> getSystemModuelsBySystem(String system){
        return permissionSession.getSystemModulesBySystem(system);
    }

    public String deletePermission(String system, String systemModule){
        try {
            String permission = permissionSession.deletePermission(system, systemModule);
            msgController.addMessage("Delete " + permission + " successfully!");
        } catch (NoSuchPermissionException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToViewAllPermission();
    }
    
    //Getter and Setter
    
    /**
     * @return the selectedUser
     */

    public List<String> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<String> permissionList) {
        this.permissionList = permissionList;
    }

    public String[] getSelectedPermissions() {
        return selectedPermissions;
    }

    public void setSelectedPermissions(String[] selectedPermissions) {
        this.selectedPermissions = selectedPermissions;
    }

    
    
}
