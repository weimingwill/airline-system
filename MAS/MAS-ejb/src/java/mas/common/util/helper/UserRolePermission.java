/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.util.helper;

/**
 *
 * @author winga_000
 */
public class UserRolePermission {
    private String user;
    private String role;
    private String permission;

    public UserRolePermission(String user, String role, String permission) {
        this.user = user;
        this.role = role;
        this.permission = permission;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
    
    
}
