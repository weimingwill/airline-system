/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.session;

import javax.ejb.Local;
import mas.common.entity.SystemUser;

/**
 *
 * @author Lewis
 */
@Local
public interface SystemUserSessionLocal {
    public SystemUser getSystemUser(String userName);
    public Boolean verifySystemUserPassword(String userName, String inputPassword);
    
}
