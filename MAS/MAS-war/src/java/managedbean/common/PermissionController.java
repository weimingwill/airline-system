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
import mas.common.session.PermissionSessionLocal;
import mas.common.session.SystemUserSessionLocal;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import managedbean.application.NavigationController;
import mas.common.entity.Permission;
import mas.common.util.exception.ExistSuchPermissionException;
import mas.common.util.helper.UserMsg;

@Named(value = "permissionController")
@RequestScoped
public class PermissionController implements Serializable {

    @Inject
    UserController userController;
    @Inject
    NavigationController navigationController;

    @EJB
    private SystemUserSessionLocal systemUserSession;
    @EJB
    private PermissionSessionLocal permissionSession;

    private String username;
    private List<String> permissionList;
    private String[] selectedPermissions;
    
//    private List<SelectItem> usernameList;
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
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        this.username = (String) sessionMap.get("username");
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



    
//
//    public void displayRolePermission() {
//        
//        try {
//            setRolePermissions(permissionSession.getRolePermissions(inputRoleName));
//        } catch (NoSuchRoleException ex) {
//            
//        } catch (NoSuchPermissionException ex) {
//            setMsg(ex.getMessage());
//            setRolePermissions(null);
//        }
//    }

    
    //Getter and Setter
    
    /**
     * @return the selectedUser
     */
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


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
