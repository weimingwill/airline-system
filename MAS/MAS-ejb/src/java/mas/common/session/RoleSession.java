/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mas.common.entity.Permission;
import mas.common.entity.SystemRole;
import mas.common.entity.SystemUser;
import mas.common.util.exception.NoSuchPermissionException;
import mas.common.util.exception.NoSuchRoleException;
import mas.common.util.exception.ExistSuchRoleException;
import mas.common.util.helper.RolePermission;
import mas.common.util.helper.UserMsg;
import mas.util.helper.SafeHelper;

/**
 *
 * @author winga_000
 */
@Stateless
public class RoleSession implements RoleSessionLocal {

    @PersistenceContext
    private EntityManager entityManager;

    @EJB
    private SystemUserSessionLocal systemUsersSession;
    @EJB
    private PermissionSessionLocal permissionSession;

    @Override
    public SystemRole getSystemRoleByName(String roleName) throws NoSuchRoleException {
        Query query = entityManager.createQuery("SELECT r FROM SystemRole r WHERE r.roleName = :inRoleName and r.deleted = FALSE");
        query.setParameter("inRoleName", roleName);
        SystemRole systemRole = null;
        try {
            systemRole = (SystemRole) query.getSingleResult();
        } catch (NoResultException ex) {
            throw new NoSuchRoleException(UserMsg.NO_SUCH_ROLE_ERROR);
        }
        return systemRole;
    }

    @Override
    public List<Permission> getRolePermissions(String roleName) throws NoSuchRoleException {
        Query query = entityManager.createQuery("SELECT p FROM SystemRole r join r.permissions rp, Permission p "
                + "WHERE rp.permissionId = p.permissionId and r.roleName = :inRoleName and r.deleted = FALSE and p.deleted = FALSE and r.deleted = FALSE");
        query.setParameter("inRoleName", roleName);
        List<Permission> permissions = null;
        try {
            permissions = (List<Permission>) query.getResultList();
        } catch (NoResultException ex) {
            throw new NoSuchRoleException(UserMsg.NO_SUCH_ROLE_ERROR);
        }
        return permissions;
    }

    @Override
    public List<SystemRole> getAllRoles() {
        Query query = entityManager.createQuery("SELECT r FROM SystemRole r where r.deleted = FALSE");
        return query.getResultList();
    }

    @Override
    public List<String> getRolesNameList() {
        Query query = entityManager.createQuery("SELECT r.roleName FROM SystemRole r where r.deleted = FALSE");
        return (List<String>) query.getResultList();
    }

    @Override
    public void createSystemRole(String roleName, String[] permissions) throws ExistSuchRoleException, NoSuchPermissionException {
        verifySystemRole(roleName);
        SystemRole role = new SystemRole();
        role.create(roleName);
        for (String permission : permissions) {
            String system = permission.split(":")[0];
            String module = permission.split(":")[1];
            Permission p = permissionSession.getPermission(system, module);
            role.getPermissions().add(p);
            p.getSystemRoles().add(role);
        }
        entityManager.persist(role);
        entityManager.flush();
    }

    @Override
    public void verifySystemRole(String roleName) throws ExistSuchRoleException {
        List<SystemRole> roles = getAllRoles();
        for (SystemRole role : roles) {
            if (role.getRoleName().equals(roleName)) {
                throw new ExistSuchRoleException(UserMsg.EXIST_ROLE_ERROR);
            }
        }
    }

    @Override
    public void grantRolePermissions(SystemRole role, List<Permission> permissions) {
        SystemRole systemRole = entityManager.find(SystemRole.class, role.getSystemRoleId());
        systemRole.setPermissions(permissions);
        entityManager.merge(systemRole);
    }

    @Override
    public String deleteRole(String roleName) throws NoSuchRoleException {
        SystemRole systemRole = getSystemRoleByName(roleName);
        if (systemRole == null) {
            throw new NoSuchRoleException(UserMsg.NO_SUCH_ROLE_ERROR);
        } else {
            systemRole.setDeleted(true);
            return roleName;
        }
    }

}
