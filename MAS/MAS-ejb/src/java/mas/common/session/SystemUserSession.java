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
import mas.common.util.exception.NoSuchEmailException;
import mas.common.util.helper.UserMsg;
import mas.common.util.exception.InvalidPasswordException;
import mas.common.util.exception.NoMessageException;
import mas.common.util.exception.NoSuchUsernameException;
import mas.common.util.exception.ExistSuchUserException;
import mas.common.util.helper.CreateToken;

/**
 *
 * @author Lewis
 */
@Stateless
public class SystemUserSession implements SystemUserSessionLocal {

    @EJB
    private PermissionSessionLocal accessControlSession;

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
            user = null;
        }
        return user;
    }

    @Override
    public SystemUser getSystemUserByEmail(String email) {
        Query query = entityManager.createQuery("SELECT u FROM SystemUser u WHERE u.email = :email");
        query.setParameter("email", email);
        SystemUser user = null;
        try {
            user = (SystemUser) query.getSingleResult();
        } catch (NoResultException ex) {
            ex.printStackTrace();
        }
        return user;
    }

    @Override
    public void verifySystemUserPassword(String username, String inputPassword) throws NoSuchUsernameException, InvalidPasswordException {
        SystemUser user = getSystemUserByName(username);
        if (user == null) {
            throw new NoSuchUsernameException(UserMsg.WRONG_USERNAME_ERROR);
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
        try {
            return user.getSystemMsgs();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<SystemMsg> getUserUnreadMessages(String username) throws NoSuchUsernameException{
        SystemUser user = getSystemUserByName(username);
        if (user == null) {
            throw new NoSuchUsernameException(UserMsg.WRONG_USERNAME_ERROR);
        } else {
            List<SystemMsg> unreadMsg = new ArrayList<SystemMsg>();
            try {
                for (SystemMsg msg : user.getSystemMsgs()) {
                    if (!msg.isReaded()) {
                        unreadMsg.add(msg);
                    }
                }
                return unreadMsg;
            } catch (Exception e) {
                return null;
            }
        }
    }

    @Override
    public void readUnreadMessages(String username) throws NoMessageException{
        List<SystemMsg> unreadMsgs = getUserMessages(username);
        if(unreadMsgs == null){
            throw new NoMessageException(UserMsg.NO_MESSAGE_ERROR);
        } else {
            for (SystemMsg msg : unreadMsgs) {
                msg.setReaded(true);
                entityManager.merge(msg);
            }            
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
    public void createUser(String username, String password) throws ExistSuchUserException {
        SystemUser user = getSystemUserByName(username);
        if (user != null) {
            throw new ExistSuchUserException(UserMsg.WRONG_USERNAME_EXIST_ERROR);
        } else {
            SystemUser systemUser = new SystemUser();
            systemUser.setUsername(username);
            systemUser.setPassword(password);
            entityManager.persist(systemUser);
        }
    }

    @Override
    public List<String> getSystemUsernameList() {
        Query query = entityManager.createQuery("SELECT u.username FROM SystemUser u");
        return (List<String>) query.getResultList();
    }

    @Override
    public void verifySystemUserEmail(String email) throws NoSuchEmailException {
        SystemUser user = getSystemUserByEmail(email);
        if (user == null) {
            throw new NoSuchEmailException(UserMsg.NO_SUCH_EMAIL_ERROR);
        }
    }

    @Override
    public void resetPassword(String email, String password) throws NoSuchEmailException {
        SystemUser user = getSystemUserByEmail(email);
        verifySystemUserEmail(email);
        user.setPassword(password);
        entityManager.merge(user);
    }

    @Override
    public void setResetDigest(String email, String resetDigest) {
        SystemUser user = getSystemUserByEmail(email);
        user.setResetDigest(resetDigest);
        entityManager.merge(user);
    }

    @Override
    public void expireResetPassword(String email) {       
        SystemUser user = getSystemUserByEmail(email);
        String resetDigest = CreateToken.createNewToken();
        user.setResetDigest(resetDigest);
        entityManager.merge(user);
    }

    @Override
    public void lockUser(String username) {
        SystemUser user = getSystemUserByName(username);
        user.setLocked(true);
        entityManager.merge(user);
    }

    @Override
    public void unlockUser(String username) {
        SystemUser user = getSystemUserByName(username);
        user.setLocked(false);
        entityManager.merge(user);
    }
}
