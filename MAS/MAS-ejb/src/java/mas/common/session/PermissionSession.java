/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.session;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mas.common.entity.Permission;
import mas.common.util.exception.ExistSuchPermissionException;
import mas.common.util.exception.NoSuchPermissionException;
import mas.common.util.helper.UserMsg;

/**
 *
 * @author Lewis
 */
@Stateless
public class PermissionSession implements PermissionSessionLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    private EntityManager entityManager;
    
        @Override
    public Permission getPermissionById(Long permissionId) throws NoSuchPermissionException{
        Query query = entityManager.createQuery("SELECT p FROM Permission p WHERE p.permissionId = :inId and p.deleted = FALSE");
        query.setParameter("inId", permissionId);
        Permission permission = null;
        try {
            permission = (Permission) query.getSingleResult();
        } catch (NoResultException ex) {
            throw new NoSuchPermissionException(UserMsg.NO_PERMISSION_ERROR);
        }
        return permission;        
    }
    
    @Override
    public List<Permission> getPermissionsBySystem(String system) throws NoSuchPermissionException{
        Query query = entityManager.createQuery("SELECT p FROM Permission p WHERE p.system = :inSystem and p.deleted = FALSE");
        query.setParameter("inSystem", system);
        List<Permission> permissions = null;
        try {
            permissions = (List<Permission>) query.getResultList();
        } catch (NoResultException ex) {
            throw new NoSuchPermissionException(UserMsg.NO_PERMISSION_ERROR);
        }
        return permissions;
    }

    @Override
    public List<Permission> getAllPermissions() {
        Query query = entityManager.createQuery("SELECT p FROM Permission p where p.deleted = FALSE");
        return query.getResultList();        
    }

    @Override
    public Permission getPermission(String system, String systemModule) throws NoSuchPermissionException{
        Query query = entityManager.createQuery("SELECT p FROM Permission p "
                + "WHERE p.system = :inSystem AND p.systemModule = :inModule and p.deleted = FALSE");
        query.setParameter("inSystem", system);
        query.setParameter("inModule", systemModule);
        Permission permission = null;
        try {
            permission = (Permission) query.getSingleResult();
        } catch (NoResultException ex) {
            throw new NoSuchPermissionException(UserMsg.NO_PERMISSION_ERROR);
        }
        return permission;
    }

    @Override
    public List<String> getSystemModulesBySystem(String sysetm) {
        List<String> systemModules = new ArrayList<>();
        try {
            for (Permission permission : getPermissionsBySystem(sysetm)) {
                systemModules.add(permission.getSystemModule().replace(" Module", ""));
            }
        } catch (NoSuchPermissionException ex) {
            return null;
        }
        return systemModules;
    }
    
    @Override
    public void createPermission(String system, String systemModule) throws ExistSuchPermissionException{
        verifyPermission(system, systemModule);
        Permission permission = new Permission();
        permission.setSystem(system);
        permission.setSystemModule(systemModule);
        entityManager.persist(permission);
        entityManager.flush();
    }

    @Override
    public void verifyPermission(String system, String systemModule) throws ExistSuchPermissionException {
        List<Permission> permissions = getAllPermissions();
        for (Permission permission : permissions) {
            if (permission.getSystemModule().equals(system) && permission.getSystemModule().equals(systemModule)) {
                throw new ExistSuchPermissionException(UserMsg.EXIST_PERMISSION_ERROR);
            }
        }
    }

    @Override
    public String deletePermission(String system, String systemModule) throws NoSuchPermissionException {
        Permission permission = getPermission(system, systemModule);
        if (permission == null) {
            throw new NoSuchPermissionException(UserMsg.NO_PERMISSION_ERROR);
        } else {
            permission.setDeleted(true);
            entityManager.merge(permission);
        }
        return permission.getPermissionName();
    }
}
