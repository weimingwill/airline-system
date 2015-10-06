/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.session;

import java.util.List;
import javax.ejb.Local;
import mas.common.entity.Permission;
import mas.common.entity.SystemRole;
import mas.common.util.exception.NoSuchPermissionException;
import mas.common.util.exception.NoSuchRoleException;
import mas.common.util.exception.ExistSuchRoleException;

/**
 *
 * @author winga_000
 */
@Local
public interface RoleSessionLocal {

    public SystemRole getSystemRoleByName(String roleName) throws NoSuchRoleException;

    public List<SystemRole> getAllRoles();

    public List<Permission> getRolePermissions(String roleName) throws NoSuchRoleException;

    public void createSystemRole(String roleName, String[] permissions) throws ExistSuchRoleException, NoSuchPermissionException;

    public void verifySystemRole(String roleName) throws ExistSuchRoleException;

    public List<String> getRolesNameList();

    public void grantRolePermissions(SystemRole role, List<Permission> permissions);

    public String deleteRole(String roleName) throws NoSuchRoleException;

}
