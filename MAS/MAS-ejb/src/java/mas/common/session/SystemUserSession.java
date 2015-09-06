/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.session;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mas.common.entity.SystemMsg;
import mas.common.entity.SystemUser;
import mas.common.util.helper.UserMsg;
import mas.common.util.exception.InvalidPasswordException;
import mas.common.util.exception.UserDoesNotExistException;
import mas.common.util.exception.UserExistException;

/**
 *
 * @author Lewis
 */
@Stateless
public class SystemUserSession implements SystemUserSessionLocal {

    @EJB
    private AccessControlSessionLocal accessControlSession;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    private EntityManager entityManager;
    
    private SystemMsg systemMsg;    
    
    @Override
    public SystemUser getSystemUserByName(String username) {
        Query query = entityManager.createQuery("SELECT u FROM SystemUser u WHERE u.username = :inUserName");
        query.setParameter("inUserName", username);
        SystemUser user = null;
        try {
            user = (SystemUser) query.getSingleResult();
        } catch (NoResultException ex) {
            ex.printStackTrace();
        }
        return user;
    }
    
    @Override
    public void verifySystemUserPassword(String username, String inputPassword) throws UserDoesNotExistException, InvalidPasswordException {
        SystemUser user = getSystemUserByName(username);
        if (user == null) {
            throw new UserDoesNotExistException(UserMsg.WRONG_USERNAME_ERROR);
        } else {
            String userPassword = user.getPassword();
            if (!userPassword.equals(inputPassword)) {
                throw new InvalidPasswordException(UserMsg.WRONG_PASSWORD_ERROR);
            }
        }
    }

    @Override
    public List<SystemMsg> getUserMessages(String username) {
        SystemUser user = getSystemUserByName(username);
        return user.getSystemMsgs();
    }

    @Override
    public List<SystemMsg> getUserUnreadMessages(String username) {
        SystemUser user = getSystemUserByName(username);
        List<SystemMsg> unreadMsg = new ArrayList<SystemMsg>();
        try {
            for(SystemMsg msg : user.getSystemMsgs()){
                if(!msg.isReaded()){
                    unreadMsg.add(msg);
                }
            }
            return unreadMsg;            
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void readUnreadMessages(String username) {
        List<SystemMsg> unreadMsgs = getUserMessages(username);
        for(SystemMsg msg : unreadMsgs){
            msg.setReaded(true);
            entityManager.merge(msg);
        }
    }

    @Override
    public List<SystemUser> getAllUsers() {
        Query query = entityManager.createQuery("SELECT u FROM SystemUser u");
        return query.getResultList();          
    }

    @Override
    public List<SystemUser> getAllOtherUsers(String username) {
        Query query = entityManager.createQuery("SELECT u FROM SystemUser u where u.username <> :username");
        query.setParameter("username", username);
        return query.getResultList();
    }

    @Override
    public void createUser(String username, String password) throws UserExistException{
        SystemUser user = getSystemUserByName(username);
        if(user != null){
            throw new UserExistException(UserMsg.WRONG_USERNAME_EXIST_ERROR);
        } else{
            SystemUser systemUser = new SystemUser();
            systemUser.setUsername(username);
            systemUser.setPassword(password);
            entityManager.persist(systemUser);
        }
    }
}
