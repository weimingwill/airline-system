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
import mas.common.util.exception.PermissionDoesNotExistException;
import mas.common.util.exception.RoleDoesNotExistException;

/**
 *
 * @author Lewis
 */
@Local
public interface AccessControlSessionLocal {
    public SystemUser getSystemUser(String username);
    public SystemRole getSystemRoleByName(String roleName) throws RoleDoesNotExistException;
    public List<SystemRole> getUserRoles(String username);
    public Permission getPermission(String module, String title);
    public List<Permission> getPermissionsByModule(String module);
    public List<Permission> getRolePermissions(String roleName) throws RoleDoesNotExistException, PermissionDoesNotExistException;
    public Long addNewSystemRole(String roleName);
    public Long addNewPermission(String module, String title);
    public void assignRoleToPermissions(String roleName, Map<String, ArrayList<String>> permissions) throws RoleDoesNotExistException;
    public void assignUserToRole(String username, ArrayList<String> roles);
    public List<String> getRolesNameList();
}
