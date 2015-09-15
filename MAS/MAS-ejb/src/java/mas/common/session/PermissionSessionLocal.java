/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.session;

import java.util.List;
import javax.ejb.Local;
import mas.common.entity.Permission;
import mas.common.util.exception.ExistSuchPermissionException;
import mas.common.util.exception.NoSuchPermissionException;

/**
 *
 * @author Lewis
 */
@Local
public interface PermissionSessionLocal {
    public List<Permission> getAllPermissions();
    public Permission getPermissionById(Long permissionId);
    public Permission getPermission(String system, String systemModule);
    public List<Permission> getPermissionsByModule(String system);
    public void createPermission(String system, String systemModule) throws ExistSuchPermissionException;
    public void verifyPermission(String system, String systemModule) throws ExistSuchPermissionException;
//    public Permission deletePermission(String roleName, String module, String title) throws NoSuchPermissionException;
}
