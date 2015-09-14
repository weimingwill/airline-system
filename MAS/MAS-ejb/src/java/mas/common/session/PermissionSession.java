/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.session;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mas.common.entity.Permission;
import mas.common.util.exception.ExistSuchPermissionException;
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
    public List<Permission> getAllPermissions() {
        Query query = entityManager.createQuery("SELECT p FROM Permission p");
        return query.getResultList();        
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
    public void createPermission(String module, String title) throws ExistSuchPermissionException{
        verifyPermission(module, title);
        Permission permission = new Permission();
        permission.setSystemModule(module);
        permission.setTitle(title);
        entityManager.persist(permission);
        entityManager.flush();
    }

    @Override
    public void verifyPermission(String module, String title) throws ExistSuchPermissionException {
        List<Permission> permissions = getAllPermissions();
        for (Permission permission : permissions) {
            if (permission.getSystemModule().equals(module) && permission.getTitle().equals(title)) {
                throw new ExistSuchPermissionException(UserMsg.EXIST_PERMISSION_ERROR);
            }
        }
    }

    @Override
    public Permission getPermissionById(Long permissionId) {
        Query query = entityManager.createQuery("SELECT p FROM Permission p WHERE p.permissionId = :inId");
        query.setParameter("inId", permissionId);
        Permission permission = null;
        try {
            permission = (Permission) query.getSingleResult();
        } catch (NoResultException ex) {
            ex.printStackTrace();
        }
        return permission;        
    }

//    @Override
//    public Permission deletePermission(String roleName, String module, String title) throws NoSuchPermissionException {
//        
//    }
}
