/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;
import mas.common.entity.Permission;
import mas.common.entity.SystemRole;
import mas.common.entity.SystemUser;
import mas.common.util.exception.NoSuchPermissionException;
import mas.common.util.exception.NoSuchRoleException;
import mas.common.util.exception.PermissionExistException;
import mas.common.util.exception.RoleExistException;

/**
 *
 * @author Lewis
 */
@Local
public interface AccessControlSessionLocal {
    public SystemUser getSystemUser(String username);
    public SystemRole getSystemRolesByName(String roleName) throws NoSuchRoleException;
    public List<SystemRole> getAllRoles();
    public List<SystemRole> getUserRoles(String username);
    public List<Permission> getAllPermissions();
    public Permission getPermissions(String module, String title);
    public List<Permission> getPermissionsByModule(String module);
    public List<Permission> getRolePermissions(String roleName) throws NoSuchRoleException, NoSuchPermissionException;
    public void createSystemRole(String roleName, String[] permissions) throws RoleExistException;
    public void verifySystemRole(String roleName) throws RoleExistException;
    public void createPermission(String module, String title) throws PermissionExistException;
    public void verifyPermission(String module, String title) throws PermissionExistException;
    public void assignRoleToPermissions(String roleName, Map<String, ArrayList<String>> permissions) throws NoSuchRoleException;
    public void assignUserToRole(String username, ArrayList<String> roles);
    public List<String> getRolesNameList();
}
