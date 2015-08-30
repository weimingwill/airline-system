/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mas.common.entity.SystemRole;
import mas.common.entity.Permission;
import mas.common.entity.SystemUser;

/**
 *
 * @author Lewis
 */
@Stateless
public class AccessControlSession implements AccessControlSessionLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public SystemUser getSystemUserByName(String userName) {
        Query query = entityManager.createQuery("SELECT u FROM SystemUser u WHERE u.username = :inUserName");
        query.setParameter("inUserName", userName);
        SystemUser user = null;
        try {
            user = (SystemUser) query.getSingleResult();
        } catch (NoResultException ex) {
            ex.printStackTrace();
        }
        return user;
    }

    @Override
    public SystemRole getSystemRoleByName(String roleName) {
        Query query = entityManager.createQuery("SELECT r FROM SystemRole r WHERE r.roleName = :inRoleName");
        query.setParameter("inRoleName", roleName);
        SystemRole systemRole = null;
        try {
            systemRole = (SystemRole) query.getSingleResult();
        } catch (NoResultException ex) {
            ex.printStackTrace();
        }
        return systemRole;
    }

    @Override
    public List<Permission> getPermissionsByModule(String module) {
        Query query = entityManager.createQuery("SELECT p FROM Permission p WHERE p.systemModule = :inModule");
        query.setParameter("inModule", module);
        List<Permission> permissions = null;
        try {
            permissions = (List<Permission>) query.getResultList();
        } catch (NoResultException ex) {
            ex.printStackTrace();
        }
        return permissions;
    }

    @Override
    public Permission getPermission(String module, String title) {
        Query query = entityManager.createQuery("SELECT p FROM Permission p WHERE p.systemModule = :inModule AND p.title = :inTitle");
        query.setParameter("inModule", module);
        query.setParameter("inTitle", title);
        Permission permission = null;
        try {
            permission = (Permission) query.getSingleResult();
        } catch (NoResultException ex) {
            ex.printStackTrace();
        }
        return permission;
    }

    @Override
    public List<Permission> getRolePermissions(String roleName) {
        SystemRole role = getSystemRoleByName(roleName);
        List<Permission> permissions = role.getPermissions();
        return permissions;
    }

    @Override
    public List<SystemRole> getUserRoles(String userName) {
        SystemUser user = getSystemUserByName(userName);
        List<SystemRole> roles = user.getRoles();
        return roles;
    }

    @Override
    public Long addNewPermission(String module, String title) {
        Permission permission = new Permission();
        permission.setSystemModule(module);
        permission.setTitle(title);
        entityManager.persist(permission);
        entityManager.flush();
        return permission.getId();
    }

    @Override
    public Long addNewSystemRole(String roleName) {
        SystemRole role = new SystemRole();
        role.setRoleName(roleName);
        entityManager.persist(role);
        entityManager.flush();
        return role.getId();
    }

    @Override
    public void assignRoleToPermissions(String roleName, Map<String, ArrayList<String>> permissions) {
        SystemRole systemRole = getSystemRoleByName(roleName);
        String module;
        List<Permission> permissionList = new ArrayList<Permission>();
        
        for (Map.Entry<String, ArrayList<String>> entry : permissions.entrySet()) {
            module = entry.getKey();
            for(String title: entry.getValue()){
                try {
                    permissionList.add(getPermission(module,title));
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        systemRole.setPermissions(permissionList);
        entityManager.merge(systemRole);
    }

    @Override
    public void assignUserToRole(String username, ArrayList<String> roles) {
        SystemUser systemUser = getSystemUserByName(username);
        List<SystemRole> roleList = new ArrayList<SystemRole>();
        for(String roleName: roles){
            try{
                roleList.add(getSystemRoleByName(roleName));
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        systemUser.setRoles(roleList);
        entityManager.merge(systemUser);
    }
}
