/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        Query query = entityManager.createQuery("SELECT r FROM SystemRole r WHERE r.roleName = :inRoleName");
        query.setParameter("inRoleName", roleName);
        SystemRole systemRole = null;
        try {
            systemRole = (SystemRole) query.getSingleResult();
        } catch (NoResultException ex) {
            throw new NoSuchRoleException(UserMsg.NO_SUCH_ROLE_ERROR);
        }
        return systemRole;
    }

//    @Override
//    public List<SystemRole> getSystemRolesByName(String roleName) throws NoSuchRoleException {
//        Query query = entityManager.createQuery("SELECT r FROM SystemRole r WHERE r.roleName = :inRoleName");
//        query.setParameter("inRoleName", roleName);
//        SystemRole systemRole = null;
//        try {
//            systemRole = (SystemRole) query.getResultList();
//        } catch (NoResultException ex) {
//            throw new NoSuchRoleException(UserMsg.NO_SUCH_ROLE_ERROR);
//        }
//        return systemRole;
//    }
    @Override
    public List<Permission> getRolePermissions(String roleName) throws NoSuchRoleException{
        SystemRole role = getSystemRoleByName(roleName);
        if (role == null) {
            throw new NoSuchRoleException(UserMsg.NO_SUCH_ROLE_ERROR);
        } else {
            List<Permission> permissions = role.getPermissions();
            return permissions;
        }
    }

    @Override
    public List<SystemRole> getAllRoles() {
        Query query = entityManager.createQuery("SELECT r FROM SystemRole r");
        return query.getResultList();
    }

    @Override
    public void createSystemRole(String roleName, String[] permissions) throws ExistSuchRoleException {
        verifySystemRole(roleName);
        SystemRole role = new SystemRole();
        role.create(roleName);
        for (String permission : permissions) {
            String module = permission.split(":")[0];
            String title = permission.split(":")[1];
            Permission p = permissionSession.getPermission(module, title);
            role.getPermissions().add(p);
            p.getSystemRoles().add(role);
        }
        entityManager.persist(role);
        entityManager.flush();
    }

    @Override
    public void assignRoleToPermissions(String roleName, Map<String, ArrayList<String>> permissions) throws NoSuchRoleException {
        SystemRole systemRole = getSystemRoleByName(roleName);
        String module;
        List<Permission> permissionList = new ArrayList<Permission>();
        for (Map.Entry<String, ArrayList<String>> entry : permissions.entrySet()) {
            module = entry.getKey();
            for (String title : entry.getValue()) {
                try {
                    permissionList.add(permissionSession.getPermission(module, title));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        systemRole.setPermissions(permissionList);
        entityManager.merge(systemRole);
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
    public List<String> getRolesNameList() {
        Query query = entityManager.createQuery("SELECT r.roleName FROM SystemRole r");
        return (List<String>) query.getResultList();
    }

    @Override
    public List<RolePermission> getAllRolesPermission() {
        List<RolePermission> rolePermissionList = new ArrayList<>();
        for (SystemRole role : getAllRoles()) {
            for (Permission permission : role.getPermissions()) {
                RolePermission rolePermission = new RolePermission(role.getRoleName(), permission.getPermissionName());
                rolePermissionList.add(rolePermission);
            }
        }
        return rolePermissionList;
    }

    @Override
    public void grantRolePermissions(SystemRole role, List<Permission> permissions) {
        SystemRole systemRole = entityManager.find(SystemRole.class, role.getSystemRoleId());
        systemRole.setPermissions(permissions);
        entityManager.merge(systemRole);
    }

}
