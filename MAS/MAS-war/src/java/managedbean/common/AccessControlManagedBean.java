/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.common;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.bean.RequestScoped;
import javax.inject.Named;
import mas.common.session.AccessControlSessionLocal;

/**
 *
 * @author Lewis
 */
@Named(value = "accessControlManagedBean")
@RequestScoped
public class AccessControlManagedBean implements Serializable{
    @EJB
    private AccessControlSessionLocal accessControlSession;
    
    /**
     * Creates a new instance of AccessControlManagedBean
     */
    public AccessControlManagedBean() {
    }
    
    public void createSystemRole(String roleName){
        accessControlSession.addNewSystemRole(roleName);
    }
    
    public void createPermission(String module, String title){
        accessControlSession.addNewPermission(module, title);
    }
    
    public void updateRolePermission(String roleName, Map<String, ArrayList<String>> permissions){
        accessControlSession.assignRoleToPermissions(roleName, permissions);
    }
    
    public void updateUserRole(String username, ArrayList<String> roles){
        accessControlSession.assignUserToRole(username, roles);
    }
    
    public void displayUserRoles(String username){
        accessControlSession.getUserRoles(username);
    }
    
    public void displayRolePermission(String roleName){
        accessControlSession.getRolePermissions(roleName);
    }
}
