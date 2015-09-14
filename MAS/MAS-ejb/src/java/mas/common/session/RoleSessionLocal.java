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
import mas.common.util.exception.NoSuchPermissionException;
import mas.common.util.exception.NoSuchRoleException;
import mas.common.util.exception.ExistSuchRoleException;
import mas.common.util.helper.RolePermission;

/**
 *
 * @author winga_000
 */
@Local
public interface RoleSessionLocal {
    public SystemRole getSystemRoleByName(String roleName) throws NoSuchRoleException;
//    public List<SystemRole> getSystemRolesByName(String roleName) throws NoSuchRoleException;
    public List<SystemRole> getAllRoles();
    public List<Permission> getRolePermissions(String roleName) throws NoSuchRoleException;
    public void createSystemRole(String roleName, String[] permissions) throws ExistSuchRoleException;
    public void verifySystemRole(String roleName) throws ExistSuchRoleException;
    public void assignRoleToPermissions(String roleName, Map<String, ArrayList<String>> permissions) throws NoSuchRoleException;
//    public void assignUserToRole(String username, ArrayList<String> roles);
    public List<String> getRolesNameList();
    public List<RolePermission> getAllRolesPermission();
    public void grantRolePermissions(SystemRole role, List<Permission> permissions);
}
