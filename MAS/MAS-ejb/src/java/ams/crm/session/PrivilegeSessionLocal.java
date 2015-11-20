/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.crm.entity.Membership;
import ams.crm.entity.Privilege;
import ams.crm.entity.PrivilegeValue;
import ams.crm.util.exception.NoSuchPrivilegeValueException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Bowen
 */
@Local
public interface PrivilegeSessionLocal {
    public List<Privilege> getAllPrivileges();
    public List<Membership> getAllMemberships();
    public PrivilegeValue getPVById(Long pId, Long mid)throws NoSuchPrivilegeValueException;
}
